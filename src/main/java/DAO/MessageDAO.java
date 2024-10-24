package DAO;

import Model.Message;
import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public static Message createMessage(Message message) {
        Connection conn = ConnectionUtil.getConnection();
        Message createdMessage = null;
     
        try {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, message.getPosted_by());
            stmt.setString(2, message.getMessage_text());
            stmt.setLong(3, message.getTime_posted_epoch());
    
            int rowsAffected = stmt.executeUpdate();
    
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1); // Retrieve the generated message_id
                    createdMessage = new Message(generatedId, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return createdMessage;  // Return the created message object
    }
    
    

    public List<Message> getAllMessages() {
        Connection conn = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
    
        try {
            String sql = "SELECT * FROM message";
            PreparedStatement stmt = conn.prepareStatement(sql);
    
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                // Correct parameter ordering in the Message constructor
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),           // 'posted_by' should be the second parameter
                    rs.getString("message_text"),      // 'message_text' should be the third parameter
                    rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return messages;
    }
    

    public static Message getMessageById(int messageId) {
        Connection conn = ConnectionUtil.getConnection();
        Message message = null;

        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, messageId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static Message deleteMessageById(int messageId) {
        Connection conn = ConnectionUtil.getConnection();
        Message deletedMessage = getMessageById(messageId);

        if (deletedMessage != null) {
            try {
                String sql = "DELETE FROM message WHERE message_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, messageId);

                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return deletedMessage;
    }

    public static Message updateMessage(int messageId, String newMessageText) {
        Connection conn = ConnectionUtil.getConnection();
        Message updatedMessage = null;

        try {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newMessageText);
            stmt.setInt(2, messageId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                updatedMessage = getMessageById(messageId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedMessage;
    }

    public static List<Message> getMessagesByAccountId(int accountId) {
        Connection conn = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}