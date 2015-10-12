package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

import dk.au.cs.nicolai.pvc.littlebigbrother.ApplicationController;
import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;
import dk.au.cs.nicolai.pvc.littlebigbrother.exception.UserNotLoggedInException;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.SimpleDateTime;

/**
 * Created by Nicolai on 01-10-2015.
 */
public abstract class AbstractReminder extends ParseObject {
    private boolean isBound = false;
    protected boolean fresh = true;

    private static final String REMINDER_OWNER_ATTRIBUTE    = "owner";
    private static final String REMINDER_TYPE_ATTRIBUTE     = "type";
    private static final String REMINDER_TITLE_ATTRIBUTE    = "title";
    private static final String REMINDER_DESCRIPTION_ATTRIBUTE = "description";

    private static final String REMINDER_EXPIRES_ATTRIBUTE   = "expires";

    private int id;

    protected AbstractReminder() {}

    protected AbstractReminder(ReminderType type) throws UserNotLoggedInException {

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            setOwner(currentUser);
            setType(type);
            id = ApplicationController.getReminderId();
        } else {
            throw new UserNotLoggedInException("Cannot create Reminder");
        }
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public abstract String typeDetails();

    protected final ParseUser getOwner() {
        return getParseUser(REMINDER_OWNER_ATTRIBUTE);
    }

    protected final boolean setOwner(ParseUser owner) {
        fresh = false;

        if (!isBound) {
            put(REMINDER_OWNER_ATTRIBUTE, owner);
            isBound = true;
            return true;
        } else {
            return false;
        }
    }

    private final void setType(ReminderType type) {
        put(REMINDER_TYPE_ATTRIBUTE, type.value());

        fresh = false;
    }

    public final ReminderType type() {
        return ReminderType.fromTypeString(getString(REMINDER_TYPE_ATTRIBUTE));
    }

    public final GoogleMaterial.Icon icon() {
        return type().icon();
    }

    public final String getTitle() {
        return getString(REMINDER_TITLE_ATTRIBUTE);
    }

    public final void setTitle(String name) {
        put(REMINDER_TITLE_ATTRIBUTE, name);

        fresh = false;
    }

    public final String getDescription() {
        return getString(REMINDER_DESCRIPTION_ATTRIBUTE);
    }

    public final void setDescription(String description) {
        put(REMINDER_DESCRIPTION_ATTRIBUTE, description);

        fresh = false;
    }

    public final SimpleDateTime getExpires() {
        Date expires = getDate(REMINDER_EXPIRES_ATTRIBUTE);

        if (expires != null) {
            return new SimpleDateTime(getDate(REMINDER_EXPIRES_ATTRIBUTE));
        }

        return null;
    }

    public final void setExpires(SimpleDateTime date) {
        put(REMINDER_EXPIRES_ATTRIBUTE, date.asDate());

        fresh = false;
    }

    public final String getExpiresInString() {
        SimpleDateTime expires = getExpires();

        if (expires == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(expires.asDate());

        long diff = cal2.getTimeInMillis() - cal.getTimeInMillis();

        if (diff < 0) {
            return null;
        }

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
