/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package newpackage;

import dao.DBConnection;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.*;
import static java.util.regex.Pattern.matches;

/**
 *
 * @author Shivam
 */
@WebServlet(name = "ChatBotServlet", urlPatterns = {"/ChatBotServlet"})
public class ChatBotServlet extends HttpServlet {

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
            out.println("<title>Servlet ChatBotServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChatBotServlet at " + request.getContextPath() + "</h1>");
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
        String userMessage = request.getParameter("message").toLowerCase();
        String botReply = "I'm not sure I understand. You can ask me about laundry status, prices, or best day.";

        if (containsAny(userMessage, "status", "progress", "track", "update", "check")) {
            botReply = "You can check your laundry status in the 'Pending Orders' section of your dashboard.";
        } else if (containsAny(userMessage, "price", "cost", "rate", "charges", "fee", "how much")) {
            botReply = "Free For 10 Washes after that , Standard Wash: ₹50 | Express Wash: ₹80.";
        } else if (containsAny(userMessage, "book", "schedule", "reserve", "order", "make")) {
            botReply = "To book a laundry order, click on the 'Book Laundry' button on your dashboard.";
        } else if (containsAny(userMessage, "best day", "less crowded", "least crowded", "less busy", "least busy", "free day", "not busy", "quiet day")) {
            botReply = getLeastCrowdedDay();
        }

        response.setContentType("text/plain");
        response.getWriter().write(botReply);
    }
    private boolean containsAny(String input, String... keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String getLeastCrowdedDay() {
        String sql = "SELECT TO_CHAR(booking_date, 'Day') AS weekday, COUNT(*) AS total_orders " +
                     "FROM laundry_orders " +
                     "WHERE booking_date >= CURRENT_DATE " +
                     "AND booking_date < CURRENT_DATE + INTERVAL '7 days' " +
                     "GROUP BY TO_CHAR(booking_date, 'Day') " +
                     "ORDER BY total_orders ASC LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String day = rs.getString("weekday").trim();
                int count = rs.getInt("total_orders");
                return "Based on recent bookings, " + day + " is the least crowded day with only " + count + " orders.";
            } else {
                return "No upcoming bookings yet — any day is good to choose!";
            }

        } catch (Exception e) {
            return "Sorry, I couldn’t fetch the data right now.";
        }
    
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
