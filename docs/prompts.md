# AI-Assisted Development — Prompts Log

This log records the prompts given to Claude (via Cowork) that drove development of the
Gym Membership Login System, satisfying the "Prompts" submission item. Prompts are grouped
by development phase in chronological order. Routine debugging pastes (raw terminal output,
stack traces) are summarized rather than quoted verbatim, since they were data supplied for
diagnosis rather than directives. One credential (a GitHub Personal Access Token, used once
to attempt a push and never stored) has been redacted.

## Phase 1 — Initial completion request

> I need it completed

Context: the project folder at the time contained a partial implementation (`Main.java`,
`LoginFrame.java`, `DashboardFrame.java`, `DatabaseConnection.java`, `UserDAO.java`) plus the
team's SRS document (`Software SRS Group 5 (1).pdf`) and the assignment's submission
checklist (`projectDescription.txt`). Claude read both documents, then asked two clarifying
questions before starting:

- What should be prioritized first: finishing the Java code, JUnit tests, javadoc/diagrams,
  or the final presentation? — **Answer:** finish the code, add JUnit tests, and generate
  javadoc + diagrams.
- How much of the SRS scope should the finished code actually implement (full scope vs.
  core-only)? — **Answer:** full scope.

Mid-session, before implementation began, two related requests arrived:

> i need code deposited to github account @https://github.com/dipeshmahato111/GymMembershipLoginSystem

> https://github.com/dipeshmahato111/GymMembershipLoginSystem

Claude asked how to handle GitHub authentication (paste a Personal Access Token vs. receive
manual commands to run locally):

> I'll paste a Personal Access Token

> tell me how to paste PAT. I need step by step guide where to find it

> `[GitHub Personal Access Token — redacted]`

Claude then built out the full application: `model`, `security`, `database`, `controller`,
and `view` layers; the MySQL schema (`db/schema.sql`); JUnit tests (`test/`); the class
diagram, statechart diagrams, structure/algorithms write-up, and acceptance test plan
(`docs/`); and a project `README.md`.

## Phase 2 — Getting the code onto GitHub

Claude's sandbox has no outbound network access to github.com, so pushing had to happen from
the user's own machine. This phase was an iterative debugging exchange: the user ran the
commands Claude provided and pasted back terminal output at each step; Claude diagnosed each
issue and adjusted its instructions. Issues resolved in this phase included a stale
`.git/index.lock`, an `error: remote origin already exists`, and a non-fast-forward push
rejection (resolved with `git push --force` since the remote had no prior work worth
keeping). Representative user messages:

> I tried but unable to push it.

> \[pasted terminal output showing `git commit`, `git push` results at each step]

## Phase 3 — Runtime bugs after the first push

> I tried to use main.java and it didn't ran

> i see this Caused by: java.lang.NullPointerException: Cannot read the array length because "<local1>" is null ...

Claude traced this to a static-field initialization-order bug in `DatabaseConnection.java`
(a field was read before its own static initializer had run) and fixed it directly in the
project.

> It says "Database unavailable. Please try again later"

Claude added exception logging to surface the real error, then asked whether MySQL was
running and whether `db/schema.sql` had been executed yet (it had not). After the user ran
the schema script:

> Warning: could not initialize default data - Column 'full_name' not found.

This indicated a pre-existing `gym_management` database with an old, incompatible `users`
table. Claude walked the user through dropping and recreating the database from the current
schema; the user confirmed:

> it worked

## Phase 4 — Visual branding

> now push updated repo to github

> i just coonnected extension can you check again *(referring to the Claude in Chrome
> browser extension, after Claude reported it could not capture a live screenshot without it)*

> I want to add logo and font in purple background

Claude asked three clarifying questions (logo: provided vs. Claude-generated; scope: login
screen only vs. every dashboard; font style) before implementing a purple-themed login
screen with a programmatically drawn logo (`view/UiTheme.java`, `view/GymLogoPanel.java`,
updated `view/LoginFrame.java`).

## Phase 5 — Final presentation

> Check MOT and also make final presentation

> Check the folder that i am giving right now *(clarifying "MOT"; no file/folder was actually
> attached in the message, so this was flagged back to the user rather than guessed at)*

Claude asked clarifying questions on deck length, how to render the diagrams (native
PowerPoint shapes vs. rendered images), and how to handle the GitHub "Network" graph
screenshot slide, then generated a 13-slide branded deck
(`docs/GymMembershipLoginSystem-Presentation.pptx`) covering purpose/scope, architecture,
roles, class structure, statecharts, use cases, security/NFR highlights, acceptance testing,
tech stack, and version control.

> also check slide 5. I don't think it is well created

Claude redesigned the class-structure slide: a clean tree-style inheritance connector for the
four `User` subclasses, and five associated classes (FitnessClass, Membership, Payment,
Attendance, Booking) redistributed evenly across the slide with correctly labeled
relationships, replacing the earlier lopsided layout with a misattributed connector line.

> i just coonnected extension can you check again

With the Chrome extension connected, Claude navigated to the repository's Network graph,
captured a real screenshot, and embedded it in the presentation in place of the earlier
placeholder slide.

## Phase 6 — Deliverables audit

> check this txt folder and let me know if every required files is located in my folder or I am missing something.

Claude cross-checked the project folder against every item in `projectDescription.txt`,
reporting what was present (source code, tests, diagrams, write-up, presentation) versus
missing (this prompts log, executed acceptance-test screenshots, generated javadoc HTML —
the last two require a live MySQL connection / local JDK that aren't available in Claude's
sandbox).

> yes *(requesting this prompts log and an acceptance-testing checklist)*
