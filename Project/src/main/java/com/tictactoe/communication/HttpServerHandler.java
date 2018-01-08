package com.tictactoe.communication;

import com.tictactoe.controller.GameController;
import com.tictactoe.model.Player;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

import static com.tictactoe.controller.GameController.GAME_ID;
import static com.tictactoe.controller.GameController.PLAYER_LETTER;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

/**
 * Handles HTTP Web Socket upgrade requests. Executes Web Socket Handshake and initiates game if WebSocket Upgrade
 * request. Returns Forbidden otherwise
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private final GameController gameController;
    private final WebSocketServerHandshakerFactory wsFactory;

    public HttpServerHandler(GameController gameController, WebSocketServerHandshakerFactory wsFactory) {
        this.gameController = gameController;
        this.wsFactory = wsFactory;
    }

    /**
     * An incoming message (event). Invoked when a player navigates to the page.
     * The initial page load triggers an HttpRequest. We perform the WebSocket handshake and assign WebSocket handlers
     * to the channel.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof HttpRequest) {
                handleHttpRequest(ctx, (HttpRequest) msg);
            } else {
                System.out.println("Received an unsupported message" + msg.getClass());
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * Checks if the request is a valid WebSocket Upgrade request, performs the WebSocket handshake, removes the current
     * handler from the channel pipeline and signals to the GameController to initiate a game.
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) {

        if (!isWebSocketUpgradeRequest(req)) {
            // Sends a forbidden response if not a WebSocket Upgrade request
            sendHttpResponse(ctx, req, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }

        // Handshake
        final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            // If the handshaker is not initialized properly return Unsupported Version response and close the channel
            wsFactory.sendUnsupportedVersionResponse(ctx.channel())
                    .addListener(ChannelFutureListener.CLOSE);
        } else {

            // Execute WebSocket handshake
            handshaker.handshake(ctx.channel(), req).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    // When handshake complete, switch handlers and initialize game
                    Channel channel = channelFuture.channel();
                    switchToWebSocketHandlers(channel, handshaker);
                    System.out.println("Init game !!!");
                    Player player = gameController.initGame(new TextWebSocketFrameWriter(channel));
                    // Add attributes to the channel. We will need them to identify the user when a CloseWebSocketFrame is received
                    channel.attr(GAME_ID).set(player.getGameId());
                    channel.attr(PLAYER_LETTER).set(player.getLetter());
                }
            });
        }
    }

    private boolean isWebSocketUpgradeRequest(HttpRequest req) {
        return req.method() == HttpMethod.GET && // The quest should be GET
                req.headers().get("Connection") != null &&
                req.headers().get("Connection").equalsIgnoreCase("Upgrade") && // The Connection header should be Upgrade
                req.headers().get("Upgrade") != null &&
                req.headers().get("Upgrade").equalsIgnoreCase("WebSocket"); // The Upgrade header should be WebSocket
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void switchToWebSocketHandlers(Channel channel, WebSocketServerHandshaker handshaker) {
        // Remove HttpRequest handlers
        channel.pipeline().remove("httpServerHandler");

        // Add WebSocket handlers and an Exception handler
        channel.pipeline()
                .addLast("webSocketHandler", new WebSocketHandler(handshaker, gameController))
                .addLast("textWebSocketFrameHandler", new TextWebSocketFrameHandler(gameController))
                .addLast("exceptionHandler", new ExceptionHandler());
    }
}
