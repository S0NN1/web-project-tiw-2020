package it.polimi.tiw.beans;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;

public class Meeting implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private Date date;

    private Time time;

    private Duration duration;

    private int capacity;

    private int userID;

    private ArrayList<Integer> partecipants;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<Integer> getPartecipants() {
        return partecipants;
    }

    public void setPartecipants(ArrayList<Integer> partecipants) {
        this.partecipants = partecipants;
    }
}
