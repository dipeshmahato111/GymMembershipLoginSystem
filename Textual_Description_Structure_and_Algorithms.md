# Gym Membership Login System — Structure and Algorithms

## 1. System Structure

The system follows a classic four-layer architecture on top of a MySQL
database accessed through JDBC:

```
Main (console entry point)
   │
   ▼
Controller layer   — one class per use case (SRS 3.2)
   │                 AuthenticationController, RegistrationController,
   │                 AttendanceController, BookingController, PaymentController
   ▼
DAO layer           — one class per table, JDBC PreparedStatements only
   │                 UserDAO, MemberDAO, MembershipDAO, AttendanceDAO,
   │                 FitnessClassDAO, BookingDAO, PaymentDAO
   ▼
DatabaseConnection  — single point that opens a java.sql.Connection
   │
   ▼
MySQL (schema in sql/schema.sql)

Model layer (used across all layers): User, Member, Trainer, Membership,
Attendance, FitnessClass, Booking, Payment — plain data objects with no
persistence logic of their own.
```

**Why this split:** each controller method corresponds 1:1 to a use case in
SRS Section 3.2, so the *primary flow* and *alternate flows* documented there
map directly onto `if`/`switch` branches and enum return values
(e.g. `AttendanceController.CheckInResult`) rather than being scattered across
the UI. This keeps the business rules testable independently of any
particular front end (console today; the same controllers already back the
web-based screenshots used in the acceptance test report) and independently
of the database (DAOs are the only classes that touch JDBC).

Password hashing and input validation live in a small `util` package since
they are cross-cutting concerns used by multiple controllers rather than
belonging to any one use case.

## 2. Algorithms Critical to System Behavior

### 2.1 Salted password hashing (`PasswordUtil`)

Plaintext passwords are never stored or compared (SRS NFR 4.5). On
registration:

1. Generate a 16-byte random salt using `SecureRandom`.
2. Compute `SHA-256(salt || password)`.
3. Store `Base64(salt) : Base64(hash)` as a single string.

On login, the stored salt is extracted, the same digest is recomputed over
the submitted password, and the two byte arrays are compared with
`MessageDigest.isEqual` (a constant-time comparison, which avoids leaking
timing information about how many leading bytes matched). Because the salt
is random per user, two users with the same password produce different
stored hashes, which defeats precomputed rainbow-table attacks. (Production
note: a dedicated adaptive-hashing algorithm such as bcrypt or Argon2 is
recommended over SHA-256 for password storage specifically, since it can be
tuned to be deliberately slow against brute-force GPU cracking — this is
called out explicitly in the README.)

### 2.2 Brute-force lockout (`AuthenticationController.login`)

Implements SRS NFR 4.2 ("lock out any user account for 15 minutes after 5
consecutive failed login attempts"):

1. On each failed login, increment `failed_attempts` for that user.
2. If `failed_attempts >= 5`, set `locked_until = now + 15 minutes`.
3. On every login attempt, before checking the password, check whether
   `locked_until` is in the future; if so, reject immediately without even
   evaluating the password (this also avoids leaking, via response timing,
   whether the account is locked vs. whether the password was simply wrong).
4. On any *successful* login, `failed_attempts` is reset to 0 and
   `locked_until` is cleared — a successful login before the 5th failure
   clears the counter, matching the "consecutive" wording in the NFR.

### 2.3 Race-safe class booking (`BookingDAO.bookClass`)

This is the algorithm with the trickiest correctness requirement: two members
booking the last open seat in a fitness class at nearly the same instant must
not both succeed (SRS 3.2.4 alternate flow: "Class is already full"). A naive
"count bookings, then insert if count < capacity" done as two separate
statements has a classic time-of-check-to-time-of-use race condition — both
requests could pass the check before either insert commits.

The fix wraps the whole operation in a single database transaction with a
pessimistic row lock:

1. `SELECT max_capacity FROM fitness_class WHERE class_id = ? FOR UPDATE` —
   this locks the class row for the duration of the transaction. A second,
   concurrent call to `bookClass` for the same class blocks here until the
   first transaction commits or rolls back.
2. Count current `CONFIRMED` bookings for the class.
3. If `count >= max_capacity`, throw `BookingException("Class is already
   full.")` and roll back — no row is ever inserted.
4. Otherwise check for an existing `CONFIRMED` booking by the same member for
   the same class (prevents double-booking; also enforced at the schema level
   by a `UNIQUE (member_id, class_id)` constraint as a second line of
   defense).
5. Insert the new booking and `commit()`.

Because step 1's lock is held until commit, a second transaction attempting
to book the same class cannot read a stale "seats available" count — it
waits, then sees the up-to-date count including the first transaction's
insert. This guarantees at most `max_capacity` `CONFIRMED` bookings ever
exist for a class, even under concurrent access.

### 2.4 Membership / attendance status evaluation

`Membership.isActive()` and the checks in `AttendanceController.checkIn()`
implement the "membership status verification" step of SRS 3.2.3:

- A membership is active only if its stored `status` is `ACTIVE` **and**
  its `endDate` has not yet passed (`!LocalDate.now().isAfter(endDate)`).
  Both conditions are required deliberately: an admin can suspend a
  membership that hasn't reached its end date yet (status becomes
  `SUSPENDED`), and a membership can reach its end date without an admin
  action changing its status (the date check catches this even if a batch
  job hasn't yet flipped the stored status to `EXPIRED`).
- `AttendanceController.checkIn()` layers **member**-level status
  (`SUSPENDED` members are blocked outright, independent of their
  membership) on top of the **membership**-level check above, matching the
  distinct "Member" and "Membership" entities in the ERD and their
  independent status enums documented in the statechart diagrams.

## 3. Data Integrity Choices Worth Calling Out

- All SQL access goes through `PreparedStatement` — no string concatenation
  of user input into SQL — which structurally prevents SQL injection
  (SRS NFR 4.5) rather than relying on input sanitization alone.
- Foreign keys in `sql/schema.sql` mirror every association in the ERD
  (Member → Membership, Member → Attendance, Member → Booking,
  Trainer → FitnessClass, FitnessClass → Booking, Membership → Payment),
  so referential integrity is enforced by the database itself, not just by
  application code.
- Enum-backed status columns (`member.status`, `membership.status`,
  `booking.status`, `payment.status`) are declared as SQL `ENUM` types,
  which rejects any value outside the finite state set shown in the
  statechart diagrams at the database layer, in addition to Java's own enum
  type safety at the application layer.
