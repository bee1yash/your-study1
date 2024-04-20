package com.example.yourstudy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class FullScreenImageActivity extends Activity {

    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private float mInitialScaleFactor = 1.0f;
    private ImageView fullScreenImageView;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId;
    private static final float MAX_SCALE_FACTOR = 5.0f;
    private static final float TOUCH_SCALE_FACTOR = 0.7f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        fullScreenImageView = findViewById(R.id.full_screen_image_view);
        String imagePath = getIntent().getStringExtra("image_path");
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            fullScreenImageView.setImageBitmap(bitmap);
        }

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        fullScreenImageView.post(new Runnable() {
            @Override
            public void run() {
                mInitialScaleFactor = fullScreenImageView.getScaleX();
            }
        });

        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);

                final int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_DOWN: {
                        final int pointerIndex = event.getActionIndex();
                        final float x = event.getX(pointerIndex);
                        final float y = event.getY(pointerIndex);
                        mLastTouchX = x;
                        mLastTouchY = y;
                        mActivePointerId = event.getPointerId(0);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        final int pointerIndex = event.findPointerIndex(mActivePointerId);
                        final float x = event.getX(pointerIndex);
                        final float y = event.getY(pointerIndex);
                        final float dx = (x - mLastTouchX) * TOUCH_SCALE_FACTOR;
                        final float dy = (y - mLastTouchY) * TOUCH_SCALE_FACTOR;
                        fullScreenImageView.setX(fullScreenImageView.getX() + dx);
                        fullScreenImageView.setY(fullScreenImageView.getY() + dy);
                        mLastTouchX = x;
                        mLastTouchY = y;
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        final int pointerIndex = event.getActionIndex();
                        final float x = event.getX(pointerIndex);
                        final float y = event.getY(pointerIndex);
                        mLastTouchX = x;
                        mLastTouchY = y;
                        mActivePointerId = event.getPointerId(pointerIndex);
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP: {
                        final int pointerIndex = event.getActionIndex();
                        final int pointerId = event.getPointerId(pointerIndex);
                        if (pointerId == mActivePointerId) {
                            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                            mLastTouchX = event.getX(newPointerIndex);
                            mLastTouchY = event.getY(newPointerIndex);
                            mActivePointerId = event.getPointerId(newPointerIndex);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                        break;
                    }
                }
                return true;
            }
        });
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScaleFactor = mScaleFactor * scaleFactor;
            if (newScaleFactor < mInitialScaleFactor) {
                scaleFactor = mInitialScaleFactor / mScaleFactor;
                mScaleFactor = mInitialScaleFactor;
            } else if (newScaleFactor > MAX_SCALE_FACTOR) {
                scaleFactor = MAX_SCALE_FACTOR / mScaleFactor;
                mScaleFactor = MAX_SCALE_FACTOR;
            } else {
                mScaleFactor = newScaleFactor;
            }
            fullScreenImageView.setScaleX(mScaleFactor);
            fullScreenImageView.setScaleY(mScaleFactor);
            return true;
        }
    }
    }
