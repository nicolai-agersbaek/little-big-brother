package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import android.app.Activity;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import dk.au.cs.nicolai.pvc.littlebigbrother.R;

/**
 * Created by Nicolai on 27-09-2015.
 */
public class ApplicationDrawerItem extends PrimaryDrawerItem {

    public final ITEM_TYPE type;
    private final ApplicationDrawerItemOnClickedCallback callback;

    private enum ITEM_TYPE {
        MAP, WIFI, REMINDERS, LOGOUT
    }

    private ApplicationDrawerItem(int nameResId, GoogleMaterial.Icon icon, ITEM_TYPE type, ApplicationDrawerItemOnClickedCallback callback) {
        super.withName(nameResId).withIcon(icon);
        this.type = type;
        this.callback = callback;
    }

    private ApplicationDrawerItem(int nameResId, ITEM_TYPE type, ApplicationDrawerItemOnClickedCallback callback) {
        super.withName(nameResId);
        this.type = type;
        this.callback = callback;
    }

    public static ApplicationDrawerItem MAP(ApplicationDrawerItemOnClickedCallback onClickedCallback) {
        return new ApplicationDrawerItem(R.string.drawer_item_map, GoogleMaterial.Icon.gmd_map, ITEM_TYPE.MAP, onClickedCallback);
    }

    public static ApplicationDrawerItem WIFI(ApplicationDrawerItemOnClickedCallback onClickedCallback) {
        return new ApplicationDrawerItem(R.string.drawer_item_wifi, GoogleMaterial.Icon.gmd_wifi, ITEM_TYPE.WIFI, onClickedCallback);
    }

    public static ApplicationDrawerItem REMINDERS(ApplicationDrawerItemOnClickedCallback onClickedCallback) {
        return new ApplicationDrawerItem(R.string.drawer_item_reminders, ITEM_TYPE.REMINDERS, onClickedCallback);
    }

    public static ApplicationDrawerItem LOGOUT(ApplicationDrawerItemOnClickedCallback onClickedCallback) {
        return new ApplicationDrawerItem(R.string.drawer_item_logout, ITEM_TYPE.LOGOUT, onClickedCallback);
    }

    public void onClicked(Activity activity) {
        callback.onClicked(activity);
    }
}
