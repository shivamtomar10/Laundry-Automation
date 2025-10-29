<%-- 
    Document   : dashboard
    Created on : 26 Sept 2025, 11:00:09 pm
    Author     : Shivam
--%>

<%@page import="model.LaundryOrder"%>
<%@page import="dao.DBConnection"%>
<%@page import="java.sql.*"%>
<%--<%@page import="dao.WashDao"%>--%>
<%--<%@page import="model.Wash"%>--%>
<%--<%@page import="model.LaundryBooking"%>--%>
<%@page import="dao.LaundryDao"%>
<%@page import="java.util.List"%>
<%@page import="model.Student"%>
<%@page import="model.Hostel"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Student student = (Student) session.getAttribute("student");
    if (student == null) {
        response.sendRedirect("studentLogin.jsp");
        return;
    }
    Hostel hostel = student.getHostel();

    // Prevent browser caching
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
    response.setDateHeader("Expires", 0); // Proxies.
    
    // Check if student is logged in
    if (session.getAttribute("student") == null) {
        response.sendRedirect("studentLogin.jsp");
        return;
    }

    Connection conn = DBConnection.getConnection();
    dao.LaundryDao dao = new dao.LaundryDao();

    int yearlyLimit = 10; // Set yearly free washes
    int remainingWashes = dao.getRemainingFreeWashes(student.getId(), yearlyLimit); // implement in LaundryDao
    int totalOrders = dao.getTotalCompleted(student.getId());
    List<model.LaundryOrder> pendingOrders = dao.getPendingOrders(student.getId()); // implement this
    List<model.LaundryOrder> orderHistory = dao.getOrderHistory(student.getId());
%>

<!DOCTYPE html>
<html>
   <head>
    <meta charset="UTF-8">
    <title>Student Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/js/all.min.js"></script>
    
    <style>
    body {
        background: linear-gradient(to right, #e3f2fd, #ffffff);
        font-family: 'Poppins', sans-serif;
    }

    .navbar {
        background-color: #007bff;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }

    .navbar-brand {
        color: #fff !important;
        font-weight: 600;
    }

    .nav-link {
        color: #fff !important;
        font-weight: 500;
    }

    .nav-link:hover {
        color: #e3f2fd !important;
    }

    .profile-card {
        border-radius: 20px;
        background: #fff;
        box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
        padding: 40px 30px;
        margin-bottom: 40px;
        transition: all 0.3s ease;
    }

    .profile-card:hover {
        transform: translateY(-3px);
    }

    .profile-img {
        width: 120px;
        height: 120px;
        border-radius: 50%;
        border: 4px solid #007bff;
        margin-bottom: 15px;
    }

    .grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 20px;
        margin-top: 20px;
    }

    .card {
        background: #fff;
        border-radius: 15px;
        padding: 25px 20px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        transition: all 0.3s ease;
        text-align: center;
    }

    .card:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 25px rgba(0,0,0,0.15);
    }

    .card h3 {
        margin-bottom: 15px;
        font-size: 1.3rem;
        color: #007bff;
    }

    .card table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 10px;
    }

    .card table th, .card table td {
        border: 1px solid #ddd;
        padding: 8px;
        font-size: 0.9rem;
    }

    .card table th {
        background-color: #007bff;
        color: #fff;
    }

    .stat-value {
        font-size: 2rem;
        font-weight: 600;
        color: #007bff;
    }
   #chatbot-container {
    position: fixed;
    bottom: 80px;
    right: 20px;
    width: 320px;
    height: 400px; /* important */
    background: #e9ecef;  /* Slightly gray to highlight message bubbles */
    border-radius: 10px;
    box-shadow: 0 4px 10px rgba(0,0,0,0.3);
    display: none; /* hidden initially */
    flex-direction: column;
    z-index: 1000; /* make sure it appears above other content */
    overflow: hidden;
    justify-content: end;
}

#chatbot-messages {
    flex: 1; /* take remaining height */
    padding: 10px;
    overflow-y: auto;
    font-size: 14px;
    background: #e9ecef;
    display:flex;
    flex-direction: column;
}

#chatbot-input {
    display: flex;
    padding: 10px;
    border-top: 1px solid #ddd;
    /*color: #333*/
}

#chatbot-input input {
    flex: 1;
    padding: 5px;
    width: 100%;
    border: 1px solid #ccc; 
    border-radius: 5px;
}

#chatbot-input button {
    margin-left: 5px;
    background: #007bff;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 5px;
}
#chatbot-toggle {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: #007bff;
  color: white;
  border-radius: 50%;
  padding: 15px;
  cursor: pointer;
  font-size: 22px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.3);
  z-index: 1000; /* always on top */
}
.user-message {
  background: #007bff;
  color: white;
  align-self: flex-end;
  margin: 8px 0;
  padding: 10px 14px;
  border-radius: 18px 18px 0 18px;
  max-width: 80%;
  box-shadow: 0 2px 4px rgba(0,0,0,0.15);
  word-wrap: break-word;
}

.bot-message {
  background: #f1f1f1;
  color: #000;
  align-self: flex-start;
  margin: 8px 0;
  padding: 10px 14px;
  border-radius: 18px 18px 18px 0;
  max-width: 80%;
  box-shadow: 0 2px 4px rgba(0,0,0,0.15);
  word-wrap: break-word;
}
#chatbot-header {
  background: #007bff;
  color: white;
  padding: 8px 12px;
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

#chatbot-header button {
  background: transparent;
  color: white;
  border: none;
  font-size: 16px;
  cursor: pointer;
}
</style>

</head>

<body>

<!-- 🔹 Top Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark">
  <div class="container-fluid px-4">
    <a class="navbar-brand" href="#">
      <i class="fa-solid fa-shirt"></i> Student Laundry Dashboard
    </a>
    <div class="d-flex ms-auto">
    <!--      <a href="#" class="nav-link me-3">
            <i class="fa-solid fa-user"></i> <%= student.getName() %>
          </a>-->
      <a href="logout.jsp" class="btn btn-light btn-sm">
        <i class="fa-solid fa-right-from-bracket text-danger"></i> Logout
      </a>
    </div>
  </div>
</nav>

<!-- 🔹 Main Content -->
    <div class="container mt-5">
    <div class="profile-card text-center mx-auto col-md-8">
        <img src="https://cdn-icons-png.flaticon.com/512/149/149071.png" 
     alt="Person" 
     class="profile-img" 
     style="width:40px; height:40px; border-radius:50%;">

        <h2 class="fw-bold mb-1">Welcome, <%= student.getName() %></h2>
        <p class="text-muted mb-2"><i class="fa-solid fa-envelope"></i> <%= student.getEmail() %></p>
        <p class="text-secondary mb-4"><i class="fa-regular fa-calendar"></i> 
            Today: <%= new java.text.SimpleDateFormat("dd MMM yyyy, HH:mm").format(new java.util.Date()) %></p>

        <div class="row text-center mb-4">
            <div class="col-md-4">
                <div class="stat-card">
                    <i class="fa-solid fa-building fa-2x text-primary mb-2"></i>
                    <h6>Hostel</h6>
                    <p class="fw-semibold"><%= hostel.getName() %></p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card">
                    <i class="fa-solid fa-door-open fa-2x text-success mb-2"></i>
                    <h6>Hostel Block</h6>
                    <p class="fw-semibold"><%= hostel.getBlock() %></p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card">
                    <i class="fa-solid fa-soap fa-2x text-info mb-2"></i>
                    <h6>Laundry Block</h6>
                    <p class="fw-semibold"><%= hostel.getLaundryBlock() %></p>
                </div>
            </div>
        </div>
    </div>

    <div class="grid">
    <!-- Book Wash Now -->
    
<div class="section-card text-center">
    <h3>Book Wash Now</h3>
    <a href="bookWash.jsp" class="btn btn-primary mt-2">Go to Booking Page</a>
</div>

</div>

    </div>

    <!-- Pending Washes -->
<div class="card">
    <h3>Pending Orders</h3>
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Order ID</th>
                <th>Date</th>
                <th>Status</th>
                <th>Accepted By</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% for(LaundryOrder o: pendingOrders){ %>
            <tr>
                <td><%= o.getOrderId() %></td>
                <td><%= o.getBookingDate() %></td>
                <td><%= o.getStatus() %></td>
                <td><%= o.getEmployeeName() %></td>
                <td>
                    <!-- Button triggers modal -->
                    <!--<p>Modal ID: qrModal<%= o.getOrderId() %></p>-->
                    <button type="button" class="btn btn-sm btn-primary"
                            data-bs-toggle="modal"
                            data-bs-target="#qrModal<%= o.getOrderId() %>">
                        <i class="fa-solid fa-eye"></i> View QR Code
                    </button>

                   <!-- Modal -->
                    <div class="modal fade" id="qrModal<%= o.getOrderId() %>" tabindex="-1" aria-hidden="true" data-bs-focus="false">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">QR Code for Order <%= o.getOrderId() %></h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body text-center">
                                    <!-- Hidden focusable element to prevent blinking -->
                                    <input type="text" style="opacity:0; height:0; width:0; position:absolute;">
                                    <img src="data:image/png;base64,<%= o.getQrBase64() %>" alt="QR Code" class="img-fluid"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
</div>
<!-- Remaining Free Washes -->
<div class="card">
    <h3>Remaining Free Washes</h3>
    <div class="stat-value"><%= remainingWashes %></div>
</div>

<!-- Total Orders Done -->
<div class="card">
    <h3>Total Orders Done</h3>
    <div class="stat-value"><%= totalOrders %></div>
</div>

<!-- Order History -->
<div class="card">
    <h3>Wash History</h3>
    <table>
        <tr><th>Order ID</th><th>Date</th><th>Status</th><th>Free</th><th>Amount</th><th>Dispatch By</th></tr>
        <% for(LaundryOrder o: orderHistory){ %>
            <tr>
                <td><%= o.getOrderId() %></td>
                <td><%= o.getBookingDate() %></td>
                <td><%= o.getStatus() %></td>
                <td><%= o.isFree() ? "Yes" : "No" %></td>
                <td><%= o.getAmount() %></td>
                <td><%= o.getEmployeeName() %></td>
            </tr>
        <% } %>
    </table>
</div>


    <!-- Chatbot Toggle Button -->
    <div id="chatbot-toggle" onclick="toggleChat()">💬</div>

    <!-- Chatbot Container -->
    <div id="chatbot-container">
        <div id="chatbot-header">
            <span>Laundry Assistant 🤖</span>
            <button onclick="toggleChat()">✖</button>
        </div>
        <div id="chatbot-messages"></div>
        <div id="chatbot-input">
            <input type="text" id="userInput" placeholder="Ask me anything..." onkeydown="if(event.key==='Enter') sendMessage()">
            <button onclick="sendMessage()">Send</button>
        </div>
    </div>

    <!-- Chatbot JS -->
   <script>
    function toggleChat() {
        const chat = document.getElementById('chatbot-container');
        chat.style.display = (chat.style.display === 'none' || chat.style.display === '') ? 'flex' : 'none';
    }

    function sendMessage() {
        const input = document.getElementById("userInput");
        const message = input.value.trim();
        if (!message) return;

        const chatBox = document.getElementById('chatbot-messages');
        chatBox.innerHTML += '<div class="user-message"><b>You:</b> ' + message + '</div>';
        input.value = '';

        // 👇 Send the message to servlet using AJAX
        fetch('ChatBotServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'message=' + encodeURIComponent(message)
        })
        .then(response => response.text())
        .then(reply => {
            chatBox.innerHTML += '<div class="bot-message"><b>Bot:</b> ' + reply + '</div>';
            chatBox.scrollTo({
                top: chatBox.scrollHeight,
                behavior: 'smooth'
            });
        })
        .catch(err => {
            chatBox.innerHTML += '<div class="bot-message"><b>Bot:</b> Sorry, there was an error fetching the reply.</div>';
        });
    }
</script>



<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"></script>

</body>     
</html>
