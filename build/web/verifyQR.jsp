  <%-- 
    Document   : verifyQR.jsp
    Created on : 21 Oct 2025, 11:04:56 pm
    Author     : Shivam
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*, java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>QR Verification</title>
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: #f4f6f8;
            text-align: center;
            padding: 60px;
        }
        .card {
            background: #fff;
            display: inline-block;
            padding: 30px 50px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.15);
            max-width: 500px;
        }
        h2 {
            color: #2E7D32;
        }
        .error {
            color: #E53935;
            font-weight: bold;
        }
        .success {
            color: #43A047;
            font-weight: bold;
        }
        a {
            display: inline-block;
            margin-top: 25px;
            background: #2E7D32;
            color: white;
            padding: 10px 18px;
            border-radius: 8px;
            text-decoration: none;
            transition: 0.3s;
        }
        a:hover {
            background: #1B5E20;
        }
    </style>
</head>
<body>
<%
    String qrContent = request.getParameter("qrContent");

    if (qrContent == null || qrContent.trim().isEmpty()) {
%>
        <div class="card">
            <h2>QR Verification Failed</h2>
            <p class="error">No QR data received from client.</p>
            <a href="employeeDashboard.jsp">Go Back</a>
        </div>
<%
    } else {
        try {
            // ✅ Example: Database validation (optional)
            // Replace with your own DB connection logic
            /*
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/laundrydb", "postgres", "shivam");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM laundry_orders WHERE qr_code = ?");
            ps.setString(1, qrContent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Mark order as verified or accepted
            }
            con.close();
            */

%>
        <div class="card">
            <h2>QR Verified Successfully ✅</h2>
            <p class="success">QR Code Content:</p>
            <p><strong><%= qrContent %></strong></p>
            <a href="employeeDashboard.jsp">Return to Dashboard</a>
        </div>
<%
        } catch (Exception e) {
%>
        <div class="card">
            <h2>Verification Error</h2>
            <p class="error"><%= e.getMessage() %></p>
            <a href="employeeDashboard.jsp">Try Again</a>
        </div>
<%
        }
    }
%>
</body>
</html>
