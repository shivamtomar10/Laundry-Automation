<%-- 
    Document   : employeeLogin
    Created on : 19 Oct 2025, 2:51:21 pm
    Author     : Shivam
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
    <title>Employee / Admin Login</title>

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #74b9ff, #0984e3);
            height: 100vh;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .login-container {
            background-color: #fff;
            padding: 40px 50px;
            border-radius: 12px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
            width: 350px;
            text-align: center;
            animation: fadeIn 0.6s ease;
        }

        .login-container h2 {
            margin-bottom: 20px;
            color: #2d3436;
        }

        .login-container input {
            width: 100%;
            padding: 12px;
            margin: 10px 0;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 15px;
        }

        .login-container button {
            width: 100%;
            padding: 12px;
            background-color: #0984e3;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            transition: 0.3s ease;
        }

        .login-container button:hover {
            background-color: #74b9ff;
        }

        .footer-text {
            margin-top: 15px;
            font-size: 13px;
            color: #636e72;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-15px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
    </head>
    <body>
        <div class="login-container">
        <form action="EmployeeLoginServlet" method="post">
            <h2>Employee / Admin Login</h2>
            <input type="email" name="email" placeholder="Enter email" required>
            <input type="password" name="password" placeholder="Enter password" required>
            <button type="submit">Login</button>
            <p class="footer-text">Only authorized staff allowed</p>
        </form>
    </div>

    </body>
</html>
