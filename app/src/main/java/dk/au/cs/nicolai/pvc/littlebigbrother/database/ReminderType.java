package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

/**
 * Created by Nicolai on 01-10-2015.
 */
public enum ReminderType {
    //LOCATION("LOCATION", (new ApplicationController()).getString(R.string.reminderIcon_Location)),

    LOCATION("LOCATION", LittleBigBrother.Icons.REMINDER_ICON_LOCATION),
    TARGET_USER("TARGET_USER", LittleBigBrother.Icons.REMINDER_ICON_TARGET_USER),
    DATE_TIME("DATE_TIME", LittleBigBrother.Icons.REMINDER_ICON_DATE_TIME);

    private String value;
    private GoogleMaterial.Icon typeIcon;

    ReminderType(String value, GoogleMaterial.Icon typeIcon) {
        this.value = LittleBigBrother.enumValue(this, value);
        this.typeIcon = typeIcon;
    }

    public String value() {
        return value;
    }

    public GoogleMaterial.Icon icon() {
        return typeIcon;
    }

    @Override
    public String toString() {
        return value();
    }
}
