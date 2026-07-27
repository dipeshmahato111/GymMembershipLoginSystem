package model;

import java.time.LocalDate;

/** User with complete system management privileges. */
public class Administrator extends User {

    private String adminLevel;

    public Administrator() {
        super();
        this.role = Role.ADMIN;
    }

    public Administrator(int userId, String username, String fullName, String email, String phone,
                          String status, LocalDate joinDate, String adminLevel) {
        super(userId, username, fullName, email, phone, Role.ADMIN, status, joinDate);
        this.adminLevel = adminLevel;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }
}
