package client.startup;

import client.net.ServerConnectionHandler;

public class Client {

    public static void main(final String[] args) {
        try {
            final ServerConnectionHandler sch = new ServerConnectionHandler();
            sch.connect();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
