<%-- 
    Document   : logout
    Created on : 28 Sept 2025, 11:35:53 am
    Author     : Shivam
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Invalidate the session to log out the user
    if (session != null) {
        session.invalidate();
    }
    // Redirect to login page after 2 seconds
    response.sendRedirect("index.html");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Logging Out</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
        <style>
        body {
            background-color: #f8f9fa;
            font-family: Arial, sans-serif;
            text-align: center;
            padding-top: 100px;
        }
        .logout-box {
            display: inline-block;
            padding: 30px;
            border: 1px solid #ccc;
            border-radius: 10px;
            background-color: #fff;
            box-shadow: 2px 2px 12px rgba(0,0,0,0.1);
        }
        .logout-box h3 {
            color: #333;
        }
        .logout-box a {
            display: inline-block;
            margin-top: 20px;
            text-decoration: none;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
        }
        .logout-box a:hover {
            background-color: #0056b3;
        }
    </style>
    </head>
    <body>
    <div class="logout-box">
        <h3>You have been logged out</h3>
        <p>Redirecting to login page...</p>
        <a href="index.html">Go to LMS</a>
    </div>
    </body>
</html>
