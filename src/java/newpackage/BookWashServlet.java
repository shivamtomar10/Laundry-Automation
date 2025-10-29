/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package newpackage;

//import dao.WashDao;
//import model.Wash;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import dao.LaundryDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//import javax.servlet.http.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import javax.imageio.ImageIO;
import model.LaundryOrder;

/**
 *
 * @author Shivam
 */
@WebServlet(name = "BookWashServlet", urlPatterns = {"/BookWashServlet"})
public class BookWashServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet BookWashServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BookWashServlet at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession();
        model.Student student = (model.Student) session.getAttribute("student");
        if (student == null) {
            response.sendRedirect("studentLogin.jsp");
            return;
        }

        try {
            LaundryDao dao = new LaundryDao();

            // Create LaundryOrder object
            LaundryOrder order = new LaundryOrder();
            order.setStudentId(student.getId());
            order.setServiceType("Standard"); // or get from request
            order.setBookingDate(new Timestamp(System.currentTimeMillis()));
            order.setStatus("Pending");
            order.setQrGenerated(false);
            order.setFree(true); // optional, can check remaining free washes
            order.setAmount(0.0); // optional, calculate if paid

            // Insert into laundry_orders table
            int orderId=dao.addLaundryOrder(order); // you need to implement insertOrder() in LaundryDao
            order.setOrderId(orderId);
            // Generate QR code containing orderId and studentId
            String qrText = "orderId:" + order.getOrderId() + ";studentId:" + student.getId();
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
            // Store QR in session to display on next page
            session.setAttribute("qrImage", base64Image);
            session.setAttribute("orderId", order.getOrderId());
            
            order.setQrBase64(base64Image);
            
            response.sendRedirect("showQRCode.jsp");

        } catch (WriterException | IOException e) {
            response.getWriter().println("⚠️ Error: " + e.getMessage());
        } catch (SQLException ex) {
            System.getLogger(BookWashServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for booking laundry orders and generating QR code";
    }

}
