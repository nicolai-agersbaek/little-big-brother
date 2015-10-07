package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import dk.au.cs.nicolai.pvc.littlebigbrother.exception.UserNotLoggedInException;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.SimpleDateTime;

/**
 * Created by Nicolai on 27-09-2015.
 */
@ParseClassName("Reminder")
public class Reminder extends AbstractReminder {

    public Reminder() {}

    protected Reminder(ReminderType type) throws UserNotLoggedInException {
        super(type);
    }

    @ParseClassName("Reminder.Location")
    public static final class Location extends ReminderWithRadius {
        private static final String REMINDER_POSITION_ATTRIBUTE = "position";

        public Location() throws
                UserNotLoggedInException {
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

    @ParseClassName("Reminder.TargetUser")
    public static final class TargetUser extends ReminderWithRadius {
        private static final String REMINDER_TARGET_USER_ATTRIBUTE = "target";

        public TargetUser() throws
                UserNotLoggedInException {

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

    @ParseClassName("Reminder.DateTime")
    public static final class DateTime extends Reminder {
        private static final String REMINDER_DATE_ATTRIBUTE = "date";

        public DateTime() throws
                UserNotLoggedInException {

            super(ReminderType.TARGET_USER);
        }

        public final SimpleDateTime getDate() {
            return new SimpleDateTime(getDate(REMINDER_DATE_ATTRIBUTE));
        }

        public final void setDate(SimpleDateTime date) {
            put(REMINDER_DATE_ATTRIBUTE, date.asDate());
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