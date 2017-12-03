package client.view;


import client.model.Client;
import common.*;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private FileCatalog server;
    private Client client;
    private Scanner sc = new Scanner(System.in);

    public UserInterface(FileCatalog server, Client client) throws FileCatalogException {

        this.server = server;
        this.client = client;

        System.out.println("Connected to server ...");
        run();
    }

    private void printCommands() {
        System.out.println();
        System.out.println("1. To register type 'register' ");
        System.out.println("2. To remove your account type 'unregister' ");
        System.out.println("3. To login type 'login' ");
        System.out.println("4. To logout type 'logout' ");
        System.out.println("5. To list all files type 'list' ");
        System.out.println("6. To upload file type 'upload' ");
        System.out.println("7. To delete file type 'delete' ");
        System.out.println("8. To update file type 'update' ");
        System.out.println("9. To register for notification when some of your public files is accessed type 'notify' ");
        System.out.println("10. To see this menu again type 'help' ");
    }

    void run() throws FileCatalogException {
        while (true) {
            try {

                printCommands();
                //Convert string to commands
                Commands commands = readCommand();
                switch (commands) {
                    case LOGIN:
                        login();
                        break;
                    case LOGOUT:
                        logout();
                        break;
                    case REGISTER:
                        registerUser();
                        break;
                    case UNREGISTER:
                        unregister();
                        break;
                    case LIST:
                        listFiles();
                        break;
                    case UPLOAD:
                        upload();
                        break;
                    case DOWNLOAD:
                        download();
                        break;
                    case HELP:
                        printCommands();
                        break;
                    case DELETE:
                        delete();
                        break;
                    case NOTIFY:
                        registerForNotification();
                        break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void download() {
        try {
            System.out.println("Please enter file name to download it: ");
            String fileName = sc.nextLine();

            System.out.println(server.downloadFile(client.getSessionID(), fileName));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (FileCatalogException e) {
            e.printStackTrace();
        }

    }

    private void delete() {
        try {
            System.out.println("Please enter file name to delete: ");
            String fileName = sc.nextLine();
            System.out.println(server.deleteFile(client.getSessionID(), fileName));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (FileCatalogException e) {
            e.printStackTrace();
        }
    }

    private void upload() throws RemoteException {
        try {
            System.out.println("Please enter full file path: ");
            String filePath = sc.nextLine();
            File file = new File(filePath);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("The file is not existing or it is dir ...");
                return;
            }

            System.out.println("Please enter file assess permissions (public/private): ");
            AccessPermissions accessPermissions = readAccessPermission();

            System.out.println("Please enter file operation permissions (write/read): ");
            OperationPermissions operationPermissions = readOperationPermission();

            server.uploadFile(client.getSessionID(), new FileDTO(file.getName(), file.length(), accessPermissions, operationPermissions));
        } catch (FileCatalogException e) {
            if (e.getMessage().equals("User not logged in")) {
                System.out.println("You are not logged in, please login.");
            } else {
                System.out.println("An error has occurred while uploading the file.");
            }
        }
    }

    private void update() throws RemoteException {
        try {
            System.out.println("Please enter full file path: ");
            String filePath = sc.nextLine();
            File file = new File(filePath);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("The file is not existing or it is dir ...");
                return;
            }

            System.out.println("Please enter file assess permissions (public/private): ");
            AccessPermissions accessPermissions = readAccessPermission();

            System.out.println("Please enter file operation permissions (write/read): ");
            OperationPermissions operationPermissions = readOperationPermission();

            server.updateFile(client.getSessionID(), new FileDTO(file.getName(), file.length(), accessPermissions, operationPermissions));
        } catch (FileCatalogException e) {
            if (e.getMessage().equals("User not logged in")) {
                System.out.println("You are not logged in, please login.");
            } else {
                System.out.println("An error has occurred while uploading the file.");
            }
        }
    }

    private void listFiles() throws RemoteException, FileCatalogException {
        List<String> fileList = server.listExistingFiles(client.getSessionID());
        if (fileList != null) {
            System.out.println("Here is your files: ");
            for (String s : fileList) {
                System.out.println(s);
            }
            if (fileList.isEmpty()) {
                System.out.println("No files to list.");
            }
        }
    }


    private void unregister() throws RemoteException, FileCatalogException {
        server.unRegisterUser(client.getSessionID());
    }

    private void registerUser() throws RemoteException, FileCatalogException {
        System.out.println("Enter your username: ");
        String name = sc.nextLine();
        System.out.println("Enter your password: ");
        String pass = sc.nextLine();
        server.registerNewUser(name, pass);
    }

    private void logout() throws RemoteException, FileCatalogException {

        server.logOut(client.getSessionID());
        System.out.println("You have been logged out! Have a nice day");
    }

    private void login() throws RemoteException {
        try {
            System.out.println("Enter your username: ");
            String name = sc.nextLine();
            System.out.println("Enter your password: ");
            String pass = sc.nextLine();
            server.logIn(name, pass);
            String sessionID = server.logIn(name, pass);
            client.setSessionID(sessionID);
        } catch (FileCatalogException e) {
            System.out.println("There was a problem logging in. Please check your user name and password!");
        }
    }

    private void registerForNotification() throws RemoteException {
        try {
            server.registerForNotification(client.getSessionID(), client);
        } catch (FileCatalogException e) {
            System.out.println("There was a problem registering for notification.");
        }
    }

    private AccessPermissions readAccessPermission() {
        AccessPermissions accessPermissions = null;

        while (accessPermissions == null) {
            try {
                accessPermissions = AccessPermissions.valueOf(sc.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Wrong access permission. Available values (public/private)");
            }
        }

        return accessPermissions;
    }

    private OperationPermissions readOperationPermission() {
        OperationPermissions operationPermissions = null;

        while (operationPermissions == null) {
            try {
                operationPermissions = OperationPermissions.valueOf(sc.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Wrong operation permission. Available values (write/read)");
            }
        }

        return operationPermissions;
    }

    private Commands readCommand() {
        Commands commands = null;

        while (commands == null) {
            try {
                commands = Commands.valueOf(sc.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Command does not exist. Use one of the following commands:");
                printCommands();
            }
        }

        return commands;
    }
}
