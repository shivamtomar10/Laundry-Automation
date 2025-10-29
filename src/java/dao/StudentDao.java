/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import model.Student;
import java.sql.*;
import model.Hostel;

/**
 *
 * @author Shivam
 */
public class StudentDao {
    private static final String URL = "jdbc:postgresql://localhost:5432/laundrydb";
    private static final String USER = "postgres";
    private static final String PASS = "shivam"; // change as needed

    // Get student by email & password (for login)
    public static Student getStudentByEmailPassword(String email, String password) throws SQLException {
        Student student = null;
        String sql = "SELECT s.student_id, s.name, s.email, s.password, " +
                     "h.hostel_name, h.hostel_block, h.laundry_block " +
                     "FROM students s " +
                     "JOIN hostels h ON s.hostel_id = h.id " +
                     "WHERE s.email=? AND s.password=?";
        try{
            Class.forName("org.postgresql.Driver");
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("Connection done");
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                student = new Student();
                student.setId(rs.getInt("student_id"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setPassword(rs.getString("password"));

                Hostel hostel = new Hostel();
                hostel.setName(rs.getString("hostel_name"));
                hostel.setBlock(rs.getString("hostel_block"));
                hostel.setLaundryBlock(rs.getString("laundry_block"));

                student.setHostel(hostel);
            }

        } catch (SQLException e) {
            throw e;
        }
        }catch(ClassNotFoundException e){
            
        }
        return student;
    }

    public static Student getStudentByEmail(String email) throws SQLException {
        Student student = null;
    String sql = "SELECT s.student_id, s.name, s.email, s.password, " +
                 "h.hostel_name, h.hostel_block, h.laundry_block " +
                 "FROM students s " +
                 "JOIN hostels h ON s.hostel_id = h.hostel_id " +
                 "WHERE s.email = ?";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            student = new Student();
            student.setId(rs.getInt("student_id"));
            student.setName(rs.getString("name"));
            student.setEmail(rs.getString("email"));
            student.setPassword(rs.getString("password"));

            Hostel hostel = new Hostel();
            hostel.setName(rs.getString("hostel_name"));
            hostel.setBlock(rs.getString("hostel_block"));
            hostel.setLaundryBlock(rs.getString("laundry_block"));

            student.setHostel(hostel);
        }
    }
    return student;
    }
}
