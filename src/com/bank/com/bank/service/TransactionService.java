package com.bank.service;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TransactionService {


    public static void saveTransaction(Connection con, int accountId, String type, double amount) {

        try {
            String query = "insert into transactions(account_id, type, amount) values(?,?,?)";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setDouble(3, amount);

            ps.executeUpdate();
            System.out.println("Transaction Saved: " + type);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}