package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

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
        public TextView networkNameView;
        public TextView pairedUsernameView;
        public Button pairUnpairButton;
        public ParseUser pairedUser;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) { // No View available for reuse
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // TODO: Use View Holder pattern to recycle view and enable smoother scrolling
            rowView = inflater.inflate(R.layout.network_list_row, parent, false);

            // Configure ViewHolder
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.networkNameView = (TextView) rowView.findViewById(R.id.network_name);
            viewHolder.pairedUsernameView = (TextView) rowView.findViewById(R.id.paired_user);

            // Configure PAIR/UN-PAIR button
            viewHolder.pairUnpairButton = (Button) rowView.findViewById(R.id.wifi_button_pair_device);

            // TODO: Set onClickEventListener


            rowView.setTag(viewHolder);
        }

        // Get user data so we can customize PAIR/UN-PAIR buttons
        ParseUser user = ParseUser.getCurrentUser();

        // Fill in data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        String networkName = networks.get(position).getNetworkName();
        ParseUser pairedUser = networks.get(position).getPairedUser();
        String pairedWithText = "";

        if (pairedUser != null) {
            // Set 'Paired with...' text
            pairedWithText = STRING_PAIRED_WITH + " " + pairedUser.getUsername();

            if (pairedUser.equals(user)) { // Can UN-PAIR device
                holder.pairUnpairButton.setText(STRING_UNPAIR_DEVICE);
                holder.pairUnpairButton.setTextColor(COLOR_BUTTON_UNPAIR);
            }
        } else {
            // Set PAIR/UN-PAIR button to different text and color
            holder.pairUnpairButton.setText(STRING_PAIR_DEVICE);
            holder.pairUnpairButton.setTextColor(COLOR_BUTTON_PAIR);
        }

        holder.networkNameView.setText(networkName);
        holder.pairedUsernameView.setText(pairedWithText);

        return rowView;
    }
}