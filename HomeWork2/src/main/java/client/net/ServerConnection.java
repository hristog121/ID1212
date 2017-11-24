package client.net;

import common.InputMessageHandler;
import common.OutputMessageHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ServerConnection {
    private static final int PORT = 1112;
    private SocketChannel socketChannel;
    private Selector selector;
    private ServerConnectionHandler serverConnectionHandler;

    public void start(final InputMessageHandler inputMessageHandler, final OutputMessageHandler outputMessageHandler) {
        System.out.println("Server starts to listen on port " + PORT);
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("localhost", 1112));
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            serverConnectionHandler = new ServerConnectionHandler(
                socketChannel,
                inputMessageHandler,
                outputMessageHandler
            );
            processServerConnection();
        } catch (final IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (selector != null) {
                    selector.close();
                }
                if (socketChannel != null) {
                    socketChannel.close();
                }
            } catch (final IOException ex) {
                System.out.println("Error while releasing resources");
                ex.printStackTrace();
            }
        }
    }

    public void getReadyToSend() {
        socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
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
                    serverConnectionHandler.receiveFromServer();
                } else if (selKey.isWritable()) {
                    serverConnectionHandler.sendToServer();
                    selKey.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }
}
