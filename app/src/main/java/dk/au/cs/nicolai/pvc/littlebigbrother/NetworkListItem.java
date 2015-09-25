package dk.au.cs.nicolai.pvc.littlebigbrother;

import com.parse.ParseUser;

/**
 * Created by Nicolai on 25-09-2015.
 */
public final class NetworkListItem {
    private String networkName;
    private String pairedUsername;
    private ParseUser pairedUser;

    public NetworkListItem(String networkName) {
        this.networkName = networkName;
    }

    public NetworkListItem(String networkName, String pairedUsername) {
        this(networkName);
        this.pairedUsername = pairedUsername;
    }

    public NetworkListItem(String networkName, ParseUser pairedUser) {
        this(networkName);
        this.pairedUser = pairedUser;
    }

    public String getNetworkName() {
        return networkName;
    }

    public String getPairedUsername() {
        if (isPairedWithUser()) {
            return pairedUser.getUsername();
        } else {
            return pairedUsername;
        }
    }

    public boolean isPairedWithUser() {
        return (pairedUser != null);
    }
}
