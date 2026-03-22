package service;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DepositService {

    public static void deposit(Connection con, int id, double amount) {

        try {
            String query = "update accounts set balance = balance + ? where id = ?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setDouble(1, amount);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Amount Deposited Successfully");

                TransactionService.saveTransaction(con, id, "DEPOSIT", amount);
                con.commit();
            } else {
                System.out.println("Account Not Found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}