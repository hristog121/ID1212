package com.tictactoe.communication;

import com.tictactoe.controller.GameController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;

import static com.tictactoe.controller.GameController.GAME_ID;
import static com.tictactoe.controller.GameController.PLAYER_LETTER;

/**
 * Handles web socket frame messages
 */
public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    private GameController gameController;
    private WebSocketServerHandshaker handshaker;

    public WebSocketHandler(
        WebSocketServerHandshaker handshaker,
        GameController gameController) {
        this.handshaker = handshaker;
        this.gameController = gameController;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object e) {
        if (e instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) e);
        } else {
            System.out.println("Did not receive a WebSocketFrame " + e.getClass());
        }
    }

    /**
     * Handles CloseWebSocketFrame - closes the channel
     * PingWebSocketFrame - sends a PongWebSocketFrame as a response
     * TextWebSocketFrame - transfers the message to the next handler
     * throws UnsupportedOperationException otherwise
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            // Close the game when a user resigns.
            gameController.closeGame(ctx.channel().attr(GAME_ID).get(), ctx.channel().attr(PLAYER_LETTER).get());
            // Close the channel.
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
            return;
        } else if (frame instanceof PingWebSocketFrame) { // Check for a ping frame
            // Send a pong frame
            frame.release();
            ctx.channel().writeAndFlush(new PongWebSocketFrame());
            return;
        } else if (!(frame instanceof TextWebSocketFrame)) { // Check if it is not a TextWebSocketFrame otherwise
            // throw an UnsupportedOperationException
            frame.release();
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        // If it is a TextWebSocketFrame send it to the next handler in the pipeline (TextWebSocketFrameHandler)
        ctx.fireChannelRead(frame);
    }
}
