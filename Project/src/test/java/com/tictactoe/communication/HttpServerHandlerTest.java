package com.tictactoe.communication;

import com.tictactoe.controller.GameController;
import com.tictactoe.model.Game;
import com.tictactoe.model.Player;
import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelProgressivePromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.tictactoe.controller.GameController.GAME_ID;
import static com.tictactoe.controller.GameController.PLAYER_LETTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpServerHandlerTest {

    private GameController gameController;
    private WebSocketServerHandshakerFactory webSocketServerHandshakerFactory;
    private WebSocketServerHandshaker handshaker;
    private Player player;
    private EmbeddedChannel ch;

    @BeforeEach
    public void setup() {
        gameController = mock(GameController.class);
        player = mock(Player.class);
        when(gameController.initGame(any())).thenReturn(player);
        when(player.getLetter()).thenReturn(Game.PlayerLetter.X);
        when(player.getGameId()).thenReturn(0);

        webSocketServerHandshakerFactory = mock(WebSocketServerHandshakerFactory.class);
        handshaker = mock(WebSocketServerHandshaker.class);

        HttpServerHandler channelHandler = new HttpServerHandler(gameController, webSocketServerHandshakerFactory);
        ch = new EmbeddedChannel();
        ch.pipeline().addLast("httpServerHandler", channelHandler);
    }

    @AfterEach
    public void teardown() {
        ch.finish();
    }

    @Test
    public void shouldHandleWebSocketUpgradeRequest() {
        // Setup
        when(webSocketServerHandshakerFactory.newHandshaker(any())).thenReturn(handshaker);
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.get("Connection")).thenReturn("Upgrade");
        when(headers.get("Upgrade")).thenReturn("WebSocket");
        HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.headers()).thenReturn(headers);
        when(httpRequest.method()).thenReturn(HttpMethod.GET);

        DefaultChannelProgressivePromise promise = new DefaultChannelProgressivePromise(ch);
        when(handshaker.handshake(any(Channel.class), any(HttpRequest.class))).thenReturn(promise);

        // Execute
        ch.writeInbound(httpRequest);
        promise.setSuccess();

        // Check result
        assertEquals(0, ch.attr(GAME_ID).get().intValue());
        assertEquals(Game.PlayerLetter.X, ch.attr(PLAYER_LETTER).get());
        List<String> handlers = ch.pipeline().names();
        assertTrue(handlers.contains("webSocketHandler"));
        assertTrue(handlers.contains("textWebSocketFrameHandler"));
        assertTrue(handlers.contains("exceptionHandler"));
        assertFalse(handlers.contains("httpServerHandler"));
        assertFalse(handlers.contains("httpRequestDecoder"));
        assertFalse(handlers.contains("httpResponseEncoder"));
    }

    @Test
    public void shouldReturnForbiddenWhenNotWebSocketUpgradeRequest() {
        // Setup
        when(webSocketServerHandshakerFactory.newHandshaker(any())).thenReturn(handshaker);
        HttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost:9000/");

        // Execute
        ch.writeInbound(httpRequest);

        // Check result
        HttpResponse response = ch.readOutbound();
        assertEquals(HttpResponseStatus.FORBIDDEN, response.status());
    }

    @Test
    public void shouldReturnVersionUnsupported() {
        // Setup
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.get("Connection")).thenReturn("Upgrade");
        when(headers.get("Upgrade")).thenReturn("WebSocket");
        HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.headers()).thenReturn(headers);
        when(httpRequest.method()).thenReturn(HttpMethod.GET);

        // Execute
        ch.writeInbound(httpRequest);

        // Check result
        HttpResponse response = ch.readOutbound();
        assertEquals(HttpResponseStatus.UPGRADE_REQUIRED, response.status());
    }
}