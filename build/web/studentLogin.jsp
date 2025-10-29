<%-- 
    Document   : login
    Created on : 26 Sept 2025, 10:56:39 pm
    Author     : Shivam
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Student Login</title>
        <meta name="google-signin-client_id"
          content="885385130463-4bgn4sufqhjk1en1qjj7cibskg857j5b.apps.googleusercontent.com">

        <script src="https://accounts.google.com/gsi/client" async defer></script>
        <style>
        body {
            font-family: Arial, sans-serif;
            text-align: center;
            margin-top: 60px;
        }
        .container {
            display: inline-block;
            padding: 25px;
            border: 1px solid #ccc;
            border-radius: 10px;
            background: #f7f7f7;
        }
        .error {
            color: red;
            margin-bottom: 15px;
        }
        input[type=text], input[type=password] {
            width: 250px;
            padding: 8px;
            margin: 8px 0;
        }
        button {
            padding: 8px 20px;
            background: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        button:hover {
            background: #45a049;
        }
        .divider {
            margin: 20px 0;
            font-weight: bold;
        }
        </style>
        <script>
            function handleCredentialResponse(response) {
            // Send ID token to servlet for verification
                const form = document.createElement("form");
                form.method = "POST";
                form.action = "LoginServlet";
            
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = "idtoken";
                input.value = response.credential;
                form.appendChild(input);

                document.body.appendChild(form);
                form.submit();
            }

            window.onload = function (){
                google.accounts.id.initialize({
                    client_id: "885385130463-4bgn4sufqhjk1en1qjj7cibskg857j5b.apps.googleusercontent.com",
                    callback: handleCredentialResponse
                });
                google.accounts.id.renderButton(
                    document.getElementById("googleButton"),
                    { theme: "outline", size: "large" } // customization
                );
            };
    </script>
    </head>
    <body>
        <div class="container">

            <h2>Student Login</h2>

            <% if (error != null) { %>
            <div class="error"><%= error %></div>
            <% } %>

            <!-- ✅ Normal Username/Password form -->
        <form action="NormalLoginServlet" method="post">
            <input type="text" name="username" placeholder="College Email" required><br>
            <input type="password" name="password" placeholder="Password" required><br>
            <button type="submit">Login</button>
        </form>

        <div class="divider">OR</div>

            <!-- ✅ Google Sign-In Button -->
        <div id="googleButton"></div>

    </div>
        
    </body>
</html>
