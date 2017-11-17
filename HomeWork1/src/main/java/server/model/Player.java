package server.model;

import server.net.ClientCommunication;

import java.io.IOException;

public class Player {
    private ClientCommunication connectionWithClient;
    private int score;

    //Constructor for player
    public Player(ClientCommunication connectionWithClient) {
        this.score = 0;
        this.connectionWithClient = connectionWithClient;
    }

    public void start() throws IOException, InterruptedException {
        while (true) {
            Game hangman = new Game(connectionWithClient);
            Game.GameResult result = hangman.start();
            switch (result) {
                case WIN:
                    score++;
                    break;
                case LOOSE:
                    score--;
                    break;
                case STOPPED:
                    return;
            }
            //Prints the current score. The score can be negative as well
            printScore();
            //Ask the player if he wants to play another game or quit
            promptForAnotherGame();

            ClientInput clientInput = connectionWithClient.readClientInput();
            if (clientInput.isDisconnected()) {
                return;
            }
            if (clientInput.getInput().equals("!")) {
                System.out.println("The client " + Thread.currentThread().getName() + " has quit the game! ");
                return;
            }
        }
    }

    //Method that prints the current score
    private void printScore() {
        connectionWithClient.printToClient("SCORE: " + score);
    }

    //Method for prompting and asking the player if he wants to play another game
    private void promptForAnotherGame() {
        connectionWithClient.printToClient("Do you want to play again? Press '1' - Play, Press '!' - Quit");
    }
}
