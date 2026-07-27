package controller;

/**
 * Generic outcome wrapper returned by controller operations, carrying a
 * success flag, a user-facing message, and an optional generated id
 * (e.g. the new user id after registration).
 */
public class Result {

    private final boolean success;
    private final String message;
    private final int generatedId;

    public Result(boolean success, String message) {
        this(success, message, -1);
    }

    public Result(boolean success, String message, int generatedId) {
        this.success = success;
        this.message = message;
        this.generatedId = generatedId;
    }

    public static Result ok(String message) {
        return new Result(true, message);
    }

    public static Result ok(String message, int generatedId) {
        return new Result(true, message, generatedId);
    }

    public static Result fail(String message) {
        return new Result(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getGeneratedId() {
        return generatedId;
    }
}
