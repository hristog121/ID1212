package server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {

    private static final int PORT = 1112;
    private ServerSocketChannel serverSocketChannel;
    private ServerSocket serverSocket;
    private Selector selector;

    public static void main(final String[] args) throws IOException {
        final Server server = new Server();
        server.start();
    }

    public void start() {
        System.out.println("Server starts to listen on port " + PORT);
        try {
            serverSocketChannel = ServerSocketChannel.open();// Create
            serverSocketChannel.configureBlocking(false);
            serverSocket = serverSocketChannel.socket();
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            processConnection();
        } catch (final IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (selector != null) {
                    selector.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
                if (serverSocketChannel != null) {
                    serverSocketChannel.close();
                }
            } catch (final IOException ex) {
                System.out.println("Error while releasing resources");
                ex.printStackTrace();
            }
        }
    }

    private void processConnection() throws IOException {
        while (true) {
            selector.select();
            final Iterator selKeysIterator = selector.selectedKeys().iterator();
            while (selKeysIterator.hasNext()) {
                final SelectionKey selKey = (SelectionKey) selKeysIterator.next();
                selKeysIterator.remove();
                //if the key is present in the key set
                if (selKey.isAcceptable()) {
                    acceptConnection();
                } else if (selKey.isReadable()) {
                    receiveFromClient(selKey);
                } else if (selKey.isWritable()) {
                    writeToClient(selKey);
                }
            }
        }
    }

    private void acceptConnection() throws IOException {
        final SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_WRITE, new ClientConnectionHandler(selector, socketChannel));
    }

    private void receiveFromClient(final SelectionKey selKey) throws IOException {
        try {
            final ClientConnectionHandler client = (ClientConnectionHandler) selKey.attachment();
            client.receiveFromClient();
        } catch (final IOException ex) {
            selKey.cancel();
            System.out.println(ex.getMessage());
        }
    }

    private void writeToClient(final SelectionKey selKey) throws IOException {
        try {
            final ClientConnectionHandler client = (ClientConnectionHandler) selKey.attachment();
            client.sendToClient();
            selKey.interestOps(SelectionKey.OP_READ);
        } catch (final IOException ex) {
            selKey.cancel();
            System.out.println(ex.getMessage());
        }
    }
}
