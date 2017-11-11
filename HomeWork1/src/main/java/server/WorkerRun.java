package server;
/* Will handle the request from the server for multiple clients */


import java.io.*;
import java.net.Socket;

public class WorkerRun extends Thread {
    private static final int port = 1112;
    protected Socket clientSocket = null;
    protected String clientGuess = "\0";

    public WorkerRun(Socket clientSocket) {
        this.clientSocket = clientSocket;

    }

    //########################## DISPLAY TO CLIENT THE CURRENT STATUS OF THE WORD + HOW MANY ################
    //########################## ATTEMPTS ARE LEFT + ASK FOR INPUT###########################################

    private static void printToClient(int count, char[] newMasked, PrintWriter out) {

        //count = newMasked.length;
        for (int i = 0; i < newMasked.length; i++) {

            out.print(newMasked[i] + " ");

        }

        out.println("\nPlease guess a letter");

        out.println();


    }

    @Override
    public void run() {
        try {


            while (true) {

                //Initialize Input and Output Streams

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Take the words from the word.txt file
                FileInputStream getFile = new FileInputStream("/Users/iceroot/Documents/KTH/Year2/P2/Homeworks/ID1212/HomeWork1/src/main/resources/words.txt");
                DataInputStream getData = new DataInputStream(getFile);


                String word = "\0";

                //Get a random word from the txt file
                int random = 50 + (int) Math.round(Math.random() * 10000);
                for (int i = 0; i < random; i++) {
                    word = getData.readLine();
                }
                getData.close();


                // Declarations
                String clientGuess;
                boolean found = false;
                char[] wordArray = word.toCharArray();
                char[] newMasked = new char[wordArray.length];
                int count = wordArray.length;
                int score = 0;


                // Loop for filing up an a array with dashes ( mask the word )

                for (int i = 0; i < newMasked.length; i++) {
                    newMasked[i] = '_';
                }

                // Print out the word that the player will guess + name of the thread - SERVER SIDE
                System.out.println("The word for the player is: " + word + " " + Thread.currentThread().getName());

                boolean gameEnded = false;

                //Game logic
                while (clientSocket.isConnected() && !gameEnded) {
                    try {
                        found = false;
                        // First output to the client
                        printToClient(count, newMasked, out);

                        //Read a line from the input from client
                        clientGuess = in.readLine();
                        if (clientGuess == null) { //TODO: Котката да види дали има друг начин да се разбере, че клиента се е разкачил
                            break;
                        }
                        // Print out the word that the player will guess - SERVER SIDE
                        //System.out.println("Receive " + clientGuess);


                        //If the player inputs the whole word and it is correct - WIN
                        if (word.equals(clientGuess)) {
                            score++;
                            out.println("You WIN!");

                            gameEnded = true;
                        }


                        //Check if the player input 'letter' is in the word
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

                        }

                        // If the letter is not found msg the client + how many guesses are left
                        if (!found) {
                            System.out.println("Was NOT found ");
                            out.println("There is no " + clientGuess + " found!");
                            count--;
                            out.println("You have " + count + " guesses left !");

                        }
                        // If count reaches 0 the player loses - msg to client
                        if (count == 0) {
                            score--;
                            out.println("Sorry you LOSE!!! The word was: " + word);
                            gameEnded = true;
                        }

                        //If the player guess the whole word letter by letter - WIN
                        //and a score counter
                        if (word.equals(new String(newMasked))) {
                            score++;
                            out.println("YOU WIN!!! ");
                            gameEnded = true;

                        }


                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}



