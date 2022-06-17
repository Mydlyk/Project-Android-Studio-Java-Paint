package com.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    // thread
    private Thread thread;
    private boolean isThreadRunning = false;
    private final Object lock;

    // paint
    private SurfaceHolder surfaceHolder;
    private Bitmap bitmap;
    private Bitmap tempBitmap;
    private Canvas localCanvas;
    private Paint paint;
    private Paint paintDefault;
    private float lastX;
    private float lastY;

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        lock = new Object();

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(7);
        paint.setStyle(Paint.Style.STROKE);

        paintDefault = new Paint();
        paintDefault.setColor(Color.BLACK);
        tempBitmap = null;
    }

    public void startDrawing() {
        thread = new Thread(this);
        isThreadRunning = true;
        thread.start();
    }

    public void stopDrawing() {
        isThreadRunning = false;
    }

    public void changeColor(@ColorInt int color) {
        paint.setColor(color);
    }

    public void clearCanvas() {
        localCanvas.drawRect(0, 0, localCanvas.getWidth(), localCanvas.getHeight(), paintDefault);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        stopDrawing();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                localCanvas.drawCircle(event.getX(), event.getY(), 6, paint);
                break;

            case MotionEvent.ACTION_MOVE:
                Path path = new Path();
                path.moveTo(lastX, lastY);
                path.lineTo(event.getX(), event.getY());
                localCanvas.drawPath(path, paint);
                break;

            case MotionEvent.ACTION_UP:
                localCanvas.drawCircle(event.getX(), event.getY(), 6, paint);
                break;
        }

        lastX = event.getX();
        lastY = event.getY();
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable parentState = bundle.getParcelable("STATE_PARENT");
        super.onRestoreInstanceState(parentState);
        tempBitmap = bundle.getParcelable("STATE_BITMAP");
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle state = new Bundle();
        state.putParcelable("STATE_PARENT", superState);
        state.putParcelable("STATE_BITMAP", bitmap);
        return state;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        synchronized(lock) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            localCanvas = new Canvas(bitmap);
            if (tempBitmap != null) localCanvas.drawBitmap(tempBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void run() {
        while (isThreadRunning) {
            Canvas canvas = null;

            // draw
            try {
                synchronized(surfaceHolder) {
                    if (!surfaceHolder.getSurface().isValid()) continue;
                    canvas = surfaceHolder.lockCanvas(null);
                }
            } finally {
                if (canvas != null) {
                    synchronized(lock) {
                        canvas.drawBitmap(bitmap, 0, 0, null);
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }

            // sleep
            try {
                Thread.sleep(1000 / 25);
            } catch( InterruptedException ignored) {}
        }
    }
}
