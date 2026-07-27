package model;

import java.time.LocalDateTime;

/** Reservation made by a member for a fitness class. */
public class Booking {

    private int bookingId;
    private int memberId;
    private int classId;
    private LocalDateTime bookingDate;
    private String status; // CONFIRMED, CANCELLED

    public Booking() {
    }

    public Booking(int bookingId, int memberId, int classId, LocalDateTime bookingDate, String status) {
        this.bookingId = bookingId;
        this.memberId = memberId;
        this.classId = classId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
