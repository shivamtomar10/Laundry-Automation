<%-- 
    Document   : bookWash
    Created on : 19 Oct 2025, 12:09:23 am
    Author     : Shivam
--%>

<%@page import="model.Student"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Student student = (Student) session.getAttribute("student");
    if(student == null){
        response.sendRedirect("studentLogin.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Book Wash</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="p-5">
        <div class="container col-md-6 mx-auto">
    <div class="card shadow-sm p-4">
        <h2 class="text-center mb-4">Book Your Wash</h2>

        <form action="BookWashServlet" method="post">
            <div class="mb-3">
                <label for="numClothes" class="form-label">Number of Clothes</label>
                <input type="number" name="numClothes" id="numClothes" class="form-control" required min="1">
            </div>
            <div class="mb-3">
                <label for="instructions" class="form-label">Instructions (optional)</label>
                <textarea name="instructions" id="instructions" class="form-control" rows="3"></textarea>
            </div>
            <button type="submit" class="btn btn-success w-100">Submit & Generate QR</button>
        </form>

        <div class="mt-3 text-center">
            <a href="studentDashboard.jsp" class="btn btn-secondary">Back to Dashboard</a>
        </div>
    </div>
</div>
    </body>
</html>
