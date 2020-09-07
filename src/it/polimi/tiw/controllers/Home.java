package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.MeetingDAO;
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
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@WebServlet(name = "Home", urlPatterns ="/home")
public class Home extends HttpServlet {
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
        HttpSession session = request.getSession();
        Meeting meeting = new Meeting();
        meeting.setTopic(request.getParameter("topic"));
        meeting.setSpeakerId(((User) session.getAttribute("user")).getId());
        String dateString = request.getParameter("date");
        try {
            java.util.Date dateTemp = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            meeting.setDate(new Date(dateTemp.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String startTime = request.getParameter("startTime");
        String endTime =  request.getParameter("endTime");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            long ms = simpleDateFormat.parse(startTime).getTime();
            long ms2 = simpleDateFormat.parse(endTime).getTime();
            meeting.setStartTime(new Time(ms));
            meeting.setEndTime(new Time(ms2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        meeting.setCapacity(Integer.parseInt(request.getParameter("capacity")));
        session.setAttribute("meetingToCreate", meeting);
        response.sendRedirect("/invitations");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            MeetingDAO meetingDAO = new MeetingDAO(ConnectionHandler.getConnection(getServletContext()));
            User user = (User) session.getAttribute("user");
            ArrayList<Meeting> meetingsCreated=meetingDAO.meetingsCreated(user.getId());
            ArrayList<Meeting> meetingsInvitations= meetingDAO.meetingsInvitations(user.getId());
            session.setAttribute("meetingsCreated", meetingsCreated);
            session.setAttribute("meetingsInvitations", meetingsInvitations);
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
        templateEngine.process("home.html", webContext, response.getWriter());
    }
}
