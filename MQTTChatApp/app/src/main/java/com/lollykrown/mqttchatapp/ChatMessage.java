package com.lollykrown.mqttchatapp;

public class ChatMessage {

    private String message;

    public ChatMessage() {
    }

    public ChatMessage(String text) {
        this.message = text;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
