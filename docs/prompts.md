# Prompts Used

This project was developed leveraging an AI pair-programmer to accelerate boilerplate generation and system scaffolding. Below is the sequence of technical prompts issued to translate our Software Requirements Specification (SRS) into a functional Java architecture.

---

## Prompt 1

**Attachment:** `SRS.docx` — The team's formal Software Requirements Specification detailing the "Gym Membership Login System" (including use case specifications, entity-relationship models, class hierarchies, and non-functional security constraints).

**Prompt text:**
> Analyze the attached Software Requirements Specification (SRS.docx). Generate a Java 17 backend scaffolding utilizing a Controller-DAO-Model architectural pattern. Implement Data Access Objects (DAOs) using standard JDBC for MySQL persistence, ensuring all database interactions utilize parameterized queries to prevent SQL injection. Output the complete DDL script (`sql/schema.sql`) for the database schema, strictly adhering to the relational constraints defined in the Entity-Relationship Diagram. Finally, draft the Controller logic for the five primary use cases outlined in Section 3.2: Member Registration, Role-Based Authentication, Attendance Tracking, Fitness Class Booking, and Payment Processing. Include baseline implementations for cryptographic password hashing and brute-force account lockout constraints as specified in the non-functional requirements.

**What this produced:** The foundational Java backend and MySQL schema. This included the complete data model, the DAO layer utilizing `PreparedStatement` interfaces, controller classes managing the business rules, and a baseline entry point to validate state transitions and database connectivity.

---

## Prompt 2

**Attachment:** `IMG_2608.png` — A screenshot of the project's final deliverable constraints, mandating specific software engineering artifacts (acceptance test execution reports, UML diagrams, architectural documentation, Javadoc, and comprehensive unit testing).

**Prompt text:**
> Review the attached project requirements matrix (`IMG_2608.png`). Synthesize the remaining SDLC (Software Development Life Cycle) artifacts required for final project delivery. Specifically, generate:
> 1. A comprehensive JUnit 5 test suite targeting the Model and Controller layers, ensuring adequate branch coverage for all fail-fast validation and state-transition logic.
> 2. Javadoc-compliant API documentation for all public interfaces and classes.
> 3. The underlying modeling syntax (e.g., Mermaid or PlantUML) for the system's UML Class Diagram and Statechart Diagrams reflecting the implemented architecture.
> 4. A technical markdown specification detailing the system structure, transaction concurrency controls (e.g., race-safe class capacity checks), and security implementations.
> 5. A structured acceptance test execution plan mapping directly to the primary user flows.

**What this produced:** The remaining technical artifacts required for the final project submission. This included 38 passing JUnit 5 tests, the API documentation structure, the UML diagram files, the algorithm documentation, and the foundational layout for the presentation deck and acceptance test report.

---

## Prompt 3

**Attachment:** `LoginFrame.java`, `AuthenticationController.java`

**Prompt text:**
> The current Swing UI experiences micro-stutters during database authentication queries. Refactor the `AuthenticationController.java` and the `LoginFrame.java` event listeners to ensure all JDBC I/O operations are strictly offloaded from the Swing Event Dispatch Thread (EDT). Implement a `SwingWorker` thread to handle the database transaction asynchronously, and trigger a modal loading spinner in the UI while the thread is active, safely returning the `AuthResult` to the EDT on completion.

**What this produced:** An optimized UI concurrency model. The response provided the threaded implementation for the login process, preventing GUI lockups during network latency spikes and satisfying the system performance constraints outlined in NFR 4.3.

---

## Prompt 4

**Attachment:** `PasswordUtil.java`, `UserDAO.java`

**Prompt text:**
> Conduct a security audit on the current `PasswordUtil.java` implementation. Migrate the legacy SHA-256 hashing algorithm to `PBKDF2WithHmacSHA256` utilizing 210,000 iterations to meet current OWASP key-derivation standards. Following this, refactor `UserDAO.java` to migrate the brute-force lockout counters from in-memory application state to persistent MySQL columns (`failed_login_attempts`, `locked_until`). Ensure the schema migration script is included.

**What this produced:** A comprehensive security hardening update. The AI generated the PBKDF2 cryptography utility, the SQL `ALTER TABLE` scripts for persistent state management, and the conditional logic required to evaluate timestamp-based lockouts directly within the authentication queries.

---

## Prompt 5

**Attachment:** `ReportController.java`

**Prompt text:**
> We need to satisfy the administrative reporting capabilities defined in the SRS without introducing external third-party dependencies like Apache POI or OpenCSV. Generate a native Java utility class capable of parsing a `TableModel` from a given `JTable` and serializing the data into a strictly formatted Comma-Separated Values (.csv) file. Include standard `JFileChooser` dialog logic to allow the Administrator to dictate the output directory and filename at runtime.

**What this produced:** The `CsvExporter.java` utility and the corresponding UI event hooks. This provided the system with native data export functionality, mapping perfectly to the usability and reporting workflows required for the final product demonstration.

---

## Notes on Implementation and Iteration

While the AI generation provided the core architecture and boilerplate, our team manually engineered several critical components and system upgrades to satisfy the rigorous non-functional requirements and final polish:

- **GUI Implementation:** The initial generation provided abstracted business logic. We subsequently engineered a native Java Swing Graphical User Interface, manually tuning layout managers and binding the UI components directly to the controllers to fulfill the graphical interface requirements.
- **Fail-Fast Input Validation:** We manually engineered Regex-based input validation pipelines across the registration and billing forms to reject malformed data before it could trigger expensive database queries or state exceptions.
- **Git Repo Network Diagram:** Not included in the AI output, as it was pulled directly from our active GitHub repository history (Insights → Network) to demonstrate our authentic branching and commit lifecycle prior to final submission.