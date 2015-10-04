package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Calendar;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

/**
 * Created by Nicolai on 01-10-2015.
 */
public abstract class AbstractReminder extends ParseObject {
    private boolean isBound = false;

    private static final String REMINDER_OWNER_ATTRIBUTE    = "owner";
    private static final String REMINDER_TYPE_ATTRIBUTE     = "type";
    private static final String REMINDER_TITLE_ATTRIBUTE    = "title";
    private static final String REMINDER_DESCRIPTION_ATTRIBUTE = "description";

    private static final String REMINDER_EXPIRES_ATTRIBUTE   = "expires";

    public abstract String typeDetails();

    protected final ParseUser getOwner() {
        return getParseUser(REMINDER_OWNER_ATTRIBUTE);
    }

    protected final boolean setOwner(ParseUser owner) {
        if (!isBound) {
            put(REMINDER_OWNER_ATTRIBUTE, owner);
            isBound = true;
            return true;
        } else {
            return false;
        }
    }

    protected final void setType(ReminderType type) {
        put(REMINDER_TYPE_ATTRIBUTE, type);
    }

    public final ReminderType type() {
        return (ReminderType) get(REMINDER_TYPE_ATTRIBUTE);
    }

    public final GoogleMaterial.Icon icon() {
        return type().icon();
    }

    public final String getTitle() {
        return getString(REMINDER_TITLE_ATTRIBUTE);
    }

    public final void setTitle(String name) {
        put(REMINDER_TITLE_ATTRIBUTE, name);
    }

    public final String getDescription() {
        return getString(REMINDER_DESCRIPTION_ATTRIBUTE);
    }

    public final void setDescription(String description) {
        put(REMINDER_DESCRIPTION_ATTRIBUTE, description);
    }

    public final Calendar getExpires() {
        return (Calendar) get(REMINDER_EXPIRES_ATTRIBUTE);
    }

    public final void setExpires(Calendar date) {
        put(REMINDER_EXPIRES_ATTRIBUTE, date);
    }

    public final String getExpiresInString() {
        Calendar cal = Calendar.getInstance();
        long diff = DateTime.difference(cal, getExpires());

        cal.setTimeInMillis(diff);

        int years = cal.get(Calendar.YEAR) - 1970;
        int months = cal.get(Calendar.MONTH);
        int days = cal.get(Calendar.DAY_OF_YEAR);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);

        if (years > 0) {
            return years + " " + LittleBigBrother.Constants.Date.SHORT_YEAR_POSTFIX;
        }

        if (months > 0) {
            return months + " " + LittleBigBrother.Constants.Date.SHORT_MONTH_POSTFIX;
        }

        if (days > 0) {
            return days + " " + LittleBigBrother.Constants.Date.SHORT_DAY_POSTFIX;
        }

        if (hours > 0) {
            return hours + " " + LittleBigBrother.Constants.Date.SHORT_HOUR_POSTFIX;
        }

        if (minutes > 0) {
            return minutes + " " + LittleBigBrother.Constants.Date.SHORT_MINUTE_POSTFIX;
        }

        return null;
    }

    //public abstract Reminder clone();
}
