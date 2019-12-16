package com.jed.whatsapp;

import java.lang.String;
import java.util.Date;

public class Message {

    // ATTRIBUTES
    private Date messageDate;
    private String sender;
    private String messageText;

    // METHODS
    Message(Date messageDate, String sender, String messageText) {
        this.messageDate = messageDate;
        this.sender = sender;
        this.messageText = messageText;
    }

    // GETTER METHODS
    Date getMessageDate() {
        return messageDate;
    }

    String getSender() {
        return sender;
    }

    String getMessageText() {
        return messageText;
    }
}

