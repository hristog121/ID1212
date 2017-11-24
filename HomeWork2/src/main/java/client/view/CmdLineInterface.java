package client.view;

import static common.Command.STOP;

import client.net.ServerConnection;
import common.InputMessageHandler;
import common.OutputMessageHandler;
import java.util.Scanner;

public class CmdLineInterface {
    private final ServerConnection server;
    private final InputMessageHandler inputMessageHandler;
    private final OutputMessageHandler outputMessageHandler;

    public CmdLineInterface(
        final ServerConnection serverConnection,
        final InputMessageHandler inputMessageHandler,
        final OutputMessageHandler outputMessageHandler
    ) {
        this.server = serverConnection;
        this.inputMessageHandler = inputMessageHandler;
        this.outputMessageHandler = outputMessageHandler;
    }

    public void init() {
        initRead();
        initWrite();
    }

    private void initWrite() {
        final Thread writeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (inputMessageHandler.hasNext()) {
                        System.out.println(inputMessageHandler.nextInputMessage());
                    }
                }
            }
        });
        writeThread.start();
    }

    private void initRead() {
        final Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final String input = readClientInput();
                    handleInput(input);
                }
            }
        });
        readThread.start();
    }

    private String readClientInput() {
        final Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private void handleInput(final String input) {
        if (STOP.getRepresentation().equals(input)) {
            System.exit(1);
        } else {
            outputMessageHandler.appendOutputMessage(input);
            server.getReadyToSend();
        }
    }
}
