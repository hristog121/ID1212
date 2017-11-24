package common;

import java.util.ArrayDeque;
import java.util.Queue;

public class InputMessageHandler {
    private static final String MESSAGE_SEPARATOR = ";";
    private final StringBuilder inputBuffer;
    private final Queue<String> inputQueue;

    public InputMessageHandler() {
        this.inputBuffer = new StringBuilder();
        this.inputQueue = new ArrayDeque<>();
    }

    public synchronized void appendInputMessage(final String message) {
        inputBuffer.append(message);
        splitMessages();
    }

    public synchronized String nextInputMessage() {
        return inputQueue.poll();
    }

    public synchronized boolean hasNext() {
        return !inputQueue.isEmpty();
    }

    private void splitMessages() {
        final String clientInput = inputBuffer.toString();
        final String[] split = clientInput.split(MESSAGE_SEPARATOR);
        int countMessages = split.length;
        if (clientInput.charAt(clientInput.length() - 1) != MESSAGE_SEPARATOR.charAt(0)) {
            countMessages--;
        }
        int currentPosition = 0;
        for (int i = 0; i < countMessages; i++) {
            final String message = split[i];
            inputQueue.add(message);
            currentPosition += message.length() + 1;
        }
        if (countMessages > 0) {
            inputBuffer.delete(0, currentPosition);
        }
    }
}
