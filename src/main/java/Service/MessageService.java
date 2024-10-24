package Service;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Message;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MessageService {

    private static MessageDAO messageDAO;
    private static AccountDAO accountDAO;

    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO; 
    }

   
    
    public Message createMessage(Message message) {
        if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255 || accountDAO.getAccountById(message.getPosted_by()) == null) {
            return null;
        }


        if (!AccountService.isAccountExist(message.getPosted_by())) {
            return null;  // If the posted_by user does not exist
        }
        return MessageDAO.createMessage(message);  // Delegate message creation to DAO
}


    public List<Message> getAllMessages() {
        // Fetch all messages via DAO
        return messageDAO.getAllMessages();
    }

    public static Message getMessageById(int messageId) {
        // Fetch message by ID
        return MessageDAO.getMessageById(messageId);
    }

    public static List<Message> getMessagesByAccountId(int accountId) {
        // Fetch all messages by account ID
        return MessageDAO.getMessagesByAccountId(accountId);
    }
    

    public Message deleteMessageById(int messageId) {
        // Delete the message and return the deleted message object
        return MessageDAO.deleteMessageById(messageId);
    }

    public Message updateMessage(int messageId, String newMessageText) {
        // Validate the new message text
        if (newMessageText == null || newMessageText.isBlank() || newMessageText.length() >= 255) {
            return null;
        }

        // Delegate message update to the DAO
        return MessageDAO.updateMessage(messageId, newMessageText);
    }

    public void updateMessage(Message message) {
        String sql = "UPDATE messages SET message_text = ? WHERE message_id = ?";
        try (Connection conn = ConnectionUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, message.getMessage_text());
            stmt.setInt(2, message.getMessage_id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}