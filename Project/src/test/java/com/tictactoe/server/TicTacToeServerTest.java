package com.tictactoe.server;

import com.google.gson.JsonParser;
import org.glassfish.tyrus.client.ClientManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TicTacToeServerTest {

    /*@BeforeAll
    public static void setUp() throws Exception {
        Thread thread = new Thread(() -> {
            new TicTacToeServer().run();
        });

        thread.start();

        // make sure server gets off the ground
        Thread.sleep(10000);
    }

    @Test
    public void tryWebSocket() throws Exception {
        List<String> clientXMessagesReceived = new ArrayList<>();
        List<String> clientOMessagesReceived = new ArrayList<>();
        CountDownLatch clientXConnectedLatch = new CountDownLatch(1);
        CountDownLatch clientOConnectedLatch = new CountDownLatch(1);
        CountDownLatch clientXTurnLatch = new CountDownLatch(2);
        CountDownLatch clientOTurnLatch = new CountDownLatch(2);
        Session clientXSession = connect(clientOneConnectLatch, clientOneMessagesReceived);

        Session clientOSession = connect(clientTwoConnectLatch, clientTwoMessagesReceived);

        clientXTurnLatch.await(100, TimeUnit.SECONDS);
        clientOTurnLatch.await(100, TimeUnit.SECONDS);

        assertEquals(2, clientOneMessagesReceived.size());
        assertEquals(2, clientTwoMessagesReceived.size());
    }

    private Session connect(CountDownLatch clientConnectLatch, List<String> messagesReceived) throws Exception {
        final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
        ClientManager client = ClientManager.createClient();
        Session session = client.connectToServer(new Endpoint() {

            @Override
            public void onOpen(Session session, EndpointConfig config) {
                session.addMessageHandler(new MessageHandler.Whole<String>() {
                    @Override
                    public void onMessage(String message) {
                        messagesReceived.add(message);
                        new JsonParser().parse(message);
                    }
                });
            }
        }, cec, new URI("ws://localhost:9000/websocket/"));
        return session;
    }*/
}