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
            Thread.sleep(4000);

            if (clientInput.isDisconnected()) {
                return STOPPED;
            }
            String clientGuess = clientInput.getInput();




                //If the player inputs the whole word and it is correct - WIN
                if (hangmanWord.checkWholeWordGuessed(clientGuess)) {
                    connectionToClient.printToClient("You WIN!");
                    return WIN;
                }
                // If the letter is not found msg the client + how many guesses are left && if the client sends an empty guess
                if (!clientGuess.isEmpty() && !hangmanWord.checkLetterGuessed(clientGuess)) {       //TODO the first part of the if
                    connectionToClient.printToClient("There is no " + clientGuess + " found!");
                    count--;
                    connectionToClient.printToClient("You have " + count + " guesses left !");

                }
                //If the the whole word is guessed
                if (hangmanWord.isWordGuessed()) {
                    connectionToClient.printToClient("You WIN!");
                    return WIN;
                }
                // If count reaches 0 the player loses - msg to client and return LOOSE
                if (count == 0) {
                    connectionToClient.printToClient("Sorry you LOSE!!! The word was: " + hangmanWord.getWord());
                    return LOOSE;
                }
            }
        }


    //Prints the masked word - to player and asks him to guess a letter
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
