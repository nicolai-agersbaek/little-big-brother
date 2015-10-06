package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import java.util.Calendar;

/**
 * Created by Nicolai on 06-10-2015.
 */
public final class SimpleDateTime {

    public static final String DATE_SEPARATOR = "/";
    public static final String TIME_SEPARATOR = ":";
    public static final String DATE_TIME_SEPARATOR = " - ";

    private int year;
    private int month;
    private int day;

    private int hour;
    private int minute;

    public SimpleDateTime() {}

    public SimpleDateTime(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public SimpleDateTime(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public SimpleDateTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public SimpleDateTime(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setDate(int year, int month, int day) {
        setYear(year);
        setMonth(month);
        setDay(day);
    }

    public void setDate(SimpleDateTime dateTime) {
        setDate(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
    }

    public void setTime(int hour, int minute) {
        setHour(hour);
        setMinute(minute);
    }

    public void setTime(SimpleDateTime dateTime) {
        setTime(dateTime.getHour(), dateTime.getMinute());
    }

    public String dateString() {
        return day + DATE_SEPARATOR + (month + 1) + DATE_SEPARATOR + year;
    }

    public String timeString() {
        return hour + TIME_SEPARATOR + (minute < 10 ? "0" + minute : minute);
    }

    @Override
    public String toString() {
        return dateString() + DATE_TIME_SEPARATOR + timeString();
    }
}
