package com.festum.festumfield.View;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    public List<Size> prSupportedPreviewSizes;
    private Size prPreviewSize;
    Context context;
    private final float ASPECT_RATIO_W = 4.0f;
    private final float ASPECT_RATIO_H = 3.0f;
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.context = context;
        mCamera = camera;
        prSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera == null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }
        setCamera(camera);
        try {

            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) return;
        try {
            mCamera.stopPreview();
        } catch (Exception ignored) {
        }

        Camera.Parameters params = mCamera.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        List<Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(sizes, w, h);

        if (h > w) mCamera.setDisplayOrientation(90);
        else mCamera.setDisplayOrientation(0);

        Camera.Size size = null;
        List<Camera.Size> list = params.getSupportedPreviewSizes();

        size = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            if ((list.get(i).width * list.get(i).height) > (size.width * size.height))
                size = list.get(i);
        }

        if (size != null) {
            params.setPreviewSize(size.width, size.height);
            params.setPictureSize(size.width, size.height);
        }

        mCamera.setParameters(params);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d("TAG", "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }
}