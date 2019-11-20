package app.models;

public class Status {
    private int httpStatus;
    private String message;

    public Status() {
    }

    public Status(String message) {
        this.message = message;
    }

    public Status(int status, String message) {
        this.httpStatus = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return httpStatus;
    }

    public void setStatus(int status) {
        this.httpStatus = status;
    }
}
