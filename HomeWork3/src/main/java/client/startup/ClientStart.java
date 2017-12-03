package client.startup;

import client.model.Client;
import client.view.CommandHandler;
import common.FileCatalog;
import common.FileCatalogException;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientStart {
    public static void main(String[] args) throws FileCatalogException {

        try {
            Registry registry = LocateRegistry.getRegistry();
            Object mrun = Naming.lookup("FILE_SERVER");
            FileCatalog server = (FileCatalog) mrun;
            Client client = new Client();
            CommandHandler commandHandler = new CommandHandler(server, client);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            System.out.println("Couldn't find server");
            e.printStackTrace();
        }
        finally {
            System.out.println("Executing finallyyyyy!!!");
        }
    }
}
