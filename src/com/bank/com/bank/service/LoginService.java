package com.bank.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService {

    public static boolean login(Connection con, int id, int pin) {

        try {

            String query = "select * from accounts where id=? and pin=?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, id);
            ps.setInt(2, pin);

            ResultSet rs = ps.executeQuery();

            return rs.next(); // only true/false

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}