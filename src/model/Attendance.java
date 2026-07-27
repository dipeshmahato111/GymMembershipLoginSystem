package model;

import java.time.LocalDateTime;

/** Record of a single member check-in / check-out. */
public class Attendance {

    private int attendanceId;
    private int memberId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public Attendance() {
    }

    public Attendance(int attendanceId, int memberId, LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        this.attendanceId = attendanceId;
        this.memberId = memberId;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
