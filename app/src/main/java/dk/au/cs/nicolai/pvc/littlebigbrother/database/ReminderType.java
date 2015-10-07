package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

/**
 * Created by Nicolai on 01-10-2015.
 */
public enum ReminderType {
    LOCATION("LOCATION", LittleBigBrother.Icons.REMINDER_ICON_LOCATION),
    TARGET_USER("TARGET_USER", LittleBigBrother.Icons.REMINDER_ICON_TARGET_USER),
    DATE_TIME("DATE_TIME", LittleBigBrother.Icons.REMINDER_ICON_DATE_TIME);

    private String value;
    private GoogleMaterial.Icon typeIcon;

    ReminderType(String value, GoogleMaterial.Icon typeIcon) {
        this.value = value;
        this.typeIcon = typeIcon;
    }

    public static ReminderType fromTypeString(String typeString) {
        switch (typeString) {
            case "LOCATION":
                return LOCATION;
            case "TARGET_USER":
                return TARGET_USER;
            case "DATE_TIME":
                return DATE_TIME;
        }

        return null;
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
