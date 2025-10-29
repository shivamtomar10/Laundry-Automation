/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.*;
import model.Employee;

/**
 *
 * @author Shivam
 */
public class EmployeeDao {
    private Connection con;

    public EmployeeDao() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DBConnection.getConnection();
        
        } catch (ClassNotFoundException e) {
        }
    }

    public Employee validateEmployee(String email, String password) {
        Employee emp = null;
        try {
            String sql = "SELECT * FROM employees WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password); // later replace with hashed password
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                emp = new Employee();
                emp.setEmployeeId(rs.getInt("employee_id"));
                emp.setName(rs.getString("name"));
                emp.setEmail(rs.getString("email"));
                emp.setRole(rs.getString("role"));
                emp.setPhone(rs.getString("phone"));
                emp.setActive(rs.getBoolean("is_active"));
            }
            
        } catch (SQLException e) {
        }
        return emp;
    }
}
