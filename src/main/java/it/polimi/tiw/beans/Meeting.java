package it.polimi.tiw.beans;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Date;
import java.time.Duration;

public class Meeting implements Serializable {

    private static final long serialVersionUID=1L;

    private String title;

    private Date date;

    private Time time;

    private Duration duration;

    private int capacity;

    public Meeting(){
    title="";
    date=null;
    time=null;
    duration=null;
    capacity=0;
    }

    public Meeting(String title, Date date, Time time, Duration duration, int capacity){
        this.title=title;
        this.date=date;
        this.time=time;
        this.duration=duration;
        this.capacity=capacity;
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
}
