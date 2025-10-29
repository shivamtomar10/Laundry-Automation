/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package newpackage;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import dao.StudentDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpSession;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Collections;
import model.Student;


/**
 *
 * @author Shivam
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
    private static final String CLIENT_ID = "885385130463-4bgn4sufqhjk1en1qjj7cibskg857j5b.apps.googleusercontent.com";

    private final HttpTransport transport = new NetHttpTransport();
    private final JsonFactory jsonFactory = new JacksonFactory();
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
            out.println("<title>Servlet LoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath() + "</h1>");
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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get the ID token sent from client (usually from frontend JS)
        String idTokenString = request.getParameter("idtoken");

        if (idTokenString == null || idTokenString.isEmpty()) {
            out.println("<h3>Login failed: Invalid ID token!</h3>");
            out.println("<p>Redirecting back to login page...</p>");
            response.setHeader("Refresh", "3; URL=index.html"); // redirect after 3 seconds
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();

//                String userId = payload.getSubject();
                String email = payload.getEmail();
//                String password = (String) payload.get("password");
                if (!email.toLowerCase().endsWith("@vitstudent.ac.in")) {
                    request.setAttribute("error",
                            "Login failed: please use your vitstudent.ac.in email.");
                    request.getRequestDispatcher("studentLogin.jsp")
                           .forward(request, response);
                    return;
                }
                // Store user info in session
//                  request.getSession().setAttribute("studentId", userId);
                 

        
            
                
        try {
                Student student = StudentDao.getStudentByEmail(email); // new DAO method
                if (student != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("student", student);
                    RequestDispatcher rd = request.getRequestDispatcher("studentDashboard.jsp");
                    rd.forward(request, response);
                } else {
                        // Optional: create a new student record if it doesn't exist
                        // Or show error
                        request.setAttribute("error", "No student found with this email.");
                        request.getRequestDispatcher("studentLogin.jsp").forward(request, response);
                }
        }catch( ServletException | IOException e){
            throw e;
        }       catch (SQLException ex) { 
                    System.getLogger(LoginServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                } 
            } else {
                out.println("<h3>Login failed: Invalid ID token!</h3>");
                out.println("<p>Redirecting back to login page...</p>");
                response.setHeader("Refresh", "3; URL=index.html"); // redirect after 3 seconds
            }
        } catch (GeneralSecurityException e) {
            out.println("<h3>Login failed: Invalid ID token!</h3>");
            out.println("<p>Redirecting back to login page...</p>");
            response.setHeader("Refresh", "3; URL=index.html"); // redirect after 3 seconds
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
