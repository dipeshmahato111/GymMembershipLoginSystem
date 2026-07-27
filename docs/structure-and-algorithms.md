# System Structure and Algorithms

## 1. Layered architecture

The system follows a standard three-layer architecture matching the SRS class diagram (Figure 6):

- **`view`** - Swing GUI. `LoginFrame` is the entry point; `DashboardFrame` is an abstract base class shared by `AdminDashboard`, `TrainerDashboard`, `ReceptionistDashboard`, and `MemberDashboard`, which each add role-specific feature buttons that open feature dialogs (e.g. `CheckInDialog`, `ProcessPaymentDialog`).
- **`controller`** - business logic and validation (e.g. `AuthenticationController`, `MembershipController`, `AttendanceController`, `PaymentController`, `BookingController`, `ClassController`, `UserManagementController`, `ReportController`). Views never talk to the database directly; they only call controllers, and controllers return a `Result`/`AuthResult` object carrying a success flag and a user-facing message. This keeps validation and business rules independently testable from the GUI (see the JUnit tests under `test/controller`).
- **`database`** - one DAO class per table (`UserDAO`, `MembershipDAO`, `AttendanceDAO`, `BookingDAO`, `PaymentDAO`, `TrainerDAO`, `FitnessClassDAO`, `MembershipTierDAO`), all going through `DatabaseConnection`. Every query uses `PreparedStatement` with bound parameters - no string-concatenated SQL anywhere in the project - to satisfy SRS 4.5 ("prepared statements and parameterized queries to mitigate SQL injection").
- **`model`** - plain data classes matching the ERD/class diagram entities.
- **`security`** - `PasswordUtil`, the only place password hashing happens.

## 2. Single-table inheritance for `User`

The SRS class diagram models `Administrator`, `Trainer`, `Receptionist`, and `Member` as subclasses of an abstract `User`. Rather than four separate tables joined at login time, the database uses one `users` table with a `role` column (single-table inheritance). `UserDAO.mapRow()` reads the `role` column and instantiates the matching Java subclass. This was chosen because:

- Every role shares the same authentication fields (username, password hash, email, lockout state), so a single login query works for every role without a `UNION` across four tables.
- Role-specific attributes that only a few rows need (trainer specialization, receptionist shift, admin level) are still modeled as nullable columns / a satellite `trainers` table, avoiding sparse columns on the common path.

## 3. Password hashing algorithm (SRS 4.5)

`security.PasswordUtil` hashes passwords with **PBKDF2WithHmacSHA256** (65,536 iterations, 16-byte random salt, 256-bit derived key), built into the JDK's `javax.crypto` package. This satisfies the SRS requirement for "a secure cryptographic hashing algorithm (such as bcrypt)" while avoiding an extra third-party dependency:

1. `hash(password)` generates a random salt via `SecureRandom`, runs PBKDF2 for 65,536 iterations, and stores `iterations:base64(salt):base64(hash)` as a single string.
2. `verify(password, storedHash)` re-derives the hash using the same salt/iteration count recovered from the stored string and compares it to the stored hash using a constant-time comparison (`slowEquals`) to avoid leaking timing information about how many bytes matched.

No plaintext password is ever written to the database or logged.

## 4. Login lockout algorithm (SRS 4.2 Robustness)

`AuthenticationController.login()` implements brute-force protection:

1. On every failed password check, `UserDAO.incrementFailedAttempts()` increments a per-user counter in the database.
2. If the counter reaches 5, the account is locked by writing `locked_until = now + 15 minutes` (`UserDAO.lockAccount`).
3. Every login attempt first checks whether `locked_until` is still in the future; if so, the attempt is rejected without even checking the password (this also avoids doing redundant work while locked).
4. A successful login resets the counter and clears the lock (`UserDAO.resetFailedAttempts`).

This state machine is illustrated in `docs/statechart-account-lockout.mermaid`.

## 5. Membership activity rule

`Membership.isActive()` is the single rule used everywhere membership status matters (check-in, class booking): a membership is active only if its `status` column is `ACTIVE` **and** `endDate` has not passed. This means an operator can mark a membership `SUSPENDED` to immediately revoke access without waiting for the end date, and an `ACTIVE` membership automatically stops granting access the day after it expires without any batch job - `isActive()` is evaluated live against `LocalDate.now()` every time it's called.

## 6. Class booking capacity check

`BookingController.bookClass()` prevents overbooking with three checks, in order, each short-circuiting on failure:

1. The member's membership must be active (`Membership.isActive()`).
2. The member must not already hold a `CONFIRMED` booking for the same class (`BookingDAO.existsActiveBooking`, backed by a `UNIQUE (member_id, class_id)` constraint as a second line of defense at the database level).
3. `FitnessClassDAO` computes `currentBookings` via a correlated `COUNT(*)` subquery against `bookings` at read time (rather than maintaining a denormalized counter column), so capacity is always computed from the current, authoritative booking rows - no counter can drift out of sync with the underlying data.

## 7. Payment processing and receipt generation

`PaymentController.processPayment()` models the "Process Membership Payment" use case (SRS 3.2.5) against a `PaymentGatewaySimulator` standing in for the real external Payment Gateway actor from the SRS use case diagram (a class project has no live merchant account). The controller:

1. Looks up the membership and its price.
2. Calls `gateway.authorize(amount, method)`; a declined transaction is still recorded (`status = DECLINED`) for audit purposes.
3. On success, extends the membership's `end_date` by one billing cycle and reactivates it if it had expired, then returns a formatted receipt string built by `generateReceipt()`.

Because `PaymentGatewaySimulator` is a separate class behind a two-argument `authorize(amount, method)` method, swapping in a real payment processor later only requires replacing this one class - `PaymentController` does not need to change.
