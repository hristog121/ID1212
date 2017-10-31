package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static final int port = 1112;
    private static Socket clientSocket = null;

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
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


        int random = 1 + (int) Math.round(Math.random() * 11);
        for (int i = 0; i < random; i++) {
            word = getData.readLine();
        }
        getData.close();



        int guessesLeft = word.length();
        String clientGuess = "\0";
        boolean found = false;
        char [] wordArray = word.toCharArray();
        char [] newMasked = new char[wordArray.length];


        // mask the word

        for (int i = 0; i < newMasked.length; i++){
            newMasked[i] = '_';
        }

        System.out.println("The word for the player is: " + word);


        while (clientSocket.isConnected()) {

            try {

                printToClient(guessesLeft, newMasked, out);
                clientGuess = in.readLine();

                for (int i = 0; i < wordArray.length; i++) {
                    if (clientGuess.charAt(0) == wordArray[i]) {
                        newMasked[i] = clientGuess.charAt(0);
                       found = true;
                    }

                }
                if (!found){
                    guessesLeft --;
                    out.println("There is no " + clientGuess + "found!");

                }

               if (guessesLeft == 0){
                    out.println("##### GAME OVER #####");
                    out.println("The word was: " + word);

                    in.close();
                    out.close();
                    clientSocket.close();
                    System.exit(0);
               }

               if (newMasked.equals(word)){
                   out.println("##### You WIN #####");
               }
            }catch (Exception e){
                System.out.println(e);
                System.exit(0);
            }
        }

    }


    //######################### HOW MANY GUESSES THE CLIENT HAS LEFT    #########################################

    private static int computeGuessesLeft(char [] newMasked) {
        int count = 0;
        for (int i = 0; i < newMasked.length; i++) {
            if (newMasked[i] == '_') {
                count++;
            }

        }
        return count;
    }

    //########################## DISPLAY TO CLIENT THE CURRENT STATUS OF THE WORD + HOW MANY ################
    //########################## ATTEMPTS ARE LEFT + ASK FOR INPUT###########################################

    private static void printToClient(int guessesLeft, char[] guessed, PrintWriter out) {
        for (int i = 0; i < guessed.length; i++) {
            out.println(guessed[i]);
        }
        out.println();
        out.println("You have " + guessesLeft + " guesses left");
        out.println("Please guess a letter");

    }
}


