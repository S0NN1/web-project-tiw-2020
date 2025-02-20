package it.polimi.tiw.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/LoggedInChecker")
public class LoggedInChecker implements Filter {

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        System.out.println("Started login checker filter");

        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        HttpSession session = request.getSession();
        if(session.isNew() || session.getAttribute("user")==null) {
            response.sendRedirect("/login");
            return;
        }
        chain.doFilter(req, resp);
    }

}