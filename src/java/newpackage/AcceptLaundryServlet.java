 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package newpackage;

import dao.LaundryDao;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.Employee;
import model.LaundryOrder;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import jakarta.servlet.annotation.MultipartConfig;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 *
 * @author Shivam
 */
@WebServlet(name = "AcceptLaundryServlet", urlPatterns = {"/AcceptLaundryServlet"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 5 * 1024 * 1024,   // 5MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class AcceptLaundryServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
//    HttpServlet response;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LaundryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LaundryServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        Employee emp = (Employee) session.getAttribute("employee");

        if (emp == null) {
            response.getWriter().println("⚠️ Please log in as an employee to accept orders.");
            return;
        }

        LaundryDao dao = new LaundryDao();

        try (PrintWriter out = response.getWriter()) {

            // 1️⃣ Handle QR scanned from camera
            String qrData = request.getParameter("qrData");
            if (qrData != null && !qrData.isEmpty()) {
                // qrData = "orderId:19;studentId:7"
                String[] parts = qrData.split(";");
                int orderId = Integer.parseInt(parts[0].split(":")[1].trim());
                int studentId = Integer.parseInt(parts[1].split(":")[1].trim());

                processOrderAcceptance(orderId, studentId, emp, dao, out);
                return;
            }

            // 2️⃣ Handle QR uploaded as image
            Part qrPart = request.getPart("qrImage");
            if (qrPart != null && qrPart.getSize() > 0) {

                // Ensure uploads folder exists
                String uploadDir = getServletContext().getRealPath("") + File.separator + "uploads";
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                // Save uploaded file
                String fileName = Paths.get(qrPart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = uploadDir + File.separator + fileName;
                qrPart.write(uploadPath);

                // Decode QR image to get orderId and studentId
                int[] ids = decodeQR(uploadPath);
                int orderId = ids[0];
                int studentId = ids[1];

                processOrderAcceptance(orderId, studentId, emp, dao, out);

                // Delete temporary file
                new File(uploadPath).delete();
                return;
            }

            out.println("⚠️ No QR data received.");
        } catch (Exception e) {
            response.getWriter().println("⚠️ Error: " + e.getMessage());
        }
    }

    private void processOrderAcceptance(int orderId, int studentId, Employee emp, LaundryDao dao, PrintWriter out) throws Exception {
        LaundryOrder order = dao.getOrderById(orderId);
        if (order != null && "Pending".equals(order.getStatus()) && order.getStudentId() == studentId) {
            order.setStatus("Accepted");
            order.setEmployeeId(emp.getEmployeeId());
            dao.updateOrder(order);
            dao.decreaseRemainingWashes(order.getStudentId());
            out.println("✅ Order #" + orderId + " accepted by " + emp.getName());
        } else {
            out.println("⚠️ Order not found, already processed, or student mismatch.");
        }
    }

    // QR decoding method using ZXing
    private int[] decodeQR(String filePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(filePath));
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);

        // QR text: "orderId:19;studentId:7"
        String qrText = result.getText();
        int orderId = 0, studentId = 0;
        String[] parts = qrText.split(";");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue[0].trim().equalsIgnoreCase("orderId")) {
                orderId = Integer.parseInt(keyValue[1].trim());
            } else if (keyValue[0].trim().equalsIgnoreCase("studentId")) {
                studentId = Integer.parseInt(keyValue[1].trim());
            }
        }
        return new int[]{orderId, studentId};
    }

    @Override
    public String getServletInfo() {
        return "AcceptLaundryServlet - accepts laundry_orders via QR scan or upload";
    }

}
