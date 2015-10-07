package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collection;

import dk.au.cs.nicolai.pvc.littlebigbrother.ui.Widget;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class ListWidget<T> extends Widget {

    protected ArrayAdapter<T> adapter;
    protected ListView listView;

    public ListWidget(Activity context, int containerResId) {
        super(context, containerResId);
    }

    public ListWidget(Activity context, int containerResId, int listViewResId) {
        super(context, containerResId);

        listView = (ListView) context.findViewById(listViewResId);

        if (adapter != null) {
            listView.setAdapter(adapter);
        }
    }

    public ListWidget(Activity context, int containerResId, int listViewResId, int listItemResId) {
        this(context, containerResId, listViewResId);

        adapter = new ArrayAdapter<T>(context, listItemResId);
        adapter.setNotifyOnChange(true);
    }

    public void setAdapter(ArrayAdapter<T> adapter) {
        this.adapter = adapter;
        adapter.setNotifyOnChange(true);

        listView.setAdapter(adapter);
    }

    public void add(T item) {
        adapter.add(item);
    }

    public void remove(T item) {
        adapter.remove(item);
    }

    public <E extends Collection<T>> void addAll(E collection) {
        adapter.addAll(collection);
    }

    public <E extends Collection<T>> void removeAll(E collection) {
        for (T item:
             collection) {
            adapter.remove(item);
        }
    }
}
