# Acceptance Test Plan

These test cases exercise the five primary use cases from SRS section 3.2. Run
each one against a live MySQL instance (see the root `README.md` for setup),
and capture a screenshot of every window/pop-up mentioned so the write-up
deliverable ("Description of execution of acceptance testcases illustrated
with screenshots of all the windows and pop-up windows of the system") can be
assembled directly from this document.

For each test case below: perform the numbered steps, and after each step
take a screenshot of the resulting window before moving to the next step.

## AT-1: Receptionist - Register Member (SRS 3.2.1)

**Preconditions:** logged in as a Receptionist (or Admin) account.

1. From the dashboard, click **Register Member**. *(screenshot: empty form)*
2. Fill in username, password, full name, a valid email, phone, and select a
   membership plan. Click **Register Member**. *(screenshot: filled form)*
3. Confirm the success dialog shows the generated Member ID.
   *(screenshot: success dialog)*
4. Re-open **Register Member** and submit the same username again.
   *(screenshot: "username is already taken" error - exercises the
   Alternate Flow: Duplicate email/username)*
5. Try submitting with a blank full name. *(screenshot: validation error -
   Alternate Flow: Missing required information)*

**Expected result:** step 2 creates an active membership immediately; steps
4-5 are rejected with a clear message and no database row is created.

## AT-2: Member - Login (SRS 3.2.2)

1. From the login screen, enter the new member's username/password and
   click **Login**. *(screenshot: login screen filled in)*
2. Confirm the "Login Successful!" dialog and that the Member Dashboard
   opens next. *(screenshot: success dialog, screenshot: Member Dashboard)*
3. Log out, then attempt to log in with the wrong password 5 times in a row.
   *(screenshot: "Invalid username or password. N attempt(s) remaining"
   for at least one attempt, screenshot: final "Account locked" message -
   exercises SRS 4.2 lockout policy)*
4. Attempt a 6th login with the *correct* password while still locked.
   *(screenshot: "Account locked... try again after HH:MM" - proves the
   lockout blocks even correct credentials until it expires)*

**Expected result:** step 2 succeeds and routes to the correct
role-specific dashboard; step 3 locks the account after the 5th failure;
step 4 is rejected even with the right password until the lock expires.

## AT-3: Member - Check In (SRS 3.2.3)

1. As a Receptionist, open **Check In**, enter the member's username, and
   click **Check In**. *(screenshot: Check-In dialog with today's list)*
2. Confirm the confirmation message and that the member now appears in the
   "today" table. *(screenshot: success message)*
3. Attempt to check the same member in again immediately.
   *(screenshot: "already checked in" message - Alternate Flow)*
4. Suspend that member's membership (Admin Dashboard -> Manage Members ->
   Suspend), then attempt check-in again.
   *(screenshot: "Membership has expired or is suspended. Check-in
   denied." - Alternate Flow)*

**Expected result:** step 1 creates an attendance row with a check-in
timestamp; steps 3-4 are correctly rejected.

## AT-4: Member - Book Fitness Class (SRS 3.2.4)

**Preconditions:** a Trainer has scheduled at least one upcoming class
(Trainer Dashboard -> My Classes -> Schedule New Class).

1. As the Member, open **Fitness Classes** and select a class in the
   "Available Classes" tab, then click **Book Selected Class**.
   *(screenshot: class list, screenshot: booking confirmation)*
2. Switch to the "My Bookings" tab and confirm the booking appears.
   *(screenshot: My Bookings tab)*
3. Attempt to book the same class again. *(screenshot: "already booked"
   message - Alternate Flow)*
4. As Admin, suspend the member's membership, then attempt to book a
   different class as that member. *(screenshot: "Membership has expired"
   message - Alternate Flow)*
5. Cancel the original booking from "My Bookings". *(screenshot: booking
   removed / cancelled)*

**Expected result:** step 1 creates a `CONFIRMED` booking row; steps 3-4
are rejected; step 5 marks the booking `CANCELLED`.

## AT-5: Receptionist - Process Membership Payment (SRS 3.2.5)

1. Open **Process Payment**, look up the member by username, and confirm
   their current plan/price is displayed. *(screenshot: lookup result)*
2. Choose a payment method and click **Process Payment**.
   *(screenshot: receipt dialog)*
3. Verify the membership's expiration date advanced by one billing cycle
   (Admin Dashboard -> Manage Members). *(screenshot: updated expiry date)*
4. Look up a member and process a payment with a blank payment method
   selection forcibly cleared (or amount validation), to see the
   "Payment declined" alternate flow. *(screenshot: declined message)*

**Expected result:** step 2 records a `COMPLETED` payment and extends the
membership; step 4 records a `DECLINED` payment and leaves the membership
unchanged.

---

*Tip: MySQL Workbench or `SELECT * FROM attendance/bookings/payments` in
the MySQL shell is a fast way to double check the underlying rows match
what the UI reports after each step.*
