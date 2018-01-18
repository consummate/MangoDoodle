package mango.doodlz.dialog;

import android.app.FragmentManager;

/**
 * Created by Sunyuan on 2018/1/17.
 */

public class DialogFactory {
    public static void createColorDialog(FragmentManager fm) {
        new ColorDialogFragment().show(fm);
    }
    public static void createEraseDialog(FragmentManager fm) {
        new EraserWidthDialog().show(fm);
    }
    public static void createEraseConfirmDialog(FragmentManager fm) {
        new EraseImageDialogFragement().show(fm);
    }
    public static void createLineWidthDialog(FragmentManager fm) {
        new LineWidthDialogFragment().show(fm);
    }
}
