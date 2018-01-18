package mango.doodlz.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import mango.doodlz.R;

/**
 * Created by Sunyuan on 2018/1/15.
 */

public class EraseImageDialogFragement extends BaseDialog {
    private final String DIALOG_TAG = "erase dialog";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.message_erase);
        builder.setPositiveButton(R.string.button_erase,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id){
                        getDoodleFragment().getDoodleView().clear();
                    }});
        builder.setNegativeButton(R.string.button_cancel, null);
        return builder.create();
    }

    @Override
    String getDialogTag() {
        return DIALOG_TAG;
    }
}
