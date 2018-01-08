package com.tictactoe.communication;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionHandlerTest {

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private EmbeddedChannel ch;

    @BeforeEach
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        ch = new EmbeddedChannel();
    }

    @AfterEach
    public void teardown() {
        ch.finish();
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void shouldPrintOutExceptionWhenHandlingInput() {
        // Setup
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object e) {
                throw new RuntimeException("Some exception");
            }
        });
        ch.pipeline().addLast(new ExceptionHandler());

        // Execute
        ch.writeInbound(new PingWebSocketFrame());

        // Check result
        assertEquals("An exception was thrown while handling message\n", outContent.toString());
        assertTrue(errContent.toString().startsWith("java.lang.RuntimeException: Some exception"));
    }

    @Test
    public void shouldPrintOutExceptionWhenHandlingOutput() {
        // Setup
        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                throw new RuntimeException("Some exception");
            }
        });
        ch.pipeline().addLast(new ExceptionHandler());

        // Execute
        try {
            ch.writeOutbound(new PongWebSocketFrame());
            Thread.sleep(1000);
        } catch (Exception ex) {
            // ignore
        }

        // Check result
        assertEquals("An exception was thrown while sending message\n", outContent.toString());
        assertTrue(errContent.toString().startsWith("java.lang.RuntimeException: Some exception"));
    }
}