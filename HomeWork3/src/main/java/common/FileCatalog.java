package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileCatalog  extends Remote {

    //Register new user UUID - Session
    String registerNewUser (String userName, String passWord) throws FileCatalogException, RemoteException;

    //Unregister a user
    void unRegisterUser(String sessionID) throws FileCatalogException, RemoteException;

    //Log in for existing user
    String logIn(String userName, String passWord) throws FileCatalogException,RemoteException;

    //Log out the user
    void logOut(String sessionID)throws FileCatalogException, RemoteException;

    //Upload file
    void uploadFile(String sessionID, FileDTO uploadFile)throws FileCatalogException,RemoteException;

    //Download file
    String downloadFile(String sessionID, String fileName)throws FileCatalogException,RemoteException;

    List <String> listExistingFiles(String sessionID) throws FileCatalogException,RemoteException;

    String deleteFile(String sessionID, String fileName)throws FileCatalogException,RemoteException;

    String updateFile (String sessionID, FileDTO uploadFile)throws FileCatalogException,RemoteException;

    void registerForNotification(String sessionID, ClientCatalog clientCatalog) throws FileCatalogException,RemoteException;
}

