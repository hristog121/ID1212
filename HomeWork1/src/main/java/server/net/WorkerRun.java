package server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import server.model.Player;

public class WorkerRun implements Runnable {

    private final Socket clientSocket;

    public WorkerRun(final Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            //Waiting for a connection
            while (!clientSocket.isConnected()) {
                //Wait until connected
            }
            //Declare a new variable of type ClientCommunication
            final ClientCommunication clientCommunication = new ClientCommunication(out, in);
            //Makes a new player
            final Player player = new Player(clientCommunication);
            if (clientCommunication.readClientInput().getInput().equals("start")) {
                player.start();
            } else clientCommunication.printToClient("YOU HAVEN'T TYPED THE COMMAND CORRECTLY");

        } catch (final IOException ex) {
            ex.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
}



