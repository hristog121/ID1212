package client.net;

import common.InputMessageHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class ServerConnectionHandler {
    private static final int BUFFER_SIZE = 4;
    private SocketChannel socketChannel;
    private Selector selector;
    private InputMessageHandler inputMessageHandler;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    public void connect() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("localhost", 1112));
            selector = Selector.open();
            inputMessageHandler = new InputMessageHandler();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            final Thread readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        while (inputMessageHandler.hasNext()) {
                            System.out.print(inputMessageHandler.nextInputMessage());
                        }
                    }
                }
            });
            readThread.start();

            final Thread writeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        final Scanner sc = new Scanner(System.in);
                        final String x = sc.nextLine();
                        if (x.equals("!")) {
                            System.exit(1);
                        }
                        try {
                            sendToServer(x + ";");
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            writeThread.start();
            processServerConnection();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void processServerConnection() throws IOException {
        while (true) {
            selector.select();
            final Iterator keysIterator = selector.selectedKeys().iterator();
            while (keysIterator.hasNext()) {
                final SelectionKey selKey = (SelectionKey) keysIterator.next();
                keysIterator.remove();
                //if the key is present in the key set
                if (selKey.isConnectable()) {
                    final SocketChannel channel = (SocketChannel) selKey.channel();
                    if (channel.finishConnect()) {
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                } else if (selKey.isReadable()) {
                    receiveFromServer();
                }
            }
        }
    }

    private void receiveFromServer() throws IOException {
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

    public void sendToServer(final String string) throws IOException {
        final ByteBuffer bufSendToClient = ByteBuffer.wrap(string.getBytes());
        socketChannel.write(bufSendToClient);
        if (bufSendToClient.hasRemaining()) {
            System.out.println("ERROR");
        }
    }
}
