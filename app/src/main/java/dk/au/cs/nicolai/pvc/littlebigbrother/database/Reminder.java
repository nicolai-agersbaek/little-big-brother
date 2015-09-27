package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Nicolai on 27-09-2015.
 */
@ParseClassName("Reminder")
public final class Reminder extends ParseObject {
    private static final String REMINDER_OWNER_ATTRIBUTE    = "owner";
    private static final String REMINDER_NAME_ATTRIBUTE     = "name";
    private static final String REMINDER_DATE_ATTRIBUTE     = "date";
    private static final String REMINDER_POSITION_ATTRIBUTE = "position";
    private static final String REMINDER_RADIUS_ATTRIBUTE   = "radius";
    private static final String REMINDER_TARGET_USER_ATTRIBUTE = "target";

    public Reminder() {
        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {
            put(REMINDER_OWNER_ATTRIBUTE, user);
        } else {
            //throw new LittleBigBrotherException(LittleBigBrotherExceptionCode.USER_NOT_LOGGED_IN, "Cannot create Reminder");
        }
    }

    public String getName() {
        return getString(REMINDER_NAME_ATTRIBUTE);
    }

    public void setName(String name) {
        put(REMINDER_NAME_ATTRIBUTE, name);
    }

    public Date getDate() {
        return getDate(REMINDER_DATE_ATTRIBUTE);
    }

    public void setDate(Date date) {
        put(REMINDER_DATE_ATTRIBUTE, date);
    }

    public ParseGeoPoint getPosition() {
        return getParseGeoPoint(REMINDER_POSITION_ATTRIBUTE);
    }

    public void setPosition(ParseGeoPoint position) {
        put(REMINDER_POSITION_ATTRIBUTE, position);
    }

    public Float getRadius() {
        return (Float) getNumber(REMINDER_RADIUS_ATTRIBUTE);
    }

    public void setRadius(Float radius) {
        put(REMINDER_RADIUS_ATTRIBUTE, radius);
    }

    public ParseUser getTargetUser() {
        return getParseUser(REMINDER_TARGET_USER_ATTRIBUTE);
    }

    public void setTargetUser(ParseUser targetUser) {
        put(REMINDER_TARGET_USER_ATTRIBUTE, targetUser);
    }
}