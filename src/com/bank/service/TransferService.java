package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransferService {

    public static void transfer(Connection con, int fromId, int toId, double amount) {

        try {
            con.setAutoCommit(false);

            // Check sender balance
            String senderQuery = "select balance from accounts where id=?";
            PreparedStatement ps1 = con.prepareStatement(senderQuery);
            ps1.setInt(1, fromId);
            ResultSet rs1 = ps1.executeQuery();

            if (!rs1.next()) {
                System.out.println("Sender Account Not Found ❌");
                return;
            }

            double balance = rs1.getDouble("balance");

            if (balance < amount) {
                System.out.println("Insufficient Balance ❌");
                return;
            }

            // Check receiver exists
            String receiverQuery = "select id from accounts where id=?";
            PreparedStatement psCheck = con.prepareStatement(receiverQuery);
            psCheck.setInt(1, toId);
            ResultSet rs2 = psCheck.executeQuery();

            if (!rs2.next()) {
                System.out.println("Receiver Account Not Found ❌");
                con.rollback();   // IMPORTANT
                return;
            }

            // Deduct sender
            String deductQuery = "update accounts set balance = balance - ? where id=?";
            PreparedStatement ps2 = con.prepareStatement(deductQuery);
            ps2.setDouble(1, amount);
            ps2.setInt(2, fromId);

            int rows1 = ps2.executeUpdate();

// Add receiver
            String addQuery = "update accounts set balance = balance + ? where id=?";
            PreparedStatement ps3 = con.prepareStatement(addQuery);
            ps3.setDouble(1, amount);
            ps3.setInt(2, toId);

            int rows2 = ps3.executeUpdate();

//  Check success
            if (rows1 == 0 || rows2 == 0) {
                con.rollback();
                System.out.println("Transfer Failed ❌");
                return;
            }

//  YAHI PE SAVE KARNA HAI (ONLY ON SUCCESS)

// sender
            TransactionService.saveTransaction(con, fromId, "TRANSFER_SENT", amount);

// receiver
            TransactionService.saveTransaction(con, toId, "TRANSFER_RECEIVED", amount);

// commit
            con.commit();

            System.out.println("Transfer Successful ✅");

        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}