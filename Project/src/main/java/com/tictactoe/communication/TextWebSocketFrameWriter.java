package com.tictactoe.communication;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Message writer which sends TextWebSocketFrames to a channel
 */
public class TextWebSocketFrameWriter implements MessageWriter{
    private Channel channel;

    public TextWebSocketFrameWriter(Channel channel) {
        this.channel = channel;
    }

    public void write(String message) {
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }
}
