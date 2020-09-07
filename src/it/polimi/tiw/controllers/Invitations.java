package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.MeetingDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "Invitations", urlPatterns = "/invitations")
public class Invitations extends HttpServlet {
    String error = null;
    ArrayList<Integer> invitedUsers = new ArrayList<>();
    int attempts = 0;
    TemplateEngine templateEngine;

    @Override
    public void init() {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
        HttpSession session = request.getSession();
        invitedUsers = new ArrayList<>();
        ArrayList<User> users = (ArrayList<User>) session.getAttribute("users");
        Meeting meeting = (Meeting) session.getAttribute("meetingToCreate");
        int capacity = meeting.getCapacity();
        for (User user : users) {
            String checkbox = request.getParameter(Integer.toString(user.getId()));
            if (checkbox != null && checkbox.equals("on")) {
                invitedUsers.add(user.getId());
            }
        }
        int invitedUsersCount = invitedUsers.size();
        if (invitedUsersCount > capacity) {
            if (attempts < 3) {
                int temp = invitedUsersCount - capacity;
                error = "Exceeded maximum meeting capacity by " + temp;
                attempts++;
                webContext.setVariable("error", error);
                webContext.setVariable("invitedUsers", invitedUsers);
                templateEngine.process("invitations.html", webContext, response.getWriter());
            } else {
                session.setAttribute("users", null);
                invitedUsers = new ArrayList<>();
                error = null;
                attempts = 0;
                response.sendRedirect("/creation-failed");
            }
        } else if (invitedUsersCount == 0) {
            error = "Please select at least one participant";
            attempts++;
            webContext.setVariable("error", error);
            webContext.setVariable("invitedUsers", invitedUsers);
            templateEngine.process("invitations.html", webContext, response.getWriter());
        } else {
            try {
                MeetingDAO meetingDAO = new MeetingDAO(ConnectionHandler.getConnection(getServletContext()));
                meetingDAO.registerMeeting(meeting.getTopic(), ((User) session.getAttribute("user")).getId(), meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getCapacity(), invitedUsers);
                session.setAttribute("users", null);
                session.setAttribute("meetingToCreate", null);
                invitedUsers = new ArrayList<>();
                error = null;
                attempts = 0;
                response.sendRedirect("/home");
            } catch (IllegalAccessException | SQLException e) {
                e.printStackTrace();
            }

        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        ArrayList<User> users = null;
        try {
            UserDAO userDAO = new UserDAO(ConnectionHandler.getConnection(getServletContext()));
            users = userDAO.getUsers(((User)session.getAttribute("user")).getId());
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
        session.setAttribute("users", users);
        webContext.setVariable("error", error);
        webContext.setVariable("invitedUsers", invitedUsers);
        templateEngine.process("invitations.html", webContext, response.getWriter());
    }
}
