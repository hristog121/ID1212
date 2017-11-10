package client;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {

    private static final int PORT = 1112;
    private static final String TIME_TO_INPUT = "Please guess a letter";
    private Socket hangmanSocket = null;
    private PrintWriter outstream = null;
    private BufferedReader instream = null;


    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.connectToServer();
        client.printInstructions();
        client.run();
    }

    private void connectToServer() {
        try
        {
            hangmanSocket = new Socket("127.0.0.1", 1112);
            outstream = new PrintWriter(hangmanSocket.getOutputStream(), true);
            instream = new BufferedReader(new InputStreamReader(hangmanSocket.getInputStream()));
        }
        catch (UnknownHostException e)
        {
            System.err.println("Couldn't Identify Server 127.0.0.1");
            System.exit(1);
        }
        catch (IOException e)
        {
            System.err.println(e);
            System.exit(1);
        }
    }
    // This method will print the welcome message to the users
    private void printInstructions() {
        System.out.println("Welcome to the 97th Hangman games! I hope you can HANG out for a while!");
        System.out.println("If you want to quite just type '!' ");

        System.out.println();
    }

    private void run() {
        try {
            while (hangmanSocket.isConnected()) {
               //  System.out.println("Inside game");
                boolean received = true;
                //System.out.println(received);
                while (received) {
                    //System.out.print("Waiting -> ");
                    String lineFromServer = instream.readLine();

                    if (lineFromServer != null) {
                        //System.out.println("Printing from server");
                        System.out.println(lineFromServer);

                        if (lineFromServer.equals(TIME_TO_INPUT)) {
                            readAndSendUserInput();
                        }

                        received = true;
                    } else {
                        received = false;
                    }
                }
            }
        } catch (IOException ex){
            ex.printStackTrace();
        } finally {
            try {
                hangmanSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Waiting to close..");
        while(true);
    }

    private void readAndSendUserInput() {
        //System.out.println(instream.readLine());
        System.out.println("Before scanning");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();

        // Quit If ! is the character
        if(input == "!")
        {
            // Cleanup
            try {
                outstream.close();
                instream.close();
                hangmanSocket.close();
            } catch (IOException ex){
                ex.printStackTrace();
            }
            System.exit(0);
        }
        else
        {
            System.out.println("Sending to server");
            outstream.println(input);
        }
    }
}


