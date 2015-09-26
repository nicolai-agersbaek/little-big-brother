package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

/**
 * Created by Nicolai on 25-09-2015.
 */
public final class NetworkListArrayAdapter extends ArrayAdapter<NetworkListItem> {
    private final Context context;
    private final ArrayList<NetworkListItem> networks;

    private String STRING_PAIRED_WITH;
    private String STRING_PAIR_DEVICE;
    private String STRING_UNPAIR_DEVICE;

    private int COLOR_BUTTON_PAIR;
    private int COLOR_BUTTON_UNPAIR;

    static class ViewHolder {
        protected TextView networkNameView;
        protected TextView pairedUserView;
        protected Button pairButton;
    }

    public NetworkListArrayAdapter(Context context, ArrayList<NetworkListItem> networks) {
        super(context, R.layout.network_list_row,
                networks);
        this.context = context;
        this.networks = networks;

        this.STRING_PAIRED_WITH = context.getString(R.string.wifi_paired_with);
        this.STRING_PAIR_DEVICE = context.getString(R.string.wifi_action_pair_device);
        this.STRING_UNPAIR_DEVICE = context.getString(R.string.wifi_action_unpair_device);

        this.COLOR_BUTTON_PAIR = LittleBigBrother.Colors.HOLO_BLUE_LIGHT;
        this.COLOR_BUTTON_UNPAIR = Color.RED;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.network_list_row, parent, false);

            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.networkNameView = (TextView) view.findViewById(R.id.network_name);
            viewHolder.pairedUserView = (TextView) view.findViewById(R.id.paired_user);

            // Configure PAIR/UN-PAIR button
            viewHolder.pairButton = (Button) view.findViewById(R.id.wifi_button_pair_device);

            // TODO: Set onClickEventListener
            viewHolder.pairButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button button = (Button) view;
                    NetworkListItem element = (NetworkListItem) view.getTag();

                    boolean isPaired = (button.getText() == context.getString(R.string.wifi_action_unpair_device));

                    if (isPaired) {
                        // Un-pair user from device
                        Log.debug(this, "Unpairing user from " + element.getNetworkName());
                        setButtonToPair(button);
                        element.unpairUser();
                    } else {
                        // Pair user with device
                        Log.debug(this, "Pairing user with " + element.getNetworkName());
                        setButtonToUnpair(button);
                        element.pairUser(ParseUser.getCurrentUser());
                    }

                    viewHolder.pairedUserView.setText(getPairedWithString(element.getPairedUser()));
                }
            });
            view.setTag(viewHolder);
            viewHolder.pairButton.setTag(networks.get(position));
        } else { // Can reuse view
            // Need to set view details from tag
            view = convertView;
            ((ViewHolder) view.getTag()).pairButton.setTag(networks.get(position));
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        ParseUser user = ParseUser.getCurrentUser();
        NetworkListItem network = networks.get(position);

        // Fill in data
        String networkName = network.getNetworkName();
        ParseUser pairedUser = network.getPairedUser();

        holder.networkNameView.setText(networkName);

        if (pairedUser != null) {
            if (pairedUser.equals(user)) { // Can UN-PAIR device
                setButtonToUnpair(holder.pairButton);
            } else { // No access to pairing for this device
                setButtonDisabled(holder.pairButton);
            }
        } else {
            // Set PAIR/UN-PAIR button to different text and color
            setButtonToPair(holder.pairButton);
        }

        holder.pairedUserView.setText(getPairedWithString(pairedUser));

        return view;
    }

    private String getPairedWithString(ParseUser pairedUser) {
        if (pairedUser == null) {
            return "";
        } else {
            return STRING_PAIRED_WITH + " " + pairedUser.getUsername();
        }
    }

    private void setButtonToPair(Button button) {
        button.setText(STRING_PAIR_DEVICE);
        button.setTextColor(COLOR_BUTTON_PAIR);
    }

    private void setButtonToUnpair(Button button) {
        button.setText(STRING_UNPAIR_DEVICE);
        button.setTextColor(COLOR_BUTTON_UNPAIR);
    }

    private void setButtonDisabled(Button button) {
        button.setVisibility(View.GONE);
    }
}