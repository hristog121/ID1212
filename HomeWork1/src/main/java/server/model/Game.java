package server.model;

import client.net.Client;
import server.net.ClientCommunication;
import server.net.ClientInput;

import java.io.IOException;

import static server.model.Game.GameResult.*;

public class Game {

    private ClientCommunication connectionToClient;
    private GameWord hangmanWord;
    private int count;
    Client client = new Client();
    //Constructor
    public Game(ClientCommunication connectionToClient) throws IOException {
        this.connectionToClient = connectionToClient;
        this.hangmanWord = new GameWord();
        this.count = hangmanWord.getCount();
    }

    public GameResult start() throws IOException, InterruptedException {
        while (true) {

            printMaskedWord();

            ClientInput clientInput = connectionToClient.readClientInput();
            //So we can test if the client can disconnect
            //Thread.sleep(4000);

            if (clientInput.isDisconnected()) {
                return STOPPED;
            }
            String clientGuess = clientInput.getInput();

            if (!clientGuess.isEmpty()) {
                //If the player inputs more than 1 letter. Check if he has guessed the whole word.
                if (clientGuess.length() > 1) {
                    //If the player inputs the whole word and it is correct - WIN
                    if (hangmanWord.checkWholeWordGuessed(clientGuess)) {
                        connectionToClient.printToClient("You WIN!");
                        return WIN;
                    }
                    //If the player has input more than 1 letter but the word is not guessed decrease the count of
                    //possible guesses with 1
                    else {
                        connectionToClient.printToClient(clientGuess + " is not the word!");
                        count--;
                        connectionToClient.printToClient("You have " + count + " guesses left !");
                    }
                }
                // guess
                if (clientGuess.length() == 1) {
                    if (!hangmanWord.checkLetterGuessed(clientGuess)) {
                        // If the letter is not found msg the client + how many guesses are left && if the client sends an empty
                        connectionToClient.printToClient("There is no " + clientGuess + " found!");
                        count--;
                        connectionToClient.printToClient("You have " + count + " guesses left !");
                    }
                }
                //If the the whole word is guessed
                if (hangmanWord.isWordGuessed()) {
                    connectionToClient.printToClient("You WIN!");
                    return WIN;
                }
                // If count reaches 0 the player loses - msg to client and return LOOSE + show him the word that he
                //played on
                if (count == 0) {
                    connectionToClient.printToClient("Sorry you LOSE!!! The word was: " + hangmanWord.getWord());
                    return LOOSE;
                }
            }
        }
    }


    //Prints the masked word - to player and asks him to guess a letter or the whole word
    private void printMaskedWord() {

        connectionToClient.printToClient(hangmanWord.getNewMasked());
        connectionToClient.printToClient("Please guess a letter or the whole word!");
    }

    public enum GameResult {
        WIN,
        LOOSE,
        STOPPED
    }
}
