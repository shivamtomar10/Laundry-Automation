/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.*;

/**
 *
 * @author Shivam
 */
public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/laundrydb";
    private static final String USER = "postgres";
    private static final String PASS = "shivam";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            return null;
        }
    }
    
}
