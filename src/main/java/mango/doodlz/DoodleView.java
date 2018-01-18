package mango.doodlz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.print.PrintHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sunyuan on 2018/1/15.
 */

public class DoodleView extends View {
    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;
    private final Paint mPaintScreen;
    private final Paint mPaintLine;

    // Maps of current paths being drawn and Points in those Paths
    private final Map<Integer, Path> mPathMap = new HashMap<>();
    private final Map<Integer, Point> mPreviousPointMap = new HashMap<>();

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaintScreen = new Paint();

        // set the initial display settings for the painted line
        mPaintLine = new Paint();
        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(Color.BLACK);
		// without solid line, it will show a full
        mPaintLine.setStyle(Paint.Style.STROKE); // solid line
        mPaintLine.setStrokeWidth(5);
        mPaintLine.setStrokeCap(Paint.Cap.ROUND);

    }

    public void saveImage() {
//        final String name = "Doodlz" + System.currentTimeMillis()+".jpg";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = simpleDateFormat.format(new Date());
        final String name = "Doodlz" + date +".jpg";
        // insert the image on the device
        String location = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), mBitmap, name,
                "Doodlz Drawing");

        if (location != null) {
            SnackbarUtil.createLongSnack(this, getResources().getString(R.string.message_saved));
        } else {
            SnackbarUtil.createLongSnack(this, getResources().getString(R.string.message_error_saving));
        }
    }

    public void printImage() {
        if (PrintHelper.systemSupportsPrint()) {
            PrintHelper printHelper = new PrintHelper(getContext());
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap("Doodlz Image", mBitmap);
        } else {
            Toast message = Toast.makeText(getContext(), R.string.message_error_printing, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
            message.show();
        }
    }

    public void clear() {
        mPathMap.clear();
        mPreviousPointMap.clear();
        mBitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    public void getEraser() {
        setDrawingColor(Color.WHITE);
    }

    public void setDrawingColor(int color) {
        mPaintLine.setColor(color);
    }

    public int getDrawingColor() {
        return mPaintLine.getColor();
    }

    public void setLineWidth(int width) {
        mPaintLine.setStrokeWidth(width);
    }

    public int getLineWidth() {
        return (int) mPaintLine.getStrokeWidth();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);
        mBitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw the background screen
        canvas.drawBitmap(mBitmap, 0, 0, mPaintScreen);

        // for each path currently being drawn
        for (Integer key: mPathMap.keySet()) {
            canvas.drawPath(mPathMap.get(key), mPaintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if (action  == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        } else {
            touchMoved(event);
        }
        invalidate();
        return true;
    }

    private void touchStarted(float x, float y, int lineId) {
        Path path;
        Point point;

        if (mPathMap.containsKey(lineId)) {
            path = mPathMap.get(lineId);
            path.reset();
            point = mPreviousPointMap.get(lineId);
        } else {
            path = new Path();
            mPathMap.put(lineId, path);
            point = new Point();
            mPreviousPointMap.put(lineId, point);
        }

        // move to the coordinates of the touch
        path.moveTo(x,y);
        point.x = (int) x;
        point.y = (int) y;
    }

    private void touchMoved(MotionEvent event) {
        for (int i = 0; i<event.getPointerCount(); i++) {
            int pointerId  = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            // if there is a path associated with the pointer
            if (mPathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                //get the path and previous point associated with this pointer
                Path path = mPathMap.get(pointerId);
                Point point = mPreviousPointMap.get(pointerId);

                // calculate how far the user moved form the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // if the distance is significant enough to matter
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // move the path to the new location
                    path.quadTo(point.x, point.y, (newX+point.x)/2, (newY+point.y)/2);

                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    private void touchEnded(int lineId) {
        Path path = mPathMap.get(lineId);
        mBitmapCanvas.drawPath(path, mPaintLine);
        path.reset();
    }


}
