package model;

import java.time.LocalDate;

/**
 * Abstract base class representing any authenticated actor in the system
 * (Administrator, Trainer, Receptionist, or Member), matching the
 * {@code <<Abstract>> User} class in the SRS class diagram (Figure 6).
 *
 * <p>The system uses a single {@code users} table (single-table inheritance)
 * to store authentication data shared by every role: id, name, email and
 * password hash. Role-specific attributes live in dedicated tables/classes
 * (e.g. {@link Trainer#getSpecialization()}).</p>
 */
public abstract class User {

    protected int userId;
    protected String username;
    protected String fullName;
    protected String email;
    protected String phone;
    protected Role role;
    protected String status; // ACTIVE or SUSPENDED
    protected LocalDate joinDate;

    protected User() {
    }

    protected User(int userId, String username, String fullName, String email, String phone,
                    Role role, String status, LocalDate joinDate) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.joinDate = joinDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    /** @return true when the account status is ACTIVE (not suspended). */
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return fullName + " (" + username + ") [" + role + "]";
    }
}
