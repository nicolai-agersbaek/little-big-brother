package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * Created by Nicolai on 22-09-2015.
 */
public final class GoogleApiClientFactory {
    private GoogleApiClientFactory() {}

    public static class LocationServices {
        private LocationServices() {}

        public static <T extends Context & ConnectionCallbacks
                & OnConnectionFailedListener> GoogleApiClient build(T context) {
            // Create GoogleApiClient object for use with the UpdateUserPositionService.
            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(context)
                    .addOnConnectionFailedListener(context)
                    .addApiIfAvailable(com.google.android.gms.location.LocationServices.API)
                    .build();

            return mGoogleApiClient;
        }
    }
}
