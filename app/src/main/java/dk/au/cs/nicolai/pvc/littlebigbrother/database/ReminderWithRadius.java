package dk.au.cs.nicolai.pvc.littlebigbrother.database;

/**
 * Created by Nicolai on 04-10-2015.
 */
public abstract class ReminderWithRadius extends Reminder {
    private static final String REMINDER_RADIUS_ATTRIBUTE   = "radius";

    public ReminderWithRadius(ReminderType reminderType) {
        super(reminderType);
    }

    public final Integer getRadius() {
        return (Integer) getNumber(REMINDER_RADIUS_ATTRIBUTE);
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
}
