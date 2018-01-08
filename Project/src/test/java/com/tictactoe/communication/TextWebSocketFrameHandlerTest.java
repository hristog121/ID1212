package com.tictactoe.communication;

import com.tictactoe.communication.message.SelectedPositionMessage;
import com.tictactoe.controller.GameController;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TextWebSocketFrameHandlerTest {

    private GameController gameController;
    private TextWebSocketFrameHandler handler;
    private EmbeddedChannel ch;

    @BeforeEach
    public void setup() {
        gameController = mock(GameController.class);
        handler = new TextWebSocketFrameHandler(gameController);
        ch = new EmbeddedChannel(handler);
    }

    @AfterEach
    public void teardown() {
        ch.finish();
    }

    @Test
    public void shouldReadAndProcessJsonMessage() {
        // Setup
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame("{gameId: 0, player: X, gridId: 0}");

        // Execute
        ch.writeInbound(textWebSocketFrame);

        // Check result
        ArgumentCaptor<SelectedPositionMessage> argumentCaptor = ArgumentCaptor.forClass(SelectedPositionMessage.class);
        verify(gameController).handleMove(argumentCaptor.capture());
        SelectedPositionMessage selectedPositionMessage = argumentCaptor.getValue();
        assertEquals("X", selectedPositionMessage.getPlayer());
        assertEquals("0", selectedPositionMessage.getGridId());
        assertEquals(0, selectedPositionMessage.getGameId());
        assertEquals(0, selectedPositionMessage.getGridIdAsInt());
    }
}