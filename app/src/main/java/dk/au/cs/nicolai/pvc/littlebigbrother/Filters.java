package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.content.IntentFilter;

/**
 * Created by nicolai on 10/9/15.
 */
public interface Filters {
    IntentFilter GOOGLE_API_CLIENT_CONNECTED = new IntentFilter(LittleBigBrother.Events.GOOGLE_API_CLIENT_CONNECTED);
}
