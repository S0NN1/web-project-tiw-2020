package it.polimi.tiw.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.tiw.beans.Meeting;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "MeetingDAO")
public class MeetingDAO extends HttpServlet {
    private final Connection connection;

    public MeetingDAO(Connection connection) {
        this.connection = connection;
    }

    public void registerMeeting(String topic, int userId, java.sql.Date date, Time startTime, Time endTime, int capacity, ArrayList<Integer> list) throws SQLException {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        String query = "INSERT INTO meeting (topic, speakerid, date, start_time, end_time, capacity, participants_list ) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, topic);
            preparedStatement.setInt(2, userId);
            preparedStatement.setDate(3, date);
            preparedStatement.setTime(4, startTime);
            preparedStatement.setTime(5, endTime);
            preparedStatement.setInt(6, capacity);
            preparedStatement.setString(7, json);
            preparedStatement.executeUpdate();
        }
    }

    public ArrayList<Meeting> meetingsInvitations(int id) throws SQLException {
        ArrayList<Meeting> meetingInvitations = new ArrayList<>();
        String query = "SELECT * FROM meeting WHERE date>? OR (date=? AND start_time>=?)";
        Date date = new Date();
        java.sql.Date date1 = new java.sql.Date(date.getTime());
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, date1);
            preparedStatement.setDate(2, date1);
            preparedStatement.setTime(3, new Time(System.currentTimeMillis()));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Meeting meeting = new Meeting();
                    meeting.setId(resultSet.getInt("idmeeting"));
                    meeting.setSpeakerId(resultSet.getInt("speakerid"));
                    meeting.setTopic(resultSet.getString("topic"));
                    meeting.setDate(resultSet.getDate("date"));
                    meeting.setStartTime(resultSet.getTime("start_time"));
                    meeting.setEndTime(resultSet.getTime("end_time"));
                    meeting.setCapacity(resultSet.getInt("capacity"));
                    Gson gson = new Gson();
                    ArrayList<Integer> participants = gson.fromJson(resultSet.getString("participants_list"), new TypeToken<List<Integer>>() {
                    }.getType());
                    if (participants.contains(id)) {
                        meetingInvitations.add(meeting);
                    }
                }
            }
            return meetingInvitations;
        }
    }

    public ArrayList<Meeting> meetingsCreated(int id) throws SQLException {
        ArrayList<Meeting> meetingList = new ArrayList<>();
        Date date = new Date();
        java.sql.Date date1 = new java.sql.Date(date.getTime());
        String query = "SELECT * FROM meeting WHERE speakerid=? AND (date>? OR (date=? AND start_time>=?))";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setDate(2, date1);
            preparedStatement.setDate(3, date1);
            preparedStatement.setTime(4, new Time(System.currentTimeMillis()));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Meeting meeting = new Meeting();
                    meeting.setId(resultSet.getInt("idmeeting"));
                    meeting.setSpeakerId(resultSet.getInt("speakerid"));
                    meeting.setTopic(resultSet.getString("topic"));
                    meeting.setDate(resultSet.getDate("date"));
                    meeting.setStartTime(resultSet.getTime("start_time"));
                    meeting.setEndTime(resultSet.getTime("end_time"));
                    meeting.setCapacity(resultSet.getInt("capacity"));
                    meetingList.add(meeting);
                }
            }
            return meetingList;
        }
    }
}

