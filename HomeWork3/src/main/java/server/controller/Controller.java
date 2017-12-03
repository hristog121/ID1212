package server.controller;

import common.*;
import server.integrations.FileCatalogDBException;
import server.integrations.FileSystemDAO;
import server.model.Account;
import server.model.FileDescriptor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Controller extends UnicastRemoteObject implements FileCatalog {
    private FileSystemDAO fileDB;
    private volatile Map<Integer, ClientCatalog> clientNotifyListeners;

    public Controller(FileSystemDAO fileDB) throws RemoteException {
        super();
        this.fileDB = fileDB;
        this.clientNotifyListeners = new HashMap<>();
    }

    //Method for registering a new user and adding it to the DB -
    @Override
    public synchronized String registerNewUser(String userName, String passWord) throws FileCatalogException {
        String accountExists = "Account " + userName + " already exists try a new username";
        String accountCreated = "Account with " + userName + " was created successfully";
        try {
            if (fileDB.findAccountByName(userName) != null) {
                throw new FileCatalogException("Another account with the same username exist " + userName);
            }

            fileDB.createAccount(new Account(userName, passWord));
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can not create account: ", e);
        }
        return accountCreated;
    }

    //Delete already registered user from the DB
    @Override
    public void unRegisterUser(String sessionID) throws FileCatalogException {

        try {
            Account account = getAccount(sessionID);
            fileDB.deleteAccount(account);
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can't delete account ", e);
        }

    }

    //Check if user name and password are a match + start a new session
    @Override
    public String logIn(String userName, String passWord) throws FileCatalogException {
        try {

            Account account = fileDB.findAccountByName(userName);
            fileDB.deleteSessionForUser(account);
            if (account.getPassWord().equals(passWord)) {
                String sessionID = UUID.randomUUID().toString();
                fileDB.createSession(sessionID, account);
                return sessionID;
            } else {
                throw new FileCatalogException("WRONG PASSWORD OR USERNAME!!!!!!!!");
            }
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can't find the account ", e);
        }

    }

    //Log out - delete the started session
    @Override
    public void logOut(String sessionID) throws FileCatalogException {
        try {
            fileDB.deleteSession(sessionID);
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can not log out ", e);
        }
    }

    @Override
    public void uploadFile(String sessionID, FileDTO uploadFile) throws FileCatalogException {
        try {
            Account account = getAccount(sessionID);
            FileDescriptor existingFile = fileDB.findFileByName(uploadFile.getName());

            if (existingFile != null) {
                throw new FileCatalogException("This file already exist");
            }

            FileDescriptor descriptor = new FileDescriptor(uploadFile.getName(), uploadFile.getSize(),
                    uploadFile.getAccessPermissions(), uploadFile.getOperationPermissions(),
                    account.getId());

            fileDB.createFile(descriptor, account);
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can not upload the file ", e);
        }
    }

    @Override
    public String downloadFile(String sessionID, String fileName) throws FileCatalogException, RemoteException {
        try {
            Account account = getAccount(sessionID);
            FileDescriptor fd = fileDB.findFileByName(fileName);

            if (fd == null) {
                return "File does not exist";
            }

            if (canAccessFile(account, fd)) {
                notifyOwner(account, fd, FileOperationType.READ);
                return formatFileData(fd);
            } else {
                return "This file has private access";
            }
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can not upload the file ", e);
        }
    }

    @Override
    public List<String> listExistingFiles(String sessionID) throws FileCatalogException {
        List<String> fileDTOList = new ArrayList<>(); //TODO create new list
        try {
            Account account = getAccount(sessionID);
            List<FileDescriptor> fileDescriptorList = fileDB.listFiles(account.getId());

            for (FileDescriptor fd : fileDescriptorList) {

                if (canAccessFile(account, fd)) {

                    fileDTOList.add(formatFileData(fd));
                }
            }
            return fileDTOList;
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can't list the files", e);
        }
    }

    @Override
    public String deleteFile(String sessionID, String fileName) throws FileCatalogException, RemoteException {
        try {
            Account account = getAccount(sessionID);
            FileDescriptor existingFile = fileDB.findFileByName(fileName);

            if (existingFile == null) {
                return "File with name " + fileName + " does not exist";
            }

            if (canModifyFile(account, existingFile)) {

                int updatedRows = fileDB.deleteFile(fileName);

                final String result;
                if (updatedRows != 1) {
                    result = "File successfully deleted";
                } else {
                    result = "File not found";
                }

                notifyOwner(account, existingFile, FileOperationType.DELETE);

                return result;
            } else {
                return "You do not have permissions to update this file";
            }
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can't delete file", e);
        }
    }

    @Override
    public String updateFile(String sessionID, FileDTO uploadFile) throws FileCatalogException, RemoteException {
        try {
            Account account = getAccount(sessionID);
            FileDescriptor existingFile = fileDB.findFileByName(uploadFile.getName());
            if (existingFile == null) {
                return "File with name " + existingFile.getName() + " does not exist";
            }

            if (canModifyFile(account, existingFile)) {

                FileDescriptor descriptor = new FileDescriptor(uploadFile.getName(), uploadFile.getSize(),
                        uploadFile.getAccessPermissions(), uploadFile.getOperationPermissions(),
                        account.getId());
                int updatedRows = fileDB.updateFile(descriptor);

                final String result;
                if (updatedRows != 1) {
                    result = "File not found";
                } else {
                    result = "File updated";
                }

                notifyOwner(account, existingFile, FileOperationType.UPDATE);

                return result;
            } else {
                return "You do not have permissions to update this file";
            }
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can not upload the file ", e);
        }
    }

    @Override
    public void registerForNotification(String sessionID, ClientCatalog clientCatalog) throws FileCatalogException {
        Account account = getAccount(sessionID);
        clientNotifyListeners.put(account.getId(), clientCatalog);
    }

    private Account getAccount(String sessionID) throws FileCatalogException {
        try {
            Account account = fileDB.findAccountBySessionID(sessionID);
            if (account == null) {
                throw new FileCatalogException("User not logged in");
            }
            return account;
        } catch (FileCatalogDBException e) {
            e.printStackTrace();
            throw new FileCatalogException("Can't find user", e);
        }
    }

    private boolean canModifyFile(Account account, FileDescriptor file) {
        if (file.getOwner() == account.getId() ||
                (file.getAccessPermissions().equals(AccessPermissions.PUBLIC) &&
                        file.getOperationPermissions().equals(OperationPermissions.WRITE))) {
            return true;
        }

        return false;
    }

    private boolean canAccessFile(Account account, FileDescriptor file) {
        if (file.getOwner() == account.getId() || file.getAccessPermissions().equals(AccessPermissions.PUBLIC)) {
            return true;
        }

        return false;
    }

    private String formatFileData(FileDescriptor fd) {
        return "Name: " + fd.getName() + " Size: " + fd.getSize() + " Access permissions: " +
                fd.getAccessPermissions() + " Operation permissions: " + fd.getOperationPermissions();
    }

    private void notifyOwner(Account account, FileDescriptor existingFile, FileOperationType fileOperationType) throws RemoteException {
        if (existingFile.getOwner() == account.getId()) {
            return; // Same user, no need to notify
        }

        ClientCatalog clientCatalog = clientNotifyListeners.get(existingFile.getOwner());

        String message = "Client with user name " + account.getUserName() + " ";
        switch (fileOperationType) {
            case READ:
                message += "has read";
                break;
            case UPDATE:
                message += "has updated";
                break;
            case DELETE:
                message += "has deleted";
                break;
        }

        message += " your file with name " + existingFile.getName();

        clientCatalog.receiveMessage(message);
    }
}

