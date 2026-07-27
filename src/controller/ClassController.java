package controller;

import database.BookingDAO;
import database.FitnessClassDAO;
import model.Booking;
import model.FitnessClass;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** Trainer-side operations for the "Fitness Classes" / "Trainer Scheduling" feature (SRS 2.2). */
public class ClassController {

    private final FitnessClassDAO classDAO = new FitnessClassDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    public Result addClass(int trainerId, String className, LocalDateTime scheduleTime, int maxCapacity) {
        if (className == null || className.isBlank()) {
            return Result.fail("Class name is required.");
        }
        if (scheduleTime == null || scheduleTime.isBefore(LocalDateTime.now())) {
            return Result.fail("Schedule time must be in the future.");
        }
        if (maxCapacity <= 0) {
            return Result.fail("Capacity must be greater than zero.");
        }
        try {
            FitnessClass fc = new FitnessClass(0, trainerId, className.trim(), scheduleTime, maxCapacity);
            int id = classDAO.insert(fc);
            return Result.ok("Class scheduled.", id);
        } catch (SQLException e) {
            return Result.fail("Could not schedule class: " + e.getMessage());
        }
    }

    public Result updateClass(FitnessClass fc) {
        try {
            classDAO.update(fc);
            return Result.ok("Class updated.");
        } catch (SQLException e) {
            return Result.fail("Could not update class: " + e.getMessage());
        }
    }

    public Result deleteClass(int classId) {
        try {
            classDAO.delete(classId);
            return Result.ok("Class removed.");
        } catch (SQLException e) {
            return Result.fail("Could not remove class: " + e.getMessage());
        }
    }

    public List<FitnessClass> listByTrainer(int trainerId) {
        try {
            return classDAO.findByTrainer(trainerId);
        } catch (SQLException e) {
            return List.of();
        }
    }

    public List<FitnessClass> listAll() {
        try {
            return classDAO.findAll();
        } catch (SQLException e) {
            return List.of();
        }
    }

    public List<Booking> rosterForClass(int classId) {
        try {
            return bookingDAO.findByClass(classId);
        } catch (SQLException e) {
            return List.of();
        }
    }
}
