package controller;

import database.MembershipDAO;
import database.MembershipTierDAO;
import database.UserDAO;
import model.Member;
import model.Membership;
import model.MembershipTier;
import model.Role;
import model.User;
import security.PasswordUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implements the "Register Member" and "Renew/Suspend Membership" use
 * cases (SRS 3.2.1) and membership tier maintenance (SRS 4.4).
 */
public class MembershipController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final UserDAO userDAO = new UserDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final MembershipTierDAO tierDAO = new MembershipTierDAO();

    /**
     * Use case 3.2.1 Register Member: creates the member's login account
     * and an initial active membership for the chosen tier.
     */
    public Result registerMember(String username, String plainPassword, String fullName, String email,
                                  String phone, String tierName) {
        if (isBlank(username) || isBlank(plainPassword) || isBlank(fullName) || isBlank(email)) {
            return Result.fail("Please fill in all required fields.");
        }
        if (plainPassword.length() < 6) {
            return Result.fail("Password must be at least 6 characters.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return Result.fail("Invalid email format. Please include an '@' symbol and a valid domain.");
        }
        try {
            if (userDAO.usernameExists(username)) {
                return Result.fail("That username is already taken.");
            }
            if (userDAO.emailExists(email)) {
                return Result.fail("Duplicate email address - a member already uses this email.");
            }
            MembershipTier tier = tierDAO.findByName(tierName);
            if (tier == null) {
                return Result.fail("Unknown membership plan selected.");
            }

            Member member = new Member(0, username.trim(), fullName.trim(), email.trim(), phone, "ACTIVE",
                    LocalDate.now());
            String hash = PasswordUtil.hash(plainPassword);
            int memberId = userDAO.insertUser(member, hash, null);

            Membership membership = new Membership(0, memberId, tier.getTierName(), tier.getPrice(),
                    LocalDate.now(), LocalDate.now().plusMonths(tier.getDurationMonths()), "ACTIVE");
            int membershipId = membershipDAO.insert(membership);

            return Result.ok("Member registered successfully. Member ID: " + memberId, membershipId);
        } catch (SQLException e) {
            return Result.fail("Registration failed - database unavailable. " + e.getMessage());
        }
    }

    public List<MembershipTier> listTiers() {
        try {
            return tierDAO.findAll();
        } catch (SQLException e) {
            return List.of();
        }
    }

    public Result saveTier(MembershipTier tier) {
        if (isBlank(tier.getTierName()) || tier.getPrice() < 0 || tier.getDurationMonths() <= 0) {
            return Result.fail("Please provide a valid tier name, non-negative price, and duration.");
        }
        try {
            if (tier.getTierId() <= 0) {
                tierDAO.insert(tier);
                return Result.ok("Membership plan added.");
            } else {
                tierDAO.update(tier);
                return Result.ok("Membership plan updated.");
            }
        } catch (SQLException e) {
            return Result.fail("Could not save plan: " + e.getMessage());
        }
    }

    public Result deleteTier(int tierId) {
        try {
            tierDAO.delete(tierId);
            return Result.ok("Membership plan removed.");
        } catch (SQLException e) {
            return Result.fail("Could not remove plan: " + e.getMessage());
        }
    }

    public Membership getLatestMembership(int memberId) {
        try {
            return membershipDAO.findLatestByMemberId(memberId);
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Membership> getMembershipHistory(int memberId) {
        try {
            return membershipDAO.findByMemberId(memberId);
        } catch (SQLException e) {
            return List.of();
        }
    }

    public List<Membership> listAll() {
        try {
            return membershipDAO.findAll();
        } catch (SQLException e) {
            return List.of();
        }
    }

    public List<Membership> listExpiringWithin(int days) {
        try {
            return membershipDAO.findExpiringWithin(days);
        } catch (SQLException e) {
            return List.of();
        }
    }

    /** Extends a membership's end date by the given number of months and (re)activates it. */
    public Result renewMembership(int membershipId, int months) {
        try {
            Membership m = membershipDAO.findById(membershipId);
            if (m == null) {
                return Result.fail("Membership not found.");
            }
            LocalDate base = m.getEndDate().isBefore(LocalDate.now()) ? LocalDate.now() : m.getEndDate();
            m.setEndDate(base.plusMonths(months));
            m.setStatus("ACTIVE");
            membershipDAO.update(m);
            return Result.ok("Membership renewed until " + m.getEndDate() + ".");
        } catch (SQLException e) {
            return Result.fail("Renewal failed: " + e.getMessage());
        }
    }

    public Result suspendMembership(int membershipId) {
        try {
            Membership m = membershipDAO.findById(membershipId);
            if (m == null) {
                return Result.fail("Membership not found.");
            }
            m.setStatus("SUSPENDED");
            membershipDAO.update(m);
            return Result.ok("Membership suspended.");
        } catch (SQLException e) {
            return Result.fail("Could not suspend membership: " + e.getMessage());
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
