package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static final int port = 1112;
    private static Socket clientSocket = null;

    public static void main(String[] args) throws IOException {

        while (true) {
            System.out.println("Server starts to listen on port 1112");
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                clientSocket = serverSocket.accept();

            } catch (IOException exIo) {
                System.out.println("Problem when listening on port " + port);
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Take the words from the word.txt file
            FileInputStream getFile = new FileInputStream("/Users/iceroot/Documents/KTH/Year2/P2/Homeworks/ID1212/HomeWork1/src/main/resources/words.txt");
            DataInputStream getData = new DataInputStream(getFile);


            String word = "\0";

            int random = 50 + (int) Math.round(Math.random() * 10000);
            for (int i = 0; i < random; i++) {
                word = getData.readLine();
            }
            getData.close();


            //int guessesLeft = word.length();

            String clientGuess;
            boolean found = false;
            char[] wordArray = word.toCharArray();
            char[] newMasked = new char[wordArray.length];
            int count = wordArray.length;


            // mask the word


            for (int i = 0; i < newMasked.length; i++) {
                newMasked[i] = '_';
            }

            System.out.println("The word for the player is: " + word);

            boolean gameEnded = false;
            while (clientSocket.isConnected() && !gameEnded) {
                try {
                    found = false;
                    printToClient(count, newMasked, out); // First output to the client

                    //out.println(newMasked);  // Prints the masked word to the client - Maybe i don't need it

                    //Read a line from the input from client
                    clientGuess = in.readLine();
                    if (clientGuess == null) { //TODO: Котката да види дали има друг начин да се разбере, че клиента се е разкачил
                        break;
                    }

                    System.out.println("Receive " + clientGuess);

                    if (word.equals(clientGuess)) {
                        out.println("You WIN!");
                        gameEnded = true;
                    }

                    for (int i = 0; i < wordArray.length; i++) {

                        if (clientGuess.charAt(0) == wordArray[i]) {
                            newMasked[i] = clientGuess.charAt(0);
                            found = true;

                            //System.out.println("Was found ");
                            // Prints the masked word on the server - good of tracking the game and easier to trouble shoot.

                            for (int j = 0; j < newMasked.length; j++) {

                                System.out.print(newMasked[j] + " ");

                            }
                        }
                        // printToClient(count, newMasked, out); - DONT NEED IT
                        //found = false;


                    }


                    if (!found) {
                        System.out.println("Was NOT found ");
                        out.println("There is no " + clientGuess + " found!");
                        count--;
                        out.println("You have " + count + " guesses left !");
                        //printToClient(count, newMasked, out);
                    }

                    if (count == 0) {
                        out.println("Sorry you LOSE!!! The word was: " + word);
                        gameEnded = true;
                    }

                    //Display to client
                    System.out.println("here");
                    //printToClient(count, newMasked, out);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }


    //########################## DISPLAY TO CLIENT THE CURRENT STATUS OF THE WORD + HOW MANY ################
    //########################## ATTEMPTS ARE LEFT + ASK FOR INPUT###########################################

    private static void printToClient(int count, char[] newMasked, PrintWriter out) {

        //count = newMasked.length;
        for (int i = 0; i < newMasked.length; i++) {

            out.print(newMasked[i] + " ");


        }

        //out.println("You have " + count + " guesses left");
        out.println("\nPlease guess a letter");

        out.println();


    }
}


