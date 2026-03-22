package com.bank.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BalanceCheckService {

    public static void checkBalance(Connection con, int id) {

        try {

            String query = "select balance from accounts where id=?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                double balance = rs.getDouble("balance");

                System.out.println("\n💰 Your Current Balance: " + balance);

            } else {

                System.out.println("Account Not Found ❌");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}