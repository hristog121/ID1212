/* */


package client.startup;

import client.net.Client;

public class StartGame {
    public static void main(String[] args) {
        Client client = new Client();
        client.connectToServer();
        client.printInstructions();
        client.readAndSendUserInput();
        client.runConnection();

    }
}
