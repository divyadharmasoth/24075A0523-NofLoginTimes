import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        boolean isValidUser = false;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to DB
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sample", "root", "root");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM studentDetails WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                isValidUser = true;
            }

            // Close resources
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            out.println("<p style='color:red;'>Database Error: " + e.getMessage() + "</p>");
            return;
        }

        if (isValidUser) {
            // Track login count using session
            HttpSession session = request.getSession();
            Integer count = (Integer) session.getAttribute("loginCount");

            if (count == null) {
                count = 1;
            } else {
                count++;
            }

            session.setAttribute("loginCount", count);
            session.setAttribute("username", username);

            out.println("<html><body>");
            out.println("<h2>Welcome, " + username + "!</h2>");
            out.println("<p>You have logged in " + count + " time(s).</p>");
            out.println("</body></html>");
        } else {
            out.println("<html><body>");
            out.println("<h3 style='color:red;'>Invalid username or password.</h3>");
            out.println("</body></html>");
        }
    }
}
