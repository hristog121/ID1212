package com.tictactoe.communication;

import com.google.gson.Gson;
import com.tictactoe.controller.GameController;
import com.tictactoe.communication.message.SelectedPositionMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Handles TextWebSocketFrames. Decodes the text in the TextWebSocketFrame from a json message to a SelectedPositionMessage
 * and passes the message to the GameController to handle the user's selected position.
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private GameController gameController;

    public TextWebSocketFrameHandler(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Gson gson = new Gson();
        SelectedPositionMessage selectedPositionMessage = gson.fromJson(textWebSocketFrame.text(), SelectedPositionMessage.class);
        gameController.handleMove(selectedPositionMessage);
    }
}
