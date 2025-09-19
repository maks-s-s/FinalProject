package com.epam.rd.autocode.spring.project.model.enums;

public enum LoginStatus {
    ERROR_INVALID_CREDENTIALS("Invalid email or password", "error"),
    ERROR_EXPIRED_TOKEN("Your session has expired. Please log in again to continue", "error"),
    INFO_CHECK_EMAIL("We sent an email with password recovery link, check it!", "info"),
    INFO_LOGOUT_SUCCESS("You successfully logged out", "info"),
    INFO_SUCCESSFUL_PASSWORD_CHANGE("Your password was successfully changed", "info"),
    INFO_SUCCESSFUL_REGISTRATION("Successful registration. Please login", "info");

    private final String message;
    private final String type;

    LoginStatus(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() { return message; }
    public String getType() { return type; }
}
