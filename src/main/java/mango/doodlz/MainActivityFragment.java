package mango.doodlz;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import mango.doodlz.dialog.DialogFactory;

/**
 * Created by Sunyuan on 2018/1/15.
 */

public class MainActivityFragment extends Fragment {
    public static final String TAG = "sy__test";
    private static final int ACCELERATION_THRESHOLD = 100000;
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int UPDATE_INTERVAL = 100;
    private DoodleView mDoodleView;
    private float mAcceleration;
    private float mCurrentAcceleration;
    private float mLastAcceleration;
    private long mLastUpdateTime;
    private boolean mDialogOnScreen = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        // get reference to the DoodleView
        mDoodleView = (DoodleView) view.findViewById(R.id.doodleView);

        // init acce values
        mAcceleration = 0.00f;
        mCurrentAcceleration = SensorManager.GRAVITY_EARTH;
        mLastAcceleration = SensorManager.GRAVITY_EARTH;
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doodle_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.color:
                DialogFactory.createColorDialog(getFragmentManager());
                return true;
            case R.id.line_width:
                DialogFactory.createLineWidthDialog(getFragmentManager());
                return true;
            case R.id.eraser:
                DialogFactory.createEraseDialog(getFragmentManager());
                return true;
            case R.id.delete_drawing:
                confirmErase();
                return true;
            case R.id.save:
                saveImage();
                return true;
            case R.id.print:
                mDoodleView.printImage();
                return true;
            case R.id.tips:
                showTips();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTips() {
        SnackbarUtil.createShortSnack(mDoodleView, "You can erase all drawing by shake device.");
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        disableAccelerometerListening();
    }

    private void disableAccelerometerListening() {
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        sensorManager.unregisterListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private void enableAccelerometerListening() {
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private final SensorEventListener sensorEventListener =
            new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    long currentTime = System.currentTimeMillis();
                    long diffTime = currentTime - mLastUpdateTime;
                    if (diffTime < UPDATE_INTERVAL) {
                        return;
                    }
                    mLastUpdateTime = currentTime;
                    Log.i(TAG,"onSensorChanged mDialogOnScreen:"+mDialogOnScreen);
                    if (!mDialogOnScreen) {
                        float x = sensorEvent.values[0];
                        float y = sensorEvent.values[1];
                        float z = sensorEvent.values[2];

                        mLastAcceleration = mCurrentAcceleration;

                        mCurrentAcceleration = x * x + y * y + z * z;

                        mAcceleration = mCurrentAcceleration * (mCurrentAcceleration - mLastAcceleration);
                        if (mAcceleration > ACCELERATION_THRESHOLD) {
                            confirmErase();
                        }

                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {
                    // nothing
                }
            };

    private void confirmErase() {
        DialogFactory.createEraseConfirmDialog(getFragmentManager());
        Log.i(TAG,"confirmErase");
    }

    // returns the DoodleView
    public DoodleView getDoodleView() {
        return mDoodleView;
    }

    // indicates whether a dialog is displayed
    public void setDialogOnScreen(boolean visible) {
        Log.i(TAG,"setDialogOnScreen");
        mDialogOnScreen = visible;
    }

    private void saveImage() {
        if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager
                .PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage(R.string.permission_explanation);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[] {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                    }
                });

                builder.create().show();
            } else {
                requestPermissions(new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, SAVE_IMAGE_PERMISSION_REQUEST_CODE);
            }
        } else {
            mDoodleView.saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        switch (requestCode) {
            case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mDoodleView.saveImage();
                }
                return;
        }
    }
}
