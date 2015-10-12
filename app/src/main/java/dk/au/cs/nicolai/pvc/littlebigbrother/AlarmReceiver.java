package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

/**
 * Created by nicolai on 10/9/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderTitle = intent.getStringExtra("reminderTitle");
        String reminderDescription = intent.getStringExtra("reminderDescription");
        int reminderId = intent.getIntExtra("reminderId", 0);

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_cast_dark)
                .setContentTitle(reminderTitle)
                .setContentText(reminderDescription);

        //ApplicationController AC = new ApplicationController();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationManager notificationManager = (NotificationManager) AC.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.info(this, "Reminder: " + reminderTitle);

        notificationManager.notify(reminderId, builder.build());

        //Toast.makeText(context, reminderTitle + ": " + reminderDescription, Toast.LENGTH_SHORT).show();
    }
}
