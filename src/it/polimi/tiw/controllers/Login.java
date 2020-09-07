package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.sql.Connection;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "Login", urlPatterns = "/login")
public class Login extends HttpServlet {
    TemplateEngine templateEngine;

    @Override
    public void init(){
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email=request.getParameter("email");
        String password=request.getParameter("password");
        try {
            UserDAO userDAO = new UserDAO(ConnectionHandler.getConnection(getServletContext()));
            User user = userDAO.getUserInfo(email, password);
            if (user==null){
                WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
                session.setAttribute("loginError", "Login Error");
                templateEngine.process("login.html", webContext, response.getWriter());
            }
            else {
                session.setAttribute("user", user);
                session.removeAttribute("loginError");
                response.sendRedirect("/home");
            }
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
        webContext.setVariable("loggedIn", false);
        session.removeAttribute("loginError");
        templateEngine.process("login.html", webContext, response.getWriter());
    }
}
