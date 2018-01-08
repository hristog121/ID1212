package com.tictactoe.communication;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.net.SocketAddress;

/**
 * Inbound and outbound exception handler
 */
public class ExceptionHandler extends ChannelDuplexHandler {

    /**
     * All unhandled inbound exceptions will be captured here
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("An exception was thrown while handling message");
        cause.printStackTrace();
    }

    /**
     * All channel write requests will be processed first by this handler. If an exception is thrown down the pipeline
     * we log the exception.
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.writeAndFlush(msg, promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    System.out.println("An exception was thrown while sending message");
                    future.cause().printStackTrace();
                }
            }
        }));
    }
}
