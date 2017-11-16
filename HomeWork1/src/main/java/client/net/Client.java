package client.net;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {
    //VOLATILE SO THEY CAN BE SAFLY ACCESED FROM DIFFERENT THREADS
    private static final int PORT = 1112;
    private volatile Socket hangmanSocket = null;
    private volatile PrintWriter outstream = null;
    private volatile BufferedReader instream = null;
    private volatile boolean isGameRunning;


    public void connectToServer() {
        try {
            hangmanSocket = new Socket("127.0.0.1", PORT);
            outstream = new PrintWriter(hangmanSocket.getOutputStream(), true);
            instream = new BufferedReader(new InputStreamReader(hangmanSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Couldn't Identify Server 127.0.0.1");
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    // This method will print the welcome message to the users
    //TODO This should move to other place may be
    public void printInstructions() {
        System.out.println("Welcome to the 97th Hangman games! I hope you can HANG out for a while!");
        System.out.println("To start the game please type 'start' ");
        System.out.println("If you want to quite just type '!' ");
        isGameRunning = true;

        System.out.println();
    }

    public void runConnection() {
        try {
            while (hangmanSocket.isConnected() && isGameRunning) {

                String lineFromServer = instream.readLine();
                if (lineFromServer != null) {
                    System.out.println(lineFromServer);
                } else {
                    isGameRunning = false;
                    System.out.println("Server connection is closed! Exiting game!");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            terminate();
        }
    }

    //Takes reads and send user input to server side
    public void readAndSendUserInput() {
        //Create a new thread so the client can quit regardless of the server
        Thread clientThread = new Thread(new Runnable() {
            @Override

            // It is run from runnable
            public void run() {
                try {

                    //While the game is running read player input from the keyboard
                    while (isGameRunning) {

                        Scanner sc = new Scanner(System.in);
                        String input = sc.nextLine();

                        // Quit If '!' is the character
                        if (input.equals("!")) {
                            isGameRunning = false;
                        } else {
                            outstream.println(input);
                        }
                    }
                } finally {
                    terminate();
                }
            }
        });
        clientThread.start();
    }

    //Method that terminates all IO + closing the socket
    private void terminate() {
        try {
            outstream.close();
            instream.close();
            hangmanSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
}


