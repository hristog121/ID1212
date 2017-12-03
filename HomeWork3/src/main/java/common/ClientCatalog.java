package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCatalog extends Remote {
    void receiveMessage(String message) throws RemoteException;
}
