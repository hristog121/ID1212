import server.controller.Controller;
import server.integrations.FileSystemDAO;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

public class Server {
    public static void main(String[] args) {

        try {
            Server server = new Server();
            FileSystemDAO fileSystemDAO = new FileSystemDAO();
            server.startRegistry();
            Naming.rebind("FILE_SERVER", new Controller(fileSystemDAO)); //CommandHandler is the stub
            System.out.println("Server have started ...");
        } catch (RemoteException  | MalformedURLException e) {
            System.out.print(e);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException e) {
            //if we cant locate our registry we create it
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
}
