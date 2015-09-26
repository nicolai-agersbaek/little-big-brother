package dk.au.cs.nicolai.pvc.littlebigbrother;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Collections;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

/**
 * Created by Nicolai on 25-09-2015.
 */
public final class NetworkListItem {
    private String networkName;
    private ParseUser pairedUser;

    public NetworkListItem(String networkName) {
        this.networkName = networkName;

        // DEBUG
        this.pairedUser = ParseUser.getCurrentUser();
    }

    public NetworkListItem(String networkName, ParseUser pairedUser) {
        this(networkName);
        this.pairedUser = pairedUser;
    }

    public String getNetworkName() {
        return networkName;
    }

    public ParseUser getPairedUser() {
        return pairedUser;
    }

    public void setPairedUser(ParseUser user) {
        pairedUser = user;
    }

    public boolean isPaired() {
        return (pairedUser != null);
    }

    public void pairUserDebug(ParseUser user) {
        pairedUser = user;
    }

    public void unpairUserDebug() {
        pairedUser = null;
    }

    public void pairUser(final ParseUser user) {
        // Perform database update
        user.addUnique(LittleBigBrother.DB.USER_PAIRED_DEVICES_ATTRIBUTE, networkName);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.debug(this, "Successfully paired " + user.getUsername() + " with " + networkName);
                    pairedUser = user;
                } else {
                    Log.error(this, "Error adding network association to user: " + e.getMessage());
                }
            }
        });
    }

    public void unpairUser() {
        // Perform database update
        pairedUser.removeAll(LittleBigBrother.DB.USER_PAIRED_DEVICES_ATTRIBUTE, Collections.singletonList(networkName));
        pairedUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.debug(this, "Successfully unpaired " + pairedUser.getUsername() + " from " + networkName);
                    pairedUser = null;
                } else {
                    Log.error(this, "Error adding network association to user: " + e.getMessage());
                }
            }
        });
    }
}
