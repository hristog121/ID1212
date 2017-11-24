package server.net;

import common.InputMessageHandler;
import common.OutputMessageHandler;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ForkJoinPool;
import server.model.Player;

public class ClientConnectionHandler {

    private static final int BUFFER_SIZE = 4;
    private final InputMessageHandler inputMessageHandler;
    private final OutputMessageHandler outputMessageHandler;
    private final SocketChannel socketChannel;
    private final ByteBuffer buffer;
    private final Player player;
    private final Selector selector;

    public ClientConnectionHandler(final Selector selector, final SocketChannel socketChannel) throws IOException {
        final Socket socket = socketChannel.socket();
        System.out.println("Connection on socket " + socket + "...");
        this.socketChannel = socketChannel;
        this.selector = selector;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.inputMessageHandler = new InputMessageHandler();
        this.outputMessageHandler = new OutputMessageHandler();
        this.player = new Player(this, inputMessageHandler, outputMessageHandler);
        this.player.start();
    }

    //This will read a data from the channel and will put it in to the buffer
    public void receiveFromClient() throws IOException {
        buffer.clear();
        final int numBytes = socketChannel.read(buffer);

        if (numBytes == -1) {
            final Socket socket = socketChannel.socket();
            socket.close();
            socketChannel.close();
            throw new IOException("Client closed connection " + socket);
        }

        inputMessageHandler.appendInputMessage(new String(buffer.array(), 0, numBytes));
        ForkJoinPool.commonPool().execute(player);

    }

    //Sends to client
    public void sendToClient() throws IOException {
        while (outputMessageHandler.hasNext()) {
            final String message = outputMessageHandler.nextOutputMessage();
            final ByteBuffer bufSendToClient = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(bufSendToClient);
            if (bufSendToClient.hasRemaining()) {
                throw new IOException("Could not send message");
            }
        }
    }

    public void startListening() {
        socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }
}
