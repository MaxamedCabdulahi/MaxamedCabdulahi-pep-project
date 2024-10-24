package DAO;

import Model.Account;
import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class AccountDAO {

   

    public Account getAccountByUsername(String username) {
        Connection conn = ConnectionUtil.getConnection();
        Account account = null;
    
        try {
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
    
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                String retrievedUsername = rs.getString("username");
                String password = rs.getString("password");
    
                account = new Account(accountId, retrievedUsername, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return account;  // Return the retrieved account, or null if not found
    }
    

    public static Account createAccount(Account account) {
        Connection conn = ConnectionUtil.getConnection();
        Account createdAccount = null;

        try {
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    createdAccount = new Account(generatedId, account.getUsername(), account.getPassword());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return createdAccount;
    }



    public Account login(String username, String password) {
        Connection conn = ConnectionUtil.getConnection();
        Account account = null;

        try {
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                account = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    public boolean isAccountExist(int accountId) {
        Connection conn = ConnectionUtil.getConnection();
        boolean exists = false;

        try {
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);

            ResultSet rs = stmt.executeQuery();
            exists = rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    public Account getAccountById(int accountId) {
        try (Connection conn = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);

            ResultSet rs = stmt.executeQuery(); 

            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if no account is found with the given ID
    }
}