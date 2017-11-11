package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends Thread {

    private static final int port = 1112;
    private static Socket clientSocket = null;

    public static void main(String[] args) throws IOException {

        System.out.println("Server starts to listen on port 1112");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                //Accept connection from a client
                clientSocket = serverSocket.accept();

                //Start a new thread
                new Thread(new WorkerRun(clientSocket)).start();
            }
        } catch (IOException exIo) {
            System.out.println("Problem when listening on port " + port);
            System.exit(1);
        }
    }

}




