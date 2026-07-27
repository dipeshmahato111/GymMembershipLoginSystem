package model;

import java.time.LocalDateTime;

/** A scheduled fitness session taught by a {@link Trainer}. */
public class FitnessClass {

    private int classId;
    private int trainerId;
    private String className;
    private LocalDateTime scheduleTime;
    private int maxCapacity;

    /** Convenience field (not persisted) populated by joins for display purposes. */
    private String trainerName;
    /** Convenience field (not persisted) populated by controllers for display purposes. */
    private int currentBookings;

    public FitnessClass() {
    }

    public FitnessClass(int classId, int trainerId, String className, LocalDateTime scheduleTime, int maxCapacity) {
        this.classId = classId;
        this.trainerId = trainerId;
        this.className = className;
        this.scheduleTime = scheduleTime;
        this.maxCapacity = maxCapacity;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDateTime getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(LocalDateTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public int getCurrentBookings() {
        return currentBookings;
    }

    public void setCurrentBookings(int currentBookings) {
        this.currentBookings = currentBookings;
    }

    public int getSeatsAvailable() {
        return Math.max(0, maxCapacity - currentBookings);
    }

    @Override
    public String toString() {
        return className + " - " + scheduleTime + " (" + currentBookings + "/" + maxCapacity + ")";
    }
}
