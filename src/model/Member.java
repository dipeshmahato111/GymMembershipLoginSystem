package model;

import java.time.LocalDate;

/**
 * A registered gym customer. See SRS 1.4 Definitions.
 */
public class Member extends User {

    public Member() {
        super();
        this.role = Role.MEMBER;
    }

    public Member(int userId, String username, String fullName, String email, String phone,
                   String status, LocalDate joinDate) {
        super(userId, username, fullName, email, phone, Role.MEMBER, status, joinDate);
    }
}
