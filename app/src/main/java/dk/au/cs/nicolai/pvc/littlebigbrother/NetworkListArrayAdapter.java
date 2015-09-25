package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nicolai on 25-09-2015.
 */
public final class NetworkListArrayAdapter extends ArrayAdapter<NetworkListItem> {
    private final Context context;
    private final NetworkListItem[] networkListItems;

    public NetworkListArrayAdapter(Context context, NetworkListItem[] networkListItems) {
        super(context, R.layout.network_list_row, networkListItems);
        this.context = context;
        this.networkListItems = networkListItems;
    }

    public NetworkListArrayAdapter(Context context, ArrayList<NetworkListItem> networkListItemsArrayList) {
        super(context, R.layout.network_list_row,
                networkListItemsArrayList.toArray(new NetworkListItem[networkListItemsArrayList.size()]));
        this.context = context;
        this.networkListItems = networkListItemsArrayList.toArray(new NetworkListItem[networkListItemsArrayList.size()]);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // TODO: Use View Holder pattern to recycle view and enable smoother scrolling
        View rowView = inflater.inflate(R.layout.network_list_row, parent, false);

        TextView networkNameTextView = (TextView) rowView.findViewById(R.id.network_name);
        TextView pairedUsernameTextView = (TextView) rowView.findViewById(R.id.paired_user);

        networkNameTextView.setText(networkListItems[position].getNetworkName());
        pairedUsernameTextView.setText(networkListItems[position].getPairedUsername());

        return rowView;
    }
}