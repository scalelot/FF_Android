package com.festum.festumfield.View;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class VideoCallSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder1;
    private Camera mCamera1;
    Camera.Size mPreviewSize;

    public VideoCallSurfaceView(Context context, Camera camera) {
        super(context);
        mCamera1 = camera;
        mHolder1 = getHolder();
        mHolder1.addCallback(this);
        mHolder1.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera1 == null) {
                mCamera1.setPreviewDisplay(holder);
                mCamera1.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void refreshCamera(Camera camera) {
        if (mHolder1.getSurface() == null) {
            return;
        }
        try {
            mCamera1.stopPreview();
        } catch (Exception e) {
        }
        setCamera(camera);
        try {
            mCamera1.setPreviewDisplay(mHolder1);
            mCamera1.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        refreshCamera(mCamera1);
    }

    public void setCamera(Camera camera) {
        mCamera1 = camera;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}