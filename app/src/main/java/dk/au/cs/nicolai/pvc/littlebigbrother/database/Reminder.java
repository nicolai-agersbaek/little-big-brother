package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.Calendar;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.LittleBigBrotherException;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

/**
 * Created by Nicolai on 27-09-2015.
 */
@ParseClassName("Reminder")
public class Reminder extends AbstractReminder {

    public Reminder() {

    }

    protected Reminder(ReminderType reminderType) {

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            setOwner(currentUser);
        } else {
            Log.error(this, "Cannot create Reminder: User not logged in.");
            //throw new LittleBigBrotherException.UserNotLoggedIn("Cannot create Reminder");
        }
    }

    public static final class Location extends ReminderWithRadius {
        private static final String REMINDER_POSITION_ATTRIBUTE = "position";

        Location() {
            super(ReminderType.LOCATION);
        }

        public final ParseGeoPoint getPosition() {
            return getParseGeoPoint(REMINDER_POSITION_ATTRIBUTE);
        }

        public final void setPosition(ParseGeoPoint position) {
            put(REMINDER_POSITION_ATTRIBUTE, position);
        }

        public final String typeDetails() {
            ParseGeoPoint position = getPosition();
            return position.getLatitude() + ";" + position.getLongitude();
        }

        public final boolean equalsByValue(Reminder otherReminder) {
            if (!super.equalsByValue(otherReminder)) {
                return false;
            }

            Reminder.Location other = (Reminder.Location) otherReminder;

            return (getPosition().equals(other.getPosition()));
        }
    }

    public static final class TargetUser extends ReminderWithRadius {
        private static final String REMINDER_TARGET_USER_ATTRIBUTE = "target";

        TargetUser() throws
                LittleBigBrotherException.UserNotLoggedIn,
                LittleBigBrotherException.ReminderAlreadyBound {

            super(ReminderType.TARGET_USER);
        }

        public final ParseUser getTargetUser() {
            return getParseUser(REMINDER_TARGET_USER_ATTRIBUTE);
        }

        public final void setTargetUser(ParseUser targetUser) {
            put(REMINDER_TARGET_USER_ATTRIBUTE, targetUser);
        }

        public final String typeDetails() {
            return getTargetUser().getUsername();
        }

        public final boolean equalsByValue(Reminder otherReminder) {
            if (!super.equalsByValue(otherReminder)) {
                return false;
            }

            Reminder.TargetUser other = (Reminder.TargetUser) otherReminder;

            return (getTargetUser().equals(other.getTargetUser()));
        }
    }

    public static final class DateTime extends Reminder {
        private static final String REMINDER_DATE_ATTRIBUTE = "date";

        DateTime() throws
                LittleBigBrotherException.UserNotLoggedIn,
                LittleBigBrotherException.ReminderAlreadyBound {

            super(ReminderType.TARGET_USER);
        }

        public final Calendar getDate() {
            return (Calendar) get(REMINDER_DATE_ATTRIBUTE);
        }

        public final void setDate(Calendar date) {
            put(REMINDER_DATE_ATTRIBUTE, date);
        }

        public final String typeDetails() {
            return getDate().toString();
        }

        public final boolean equalsByValue(Reminder otherReminder) {
            if (!super.equalsByValue(otherReminder)) {
                return false;
            }

            Reminder.DateTime other = (Reminder.DateTime) otherReminder;

            return (getDate() == other.getDate());
        }
    }

    public String typeDetails() {
        return null;
    }

    public boolean equalsByValue(Reminder otherReminder) {
        return
                (type() == otherReminder.type())
                && (getTitle().equals(otherReminder.getTitle()))
                && (getDescription().equals(otherReminder.getDescription()))
                && (getExpires() == otherReminder.getExpires());
    }
}