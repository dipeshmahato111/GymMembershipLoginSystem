package model;

import java.time.LocalDate;

/** Staff member responsible for member registration and daily operations. */
public class Receptionist extends User {

    private String shift;

    public Receptionist() {
        super();
        this.role = Role.RECEPTIONIST;
    }

    public Receptionist(int userId, String username, String fullName, String email, String phone,
                         String status, LocalDate joinDate, String shift) {
        super(userId, username, fullName, email, phone, Role.RECEPTIONIST, status, joinDate);
        this.shift = shift;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
