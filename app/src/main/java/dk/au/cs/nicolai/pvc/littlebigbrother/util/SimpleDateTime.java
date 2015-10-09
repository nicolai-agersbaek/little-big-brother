package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nicolai on 06-10-2015.
 */
public final class SimpleDateTime {

    public static final String DATE_SEPARATOR = "/";
    public static final String TIME_SEPARATOR = ":";
    public static final String DATE_TIME_SEPARATOR = " - ";

    private Calendar calendar = Calendar.getInstance();

    private int year;
    private int month;
    private int day;

    private int hour;
    private int minute;

    public SimpleDateTime() {}

    public SimpleDateTime(Date date) {
        calendar.setTime(date);
        setDate(calendar);
        setTime(calendar);
    }

    public SimpleDateTime(int year, int month, int day, int hour, int minute) {
        setDate(year, month, day);
        setTime(hour, minute);
    }

    public SimpleDateTime(int year, int month, int day) {
        setDate(year, month, day);
    }

    public SimpleDateTime(int hour, int minute) {
        setTime(hour, minute);
    }

    public SimpleDateTime(Calendar calendar) {
        this.calendar = calendar;
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
        calendar.set(Calendar.YEAR, year);
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        calendar.set(Calendar.MONTH, month);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
        calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
        calendar.set(Calendar.MINUTE, minute);
    }

    public void setDate(int year, int month, int day) {
        setYear(year);
        setMonth(month);
        setDay(day);
    }

    public void setDate(SimpleDateTime dateTime) {
        setDate(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
    }

    public void setDate(Calendar calendar) {
        setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setDate(Date date) {
        calendar.setTime(date);
        setDate(calendar);
    }

    public void setTime(Date date) {
        calendar.setTime(date);
        setTime(calendar);
    }

    public void setTime(int hour, int minute) {
        setHour(hour);
        setMinute(minute);
    }

    public void setTime(SimpleDateTime dateTime) {
        setTime(dateTime.getHour(), dateTime.getMinute());
    }

    public void setTime(Calendar calendar) {
        setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public Date asDate() {
        return calendar.getTime();
    }

    public String dateString() {
        return day + DATE_SEPARATOR + (month + 1) + DATE_SEPARATOR + year;
    }

    public String timeString() {
        return hour + TIME_SEPARATOR + (minute < 10 ? "0" + minute : minute);
    }

    public long getTimeInMillis() {
        return calendar.getTimeInMillis();
    }

    public long getTimeInMillisUntilThis() {
        Calendar cal = Calendar.getInstance();

        return calendar.getTimeInMillis() - cal.getTimeInMillis();
    }

    @Override
    public String toString() {
        return dateString() + DATE_TIME_SEPARATOR + timeString();
    }
}
