package common;

import java.io.Serializable;

public class Result implements Serializable {
    private boolean succeess;
    private String errorMessage;
    private String successMessage;

    protected Result(boolean succeess, String errorMessage, String successMessage) {
        this.succeess = succeess;
        this.errorMessage = errorMessage;
        this.successMessage = successMessage;
    }

    public static Result success(String successMessage) {
        return new Result(true, "", successMessage);
    }

    public static Result error(String errorMessage) {
        return new Result(false, errorMessage, "");
    }

    public boolean isSucceess() {
        return succeess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }
}
