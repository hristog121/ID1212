package com.tictactoe.communication;

import com.tictactoe.communication.message.TurnMessage;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextWebSocketFrameWriterTest {

    private TextWebSocketFrameWriter writer;
    private EmbeddedChannel ch;

    @BeforeEach
    public void setup() {
        ch = new EmbeddedChannel();
        writer = new TextWebSocketFrameWriter(ch);
    }

    @AfterEach
    public void teardown() {
        ch.finish();
    }

    @Test
    public void shouldSendMessageWrappedInTextWebSocketFrame() {
        // Execute
        writer.write(new TurnMessage(TurnMessage.Turn.YOUR_TURN).toJson());

        // Check result
        TextWebSocketFrame textWebSocketFrame = ch.readOutbound();
        assertEquals("{\"turn\":\"YOUR_TURN\",\"type\":\"turn\"}", textWebSocketFrame.text());

        ReferenceCountUtil.release(textWebSocketFrame);
    }
}