package client.net;

import common.InputMessageHandler;
import common.OutputMessageHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ServerConnectionHandler {
    private static final int BUFFER_SIZE = 4;
    private final SocketChannel socketChannel;
    private final InputMessageHandler inputMessageHandler;
    private final OutputMessageHandler outputMessageHandler;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    public ServerConnectionHandler(
        final SocketChannel socketChannel,
        final InputMessageHandler inputMessageHandler,
        final OutputMessageHandler outputMessageHandler
    ) {
        this.socketChannel = socketChannel;
        this.inputMessageHandler = inputMessageHandler;
        this.outputMessageHandler = outputMessageHandler;
    }

    public void receiveFromServer() throws IOException {
        buffer.clear();
        final int numBytes = socketChannel.read(buffer);

        if (numBytes == -1) {
            System.out.println("Closing");
            throw new IOException("Server connection was closed");
        } else {
            final byte[] bufferArray = buffer.array();
            final String letter = new String(bufferArray, 0, numBytes);
            inputMessageHandler.appendInputMessage(letter);
        }
    }

    public void sendToServer() throws IOException {
        while (outputMessageHandler.hasNext()) {
            final ByteBuffer bufSendToClient = ByteBuffer.wrap(outputMessageHandler.nextOutputMessage().getBytes());
            socketChannel.write(bufSendToClient);
            if (bufSendToClient.hasRemaining()) {
                throw new IOException("Error while sending data to server");
            }
        }
    }
}
