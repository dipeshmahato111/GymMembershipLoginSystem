# Acceptance Testing — Quick Run Checklist

A fast, linear path through all 5 acceptance tests in one sitting. Full context, expected
results, and the "why" behind each step are in `docs/acceptance-test-plan.md` — use this
checklist just to move quickly and not miss a screenshot. Check each box as you go, and
screenshot every window/dialog marked **📸**.

Setup (once): MySQL running, `db/schema.sql` loaded, app running, logged in as
`admin` / `Admin@123` (or whatever you changed it to).

## AT-1 — Register Member

- [ ] Admin or Receptionist dashboard → **Register Member** 📸 (empty form)
- [ ] Fill in a real-looking member (username, password, name, email, phone, pick a plan) → submit 📸 (success dialog with generated Member ID)
- [ ] Open **Register Member** again, submit the *same username* → 📸 ("username already taken" error)
- [ ] Submit with the full name left blank → 📸 (validation error)

## AT-2 — Member Login

- [ ] Log out. Log in as the member you just created → 📸 (filled login screen, then the "Login Successful!" dialog, then that member's dashboard)
- [ ] Log out. Enter the *wrong* password 5 times in a row → 📸 at least one "N attempt(s) remaining" message, and 📸 the final "Account locked" message
- [ ] Try logging in again immediately with the *correct* password → 📸 ("Account locked... try again after HH:MM" — proves lockout blocks even valid credentials)

## AT-3 — Check In

- [ ] As Receptionist/Admin → **Check In**, enter the member's username → 📸 (dialog + today's check-in list showing the new row)
- [ ] Try checking the same member in again immediately → 📸 ("already checked in")
- [ ] Admin Dashboard → Manage Members → suspend that member's membership → try check-in again → 📸 ("expired or suspended... denied")

## AT-4 — Book Fitness Class

- [ ] As a Trainer → My Classes → schedule a class (needs a future date/time) *(one-time setup so there's something to book)*
- [ ] As a Member with an **active** membership → Fitness Classes → select the class → **Book Selected Class** → 📸 (class list, then confirmation)
- [ ] Switch to "My Bookings" tab → 📸 (booking listed)
- [ ] Try booking the same class again → 📸 ("already booked")
- [ ] Suspend that member's membership (Admin) → try booking a different class → 📸 ("membership has expired")
- [ ] Cancel the original booking from "My Bookings" → 📸 (booking gone / cancelled)

## AT-5 — Process Payment

- [ ] As Receptionist → Process Payment → look up a member → 📸 (plan + price shown)
- [ ] Choose a payment method → **Process Payment** → 📸 (receipt dialog)
- [ ] Admin → Manage Members → confirm that member's expiry date moved forward → 📸
- [ ] (Optional, for the declined-payment alternate flow) trigger a failed payment and 📸 the decline message

## Wrap-up

- [ ] Skim all your screenshots — confirm none show a raw Java stack trace instead of a
      friendly error message (that would itself be a bug worth fixing before submitting)
- [ ] Drop the screenshots into a doc/slide per test case, referencing the AT-# labels above
      so they map cleanly onto `docs/acceptance-test-plan.md` for the write-up deliverable
