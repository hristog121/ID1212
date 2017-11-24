package server.model;

import common.Command;
import common.InputMessageHandler;
import common.OutputMessageHandler;
import server.net.ClientConnectionHandler;
import server.net.Server;

public class Player implements Runnable {
    private int score;
    private Game hangman;
    private final InputMessageHandler inputMessageHandler;
    private final OutputMessageHandler outputMessageHandler;
    private final Server server;

    //Constructor for player
    public Player(
        final Server server,
        final InputMessageHandler inputMessageHandler,
        final OutputMessageHandler outputMessageHandler
    ) {
        this.score = 0;
        this.inputMessageHandler = inputMessageHandler;
        this.outputMessageHandler = outputMessageHandler;
        this.server = server;
    }

    @Override
    public void run() {
        while (inputMessageHandler.hasNext()) {
            final String clientInput = inputMessageHandler.nextInputMessage();
            final Command command = Command.getFromRepresentation(clientInput);
            switch (command) {
                case STOP:
                    return;
                case START_NEW_GAME:
                case START:
                    hangman = new Game(outputMessageHandler);
                    break;
                case INPUT_WORD_LETTER:
                    processClientGuess(clientInput);
                    break;
            }
            server.getReadyToSend();
        }
    }

    public void start() {
        outputMessageHandler.appendOutputMessage(
            "Welcome to the 97th Hangman games! I hope you can HANG out for a while!");
        outputMessageHandler.appendOutputMessage("To start the game please type 'start'");
        outputMessageHandler.appendOutputMessage("If you want to quite just type '!'");
    }

    private void processClientGuess(final String clientGuess) {
        if (hangman != null) {
            final Game.GameResult result = hangman.processClientInput(clientGuess);
            switch (result) {
                case WIN:
                    score++;
                    printScoreAndPromptForAnotherGame();
                    break;
                case LOOSE:
                    score--;
                    printScoreAndPromptForAnotherGame();
                    break;
            }
        }
    }

    private void printScoreAndPromptForAnotherGame() {
        hangman = null;
        outputMessageHandler.appendOutputMessage("SCORE: " + score);
        outputMessageHandler.appendOutputMessage("Do you want to play again? Press '1' - Play, Press '!' - Quit");
    }
}
