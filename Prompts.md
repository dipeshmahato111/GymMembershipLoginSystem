# Prompts Used

This project was built with Claude (Anthropic) as an AI pair-programmer. Below are
the prompts issued, in order, along with a short note on what was requested.

---

## Prompt 1

**Attachment:** `SRS.docx` — the team's Software Requirements Specification for the
"Gym Membership Login System" (use cases, ERD, class diagram, non-functional
requirements).

**Prompt text:**
> code this

**What this produced:** The initial Java + MySQL/JDBC implementation —
model/DAO/controller layers, `sql/schema.sql`, and a console-based `Main.java`
implementing the five primary use cases from SRS Section 3.2 (Register Member,
Login, Check In, Book Fitness Class, Process Membership Payment), plus the
password-hashing and account-lockout non-functional requirements.

---

## Prompt 2

**Attachment:** `IMG_2608.png` — a screenshot of the course's `projectDescription.txt`
listing the final submission requirements (prompts, acceptance test execution with
screenshots, UML class diagram, statechart diagrams, textual description of
structure/algorithms, source code, Javadoc, JUnit test cases, presentation slides).

**Prompt text:**
> fufill the following prompts

**What this produced:** The remaining project deliverables required for
submission:
- This prompts document
- UML class diagram (`diagrams/class_diagram.png`)
- Statechart diagrams (`diagrams/statecharts.png`)
- Textual description of system structure and key algorithms
  (`Textual_Description_Structure_and_Algorithms.docx`)
- JUnit 5 unit tests for the Model and utility classes (all 38 passing)
- Javadoc API documentation (`javadoc/`)
- Acceptance test case execution report with screenshots of every
  window/pop-up in an end-to-end walkthrough
  (`Acceptance_Test_Execution_Report.docx`)
- A presentation deck summarizing the above (`Presentation.pptx`)

---

## Notes on scope decisions made without an explicit prompt

A few implementation choices were made by Claude where the SRS/description
didn't specify an exact approach, and are called out here for transparency:

- **GUI vs. console:** The original Java system is console-driven (no GUI
  toolkit was specified in the SRS). To produce genuine "screenshots of every
  window and pop-up," a lightweight web-based front end mirroring the same
  controller logic and use-case flows was built specifically for the
  acceptance-test screenshots. The Java system itself is unchanged and can be
  wired to any GUI (Swing/JavaFX/web) using the same controller layer.
- **Password hashing:** Salted SHA-256 was used instead of a dedicated library
  such as bcrypt/Argon2, to keep the project dependency-free; this is flagged
  in the README as the item to swap out before any real production use.
- **Payment gateway:** `PaymentController` simulates gateway approval/decline
  rather than integrating a real payment processor, since none was specified.
- **Git repo network diagram:** Not included here, since it depends on your
  team's actual GitHub repository history (branches/commits), which only you
  have access to. Push this code to your repo and take that screenshot from
  GitHub's "Insights → Network" view before final submission.
