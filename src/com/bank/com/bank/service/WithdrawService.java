package com.bank.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WithdrawService {

    public static void withdraw(Connection con, int id, double amount) {

        try {

            String checkQuery = "select balance from accounts where id=?";
            PreparedStatement ps1 = con.prepareStatement(checkQuery);
            ps1.setInt(1, id);

            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {

                double currentBalance = rs.getDouble("balance");

                if (currentBalance >= amount) {

                    String updateQuery = "update accounts set balance = balance - ? where id=?";
                    PreparedStatement ps2 = con.prepareStatement(updateQuery);

                    ps2.setDouble(1, amount);
                    ps2.setInt(2, id);

                    ps2.executeUpdate();

                    TransactionService.saveTransaction(con, id, "WITHDRAW", amount);

                    System.out.println("Withdrawal Successful");

                } else {
                    System.out.println("Insufficient Balance ❌");
                }

            } else {
                System.out.println("Account Not Found ❌");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}