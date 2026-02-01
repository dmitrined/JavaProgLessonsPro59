package de.ait.javaproglessonspro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Choise {

    @JsonProperty("message")
    private OpenAiMessage message;

    public Choise(OpenAiMessage message) {
        this.message = message;
    }

    public OpenAiMessage getMessage() {
        return message;
    }
}