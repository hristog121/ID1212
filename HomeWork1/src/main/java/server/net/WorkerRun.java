package server.net;


import server.model.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WorkerRun extends Thread {

    private Socket clientSocket;

    public WorkerRun(Socket clientSocket) {
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
            ClientCommunication clientCommunication = new ClientCommunication(out, in);
            //Makes a new player
            Player player = new Player(clientCommunication);
            if (clientCommunication.readClientInput().getInput().equals("start")){
                player.start();
            }else clientCommunication.printToClient("YOU HAVEN'T TYPED THE COMMAND CORRECTLY");

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



