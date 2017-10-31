package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {

    private static final int PORT = 1112;




    public static void main(String[] args) throws IOException {
        Socket hangmanSocket = null;
        PrintWriter outstream = null;
        BufferedReader instream = null;

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


        // #############
        // ## INITIALIZE
        // #############
        printInstructions();



        while(hangmanSocket.isConnected())
        {
            while(instream.ready())
            {
                System.out.println(instream.readLine());
            }
            Scanner sc = new Scanner(System.in);
            char input = sc.next().charAt(0);

            // Quit If ! is the character
            if(input == '!')
            {
                // Cleanup
                outstream.close();
                instream.close();
                hangmanSocket.close();
                System.exit(0);
            }
            else
            {
                outstream.println(input);
            }
        }

        System.out.println("Waiting to close..");
        while(true);
    }

    private static void printInstructions() {
        System.out.println("Welcome to the 97th Hangman game! I hope you can HANG out for a while!");
        System.out.println("If you want to quite just type Q");

        System.out.println();
    }


}


