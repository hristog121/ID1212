package common;

public class LoginResult extends Result {
    private String sessionId;

    public LoginResult(boolean succeess, String errorMessage, String successMessage, String sessionId) {
        super(succeess, errorMessage, successMessage);
        this.sessionId = sessionId;
    }

    public static LoginResult success(String sessionId, String successMessage) {
        return new LoginResult(true, "", successMessage, sessionId);
    }

    public static LoginResult failure(String errorMessage) {
        return new LoginResult(false, errorMessage, "", "");
    }

    public String getSessionId() {
        return sessionId;
    }
}
