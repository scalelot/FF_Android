package com.festum.festumfield.verstion.firstmodule.screens.main.streaming

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.festum.festumfield.Activity.VideoCallReciveActivity
import com.festum.festumfield.CustomSdpObserver
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ActivityVideoCallingBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.webrtc.Connection
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceConnectionState
import org.webrtc.PeerConnection.IceGatheringState
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnection.SignalingState
import org.webrtc.PeerConnectionFactory
import org.webrtc.PeerConnectionFactory.InitializationOptions
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack

@AndroidEntryPoint
class VideoCallingActivity : BaseActivity<ChatViewModel>() {

    private lateinit var binding: ActivityVideoCallingBinding

    private var mConnection: Connection? = null
    private var remoteId: String? = null

    private var frontCameraID = 1
    private var backCameraID = 0
    private var currentCameraID = 1

    private var videoCapture: VideoCapturer? = null

    private var rootEglBase: EglBase? = null

    /* PeerConnection */

    private var peerConnection: PeerConnection? = null

    var factory: PeerConnectionFactory? = null

    private var videoTrackFromCamera: VideoTrack? = null

    private var audioSource: AudioSource? = null

    private var localAudioTrack: AudioTrack? = null

    var storge_permissions = arrayOf(Manifest.permission.CAMERA)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    var storge_permissions_33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.CAMERA
    )

    lateinit var permission: Array<String>

    override fun getContentView(): View {
        binding = ActivityVideoCallingBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun setupUi() {

        ActivityCompat.requestPermissions(this@VideoCallingActivity, permissions(), 1)

        remoteId = intent.getStringExtra("toUser")

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestCameraAndMicAccess() {
        if (IntentUtil.cameraPermission(this@VideoCallingActivity)
            && IntentUtil.readAudioPermission(this@VideoCallingActivity)
            && IntentUtil.readVideoPermission(this@VideoCallingActivity)
            && IntentUtil.readImagesPermission(this@VideoCallingActivity))
        {
            init()
        } else{

        }

    }

    override fun setupObservers() {

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onCameraPermission() {

        Dexter.withContext(this@VideoCallingActivity)
            .withPermissions(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                    if (permission?.areAllPermissionsGranted() == true) {

                        init()

                    } else {
                        AppPermissionDialog.showPermission(
                            this@VideoCallingActivity,
                            getString(R.string.request_camera_mic_permissions_text),
                            getString(R.string.media_permission_title)
                        )
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).withErrorListener {}
            .check()

    }

    fun init(){

        val cameraCount = Camera.getNumberOfCameras()

        for (i in 0 until cameraCount) {
            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                frontCameraID = i
                break
            }
        }

        for (i in 0 until cameraCount) {
            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                backCameraID = i
                break
            }
        }

        binding.llSwitchCamera.setOnClickListener {
            currentCameraID = if (currentCameraID == backCameraID) {
                frontCameraID
            } else {
                backCameraID
            }
            switchCamera(currentCameraID)
        }

        initializeSurfaceViews()

        initializePeerConnectionFactory()

        createVideoTrackFromCameraAndShowIt()

        initializePeerConnections()

        startStreamingVideo()

        doCall()

        /*initializePeerConnections()

        startStreamingVideo()*/

//        doCall()

    }

    private fun initializeSurfaceViews() {

        rootEglBase = EglBase.create()
        binding.surfaceView.init(rootEglBase?.eglBaseContext, null)
        binding.surfaceView.setEnableHardwareScaler(true)
        binding.surfaceView.setMirror(true)
        binding.surfaceView2.init(rootEglBase?.eglBaseContext, null)
        binding.surfaceView2.setEnableHardwareScaler(true)
        binding.surfaceView2.setMirror(true)

    }

    private fun initializePeerConnectionFactory() {
        val initializationOptions = InitializationOptions.builder(this).setEnableInternalTracer(true).createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        factory = PeerConnectionFactory.builder().setVideoEncoderFactory(DefaultVideoEncoderFactory(rootEglBase?.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase?.eglBaseContext)).createPeerConnectionFactory()
    }

    private fun createVideoTrackFromCameraAndShowIt() {

        val audioConstraints = MediaConstraints()
        val videoCapture: VideoCapturer? = createVideoCapture()
        var videoSource: VideoSource? = null

        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase?.eglBaseContext)
        videoSource = factory?.createVideoSource(videoCapture?.isScreencast == true)
        videoCapture?.initialize(surfaceTextureHelper, this, videoSource?.capturerObserver)

        videoTrackFromCamera = factory?.createVideoTrack(VideoCallReciveActivity.VIDEO_TRACK_ID, videoSource)
        videoTrackFromCamera?.setEnabled(true)

        videoCapture?.startCapture(1024, 720, 30)
        videoTrackFromCamera?.addSink(binding.surfaceView)

        //create an AudioSource instance
        audioSource = factory!!.createAudioSource(audioConstraints)
        localAudioTrack = factory!!.createAudioTrack("audio101", audioSource)

    }

    private fun initializePeerConnections() {
        peerConnection = createPeerConnection(factory)
    }

    private fun startStreamingVideo() {
        val mediaStream = factory?.createLocalMediaStream("ARDAMS")
        mediaStream?.addTrack(videoTrackFromCamera)
        mediaStream?.addTrack(localAudioTrack)
        peerConnection?.addStream(mediaStream)

//        sendMessage("got user media");
    }

    private fun createVideoCapture(): VideoCapturer? {

        videoCapture = if (useCamera2()) {
            createCameraCapture(Camera2Enumerator(this))
        } else {
            createCameraCapture(Camera1Enumerator(true))
        }
        return videoCapture

    }

    private fun useCamera2(): Boolean {
        return Camera2Enumerator.isSupported(this)
    }

    private fun switchCamera(cameraID: Int) {

        if (videoCapture is CameraVideoCapturer) {
            val cameraVideoCapture = videoCapture as CameraVideoCapturer
            cameraVideoCapture.switchCamera(null)
        }

    }

    private fun createCameraCapture(enumerator: CameraEnumerator): VideoCapturer? {

        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapture: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapture != null) {
                    return videoCapture
                }
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapture: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapture != null) {
                    return videoCapture
                }
            }
        }

        return null

    }

    private fun createPeerConnection(factory: PeerConnectionFactory?): PeerConnection? {
        val iceServers = ArrayList<IceServer>()
        val url = "stun:stun.stunprotocol.org:3478"
        val url2 = "stun:stun.l.google.com:19302"
        iceServers.add(IceServer(url))
        iceServers.add(IceServer(url2))
//        iceServers.add(IceServer(URL))
        val rtcConfig = RTCConfiguration(iceServers)
        val pcConstraints = MediaConstraints()
        val pcObserver: PeerConnection.Observer = object : PeerConnection.Observer {
            override fun onSignalingChange(signalingState: SignalingState) {
                Log.d(VideoCallReciveActivity.TAG, "onSignalingChange: ")
            }

            override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {
                Log.d(VideoCallReciveActivity.TAG, "onIceConnectionChange: ")
            }

            override fun onIceConnectionReceivingChange(b: Boolean) {
                Log.d(VideoCallReciveActivity.TAG, "onIceConnectionReceivingChange: ")
            }

            override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
                Log.d(VideoCallReciveActivity.TAG, "onIceGatheringChange: ")
            }

            override fun onIceCandidate(iceCandidate: IceCandidate) {
                Log.d(VideoCallReciveActivity.TAG, "onIceCandidate: ")
                val callData = JSONObject()
                try {
                    callData.put("userToCall", remoteId)
                    callData.put("signalData", iceCandidate.sdp)
                    callData.put("from", AppPreferencesDelegates.get().channelId)
                    callData.put("name", AppPreferencesDelegates.get().userName)
                    callData.put("isVideoCall", true)

                    SocketManager.mSocket?.emit("callUser", callData);
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                Log.d(VideoCallReciveActivity.TAG, "onIceCandidatesRemoved: ")
            }

            override fun onAddStream(mediaStream: MediaStream) {
                Log.e("VideoCallReciveActivity.TAG", "onAddStream: " + mediaStream.videoTracks.size)
                val remoteVideoTrack = mediaStream.videoTracks[0]
                Log.e("remoteVideoTrack: ", remoteVideoTrack.toString())
                val remoteAudioTrack = mediaStream.audioTracks[0]
                remoteAudioTrack.setEnabled(true)
                remoteVideoTrack.setEnabled(true)
                remoteVideoTrack.addSink(binding.surfaceView2)
            }

            override fun onRemoveStream(mediaStream: MediaStream) {
                Log.d(VideoCallReciveActivity.TAG, "onRemoveStream: ")
            }

            override fun onDataChannel(dataChannel: DataChannel) {
                Log.d(VideoCallReciveActivity.TAG, "onDataChannel: ")
            }

            override fun onRenegotiationNeeded() {
                Log.d(VideoCallReciveActivity.TAG, "onRenegotiationNeeded: ")
                //                peerConnection.createOffer(new CustomSdpObserver() {
//                    @Override
//                    public void onCreateSuccess(SessionDescription sessionDescription) {
//                        peerConnection.setLocalDescription(new CustomSdpObserver(), sessionDescription);
//                        JSONObject callData = new JSONObject();
//                        JSONObject signalDataJson = new JSONObject();
//                        Log.d("oiojhasfsdiuf", String.valueOf(callData));
//                        try {
//                            signalDataJson.put("type",sessionDescription.type.canonicalForm());
//                            signalDataJson.put("sdp", sessionDescription.description);
//
//                            callData.put("userToCall", toUserId);
//                            callData.put("signalData", signalDataJson);
//                            callData.put("from", loginUser);
//                            callData.put("name", toUserName);
//                            callData.put("isVideoCall", true);
//
//
//
//                        } catch (JSONException e) {
//                            Log.d("oiojhasfsdiuf", String.valueOf(e));
//                            e.printStackTrace();
//                        }
//                        mSocket.emit("callUser", callData);
//                    }
//                }, new MediaConstraints());
            }

            override fun onAddTrack(rtpReceiver: RtpReceiver, mediaStreams: Array<MediaStream>) {}
        }
        return factory?.createPeerConnection(rtcConfig, pcConstraints, pcObserver)
    }

    private fun doCall() {

        val sdpMediaConstraints = MediaConstraints()
        peerConnection?.createOffer(object : CustomSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {

                peerConnection?.setLocalDescription(CustomSdpObserver(), sessionDescription)
                val signalDataJson = JSONObject()
                signalDataJson.put("type", sessionDescription.type.canonicalForm())
                signalDataJson.put("sdp", sessionDescription.description)



                try {

                    val message = JSONObject().apply {

                        val jsonArray = JSONArray()
                        jsonArray.put(remoteId)
                        jsonArray.put(AppPreferencesDelegates.get().channelId)
                        put("memberIds",jsonArray)
                        put("fromId", AppPreferencesDelegates.get().channelId)
                        put("name", AppPreferencesDelegates.get().userName)
                        put("isVideoCall", true)

                    }

                    SocketManager.mSocket?.emit("callUser", message)?.on("webrtcMessage"){ args ->

                        val data = args[0] as JSONObject

                        val webRtcMessage = JSONObject().apply {

                            put("sdp",signalDataJson)
                            put("uuid","")
                            put("dest","")
                            put("channelID","")
                        }

                    }

                    Log.e("TAG", "onCreateSuccess:$message")

                } catch (e: JSONException) {
                    Log.e("sdpMediaConstraintsError", e.toString())
                    e.printStackTrace()
                }

            }

            override fun onCreateFailure(s: String) {
                Log.e("sdpMediaConstraints-Failure", s)
            }
        }, sdpMediaConstraints)
    }

    fun permissions(): Array<String> {
        try {
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                storge_permissions_33
            } else {
                storge_permissions
            }
        } catch (e: Exception) {
            Log.e("Permission:", e.toString())
        }
        return permission
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init()
            } else {
                onCameraPermission()
            }
        }
    }

}