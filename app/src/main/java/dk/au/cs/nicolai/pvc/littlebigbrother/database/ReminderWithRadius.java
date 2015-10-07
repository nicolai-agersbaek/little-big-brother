package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import dk.au.cs.nicolai.pvc.littlebigbrother.exception.UserNotLoggedInException;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

/**
 * Created by Nicolai on 04-10-2015.
 */
public abstract class ReminderWithRadius extends Reminder {
    private static final String REMINDER_RADIUS_ATTRIBUTE   = "radius";

    public ReminderWithRadius(ReminderType reminderType) throws UserNotLoggedInException {
        super(reminderType);
    }

    public final Integer getRadius() {
        return (getNumber(REMINDER_RADIUS_ATTRIBUTE) != null
                ? (Integer) getNumber(REMINDER_RADIUS_ATTRIBUTE)
                : null);
    }

    public final void setRadius(Integer radius) {
        put(REMINDER_RADIUS_ATTRIBUTE, radius);
    }

    public boolean equalsByValue(Reminder otherReminder) {
        if (!super.equalsByValue(otherReminder)) {
            return false;
        }

        ReminderWithRadius other = (ReminderWithRadius) otherReminder;

        return (getRadius().equals(other.getRadius()));
    }

    public boolean valid() {
        boolean radiusValid = (getRadius() != null);

        if (!radiusValid) {
            Log.error(this, "Radius is NULL");
        }

        return super.valid() && radiusValid;
    }
}
