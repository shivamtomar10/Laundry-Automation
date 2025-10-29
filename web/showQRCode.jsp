<%-- 
    Document   : showQrCode
    Created on : 19 Oct 2025, 12:21:00 am
    Author     : Shivam
--%>

<%@page import="java.awt.image.BufferedImage"%>
<%@page import="java.util.Base64"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Get Base64 QR image and wash ID from session
    String base64Image = (String) session.getAttribute("qrImage");
    Integer washId = (Integer) session.getAttribute("orderId");

    // If not present, redirect back to dashboard
    if(base64Image == null || washId == null){
        response.sendRedirect("studentDashboard.jsp");
        return;
    }

    // Remove from session so it doesn't persist
    session.removeAttribute("qrBase64");
    session.removeAttribute("washId");
%>
<!DOCTYPE html>
<html>
    <head>
    <meta charset="UTF-8">
    <title>QR Code</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container col-md-6 mx-auto text-center mt-5">
        <div class="card p-4 shadow-sm">
            <h2>Scan this QR for Laundry Staff</h2>
            <img src="data:image/png;base64,<%= base64Image %>" 
                 alt="QR Code" class="img-fluid mt-3">
            <p class="mt-2">Wash ID: <%= washId %></p>
            <a href="studentDashboard.jsp" class="btn btn-secondary mt-3">Back to Dashboard</a>
        </div>
    </div>
</body>
</html>