package com.tictactoe.communication.message;

import com.google.gson.Gson;

/**
 * A base for all messages that we will send or receive. Each message will have type and will be convertable to json
 */
class OutboundMessage {
    private String type;

    public OutboundMessage(String type) {
        this.type = type;
    }

    public String toJson(){
        Gson converter = new Gson();
        return converter.toJson(this);
    }

    public String getType() {
        return type;
    }
}
