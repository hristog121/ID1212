package server.model;

import static server.model.Game.GameResult.LOOSE;
import static server.model.Game.GameResult.ONGOING;
import static server.model.Game.GameResult.WIN;

import common.OutputMessageHandler;

public class Game {

    private final OutputMessageHandler outputMessageHandler;
    private final GameWord hangmanWord;
    private int count;

    //Constructor
    public Game(final OutputMessageHandler outputMessageHandler) {
        this.outputMessageHandler = outputMessageHandler;
        this.hangmanWord = new GameWord();
        this.count = hangmanWord.getCount();
        printMaskedWord();
    }

    public GameResult processClientInput(final String clientGuess) {

        //If the player inputs more than 1 letter. Check if he has guessed the whole word.
        if (clientGuess.length() > 1) {
            //If the player inputs the whole word and it is correct - WIN
            if (hangmanWord.checkWholeWordGuessed(clientGuess)) {
                outputMessageHandler.appendOutputMessage("You WIN!");
                return WIN;
            }
            //If the player has input more than 1 letter but the word is not guessed decrease the count of
            //possible guesses with 1
            else {
                outputMessageHandler.appendOutputMessage(clientGuess + " is not the word!");
                count--;
                outputMessageHandler.appendOutputMessage("You have " + count + " guesses left!");
            }
        }
        // guess
        if (clientGuess.length() == 1) {
            if (!hangmanWord.checkLetterGuessed(clientGuess)) {
                // If the letter is not found msg the client + how many guesses are left && if the client
                // sends an empty
                outputMessageHandler.appendOutputMessage("There is no " + clientGuess + " found!");
                count--;
                outputMessageHandler.appendOutputMessage("You have " + count + " guesses left!");
            }
        }
        //If the the whole word is guessed
        if (hangmanWord.isWordGuessed()) {
            outputMessageHandler.appendOutputMessage("You WIN!");
            return WIN;
        }
        // If count reaches 0 the player loses - msg to client and return LOOSE + show him the word that he
        //played on
        if (count == 0) {
            outputMessageHandler.appendOutputMessage(
                "Sorry you LOSE!!! The word was: " + hangmanWord.getWord() + "");
            return LOOSE;
        }
        printMaskedWord();
        return ONGOING;
    }

    //Prints the masked word - to player and asks him to guess a letter or the whole word
    private void printMaskedWord() {
        outputMessageHandler.appendOutputMessage(hangmanWord.getNewMasked());
        outputMessageHandler.appendOutputMessage("Please guess a letter or the whole word!");
    }

    public enum GameResult {
        WIN,
        LOOSE,
        ONGOING
    }
}
