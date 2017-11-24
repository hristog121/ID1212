package client;

import client.net.ServerConnection;
import client.view.CmdLineInterface;
import common.InputMessageHandler;
import common.OutputMessageHandler;

public class Client {

    public static void main(final String[] args) {
        final ServerConnection serverConnection = new ServerConnection();
        final InputMessageHandler inputMessageHandler = new InputMessageHandler();
        final OutputMessageHandler outputMessageHandler = new OutputMessageHandler();
        final CmdLineInterface cmdLineInterface =
            new CmdLineInterface(serverConnection, inputMessageHandler, outputMessageHandler);
        cmdLineInterface.init();

        serverConnection.start(inputMessageHandler, outputMessageHandler);
    }
}
