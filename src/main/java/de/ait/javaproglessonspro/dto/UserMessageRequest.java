package de.ait.javaproglessonspro.dto;

import jakarta.validation.constraints.NotBlank;

public class UserMessageRequest {

    @NotBlank( message = "Message can't be blank")
    private String message;

    public UserMessageRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}