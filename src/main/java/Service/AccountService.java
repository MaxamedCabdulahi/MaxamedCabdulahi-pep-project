package Service;

import DAO.AccountDAO;
import Model.Account;


public class AccountService {

    private static AccountDAO accountDAO;

    public AccountService() {
        AccountService.accountDAO = new AccountDAO();
    }

    public Account createAccount(Account account) {
        
        // Ensure that the account details are valid before calling DAO to persist
        if (account.getUsername() == null || account.getUsername().isBlank() || 
            account.getPassword() == null || account.getPassword().length() < 4) {
            return null;
        }

        // Check if the username already exists
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return null;  // Username already taken
        }

        // Delegate account creation to the DAO
        return accountDAO.createAccount(account);
    }

    

    public Account login(String username, String password) {
        Account account = getAccountByUsername(username);
        if (account != null && account.getPassword().equals(password)) { // Ensure passwords are being checked properly
            return account;
        }
        return null; // User not found or password incorrect
    }
    
    

    public Account verifyLogin(String username, String password) {
        Account account = accountDAO.getAccountByUsername(username);  // Fetch the account by username
    
        if (account != null && account.getPassword().equals(password)) {
            return account;  // Return account if the password matches
        }
    
        return null;  // Return null if the account does not exist or the password is incorrect
    }
        

    public static boolean isAccountExist(int accountId) {
        // Check if the account exists by delegating to the DAO
        return accountDAO.isAccountExist(accountId);
    }

    public Account getAccountByUsername(String username) {
        // Retrieve account details by username
        return accountDAO.getAccountByUsername(username);
    }
    public Account getAccountById(int accountId) { 
        return accountDAO.getAccountById(accountId); // Delegate the call to AccountDAO
    }
}