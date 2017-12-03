package client.model;

import common.ClientCatalog;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientCatalog {
    private String sessionID;

    public Client() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public void receiveMessage(String message) {
        System.out.println(message);
    }
}
