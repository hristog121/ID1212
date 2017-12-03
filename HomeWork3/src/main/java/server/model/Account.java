package server.model;

import server.integrations.FileSystemDAO;

public class Account {
    private String userName;
    private String passWord;
    private int id;

    public Account(String userName, String passWord, int id) {
        this.userName = userName;
        this.passWord = passWord;
        this.id = id;
    }

    public Account(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;

    }

    public String getPassWord() {
        return passWord;
    }
}
