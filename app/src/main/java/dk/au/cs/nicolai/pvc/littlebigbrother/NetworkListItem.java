package dk.au.cs.nicolai.pvc.littlebigbrother;

import com.parse.ParseUser;

/**
 * Created by Nicolai on 25-09-2015.
 */
public final class NetworkListItem {
    private String networkName;
    private ParseUser pairedUser;

    public NetworkListItem(String networkName) {
        this.networkName = networkName;
    }

    public NetworkListItem(String networkName, ParseUser pairedUser) {
        this(networkName);
        if (pairedUser != null) {
            this.pairedUser = pairedUser;
        }
    }

    public String getNetworkName() {
        return networkName;
    }

    public ParseUser getPairedUser() {
        return pairedUser;
    }
}
