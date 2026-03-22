package main;

import db.DBConnection;
import service.*;

import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int loggedInUserId = -1;
        while (true) {

            System.out.println("\n===== Bank System =====");
            System.out.println("1 Create Account");
            System.out.println("2 View Accounts");
            System.out.println("3 Deposit");
            System.out.println("4 Withdraw");
            System.out.println("5 Transfer Money");
            System.out.println("6 Transaction History");
            System.out.println("7 Set Pin");
            System.out.println("8 Forgot Pin");
            System.out.println("9 Login");
            System.out.println("10 Logout");
            System.out.println("11 Find Account ID by Name");
            System.out.println("12 Mini Statement");
            System.out.println("13 Check Balance");
            System.out.println("14 Delete Account");
            System.out.println("15 Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();

            try (Connection con = DBConnection.getConnection()) {

                // ================= CREATE ACCOUNT =================
                if (choice == 1) {

                    sc.nextLine();

                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Balance: ");
                    double balance = sc.nextDouble();

                    int pin;
                    while (true) {
                        System.out.print("Set PIN (4 digit): ");
                        pin = sc.nextInt();

                        if (pin >= 1000 && pin <= 9999) break;
                        else System.out.println("❌ Enter valid 4-digit PIN");
                    }

                    sc.nextLine();
                    System.out.print("Enter Security Answer: ");
                    String answer = sc.nextLine().trim();

                    String query = "insert into accounts(name, balance, pin, security_answer) values(?,?,?,?)";

                    PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, name);
                    ps.setDouble(2, balance);
                    ps.setInt(3, pin);
                    ps.setString(4, answer);

                    ps.executeUpdate();

                    //  Get Generated ID
                    ResultSet rs = ps.getGeneratedKeys();

                    if (rs.next()) {
                        int newId = rs.getInt(1);

                        System.out.println("Account Created Successfully ✅");
                        System.out.println("🎉 Your Account ID is: " + newId);
                    }
                }

                // ================= VIEW =================
                else if (choice == 2) {

                    if (loggedInUserId == -1) {
                        System.out.println("❌ Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    String query = "select * from accounts where id=?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, id);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        System.out.println("\n--- Account Details ---");
                        System.out.println("ID: " + rs.getInt("id"));
                        System.out.println("Name: " + rs.getString("name"));
                        System.out.println("Balance: " + rs.getDouble("balance"));
                    }
                }

                // ================= DEPOSIT =================
                else if (choice == 3) {

                    if (loggedInUserId == -1) {
                        System.out.println("❌ Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    System.out.print("Enter Amount: ");
                    double amount = sc.nextDouble();

                    DepositService.deposit(con, id, amount);
                }

                // ================= WITHDRAW =================
                else if (choice == 4) {

                    if (loggedInUserId == -1) {
                        System.out.println("❌ Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    System.out.print("Enter Amount: ");
                    double amount = sc.nextDouble();

                    WithdrawService.withdraw(con, id, amount);
                }

                // ================= TRANSFER =================
                else if (choice == 5) {

                    if (loggedInUserId == -1) {
                        System.out.println("❌ Please login first");
                        continue;
                    }

                    int fromId = loggedInUserId;

                    System.out.print("Enter Receiver ID: ");
                    int toId = sc.nextInt();

                    System.out.print("Enter Amount: ");
                    double amount = sc.nextDouble();

                    TransferService.transfer(con, fromId, toId, amount);
                }
                // ================= HISTORY =================
                else if (choice == 6) {

                    if (loggedInUserId == -1) {
                        System.out.println("❌ Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    String query = "select * from transactions where account_id=?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, id);

                    ResultSet rs = ps.executeQuery();

                    System.out.println("\n--- Transaction History ---");

                    while (rs.next()) {
                        System.out.println(
                                rs.getInt("id") + " | " +
                                        rs.getString("type") + " | " +
                                        rs.getDouble("amount") + " | " +
                                        rs.getTimestamp("date")
                        );
                    }
                }

                // ================= SET PIN =================
                else if (choice == 7) {

                    // Check login first
                    if (loggedInUserId == -1) {
                        System.out.println("❌ Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    //  Old PIN verify
                    System.out.print("Enter Old PIN: ");
                    int oldPin = sc.nextInt();

                    if (!LoginService.login(con, id, oldPin)) continue;

                    //  New PIN
                    int newPin;

                    while (true) {
                        System.out.print("Enter New PIN (4 digit): ");
                        newPin = sc.nextInt();

                        if (newPin >= 1000 && newPin <= 9999) break;
                        else System.out.println("❌ Invalid PIN");
                    }

                    String updatePin = "update accounts set pin=? where id=?";
                    PreparedStatement ps = con.prepareStatement(updatePin);
                    ps.setInt(1, newPin);
                    ps.setInt(2, id);
                    ps.executeUpdate();

                    System.out.println("PIN Updated Successfully ✅");

                    // Security Answer update
                    sc.nextLine();

                    System.out.print("Do you want to update Security Answer? (yes/no): ");
                    String choiceAns = sc.nextLine();

                    if (choiceAns.equalsIgnoreCase("yes")) {

                        System.out.print("Enter New Security Answer: ");
                        String newAnswer = sc.nextLine().trim();

                        if (newAnswer.isEmpty()) {
                            System.out.println("❌ Security Answer cannot be empty");
                        } else {

                            String updateAns = "update accounts set security_answer=? where id=?";
                            PreparedStatement ps2 = con.prepareStatement(updateAns);
                            ps2.setString(1, newAnswer);
                            ps2.setInt(2, id);

                            ps2.executeUpdate();

                            System.out.println("Security Answer Updated ✅");
                        }
                    }
                }
                // ================= FORGOT PIN =================
                else if (choice == 8) {

                    System.out.print("Enter Account ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    String query = "select security_answer from accounts where id=?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, id);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {

                        String dbAnswer = rs.getString("security_answer");

                        System.out.print("Enter your security answer: ");
                        String inputAnswer = sc.nextLine().trim();

                       // Check if not set
                        if (dbAnswer == null || dbAnswer.trim().isEmpty()) {
                            System.out.println("❗ Security Answer not set. Please update your profile first.");
                            continue;
                        }

                        // Compare
                        if (dbAnswer.equalsIgnoreCase(inputAnswer)) {

                            int otp = OTPService.generateOTP();
                            System.out.println("OTP: " + otp);

                            System.out.print("Enter OTP: ");
                            int userOtp = sc.nextInt();

                            if (otp == userOtp) {

                                System.out.println("OTP Verified ✅");

                                // NEW PIN INPUT
                                int newPin;

                                while (true) {
                                    System.out.print("Enter New PIN (4 digit): ");
                                    newPin = sc.nextInt();

                                    if (newPin >= 1000 && newPin <= 9999) {
                                        break;
                                    } else {
                                        System.out.println("❌ Invalid PIN");
                                    }
                                }

                                // UPDATE PIN
                                String updateQuery = "update accounts set pin=? where id=?";
                                PreparedStatement ps2 = con.prepareStatement(updateQuery);
                                ps2.setInt(1, newPin);
                                ps2.setInt(2, id);

                                ps2.executeUpdate();

                                System.out.println("PIN Reset Successful ✅");

                            } else {
                                System.out.println("Invalid OTP ❌");
                            }

                        } else {
                            System.out.println("Wrong Security Answer ❌");
                        }

                    } else {
                        System.out.println("Account Not Found ❌");
                    }
                }

                // ================= Login =================
                else if (choice == 9) {

                    System.out.print("Enter Account ID: ");
                    int id = sc.nextInt();

                    System.out.print("Enter PIN: ");
                    int pin = sc.nextInt();

                    if (LoginService.login(con, id, pin)) {
                        loggedInUserId = id;   // MOST IMPORTANT LINE
                        System.out.println("Login Successful ✅");
                    } else {
                        System.out.println("Login Failed ❌");
                    }
                }

                // ================= LOGOUT =================
                else if (choice == 10) {
                    loggedInUserId = -1;
                    System.out.println("Logged out successfully 👋");
                }

                // ================= Find Account ID by Name =================
                else if (choice == 11) {

                    sc.nextLine(); // clear buffer

                    System.out.print("Enter your Name: ");
                    String name = sc.nextLine();

                    String query = "select id, name from accounts where name like ?";

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, "%" + name + "%");

                    ResultSet rs = ps.executeQuery();

                    boolean found = false;

                    System.out.println("\n--- Matching Accounts ---");

                    while (rs.next()) {

                        found = true;

                        System.out.println(
                                "Account ID: " + rs.getInt("id") +
                                        " | Name: " + rs.getString("name")
                        );
                    }

                    if (!found) {
                        System.out.println("No account found with this name ❌");
                    }
                }


                // ================= Mini Statement =================
                else if (choice == 12) {

                    // Login check
                    if (loggedInUserId == -1) {
                        System.out.println("❌ Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    String query =
                            "select type, amount, date " +
                                    "from transactions " +
                                    "where account_id=? " +
                                    "order by date desc " +
                                    "limit 5";

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, id);

                    ResultSet rs = ps.executeQuery();

                    System.out.println("\n--- Mini Statement (Last 5 Transactions) ---");

                    boolean found = false;

                    while (rs.next()) {

                        found = true;

                        System.out.println(
                                rs.getString("type") + " | " +
                                        rs.getDouble("amount") + " | " +
                                        rs.getTimestamp("date")
                        );
                    }

                    if (!found) {
                        System.out.println("No transactions found ❌");
                    }
                }

                // ================= Check Balance =================

                else if (choice == 13) {

                    // Login check
                    if (loggedInUserId == -1) {
                        System.out.println("❌ Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    BalanceCheckService.checkBalance(con, id);
                }


                // ================= Delete Account =================

                else if (choice == 14) {

                    // Login check
                    if (loggedInUserId == -1) {
                        System.out.println("❌ Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    // PIN verify again
                    System.out.print("Enter PIN to confirm: ");
                    int pin = sc.nextInt();

                    if (!LoginService.login(con, id, pin)) {
                        System.out.println("❌ Incorrect PIN");
                        continue;
                    }

                    sc.nextLine(); // buffer clear

                    // ⚠️ Confirmation
                    System.out.print(
                            "⚠️ Are you sure you want to delete account? (yes/no): "
                    );

                    String confirm = sc.nextLine();

                    if (confirm.equalsIgnoreCase("yes")) {

                        boolean deleted =
                                DeleteAccountService.deleteAccount(con, id);

                        if (deleted) {

                            System.out.println(
                                    "Account Deleted Successfully ❌"
                            );

                            loggedInUserId = -1; // logout

                        } else {

                            System.out.println(
                                    "Account Deletion Failed ❌"
                            );
                        }

                    } else {

                        System.out.println(
                                "Account Deletion Cancelled"
                        );
                    }
                }

                // ================= EXIT =================
                else if (choice == 15) {
                    System.out.println("Exiting...");
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}