package service;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteAccountService {

    public static boolean deleteAccount(Connection con, int id) {

        try {

            // Step 1: Delete transactions first (important if FK exists)
            String deleteTxn =
                    "delete from transactions where account_id=?";

            PreparedStatement ps1 =
                    con.prepareStatement(deleteTxn);

            ps1.setInt(1, id);
            ps1.executeUpdate();

            // Step 2: Delete account
            String deleteAcc =
                    "delete from accounts where id=?";

            PreparedStatement ps2 =
                    con.prepareStatement(deleteAcc);

            ps2.setInt(1, id);

            int rows = ps2.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return false;
    }
}