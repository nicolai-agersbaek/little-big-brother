package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by Nicolai on 04-10-2015.
 */
public final class DateTime {
    private static final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance();

    private static final String DATE_SEPARATOR = "/";
    private static final String TIME_SEPARATOR = ":";
    private static final String DATE_TIME_SEPARATOR = " - ";

    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    public static int getShortYear(Calendar calendar) {
        String year = String.valueOf(getYear(calendar));
        String truncated = year.substring(2);
        return Integer.getInteger(truncated);
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH);
    }

    public static int getDayOfMonth(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getDayOfWeek(Calendar calendar) {
        return dateFormatSymbols.getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)];
    }

    public static int getHour(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Calendar calendar) {
        return calendar.get(Calendar.MINUTE);
    }

    public static String getDateString(Calendar calendar) {
        return
                getDayOfMonth(calendar)
                + DATE_SEPARATOR
                + getMonth(calendar)
                + DATE_SEPARATOR
                + getShortYear(calendar);
    }

    public static String getTimeString(Calendar calendar) {
        return getHour(calendar) + TIME_SEPARATOR + getMinute(calendar);
    }

    public static String toString(Calendar calendar) {
        return getDateString(calendar) + DATE_TIME_SEPARATOR + getTimeString(calendar);
    }

    public static long difference(Calendar cal1, Calendar cal2) {
        return (cal1.getTimeInMillis() - cal2.getTimeInMillis());
    }
}
