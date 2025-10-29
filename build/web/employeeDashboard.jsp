<%-- 
    Document   : employeeDashboard
    Created on : 19 Oct 2025, 3:27:35 pm
    Author     : Shivam
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Employee"%>
<%
    // Make sure employee is logged in
    Employee emp = (Employee) session.getAttribute("employee");
    if (emp == null) {
        response.sendRedirect("employeeLogin.jsp");
        return;
    }
%> 
<!DOCTYPE html>
<html>
<head>
    <!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Employee Dashboard</title>
    <script src="https://unpkg.com/html5-qrcode@2.3.3/html5-qrcode.min.js" defer></script>
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: #f4f6f8;
            margin: 0;
            padding: 0;
            text-align: center;
        }
        header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 40px;
            background-color: #2E7D32;
            color: white;
        }
        header div {
            font-size: 22px;
            font-weight: 500;
        }
        header a {
            background-color: #f44336;
            color: white;
            padding: 8px 16px;
            font-size: 16px;
            border-radius: 6px;
            text-decoration: none;
            transition: 0.3s;
        }
        header a:hover {
            background-color: #d32f2f;
        }

        .btn {
            border: none;
            border-radius: 8px;
            padding: 10px 18px;
            font-size: 16px;
            cursor: pointer;
            transition: 0.3s;
        }
        .btn-success { background-color: #2E7D32; color: white; }
        .btn-success:hover { background-color: #1B5E20; }
        .btn-danger { background-color: #f44336; color: white; }
        .btn-danger:hover { background-color: #c62828; }

        .container {
            margin-top: 60px;
        }

        /* Modal Styling */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0; left: 0;
            width: 100%; height: 100%;
            background: rgba(0,0,0,0.6);
            z-index: 999;
            justify-content: center;
            align-items: center;
        }
        .modal-box {
            background: white;
            padding: 25px;
            border-radius: 12px;
            width: 400px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.25);
            text-align: center;
        }
        #scanner, #completedScanner {
            display: none;
            width: 300px;
            height: 300px;
            margin: 10px auto;
        }
    </style>
</head>

<body>
    <!-- Header -->
    <header>
        <div>Welcome, <%= emp.getName() %> — Employee Dashboard</div>
        <a href="logout.jsp">Logout</a>
    </header>

    <div class="container">
        <!-- Accept Order Section -->
        <button class="btn btn-success" onclick="openModal('acceptModal')">Accept Order</button>

        <!-- Completed Order Section -->
        <button class="btn btn-success" onclick="openModal('completedModal')">Completed Order</button>
    </div>

    <!-- Accept Order Modal -->
    <div id="acceptModal" class="modal-overlay">
        <div class="modal-box">
            <h3>Accept Order - Scan or Upload QR</h3>
            <button class="btn btn-success" onclick="startScan('scanner')">Scan QR Code</button>
            <div id="scanner"></div>
            <form id="uploadForm" enctype="multipart/form-data" method="post" action="AcceptLaundryServlet">
                <input type="hidden" name="orderId" value="1">
                <input type="file" name="qrImage" accept="image/*" required>
                <button type="submit" class="btn btn-success">Upload QR Screenshot</button>
            </form>  
            <br>
            <button class="btn btn-danger" onclick="closeModal('acceptModal')">Close</button>
        </div>
    </div>

    <!-- Completed Order Modal -->
    <div id="completedModal" class="modal-overlay">
        <div class="modal-box">
            <h3>Completed Order - Scan QR</h3>
            <button class="btn btn-success" onclick="startScan('completedScanner')">Scan QR Code</button>
            <div id="completedScanner"></div>
            <form id="completedUploadForm" enctype="multipart/form-data" method="post" action="verifyQR.jsp">
                <input type="file" name="qrImage" accept="image/*" required>
                <button type="submit" class="btn btn-success">Upload QR Screenshot</button>
            </form>
            <br>
            <button class="btn btn-danger" onclick="closeModal('completedModal')">Close</button>
        </div>
    </div>

    <script>
        let html5QrCode = null;

        function openModal(id) {
            document.getElementById(id).style.display = "flex";
        }

        function closeModal(id) {
            document.getElementById(id).style.display = "none";
            stopScan();
        }

        function startScan(scannerId) {
            const scannerDiv = document.getElementById(scannerId);
            scannerDiv.style.display = "block";
            html5QrCode = new Html5Qrcode(scannerId);

            html5QrCode.start(
               { facingMode: "environment" },
        { fps: 10, qrbox: 250 },
        (qrMessage) => {
            alert("✅ QR Detected: " + qrMessage);
            stopScan();

            // 👇 Determine which modal is active
            if (activeModal === "acceptModal") {
                handleAcceptOrder(qrMessage);
            } else if (activeModal === "completedModal") {
                handleCompletedOrder(qrMessage);
            }

            // Hide modals after scan
            document.querySelectorAll('.modal-overlay').forEach(m => m.style.display = 'none');
        }
            ).catch(err => console.error("Scanner start failed:", err));
        }

        function stopScan() {
            if (html5QrCode) {
                html5QrCode.stop().then(() => {
                    document.getElementById('scanner').style.display = "none";
                    document.getElementById('completedScanner').style.display = "none";
                }).catch(err => console.error("Failed to stop scanner:", err));
            }
        }
        // ✅ Send QR data to backend for Accept Order
    function handleAcceptOrder(qrMessage) {
        fetch("AcceptLaundryServlet?qrData=" + encodeURIComponent(qrMessage))
            .then(res => res.text())
            .then(data => alert("Server Response: " + data))
            .catch(err => alert("Error: " + err));
    }

    // ✅ Send QR data to backend for Completed Order
    function handleCompletedOrder(qrMessage) {
        fetch("verifyQR.jsp?qrData=" + encodeURIComponent(qrMessage))
            .then(res => res.text())
            .then(data => alert("Server Response: " + data))
            .catch(err => alert("Error: " + err));
    }
    </script>
</body>
</html>