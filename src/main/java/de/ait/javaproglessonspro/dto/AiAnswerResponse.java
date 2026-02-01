package de.ait.javaproglessonspro.dto;

public class AiAnswerResponse {
    private String reply;

    public AiAnswerResponse(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}