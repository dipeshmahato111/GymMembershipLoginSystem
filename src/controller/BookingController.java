package controller;

import database.BookingDAO;
import database.FitnessClassDAO;
import database.MembershipDAO;
import model.Booking;
import model.FitnessClass;
import model.Membership;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** Implements the "Book Fitness Class" use case (SRS 3.2.4), member side. */
public class BookingController {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final FitnessClassDAO classDAO = new FitnessClassDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();

    public Result bookClass(int memberId, int classId) {
        try {
            Membership membership = membershipDAO.findLatestByMemberId(memberId);
            if (membership == null || !membership.isActive()) {
                return Result.fail("Membership has expired. Please renew before booking a class.");
            }
            FitnessClass fc = classDAO.findById(classId);
            if (fc == null) {
                return Result.fail("Class not found.");
            }
            if (bookingDAO.existsActiveBooking(memberId, classId)) {
                return Result.fail("You have already booked this class.");
            }
            if (fc.getCurrentBookings() >= fc.getMaxCapacity()) {
                return Result.fail("This class is already full.");
            }
            // A member who previously cancelled still has a row because of the
            // UNIQUE (member_id, class_id) constraint - re-activate it instead
            // of inserting, which would violate the constraint.
            Booking existing = bookingDAO.findByMemberAndClass(memberId, classId);
            if (existing != null) {
                bookingDAO.reconfirm(existing.getBookingId(), LocalDateTime.now());
                return Result.ok("Class booked: " + fc.getClassName() + " on " + fc.getScheduleTime()
                        + ".", existing.getBookingId());
            }
            Booking booking = new Booking(0, memberId, classId, LocalDateTime.now(), "CONFIRMED");
            int id = bookingDAO.insert(booking);
            return Result.ok("Class booked: " + fc.getClassName() + " on " + fc.getScheduleTime() + ".", id);
        } catch (SQLException e) {
            return Result.fail("Booking failed - database unavailable. " + e.getMessage());
        }
    }

    public Result cancelBooking(int bookingId) {
        try {
            bookingDAO.cancel(bookingId);
            return Result.ok("Booking cancelled.");
        } catch (SQLException e) {
            return Result.fail("Could not cancel booking: " + e.getMessage());
        }
    }

    public List<FitnessClass> listAvailableClasses() {
        try {
            return classDAO.findAll();
        } catch (SQLException e) {
            return List.of();
        }
    }

    public List<Booking> myBookings(int memberId) {
        try {
            return bookingDAO.findByMember(memberId);
        } catch (SQLException e) {
            return List.of();
        }
    }

    public FitnessClass findClass(int classId) {
        try {
            return classDAO.findById(classId);
        } catch (SQLException e) {
            return null;
        }
    }
}
