# System Prompt / Project Context for Claude

## Project Overview
* **Project Name:** Gym Membership Login System[cite: 6, 7]
* **Course:** CS 4398 Software Engineering Project, Fall 2026[cite: 6, 7]
* **Team Members:** Dipesh Mahato, Andrew Lee, Dalton Lyon[cite: 6, 7]
* **Goal:** A standalone Java Swing application replacing manual gym operations with a secure, role-based digital workflow covering memberships, attendance, classes, and payments[cite: 6, 7].

## Tech Stack & Constraints
* **Language:** Java JDK 17[cite: 6, 7]
* **GUI:** Java Swing[cite: 6, 10]
* **Database:** MySQL 8.x via JDBC (`mysql-connector-j-9.7.0.jar`)[cite: 6, 7]
* **Testing:** JUnit 5[cite: 6, 7]
* **Constraint:** Zero third-party dependencies beyond the MySQL JDBC driver and JUnit[cite: 6, 8]. Standard Java libraries must be used for features like CSV exporting, local preferences, and cryptographic password hashing[cite: 6, 8].

## Architectural Standards
* **Layered Design:** The system uses a strict four-layer architecture consisting of View, Controller, DAO, and Model layers[cite: 8].
* **Separation of Concerns:** Views must never touch the database directly[cite: 8, 9]. All graphical interfaces must route user actions through a dedicated Controller, which handles business logic and returns a result object[cite: 8, 9].
* **Database Access:** Every database query must be executed via `PreparedStatement` inside the DAO layer to strictly prevent SQL injection[cite: 8, 9].

## Core Functional Requirements
1. **Register Member:** Capture user profile details, assign a specific membership tier, and activate the subscription[cite: 7, 9].
2. **Role-Based Login:** Provide multi-tier authentication that routes users to specific dashboards based on their role (Administrator, Trainer, Receptionist, Member)[cite: 7, 9].
3. **Attendance Tracking:** Verify a member has an active, non-expired, and non-suspended membership before logging a timestamped check-in record[cite: 7, 9].
4. **Class Booking:** Allow members to reserve class seats, utilizing race-safe database transaction locks to enforce maximum capacity limits[cite: 7, 8].
5. **Process Payment:** Log membership billing transactions, generate receipts, and update or extend subscription states[cite: 7, 9].

## Security & Robustness Requirements
* **Cryptography:** Passwords must be hashed using salted `PBKDF2WithHmacSHA256`[cite: 9, 10]. Plaintext passwords are strictly forbidden from being stored or logged[cite: 7, 9].
* **Brute-Force Lockout:** The system must lock any user account for exactly 15 minutes after 5 consecutive failed authentication attempts[cite: 7, 9]. 
* **Fail-Fast Validation:** All form inputs (e.g., membership fees, phone numbers, email structures) must be validated client-side before triggering database submissions[cite: 7].

## Coding Directives for Claude
* **Provide Complete Implementations:** When refactoring or updating a file, output the complete, runnable code block to maintain structural integrity.
* **Preserve Test Signatures:** Do not alter existing public method signatures tested by the project's 38 passing JUnit test cases unless explicitly requested[cite: 3].
* **Maintain Swing Concurrency:** Ensure heavy database I/O operations do not block the Swing Event Dispatch Thread (EDT) to maintain UI responsiveness[cite: 7].