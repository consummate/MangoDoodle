package mango.doodlz;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Sunyuan on 2018/1/17.
 */

public class SnackbarUtil {
    public static void createShortSnack(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }
    public static void createLongSnack(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }
}
