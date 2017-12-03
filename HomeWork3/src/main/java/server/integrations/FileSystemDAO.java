package server.integrations;

import common.AccessPermissions;
import common.OperationPermissions;
import server.model.Account;
import server.model.FileDescriptor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static common.AccessPermissions.PUBLIC;

public class FileSystemDAO {

    //Declaration of the prepared statements
    private PreparedStatement createAccountStmt;
    private PreparedStatement createSessionStmt;
    private PreparedStatement deleteSessionStmt;
    private PreparedStatement deleteSessionForUserStmt;
    private PreparedStatement deleteAccountStmt;
    private PreparedStatement findAccountByNameStmt;
    private PreparedStatement findAccountBySessionIDStmt;
    private PreparedStatement createFileStmt;
    private PreparedStatement findFileByNameStmt;
    private PreparedStatement listAllFilesStmt;
    private PreparedStatement deleteFileStmt;
    private PreparedStatement updateFileStmt;

    public FileSystemDAO() throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB("HM3");
        preparedStatements(connection);
    }

    //Method to connect to DB - using mysql
    private Connection connectToDB(String datasource) throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + datasource, "leif", "leif1234");
    }

    public void createAccount(Account account) throws FileCatalogDBException {
        String failureMsg = "Could not create the account: " + account;
        try {
            createAccountStmt.setString(1, account.getUserName());
            createAccountStmt.setString(2, account.getPassWord());
            int rows = createAccountStmt.executeUpdate();
            if (rows != 1) {
                throw new FileCatalogDBException(failureMsg);
            }
        } catch (SQLException sqle) {
            throw new FileCatalogDBException(failureMsg, sqle);
        }
    }

    //Hardcode what the statements will do so we can just call them with the correct attributes
    public void preparedStatements(Connection connection) throws SQLException {
        createAccountStmt = connection.prepareStatement("INSERT INTO  HM3.accounts (USER_NAME, PASSWORD) VALUES (?,?)");
        createFileStmt = connection.prepareStatement("INSERT INTO HM3.files (name, size, owner, access_permissions, operation_permissions) VALUES (?,?,?,?,?)");
        findFileByNameStmt = connection.prepareStatement("SELECT * FROM HM3.files WHERE name = ? ");
        createSessionStmt = connection.prepareStatement("INSERT INTO HM3.session (session_id, user) VALUES (?,?)");
        findAccountByNameStmt = connection.prepareStatement("SELECT * FROM HM3.accounts WHERE user_name = ? ");
        deleteAccountStmt = connection.prepareStatement("DELETE FROM HM3.accounts WHERE id = ?");
        findAccountBySessionIDStmt = connection.prepareStatement("SELECT a.* FROM HM3.session s INNER JOIN HM3.accounts a ON s.user = a.id WHERE " +
                "session_id = ?");
        deleteSessionStmt = connection.prepareStatement("DELETE FROM HM3.session WHERE session_id = ?");
        deleteSessionForUserStmt = connection.prepareStatement("DELETE FROM HM3.session WHERE user = ?");
        listAllFilesStmt = connection.prepareStatement("SELECT * FROM HM3.files WHERE owner = ? OR access_permissions = ?");
        deleteFileStmt = connection.prepareStatement("DELETE FROM HM3.files WHERE name=?");
        updateFileStmt = connection.prepareStatement("UPDATE HM3.files SET size=?, access_permissions=?,operation_permissions=? WHERE name=?");
    }

    //Sear for a specific file by name in the DB
    public FileDescriptor findFileByName(String name) throws FileCatalogDBException {
        ResultSet result = null;
        try {
            findFileByNameStmt.setString(1, name);
            result = findFileByNameStmt.executeQuery();
            if (result.next()) {
                return new FileDescriptor(
                        result.getString("name"),
                        result.getInt("size"),
                        AccessPermissions.valueOf(result.getString("access_permissions")),
                        OperationPermissions.valueOf(result.getString("operation_permissions")), result.getInt("owner"));
            }
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can not find file with name: " + name, e);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                throw new FileCatalogDBException("Can close result", e);
            }
        }
        return null;
    }

    //Create a file reference in the DB
    public int createFile(FileDescriptor fileDescriptor, Account account) throws FileCatalogDBException {
        try {
            createFileStmt.setString(1, fileDescriptor.getName());
            createFileStmt.setLong(2, fileDescriptor.getSize());
            createFileStmt.setInt(3, account.getId());
            createFileStmt.setString(4, fileDescriptor.getAccessPermissions().name());
            createFileStmt.setString(5, fileDescriptor.getOperationPermissions().name());
            return createFileStmt.executeUpdate();
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can not create file. ", e);
        }

    }

    //Create a session ref in the DB
    public void createSession(String sessionId, Account account) throws FileCatalogDBException {

        try {
            createSessionStmt.setString(1, sessionId);
            createSessionStmt.setInt(2, account.getId());
            int rowsAffected = createSessionStmt.executeUpdate();
            if (rowsAffected != 1) {
                throw new FileCatalogDBException("Can not create the session");
            }
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can not create the session ", e);
        }

    }

    //Stop the session after the user log out
    public void deleteSession(String sessionID) throws FileCatalogDBException {
        try {
            deleteSessionStmt.setString(1, sessionID);
            deleteSessionStmt.executeUpdate();
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can not delete session ID ", e);
        }
    }

    // Delete previous user sessions
    public void deleteSessionForUser(Account account) throws FileCatalogDBException {
        try {
            deleteSessionForUserStmt.setInt(1, account.getId());
            deleteSessionForUserStmt.executeUpdate();
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can not delete sessions for " + account.getId(), e);
        }
    }

    //Will delete from Table HM.Accounts
    public void deleteAccount(Account account) throws FileCatalogDBException {
        try {
            deleteAccountStmt.setInt(1, account.getId());
            deleteAccountStmt.executeUpdate();
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can't delete account with user name: " + account.getUserName(), e);
        }

    }

    //Find account by session ID
    public Account findAccountBySessionID(String sessionID) throws FileCatalogDBException {
        ResultSet result = null;
        try {
            findAccountBySessionIDStmt.setString(1, sessionID);
            result = findAccountBySessionIDStmt.executeQuery();

            if (result.next()) {
                return new Account(result.getString("user_name"), result.getString("password"), result.getInt("id"));
            }
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can't find account by session ID " + sessionID, e);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                throw new FileCatalogDBException("Can't close result ", e);
            }
        }
        return null;
    }

    //Find account by username
    public Account findAccountByName(String userName) throws FileCatalogDBException {
        ResultSet result = null;
        try {
            findAccountByNameStmt.setString(1, userName);
            result = findAccountByNameStmt.executeQuery();
            if (result.next()) {
                return new Account(result.getString("user_name"), result.getString("password"), result.getInt("id"));
            }
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can't fine account by username: " + userName, e);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                throw new FileCatalogDBException("Can't close result ", e);
            }
        }
        return null;
    }

    //List all the files in the DB
    public List<FileDescriptor> listFiles(int owner) throws FileCatalogDBException {
        List<FileDescriptor> listOfFiles = new ArrayList();
        ResultSet result = null;
        try {

            listAllFilesStmt.setInt(1, owner);
            listAllFilesStmt.setString(2, PUBLIC.name());
            result = listAllFilesStmt.executeQuery();
            while (result.next()) {
                listOfFiles.add(new FileDescriptor(result.getString("name"), result.getLong("size"),
                        AccessPermissions.valueOf(result.getString("access_permissions")),
                        OperationPermissions.valueOf(result.getString("operation_permissions")),
                        result.getInt("owner")));
            }
        } catch (SQLException e) {
            throw new FileCatalogDBException("Cant list files", e);
        }
        return listOfFiles;
    }

    public int updateFile(FileDescriptor fileDescriptor) throws FileCatalogDBException {

        try {
            updateFileStmt.setLong(1, fileDescriptor.getSize());
            updateFileStmt.setString(2, fileDescriptor.getAccessPermissions().name());
            updateFileStmt.setString(3, fileDescriptor.getOperationPermissions().name());
            updateFileStmt.setString(4, fileDescriptor.getName());
            return updateFileStmt.executeUpdate();
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can not update file. ", e);
        }
    }

    //Stop the session after the user log out
    public int deleteFile(String fileName) throws FileCatalogDBException {
        try {
            deleteFileStmt.setString(1, fileName);
            return deleteFileStmt.executeUpdate();
        } catch (SQLException e) {
            throw new FileCatalogDBException("Can not delete file with name " + fileName, e);
        }
    }
}
