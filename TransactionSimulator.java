import java.util.*;

public class TransactionSimulator {
    
    static class User {
        String name;
        float balance;
        
        User(String name, float balance) {
            this.name = name;
            this.balance = balance;
        }
    }
    
    static class Transaction {
        int senderId;
        int recipientId;
        float amount;
        
        Transaction(int senderId, int recipientId, float amount) {
            this.senderId = senderId;
            this.recipientId = recipientId;
            this.amount = amount;
        }
    }
    
    public static void main(String[] args) {
        // Initialize users (1-indexed)
        User[] users = new User[12];
        users[1] = new User("bernie", 1200.23f);
        users[2] = new User("grommit", 2215.37f);
        users[3] = new User("maria", 2774.14f);
        users[4] = new User("mario", 12.34f);
        users[5] = new User("waldorf", 444.55f);
        users[6] = new User("whosit", 888.90f);
        users[7] = new User("whatsit", 777.60f);
        users[8] = new User("howsit", 68.70f);
        users[9] = new User("wilbur", 3476.21f);
        users[10] = new User("antonio", 2121.54f);
        users[11] = new User("calypso", 779421.33f);
        
        // Transaction data
        Transaction[] transactions = {
            new Transaction(6, 9, 173.71f),
            new Transaction(4, 8, 124.70f),
            new Transaction(6, 8, 67.38f),
            new Transaction(1, 9, 4.38f),
            new Transaction(8, 7, 38.74f),
            new Transaction(7, 2, 93.14f),
            new Transaction(9, 5, 45.42f),  // wilbur -> waldorf
            new Transaction(6, 5, 32.12f),  // whosit -> waldorf
            new Transaction(7, 10, 98.3f),
            new Transaction(7, 3, 42.58f),
            new Transaction(2, 1, 178.24f),
            new Transaction(5, 9, 78.74f),  // waldorf -> wilbur
            new Transaction(4, 8, 139.7f),
            new Transaction(9, 6, 57.84f),
            new Transaction(10, 9, 127.40f),
            new Transaction(6, 1, 24.37f),
            new Transaction(10, 2, 23.86f),
            new Transaction(4, 6, 72.6f),
            new Transaction(3, 2, 127.63f),
            new Transaction(3, 6, 133.7f),
            new Transaction(9, 5, 184.51f), // wilbur -> waldorf
            new Transaction(4, 5, 133.86f)  // mario -> waldorf
        };
        
        System.out.println("Initial balances:");
        for (int i = 1; i <= 11; i++) {
            System.out.println(users[i].name + ": " + users[i].balance);
        }
        
        System.out.println("\nProcessing transactions:");
        
        for (Transaction t : transactions) {
            User sender = users[t.senderId];
            User recipient = users[t.recipientId];
            
            // Validate transaction
            if (sender.balance >= t.amount) {
                sender.balance -= t.amount;
                recipient.balance += t.amount;
                System.out.println("✓ " + sender.name + " -> " + recipient.name + " (" + t.amount + ")");
            } else {
                System.out.println("✗ REJECTED: " + sender.name + " -> " + recipient.name + " (" + t.amount + ") - Insufficient funds");
            }
        }
        
        System.out.println("\nFinal balances:");
        for (int i = 1; i <= 11; i++) {
            System.out.println(users[i].name + ": " + users[i].balance);
        }
        
        System.out.println("\nWaldorf's final balance: " + users[5].balance);
        System.out.println("Waldorf's final balance (rounded down): " + (int)users[5].balance);
    }
}
