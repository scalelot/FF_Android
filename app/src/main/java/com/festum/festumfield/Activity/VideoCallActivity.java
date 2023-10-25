package com.festum.festumfield.Activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.R;
import com.festum.festumfield.View.VideoCallSurfaceView;
import com.festum.festumfield.verstion.firstmodule.screens.webrtc.VideoCallingActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCallActivity extends BaseActivity {

    CircleImageView videocall_image;
    TextView videocall_username, videocall_txt;
    LinearLayout ll_videoCall, ll_camera_swipe, ll_mute, ll_call_cut;
    private Context myContext1;
    private Camera mCamera1;
    private boolean cameraFront = false;
    private VideoCallSurfaceView videoCallSurfaceView;
    LinearLayout cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        videocall_image = findViewById(R.id.videocall_image);
        videocall_username = findViewById(R.id.videocall_username);
        videocall_txt = findViewById(R.id.videocall_txt);
        ll_videoCall = findViewById(R.id.ll_videoCall);
        ll_camera_swipe = findViewById(R.id.ll_camera_swipe);
        ll_mute = findViewById(R.id.ll_mute);
        ll_call_cut = findViewById(R.id.ll_call_cut);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext1 = this;
        mCamera1 = Camera.open();
        mCamera1.setDisplayOrientation(90);
        cameraPreview = findViewById(R.id.cPreview);
        videoCallSurfaceView = new VideoCallSurfaceView(myContext1, mCamera1);
        cameraPreview.addView(videoCallSurfaceView);

        ll_videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VideoCallActivity.this, VideoCallingActivity.class));
                finish();
            }
        });

        ll_camera_swipe.setOnClickListener(new View.OnClickListener() {
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
        mCamera1.startPreview();

        ll_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VideoCallActivity.this, "Video Call Mute", Toast.LENGTH_SHORT).show();
            }
        });

        ll_call_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VideoCallActivity.this, "Video Call Cut", Toast.LENGTH_SHORT).show();
            }
        });
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

        if (mCamera1 == null) {
            mCamera1 = Camera.open();
            mCamera1.setDisplayOrientation(90);
            videoCallSurfaceView.refreshCamera(mCamera1);
            Log.d("nu", "null");
        } else {
            Log.d("nu", "no null");
        }

    }

    public void chooseCamera() {
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {

                mCamera1 = Camera.open(cameraId);
                mCamera1.setDisplayOrientation(90);
                videoCallSurfaceView.refreshCamera(mCamera1);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                mCamera1 = Camera.open(cameraId);
                mCamera1.setDisplayOrientation(90);
                videoCallSurfaceView.refreshCamera(mCamera1);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera1 != null) {
            mCamera1.stopPreview();
            mCamera1.setPreviewCallback(null);
            mCamera1.release();
            mCamera1 = null;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(VideoCallActivity.this, ChatingActivity.class));
        finish();
    }
}