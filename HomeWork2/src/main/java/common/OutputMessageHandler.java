package common;

import java.util.ArrayDeque;
import java.util.Queue;

public class OutputMessageHandler {
    private static final String MESSAGE_SEPARATOR = ";";
    private final Queue<String> outputQueue;

    public OutputMessageHandler() {
        this.outputQueue = new ArrayDeque<>();
    }

    public synchronized String nextOutputMessage() {
        return outputQueue.poll();
    }

    public synchronized boolean hasNext() {
        return !outputQueue.isEmpty();
    }

    public synchronized void appendOutputMessage(final String output) {
        final StringBuilder sb = new StringBuilder(output);
        sb.append(MESSAGE_SEPARATOR);
        outputQueue.add(sb.toString());
    }
}
