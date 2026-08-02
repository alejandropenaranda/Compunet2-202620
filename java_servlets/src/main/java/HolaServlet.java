import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/saludo")
public class HolaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        String usuario = req.getParameter("nombre");
        out.println("<h1>Hola " +
                (usuario != null ? usuario : "Invitado") + "!</h1>");
    }
    
}