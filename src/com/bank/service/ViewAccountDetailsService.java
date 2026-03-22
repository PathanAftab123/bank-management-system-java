package service;

import db.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class ViewAccountDetailsService {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try (Connection con = DBConnection.getConnection()) {

            System.out.println("1. Create Account");
            System.out.println("2. View Accounts");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            if (choice == 1) {

                sc.nextLine(); // clear buffer

                System.out.print("Enter Name: ");
                String name = sc.nextLine();

                System.out.print("Enter Balance: ");
                double balance = sc.nextDouble();

                String query = "insert into accounts(name, balance) values(?, ?)";

                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, name);
                ps.setDouble(2, balance);

                ps.executeUpdate();

                System.out.println("Account Created Successfully");

            } else if (choice == 2) {

                String query = "select * from accounts";

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                System.out.println("\n--- Account List ---");

                while (rs.next()) {
                    System.out.println(
                            rs.getInt("id") + " | " +
                                    rs.getString("name") + " | " +
                                    rs.getDouble("balance")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}