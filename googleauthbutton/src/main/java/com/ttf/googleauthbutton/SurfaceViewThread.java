package com.ttf.googleauthbutton;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;

public class SurfaceViewThread extends Thread {
    private static final String TAG = "SurfaceViewThread";

    private final SurfaceHolder surfaceHolder;
    private ColorButtonStroke surfaceView;


    private boolean myThreadRun = false;

    public SurfaceViewThread(SurfaceHolder surfaceHolder,
                             ColorButtonStroke surfaceView) {
        this.surfaceHolder = surfaceHolder;
        this.surfaceView = surfaceView;
    }

    public void setRunning(boolean b) {
        myThreadRun = b;
    }

    private Handler handler;

    @Override
    public void run() {
        super.run();

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @SuppressLint("WrongCall")
            @Override
            public void run() {
                if (myThreadRun) {
                    Canvas c = null;
                    try {
                        c = surfaceHolder.lockCanvas(null);
                        synchronized (surfaceHolder) {
                            if (c != null) {
                                surfaceView.onDraw(c);
                            }
                        }
                    } finally {
                        if (c != null) {
                            surfaceHolder.unlockCanvasAndPost(c);
                        }
                    }
                    handler.postDelayed(this, 1000/60);
                }

            }
        }, 1000/60);
    }

}
