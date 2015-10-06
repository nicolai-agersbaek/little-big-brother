package dk.au.cs.nicolai.pvc.littlebigbrother.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

/**
 * Created by Nicolai on 06-10-2015.
 */
public abstract class FragmentWidget<T extends Fragment> extends Widget {

    private T fragment;

    protected FragmentWidget(Activity context, T fragment, FragmentTransaction fragmentTransaction, int containerResId) {
        super(context, containerResId);

        this.fragment = fragment;

        fragmentTransaction.add(containerResId, fragment);
        fragmentTransaction.commit();
    }

    protected T getFragment() {
        return fragment;
    }
}
