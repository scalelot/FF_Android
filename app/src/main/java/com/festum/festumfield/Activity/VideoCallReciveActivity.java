package com.festum.festumfield.Activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.festum.festumfield.BaseActivity;
import com.example.friendfield.R;
import com.festum.festumfield.View.CameraPreview;

public class VideoCallReciveActivity extends BaseActivity {

    LinearLayout ll_message, ll_videoCall, ll_switch_camera, ll_mute, ll_videoCall_cut;
    Context myContext;
    Camera mCamera;
    private boolean cameraFront = false;
    CameraPreview mPreview;
    LinearLayout cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_recive);

        ll_message = findViewById(R.id.ll_message);
        ll_videoCall = findViewById(R.id.ll_videoCall);
        ll_switch_camera = findViewById(R.id.ll_switch_camera);
        ll_mute = findViewById(R.id.ll_mute);
        ll_videoCall_cut = findViewById(R.id.ll_videoCall_cut);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
//        cameraPreview = (LinearLayout) findViewById(R.id.cPreview);
        cameraPreview = (LinearLayout) findViewById(R.id.cPreview1);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        ll_switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    releaseCamera();
                    chooseCamera();
                } else {

                }
            }
        });
        mCamera.startPreview();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }


    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPreview.refreshCamera(mCamera);
            Log.d("nu", "null");
        } else {
            Log.d("nu", "no null");
        }
    }

    public void chooseCamera() {
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {

                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(VideoCallReciveActivity.this, ChatingActivity.class));
        finish();
    }
}