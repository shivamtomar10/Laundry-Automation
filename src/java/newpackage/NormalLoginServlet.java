/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package newpackage;

import dao.StudentDao;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.*;
import model.Student;

/**
 *
 * @author Shivam
 */
@WebServlet(name = "NormalLoginServlet", urlPatterns = {"/NormalLoginServlet"})
public class NormalLoginServlet extends HttpServlet {

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
            out.println("<title>Servlet NoramlLoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NoramlLoginServlet at " + request.getContextPath() + "</h1>");
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
        String email = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();

        
            try {
                Student student = StudentDao.getStudentByEmailPassword(email, password);
                if (student != null) {
                    // Login success
                    HttpSession session = request.getSession();
                    session.setAttribute("student", student);

                    // Forward to dashboard
                    RequestDispatcher rd = request.getRequestDispatcher("studentDashboard.jsp");
                    rd.forward(request, response);
                } else {
                // Login failed
                request.setAttribute("error", "Invalid email or password");
                RequestDispatcher rd = request.getRequestDispatcher("studentLogin.jsp");
                rd.forward(request, response);
                }
            }
             catch (SQLException ex) {
//                 response.getWriter().println("I am inside");
                 response.getWriter().println("Database error: " + ex.getMessage());
                 
                 
                System.getLogger(NormalLoginServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
