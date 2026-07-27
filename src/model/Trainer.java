package model;

import java.time.LocalDate;

/**
 * Gym staff member responsible for teaching fitness classes.
 * {@code trainerId} is the surrogate key of the {@code trainers} table
 * (distinct from {@code userId}) so it can be referenced by
 * {@link FitnessClass#getTrainerId()} per the SRS ERD (Figure 5).
 */
public class Trainer extends User {

    private Integer trainerId;
    private String specialization;

    public Trainer() {
        super();
        this.role = Role.TRAINER;
    }

    public Trainer(int userId, String username, String fullName, String email, String phone,
                    String status, LocalDate joinDate, Integer trainerId, String specialization) {
        super(userId, username, fullName, email, phone, Role.TRAINER, status, joinDate);
        this.trainerId = trainerId;
        this.specialization = specialization;
    }

    public Integer getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Integer trainerId) {
        this.trainerId = trainerId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
