/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.imageio.ImageIO;
import model.LaundryOrder;

/**
 *
 * @author Shivam
 */
public class LaundryDao {
   // ✅ Add new laundry order (like addWash)
    public int addLaundryOrder(LaundryOrder order) throws SQLException {
    String sql = """
        INSERT INTO laundry_orders 
        (student_id, service_type, booking_date, status, qr_generated, employee_id, free, amount)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING order_id
    """;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, order.getStudentId());
        ps.setString(2, order.getServiceType());
        ps.setTimestamp(3, new Timestamp(order.getBookingDate().getTime()));
        ps.setString(4, order.getStatus());
        ps.setBoolean(5, order.isQrGenerated());
        ps.setObject(6, order.getEmployeeId() == 0 ? null : order.getEmployeeId());
        ps.setBoolean(7, order.isFree());
        ps.setDouble(8, order.getAmount());

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int orderId = rs.getInt("order_id");
            order.setOrderId(orderId); // ✅ Set the generated ID into object
            return orderId;
        }
    }
    throw new SQLException("Failed to insert laundry order, no ID returned.");
}

    // ✅ Get pending orders for a student (like getPendingWashes)
    public List<LaundryOrder> getPendingOrders(int studentId) throws SQLException, WriterException, IOException {
        List<LaundryOrder> list = new ArrayList<>();
        String sql = """
        SELECT lo.*, e.name AS employee_name
        FROM laundry_orders lo
        LEFT JOIN employees e ON lo.employee_id = e.employee_id
        WHERE lo.student_id = ?
          AND lo.status ='Accepted'
        ORDER BY lo.booking_date DESC
    """;
        try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            LaundryOrder order = mapOrder(rs);

            // Generate QR code for each order
            String qrText = "orderId:" + order.getOrderId() + ";studentId:" + studentId;
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrWriter.encode(qrText, BarcodeFormat.QR_CODE, 300, 300);
            BufferedImage qrImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            order.setQrBase64(base64Image);

            list.add(order);
        }
    } catch (WriterException e) {
    }
    return list;
    }

    // ✅ Get total completed washes for a student (like getTotalWashes)
    public int getTotalCompleted(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM laundry_orders WHERE student_id = ? AND status = 'Completed'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ✅ Get remaining free washes in current year
    public int getRemainingFreeWashes(int studentId, int yearlyLimit) throws SQLException {
        String sql = """
            SELECT COUNT(*) 
            FROM laundry_orders 
            WHERE student_id = ? 
              AND free = true 
              AND status IN ('Accepted', 'Completed')
              AND EXTRACT(YEAR FROM booking_date) = EXTRACT(YEAR FROM CURRENT_DATE)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int used = rs.getInt(1);
                return yearlyLimit - used;
            }
        }
        return yearlyLimit;
    }

    // ✅ Get order history for a student (like getWashHistory)
    public List<LaundryOrder> getOrderHistory(int studentId) throws SQLException {
        List<LaundryOrder> list = new ArrayList<>();
        String sql = """
    SELECT lo.*, e.name AS employee_name
    FROM laundry_orders lo
    LEFT JOIN employees e ON lo.employee_id = e.employee_id
    WHERE lo.student_id = ?
      AND lo.status ='completed'
    ORDER BY lo.booking_date DESC
""";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        }
        return list;
    }

    // ✅ Update order details (status, employee, free, amount)
    public void updateOrder(LaundryOrder order) throws SQLException {
        String sql = "UPDATE laundry_orders SET status = ?, employee_id = ?, free = ?, amount = ? WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, order.getStatus());
            ps.setInt(2, order.getEmployeeId());
            ps.setBoolean(3, order.isFree());
            ps.setDouble(4, order.getAmount());
            ps.setInt(5, order.getOrderId());
            ps.executeUpdate();
        }
    }

    // ✅ Helper method to map a result row to LaundryOrder object
    private LaundryOrder mapOrder(ResultSet rs) throws SQLException {
        LaundryOrder order = new LaundryOrder();
        order.setOrderId(rs.getInt("order_id"));
        order.setStudentId(rs.getInt("student_id"));
        order.setServiceType(rs.getString("service_type"));
        order.setBookingDate(rs.getTimestamp("booking_date"));
        order.setStatus(rs.getString("status"));
        order.setQrGenerated(rs.getBoolean("qr_generated"));
        order.setEmployeeId(rs.getInt("employee_id"));
        order.setFree(rs.getBoolean("free"));
        order.setAmount(rs.getDouble("amount"));
        order.setEmployeeName(rs.getString("employee_name")); 
        return order;
    }
    // Inside LaundryDao.java
public LaundryOrder getOrderById(int orderId) {
    LaundryOrder order = null;
    String sql = "SELECT * FROM laundry_orders WHERE order_id = ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            order = new LaundryOrder();
            order.setOrderId(rs.getInt("order_id"));
            order.setStudentId(rs.getInt("student_id"));
            order.setServiceType(rs.getString("service_type"));
            order.setBookingDate(rs.getTimestamp("booking_date"));
            order.setStatus(rs.getString("status"));
            order.setQrGenerated(rs.getBoolean("qr_generated"));
            order.setEmployeeId(rs.getInt("employee_id"));
            order.setFree(rs.getBoolean("free"));
            order.setAmount(rs.getDouble("amount"));
        }
    } catch (SQLException e) {
    }
    return order;
}
    // Inside LaundryDao.java
public void decreaseRemainingWashes(int studentId) {
    String sql = "UPDATE students SET remaining_washes = remaining_washes - 1 WHERE student_id = ? AND remaining_washes > 0";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, studentId);
        ps.executeUpdate();
    } catch (SQLException e) {
    }
}

}



