package server.model;

import java.nio.ByteBuffer;
import java.util.Queue;

public class ClientHandler implements Runnable {
    private Queue<ByteBuffer> outputQueue;
    private Queue<String> inputQueue;

    public void enqueueInput(String string) {
        inputQueue.add(string);
    }

    @Override
    public void run() {

    }
}
