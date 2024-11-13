import java.util.*;

class Account
{
    String accountNumber;
    String Name;
    String accountType;
    double balance;
    ArrayList<Transaction> transactions;
    final double SAVINGS_INTEREST_RATE = 0.02;
    int lastinterest;

    public Account(String accountNumber, String Name, String accountType, double initialDeposit)
    {
        this.accountNumber = accountNumber;
        this.Name = Name;
        this.accountType = accountType;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        this.lastinterest=-1;
    }

    public void deposit(double amount)
    {
        balance += amount;
        transactions.add(new Transaction("DEPOSIT", amount));
    }

    public boolean withdraw(double amount)
    {
        if (balance >= amount)
        {
            balance -= amount;
            transactions.add(new Transaction("WITHDRAWAL", amount));
            return true;
        }
        else
        {
            System.out.println("Insufficient balance.");
            return false;
        }
    }

    public void addMonthlyInterest()
    {
        if (accountType.equals("savings"))
        {
            Calendar cal = Calendar.getInstance();
            int currentMonth = cal.get(Calendar.MONTH);

            if (lastinterest != currentMonth)
            {
                double interest = balance * (SAVINGS_INTEREST_RATE / 12);
                balance += interest;
                transactions.add(new Transaction("INTEREST", interest));
                lastinterest = currentMonth;
                System.out.println("Interest applied for this month.");
            }
        }
    }
    public void Statement()
    {
        System.out.println("Statement for Account: " + accountNumber);
        System.out.println("       Id           date and time            Type  Amount");
        for (Transaction transaction : transactions)
        {
            System.out.println(transaction);
        }
        System.out.println("Current Balance: " + balance);
    }
}
class User
{
    String username;
    String password;
    ArrayList<Account> accounts;

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.accounts = new ArrayList<>();
    }

    public Account getAccountByNumber(String accountNumber)
    {
        for (Account account : accounts)
        {
            if (account.accountNumber.equals(accountNumber))
            {
                return account;
            }
        }
        return null;
    }
}

class Transaction
{
    static int flag = 1;
    String transactionID;
    String date;
    String type;
    double amount;

    public Transaction(String type, double amount)
    {
        this.transactionID = "Transaction"+ (flag++);
        this.date = new Date().toString();
        this.type = type;
        this.amount = amount;
    }

    public String toString()
    {
        return transactionID + "  " + date + "   " + type + "  " + amount;
    }
}

public class Main
{
    static ArrayList<User> users = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    private static User LoggedIn;

    private static void register()
    {
        String username;
        do {
            System.out.print("Enter username: ");
            username = sc.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please enter a valid username.");
            }
        } while (username.isEmpty());

        String password;
        do {
            System.out.print("Enter password: ");
            password = sc.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please enter a valid password.");
            }
        } while (password.isEmpty());

        users.add(new User(username, password));
        System.out.println("Registration successful.");

    }

    private static void login()
    {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        for (User i : users)
        {
            if (i.username.equals(username) && i.password.equals(password))
            {
                LoggedIn= i;
                System.out.println("Login successful.");
                return;
            }
        }
        System.out.println("Invalid credentials.");
    }

    private static void Menu()
    {
        while (true)
        {
            for (Account account : LoggedIn.accounts)
            {
                account.addMonthlyInterest();
            }
            System.out.println("\n1. Open new Account\n2. Deposit\n3. Withdraw\n4. Check Balance\n5. Statement\n6. Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice)
            {
                case 1:
                    openAccount();
                    break;
                case 2:
                    deposit();
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                    checkBalance();
                    break;
                case 5:
                    Statement();
                    break;
                case 6:
                    LoggedIn= null;
                    System.out.println("Logged out.");
                    return;
            }
        }
    }

    private static void openAccount()
    {
        System.out.print("Enter account holder's name: ");
        String Name = sc.nextLine();
        System.out.print("Enter account type (savings/checking): ");
        String accountType = sc.nextLine().toLowerCase();

        while (!accountType.equals("savings") && !accountType.equals("checking"))
        {
            System.out.println("Incorrect input. Please enter 'savings' or 'checking'.");
            System.out.print("Enter account type (savings/checking): ");
            accountType = sc.nextLine().toLowerCase();
        }

        System.out.print("Enter initial deposit amount: ");
        double initialDeposit = sc.nextDouble();
        sc.nextLine();

        String accountNumber = "user" + (LoggedIn.accounts.size() + 1);
        Account newAccount = new Account(accountNumber, Name, accountType, initialDeposit);
        LoggedIn.accounts.add(newAccount);
        System.out.println("Account created successfully.");
        System.out.println("Account Number: " + accountNumber);
    }

    private static void deposit()
    {
        Account account = selectAccount();
        double amount=0;
        if (account != null)
        {
            boolean valid = false;
            while (!valid) {
                System.out.print("Enter deposit amount: ");
                String input = sc.nextLine().trim();

                try {
                    amount = Double.parseDouble(input);
                    if (amount > 0)
                    {
                        valid = true;
                    } else
                    {
                        System.out.println("Deposit amount must be greater than zero. Please try again.");
                    }
                }
                catch (NumberFormatException e)
                {
                    System.out.println("Invalid input. Please enter a numeric value.");
                }
            }
            account.deposit(amount);
            System.out.println("Deposit successful.");
        }

    }

    private static void withdraw()
    {
        Account account = selectAccount();
        if (account != null)
        {
            System.out.print("Enter withdrawal amount: ");
            double amount = sc.nextDouble();
            sc.nextLine();
            if (account.withdraw(amount))
            {
                System.out.println("Withdrawal successful.");
            }
        }
    }

    private static void checkBalance()
    {
        Account account = selectAccount();
        if (account != null)
        {
            System.out.println("Current Balance: " + account.balance);
        }
    }

    private static void Statement()
    {
        Account account = selectAccount();
        if (account != null) {
            account.Statement();
        }
    }

    private static Account selectAccount()
    {
        System.out.print("Enter account number: ");
        String accountNumber = sc.nextLine();
        Account account = LoggedIn.getAccountByNumber(accountNumber);
        if (account == null)
        {
            System.out.println("Account not found.");
        }
        return account;
    }
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n1. Register\n2. Login\n3. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    if (LoggedIn != null)
                    {
                        Menu();
                    }
                    break;
                case 3:
                    System.exit(0);
            }
        }
    }
}
