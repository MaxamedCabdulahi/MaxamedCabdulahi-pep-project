package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.AccountDAO;
import DAO.MessageDAO;

public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    /**
     * Constructor initializes the account and message services, injecting DAOs.
     */
    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService(new MessageDAO(), new AccountDAO());
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     *
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);

        return app;
    }

    /**
     * Handles user registration. It validates input and creates a new account.
     * @param ctx Javalin Context object.
     */
    private void registerHandler(Context ctx) {
        // Parse request body as Account object
        Account account = ctx.bodyAsClass(Account.class);

        Account accountDAO;

        // Input validation for username and password
        if (account.getUsername() == null || account.getUsername().isBlank() ||
                account.getPassword() == null || account.getPassword().length() < 4) {
            ctx.status(400);
            return;
        }

        // Check if username already exists
        if (accountService.getAccountByUsername(account.getUsername()) != null) {
            ctx.status(400);
            return;
        }
        // Create account
        Account newAccount = accountService.createAccount(account);

        if (newAccount != null) {
            ctx.json(newAccount);
        } 
        else {
            ctx.status(200);
        }

    }

    public void registerUser(Context ctx) {
        try {
            ObjectMapper om = new ObjectMapper();
            Account account = om.readValue(ctx.body(), Account.class); // Parse JSON body into Account object
    
            // Validate username and password
            if (account.getUsername() == null || account.getUsername().isBlank() || 
                account.getPassword() == null || account.getPassword().length() < 4) {
                ctx.status(400).result("Invalid username or password"); // Invalid data
                return;
            }
    
            // Attempt to create the account using DAO
            Account createdAccount = AccountDAO.createAccount(account);
    
            if (createdAccount == null) {
                ctx.status(400).result("Username already exists"); // Username already exists
            } else {
                ctx.status(200).json(createdAccount); // Account created successfully
            }
    
        } catch (JsonProcessingException e) {
            ctx.status(400).result("Invalid JSON data"); // Catch JSON processing errors
        } catch (@SuppressWarnings("hiding") IOException e) {
            ctx.status(500).result("Internal server error"); // Catch other IO errors
        }
    }
    
    /**
     * Handles user login by validating credentials and returning the account if successful.
     * @param ctx Javalin Context object.
     */
    public void loginHandler(Context ctx) {
        // Parse request body as Account
        Account loginAttempt = ctx.bodyAsClass(Account.class);  
        Account loggedInAccount = accountService.verifyLogin(loginAttempt.getUsername(), loginAttempt.getPassword());
        
        if (loggedInAccount != null) {
            ctx.json(loggedInAccount);  // Return the Account object as JSON if login succeeds
            ctx.status(200);  // OK status
        } 
        else {
            ctx.status(401);  // Unauthorized if login fails
        }
       
    }

    /**
     * Handles message creation by validating and saving a new message.
     * @param ctx Javalin Context object.
     */
    public void createMessageHandler(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);  // Convert JSON request to Message object
        //System.out.println("Hello Message");
        Message createdMessage = messageService.createMessage(message);  // Call service layer
        System.out.println(createdMessage);
        if (createdMessage != null) {
            ctx.json(createdMessage);  // Return the created message as JSON
            ctx.status(200);  // Success status
        } else {
            ctx.status(400);  // Client error status if message creation fails
        }
    }

    /**
     * Retrieves all messages and returns them as JSON.
     * @param ctx Javalin Context object.
     */
    private void getAllMessagesHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessages(); // Fetch all messages from service
        ctx.json(messages); // Return the list of messages
    }

    /**
     * Retrieves a specific message by its ID.
     * @param ctx Javalin Context object.
     */
    private void getMessageByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id")); // Get message ID from path
        Message message = MessageService.getMessageById(messageId); // Fetch message by ID
        if (message != null) {
            ctx.json(message); // Return found message
        } else {
            ctx.json(""); // Return empty if not found
        }
    }

    /**
     * Deletes a message by its ID and returns the deleted message.
     * @param ctx Javalin Context object.
     */
    private void deleteMessageByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id")); // Get message ID from path
        Message deletedMessage = messageService.deleteMessageById(messageId); // Call service to delete message
        if (deletedMessage != null) {
            ctx.json(deletedMessage); // Return the deleted message
        } else {
            ctx.json(""); // Return empty if message not found
        }
    }

     /**
     * Updates a message by its ID with new content, validating the message text.
     * @param ctx Javalin Context object.
     */
    private void updateMessageByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));  // Get message ID from path
        Message messageUpdates = ctx.bodyAsClass(Message.class); // Parse updated message from request body

        // Validate message_text
        if (messageUpdates.getMessage_text() == null || messageUpdates.getMessage_text().isBlank() ||
                messageUpdates.getMessage_text().length() >= 255) {
            ctx.status(400); // Return Bad Request if validation fails
            return;
        }

        // Update message
        Message updatedMessage = messageService.updateMessage(messageId, messageUpdates.getMessage_text());

        if (updatedMessage != null) {
            ctx.json(updatedMessage); // Return the updated message
        } else {
            ctx.status(400); // Bad Request if update fails
        }
    }

    // Method to handle updating messages
    public void updateMessage(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        ObjectMapper om = new ObjectMapper();

        try {
            // Read the incoming message object
            Message updatedMessage = om.readValue(context.body(), Message.class);
            // Fetch the existing message from the database
            Message existingMessage = MessageService.getMessageById(messageId);

            // Check if the message exists
            if (existingMessage != null) {
                // Validate new message_text
                if (updatedMessage.getMessage_text() != null && !updatedMessage.getMessage_text().isEmpty() 
                    && updatedMessage.getMessage_text().length() <= 255) {
                    
                    existingMessage.setMessage_text(updatedMessage.getMessage_text());
                    messageService.updateMessage(existingMessage); // Update the message in the service

                    // Fetch the updated message and return it in the response
                    Message updatedMessageResponse = MessageService.getMessageById(messageId);
                    context.json(updatedMessageResponse); // Return the full updated message
                    context.status(200); // Set status to 200 OK
                } else {
                    context.status(400); // Bad Request for invalid message text
                }
            } else {
                context.status(404); // Not Found if the message does not exist
            }
        } catch (Exception e) {
            context.status(400); // Bad Request for any parsing errors or unexpected issues
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all messages posted by a specific account.
     * @param ctx Javalin Context object.
     */
    private void getMessagesByAccountIdHandler(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("account_id")); // Get account ID from path
        List<Message> messages = MessageService.getMessagesByAccountId(accountId); // Fetch messages by account ID
        ctx.json(messages); // Return the list of messages
        ctx.status(200); // OK status
    }

}