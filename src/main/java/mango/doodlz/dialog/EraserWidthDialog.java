package mango.doodlz.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import mango.doodlz.DoodleView;
import mango.doodlz.R;

/**
 * Created by Sunyuan on 2018/1/17.
 */

public class EraserWidthDialog extends BaseDialog{
    private ImageView mWidthImageView;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View eraserWidthDialogView =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_eraser_width, null);
        builder.setView(eraserWidthDialogView)
               .setTitle(R.string.title_eraser_width_dialog);

        // get the ImageView
        mWidthImageView = (ImageView) eraserWidthDialogView.findViewById(
                R.id.eraserImageView);

        // configure widthSeekBar
        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        final SeekBar widthSeekBar = (SeekBar)
                eraserWidthDialogView.findViewById(R.id.widthSeekBar);
        widthSeekBar.setOnSeekBarChangeListener(eraserWidthChanged);
        widthSeekBar.setProgress(doodleView.getLineWidth());

        // add Set Line Width Button
        builder.setPositiveButton(R.string.button_set_line_width,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doodleView.getEraser();
                        doodleView.setLineWidth(widthSeekBar.getProgress());
                    }
                }
        );

        return builder.create();
    }

    private final SeekBar.OnSeekBarChangeListener eraserWidthChanged =
            new SeekBar.OnSeekBarChangeListener() {
                final Bitmap bitmap = Bitmap.createBitmap(
                        400, 100, Bitmap.Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap); // draws into bitmap

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    // configure a Paint object for the current SeekBar value
                    Paint p = new Paint();
                    p.setColor(Color.GRAY);
                    p.setStrokeCap(Paint.Cap.ROUND);
                    p.setStrokeWidth(progress);

                    // erase the bitmap and redraw the line
                    bitmap.eraseColor(
                            getResources().getColor(android.R.color.transparent,
                                    getContext().getTheme()));
                    canvas.drawLine(30, 50, 60, 50, p);
                    mWidthImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                } // required

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                } // required
            };

    @Override
    String getDialogTag() {
        return "Eraser width dialog";
    }
}
