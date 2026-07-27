-- =====================================================================
-- Gym Membership Login System - MySQL schema
-- Matches the Entity Relationship Diagram in the SRS (Figure 5), extended
-- with authentication/RBAC fields (users.role, lockout fields) and a
-- membership_tiers table so pricing is admin-configurable at runtime.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS gym_management
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE gym_management;

-- Single-table-inheritance "users" table backs every User subclass
-- (Administrator, Trainer, Receptionist, Member) from the class diagram.
CREATE TABLE IF NOT EXISTS users (
    user_id         INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            ENUM('ADMIN','TRAINER','RECEPTIONIST','MEMBER') NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    phone           VARCHAR(20),
    status          ENUM('ACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    join_date       DATE NOT NULL DEFAULT (CURRENT_DATE),
    failed_attempts INT NOT NULL DEFAULT 0,
    locked_until    DATETIME NULL,
    admin_level     VARCHAR(30) NULL,   -- Administrator-only attribute
    shift           VARCHAR(30) NULL,   -- Receptionist-only attribute
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS membership_tiers (
    tier_id         INT AUTO_INCREMENT PRIMARY KEY,
    tier_name       VARCHAR(50) NOT NULL UNIQUE,
    price           DECIMAL(10,2) NOT NULL,
    duration_months INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS memberships (
    membership_id   INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT NOT NULL,
    tier_name       VARCHAR(50) NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          ENUM('ACTIVE','EXPIRED','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_membership_member FOREIGN KEY (member_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS attendance (
    attendance_id   INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT NOT NULL,
    check_in_time   DATETIME NOT NULL,
    check_out_time  DATETIME NULL,
    CONSTRAINT fk_attendance_member FOREIGN KEY (member_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainers (
    trainer_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL UNIQUE,
    specialization  VARCHAR(100),
    CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fitness_classes (
    class_id        INT AUTO_INCREMENT PRIMARY KEY,
    trainer_id      INT NOT NULL,
    class_name      VARCHAR(100) NOT NULL,
    schedule_time   DATETIME NOT NULL,
    max_capacity    INT NOT NULL DEFAULT 20,
    CONSTRAINT fk_class_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id      INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT NOT NULL,
    class_id        INT NOT NULL,
    booking_date    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status          ENUM('CONFIRMED','CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    CONSTRAINT fk_booking_member FOREIGN KEY (member_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_class FOREIGN KEY (class_id) REFERENCES fitness_classes(class_id) ON DELETE CASCADE,
    CONSTRAINT uniq_member_class UNIQUE (member_id, class_id)
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id      INT AUTO_INCREMENT PRIMARY KEY,
    membership_id   INT NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    payment_date    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_method  VARCHAR(30) NOT NULL,
    status          ENUM('COMPLETED','DECLINED','PENDING') NOT NULL DEFAULT 'COMPLETED',
    CONSTRAINT fk_payment_membership FOREIGN KEY (membership_id) REFERENCES memberships(membership_id) ON DELETE CASCADE
);

-- Note: an initial ADMIN user and default membership tiers are seeded
-- automatically at application startup by database.DbInitializer, so a
-- freshly created empty database is enough to boot the app (default
-- login: admin / Admin@123 -- change immediately after first login).
