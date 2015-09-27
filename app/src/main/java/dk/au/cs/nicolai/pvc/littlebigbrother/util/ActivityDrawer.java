package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.ParseUser;

import dk.au.cs.nicolai.pvc.littlebigbrother.ApplicationController;

/**
 * Created by Nicolai on 27-09-2015.
 */
public class ActivityDrawer {

    private ActivityDrawer() {}

    public static Drawer build(AppCompatActivity activity) {
        // Only build Drawer if user is logged in
        if (ParseUser.getCurrentUser() != null) {
            // User is logged in, we can show the Drawer
            return buildDrawer(activity);
        } else {
            // User is NOT logged in, do not show a Drawer
            return null;
        }
    }

    private static Drawer buildDrawer(final AppCompatActivity activity) {
        return new DrawerBuilder()
                .withActivity(activity)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        ApplicationController.DrawerItem.MAP,
                        ApplicationController.DrawerItem.WIFI,
                        //ApplicationController.DrawerItem.REMINDERS,
                        new DividerDrawerItem(),
                        ApplicationController.DrawerItem.LOGOUT
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        ApplicationDrawerItem item = (ApplicationDrawerItem) drawerItem;

                        Log.debug(this, "Clicked ApplicationDrawerItem: " + item.type);



                        item.onClicked(activity);

                        return true;
                    }
                })
                .build();
    }
}
