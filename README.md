# Gym Membership Login System

CS 4398 Software Engineering Project - Fall 2026
Team: Dipesh Mahato, Andrew Lee, Dalton Lyon

A desktop Java Swing application implementing the Gym Membership Login
System described in `Software SRS Group 5 (1).pdf`: multi-role
authentication (Admin/Trainer/Receptionist/Member), member registration,
membership plans & renewal, attendance check-in, fitness class booking,
and payment processing.

## Setup

1. **Database.** Install MySQL 8.x, then run the schema script:
   ```
   mysql -u root -p < db/schema.sql
   ```
   This creates the `gym_management` database and all tables. You do
   **not** need to manually insert an admin user or membership plans -
   the app seeds a default admin account and starter plans (Monthly,
   Annual, VIP) the first time it runs.

2. **Configure credentials.** Copy `src/resources/db.properties.example`
   to `src/resources/db.properties` and fill in your local MySQL
   username/password. This file is gitignored on purpose so real
   credentials never get committed.

3. **Open in IntelliJ IDEA** (Java JDK 17 recommended, per SRS 3.7). The
   `mysql-connector-j-9.7.0.jar` library is already wired up in the
   `.iml` module file.

4. **Run** `src/Main.java`.

5. **First login:** username `admin`, password `Admin@123`. Change this
   immediately via Manage Users, or create a fresh admin and delete this
   one, since this default is documented here in a public repo.

## Look & feel

The Login screen uses a purple brand theme (`view/UiTheme.java`) and a
logo drawn at runtime with `Graphics2D` (`view/GymLogoPanel.java`) rather
than a bundled image asset, so there's no external file to manage. The
same palette/fonts can be reused on other dashboards later by calling
into `UiTheme` from those screens.

## Running the unit tests

Open any file under `test/` in IntelliJ and run it - IntelliJ will offer
to auto-download JUnit 5 to the module the first time you run a test
(no Maven/Gradle needed). See `test/` for coverage of `PasswordUtil`,
`Membership`/`FitnessClass` model rules, and the fail-fast validation
branches of every controller.

## Generating API docs (javadoc)

```
javadoc -d docs/api -sourcepath src -subpackages model:controller:database:security:view -private
```

## Project structure

```
src/
  model/        data classes (User hierarchy, Membership, Attendance, Booking, Payment, FitnessClass, ...)
  security/     PasswordUtil (PBKDF2 password hashing)
  database/     one DAO per table + DatabaseConnection + DbInitializer
  controller/   business logic / validation, one per use case area
  view/         Swing UI (LoginFrame, role-based dashboards, feature dialogs,
                UiTheme + GymLogoPanel for the purple-branded login screen)
test/           JUnit 5 unit tests, mirroring the src package layout
db/schema.sql   MySQL schema (see also the ERD in the SRS, Figure 5)
docs/           class diagram, statechart diagrams, structure/algorithm write-up, acceptance test plan
```

See `docs/structure-and-algorithms.md` for a walkthrough of the
architecture and the key algorithms (password hashing, login lockout,
membership activity rule, booking capacity check, payment processing).

## Submission checklist mapping

| Deliverable | Where |
|---|---|
| Description of execution of acceptance testcases + screenshots | `docs/acceptance-test-plan.md` (run locally and capture screenshots per step) |
| UML class diagram | `docs/class-diagram.mermaid` |
| System statechart diagrams | `docs/statechart-membership.mermaid`, `docs/statechart-attendance.mermaid`, `docs/statechart-account-lockout.mermaid` |
| Textual description of structure/algorithms | `docs/structure-and-algorithms.md` |
| Source code | this repository |
| API docs (javadoc) | generate locally with the command above |
| Unit test cases (JUnit) | `test/` |
| Presentation slides | not included - build separately from the above |
| Prompts.md | The prompts used with Claude to build this project. |
| JUnit_Test_Run_Output.txt | Console log of the last full test run — 38/38 tests passing. |
