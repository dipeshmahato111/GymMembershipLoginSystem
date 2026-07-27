package model;

/**
 * The set of access roles supported by the Gym Membership Login System.
 * Roles drive Role-Based Access Control (RBAC) as required by SRS section 4.5.
 */
public enum Role {
    ADMIN,
    TRAINER,
    RECEPTIONIST,
    MEMBER
}
