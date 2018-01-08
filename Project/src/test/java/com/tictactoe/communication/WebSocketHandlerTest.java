package com.tictactoe.communication;

import com.tictactoe.controller.GameController;
import com.tictactoe.model.Game;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tictactoe.controller.GameController.GAME_ID;
import static com.tictactoe.controller.GameController.PLAYER_LETTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WebSocketHandlerTest {

    private GameController gameController;
    private WebSocketServerHandshaker handshaker;
    private WebSocketHandler webSocketHandler;
    private EmbeddedChannel ch;

    @BeforeEach
    public void setup() {
        gameController = mock(GameController.class);
        handshaker = mock(WebSocketServerHandshaker.class);
        webSocketHandler = new WebSocketHandler(handshaker, gameController);
        ch = new EmbeddedChannel(webSocketHandler);
    }

    @AfterEach
    public void teardown() {
        ch.finish();
    }

    @Test
    public void shouldCloseGameAndCloseChannel() {
        // Setup
        ch.attr(GAME_ID).set(0);
        ch.attr(PLAYER_LETTER).set(Game.PlayerLetter.X);
        CloseWebSocketFrame frame = new CloseWebSocketFrame();

        // Execute
        ch.writeInbound(frame);

        // Check result
        verify(gameController).closeGame(0, Game.PlayerLetter.X);
        verify(handshaker).close(eq(ch), eq(frame));
    }

    @Test
    public void shouldSendPongFrame() {
        // Setup
        PingWebSocketFrame frame = new PingWebSocketFrame();

        // Execute
        ch.writeInbound(frame);

        // Check result
        Object result = ch.readOutbound();
        assertTrue(result instanceof PongWebSocketFrame);

        ReferenceCountUtil.release(result);
    }

    @Test
    public void shouldThrowExceptionIfNotTextWebSocketFrame() {
        // Setup
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame();

        // Execute
        try {
            ch.writeInbound(frame);
            fail("Should have thrown exception");
        } catch (UnsupportedOperationException ex) {
            // Check result
            assertEquals("io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame frame types not supported", ex.getMessage());
        }
    }

    @Test
    public void shouldNotChangeTextWebSocketFrame() {
        // Setup
        TextWebSocketFrame frame = new TextWebSocketFrame();

        // Execute
        ch.writeInbound(frame);

        // Check result
        TextWebSocketFrame readFrame = ch.readInbound();
        assertEquals(frame, readFrame);

        ReferenceCountUtil.release(readFrame);
    }
}