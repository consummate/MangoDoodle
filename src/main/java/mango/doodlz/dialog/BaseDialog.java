package mango.doodlz.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

import mango.doodlz.MainActivityFragment;
import mango.doodlz.R;

/**
 * Created by Sunyuan on 2018/1/17.
 */

public abstract class BaseDialog extends DialogFragment {

    abstract String getDialogTag();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }

    // gets a reference to the MainActivityFragment
    protected MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

    public void show(FragmentManager fm) {
        show(fm, getDialogTag());
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }
}
