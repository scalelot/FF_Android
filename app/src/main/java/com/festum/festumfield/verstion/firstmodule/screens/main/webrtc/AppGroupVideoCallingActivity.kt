package com.festum.festumfield.verstion.firstmodule.screens.main.webrtc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.festum.festumfield.CustomSdpObserver
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ActivityVideoCallingBinding
import com.festum.festumfield.databinding.ActivityVideoGroupCallingBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.Media
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.Product
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProductItem
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.Text
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import io.socket.emitter.Emitter
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
import org.webrtc.RtpReceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AppGroupVideoCallingActivity : BaseActivity<ChatViewModel>() {

    private lateinit var binding: ActivityVideoGroupCallingBinding

    private var remoteId: String? = null
    private var remoteUser: String? = null
    private lateinit var groupListItem: GroupListItems
    private var callReceive: Boolean? = null

    private var frontCameraID = 1
    private var backCameraID = 0
    private var currentCameraID = 1

    private val handler = Handler(Looper.getMainLooper())
    private var callDurationInSeconds = 0

    private var videoCapture: VideoCapturer? = null

    private var rootEglBase: EglBase? = null

    private var callId: String? = null

    /* PeerConnection */


    private var peerConnection: PeerConnection? = null

    var factory: PeerConnectionFactory? = null

    private var videoTrackFromCamera: VideoTrack? = null

    private var audioSource: AudioSource? = null

    private var localAudioTrack: AudioTrack? = null

    var storge_permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    var storge_permissions_33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.CAMERA
    )

    var userFragment = ""
    var remoteOffer: String = ""

    private val rtcAudioManager by lazy { RTCAudioManager.create(this) }
    private var isSpeakerMode = true
    private var isMute = false

    private var isCameraPause = false
    private var isCameraRotation = false


    lateinit var permission: Array<String>

    val jsonArray = JSONArray()
    var channelId: String = ""

    override fun getContentView(): View {
        binding = ActivityVideoGroupCallingBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun setupUi() {

        val intent = intent.extras

        val jsonList = intent?.getString("groupList")

        callReceive = intent?.getBoolean("callReceive", false)

        groupListItem = Gson().fromJson(jsonList, GroupListItems::class.java)

        remoteUser = intent?.getString("acceptedMemberName")
        remoteId = intent?.getString("acceptedMemberId")
        callId = intent?.getString("callId")

        groupListItem.members?.forEach {
            jsonArray.put(it.membersList?.id)
        }

        Log.e("TAG", "remoteId:--- $remoteId")

        /*remoteId = intent?.getString("remoteChannelId")*/

        ActivityCompat.requestPermissions(this@AppGroupVideoCallingActivity, permissions(), 1)

        binding.fromText.text = AppPreferencesDelegates.get().userName
        binding.toText.text = groupListItem.name


        /*runOnUiThread {

            val webRtcMessage = JSONObject().apply {

                put("displayName", AppPreferencesDelegates.get().userName)
                put("uuid", AppPreferencesDelegates.get().channelId)
                put("dest", remoteId)
                put("channelID", remoteId)

            }

            *//* Log.e("TAG", "webrtcMessage:---+++----++-- $webRtcMessage")
             Log.e("TAG", "uuid:--- " + AppPreferencesDelegates.get().channelId )
             Log.e("TAG", "remoteId:--- $remoteId")*//*

            SocketManager.mSocket?.emit("webrtcMessage", webRtcMessage)?.on("webrtcMessage") { args ->

                    val receiverData = args[0] as JSONObject

                    Log.e("TAG", "webrtcMessage:---+++----++-- $receiverData")
                    Log.e("TAG", "uuid:--- " + AppPreferencesDelegates.get().channelId)

                    val sdpResponse = receiverData.optJSONObject("sdp")
                    val type = sdpResponse?.optString("type")
                    val sdpOffer = sdpResponse?.optString("sdp")

                    val iceCandidate = receiverData.optJSONObject("ice")

                    val candidate = iceCandidate?.optString("candidate")
                    val sdpMid = iceCandidate?.optString("sdpMid")
                    val sdpMLineIndex = iceCandidate?.optString("sdpMLineIndex")
                    val usernameFragment = iceCandidate?.optString("usernameFragment")

                    if (type.equals("offer")) {

                        Log.e("TAG", "offer:----------$receiverData")

                        handleRemoteVideoOffer(sdpOffer)


                    }
                    if (type.equals("answer")) {

                        Log.e("TAG", "answer:----------$receiverData")

                        createAnswerFromRemoteOffer(sdpOffer)

                    }

                    if (iceCandidate != null) {

                        addRemoteIceCandidate(iceCandidate)

                    }

                }?.on("endCall") { args ->


                try {

                    val data = args[0] as JSONObject
                    Log.e("TAG", "setupUi: -----endCall---$data")
                    AppPreferencesDelegates.get().isVideoCalling = true
                    AppPreferencesDelegates.get().isAudioCalling = true
                    stop()
                    finish()

                } catch (e: Exception) {

                    stop()

                }

            }

        }*/

        runOnUiThread {

            val joinRoom = JSONObject().apply {

                put("roomId",groupListItem.id.toString())
                put("userName",AppPreferencesDelegates.get().userName)

            }

            SocketManager.mSocket?.emit("FF-join-room",joinRoom)

            SocketManager.mSocket?.on("FF-user-join"){ args ->

                val message = args[0]

                runOnUiThread{

                    Log.e("TAG", "FF-user-join:-----${message}")

                }

            }

            SocketManager.mSocket?.on(AppPreferencesDelegates.get().channelId){ args ->

                val message = args[0] as JSONObject

                Log.e("TAG", "getMessage: -----$message")

                val data = message.optJSONObject("data")

                when(message.optString("event").toString()){

                    "onIncomingChat" -> {}
                    "onGroupCallStarted" -> {}
                    "onCallStarted" -> {

                        val callId = data?.optString("callid")
                        AppPreferencesDelegates.get().isCallId = callId.toString()
                        Log.e("TAG", "onCallStarted---: $data")

                    }
                    "onGroupUpdate" -> {}
                    "onGroupCreation" -> {}
                    "messageDelivered" -> {}
                    "onCallAccepted" -> {}
                    "messageSeen" -> {}

                    "FF-user-join" -> {
                        Log.e("TAG", "onGroupUpdate---: $data")
                    }

                }

            }

        }

    }

    override fun setupObservers() {

    }

     @RequiresApi(Build.VERSION_CODES.TIRAMISU)
     private fun onCameraPermission() {

         Dexter.withContext(this@AppGroupVideoCallingActivity)
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
                             this@AppGroupVideoCallingActivity,
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

     @SuppressLint("SuspiciousIndentation")
     fun init() {


         binding.llSwitchCamera.setOnClickListener {

             if (isCameraRotation){
                 isCameraRotation = false
                 binding.surfaceView.setMirror(true)
             }else{
                 isCameraRotation = true
                 binding.surfaceView.setMirror(false)
             }

             switchCamera()

         }

         binding.llVideoCallCut.setOnClickListener {

             try {

                 val jsonObj = JSONObject()
                 jsonObj.put("id", AppPreferencesDelegates.get().channelId)
                 SocketManager.mSocket?.emit("endCall", jsonObj)
                 AppPreferencesDelegates.get().isVideoCalling = true
                 AppPreferencesDelegates.get().isAudioCalling = true
                 finish()


             } catch (e: Exception) {
                 Log.e("TAG", "error:" + e.message)
             }

         }

         binding.llVideoCall.setOnClickListener {
             if (isCameraPause){
                 isCameraPause = false
                 binding.videoCall.setImageResource(R.drawable.ic_baseline_videocam_24)
                 videoTrackFromCamera?.setEnabled(true)
             }else{
                 isCameraPause = true
                 binding.videoCall.setImageResource(R.drawable.ic_baseline_videocam_off_24)
                 videoTrackFromCamera?.setEnabled(false)
             }

         }

         binding.llMessage.setOnClickListener {

             if (isSpeakerMode){
                 isSpeakerMode = false
                 binding.speaker.setImageResource(R.drawable.ic_baseline_hearing_24)
                 rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.EARPIECE)
             }else{
                 isSpeakerMode = true
                 binding.speaker.setImageResource(R.drawable.ic_baseline_speaker_up_24)
                 rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)

             }

         }

         binding.llMute.setOnClickListener {

             if (isMute){
                 isMute = false
                 binding.mute.setImageResource(R.drawable.ic_baseline_mic_24)
                 localAudioTrack?.setEnabled(true)
             }else{
                 isMute = true
                 binding.mute.setImageResource(R.drawable.ic_baseline_mic_off_24)
                 localAudioTrack?.setEnabled(false)
             }


         }

         initializeSurfaceViews()

         initializePeerConnectionFactory()

         createVideoTrackFromCameraAndShowIt()

         if (callReceive == false)
         doCall()

 //        rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)

     }

     private fun initializeSurfaceViews() {

         rootEglBase = EglBase.create()
         binding.surfaceView.init(rootEglBase?.eglBaseContext, null)
         binding.surfaceView.setEnableHardwareScaler(true)
         binding.surfaceView.setMirror(true)
         binding.surfaceView2.init(rootEglBase?.eglBaseContext, null)
         binding.surfaceView2.setEnableHardwareScaler(true)
         binding.surfaceView2.setMirror(true)
         binding.surfaceView3.init(rootEglBase?.eglBaseContext, null)
         binding.surfaceView3.setEnableHardwareScaler(true)
         binding.surfaceView3.setMirror(true)

     }

     private fun initializePeerConnectionFactory() {
         val initializationOptions =
             PeerConnectionFactory.InitializationOptions.builder(this).setEnableInternalTracer(true)
                 .createInitializationOptions()
         PeerConnectionFactory.initialize(initializationOptions)
         factory = PeerConnectionFactory.builder().setVideoEncoderFactory(
             DefaultVideoEncoderFactory(
                 rootEglBase?.eglBaseContext,
                 true,
                 true
             )
         )
             .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase?.eglBaseContext))
             .createPeerConnectionFactory()
     }

     private fun createVideoTrackFromCameraAndShowIt() {

         val audioConstraints = MediaConstraints()
         val videoCapture: VideoCapturer? = createVideoCapture()
         var videoSource: VideoSource? = null

         val surfaceTextureHelper =
             SurfaceTextureHelper.create("CaptureThread", rootEglBase?.eglBaseContext)
         videoSource = factory?.createVideoSource(videoCapture?.isScreencast == true)
         videoCapture?.initialize(surfaceTextureHelper, this, videoSource?.capturerObserver)

        val  videoTrackFromCamera = factory?.createVideoTrack("localVideoTrack", videoSource)
         videoTrackFromCamera?.setEnabled(true)

         videoCapture?.startCapture(1024, 720, 30)
         videoTrackFromCamera?.addSink(binding.surfaceView)

         //create an AudioSource instance
         val audioSource = factory?.createAudioSource(audioConstraints)
         val localAudioTrack = factory?.createAudioTrack("audio101", audioSource)

         peerConnection = createPeerConnection(factory)

         val mediaStream = factory?.createLocalMediaStream("ARDAMS")
         mediaStream?.addTrack(videoTrackFromCamera)
         mediaStream?.addTrack(localAudioTrack)
         peerConnection?.addStream(mediaStream)

     }


     private fun createVideoCapture(): VideoCapturer? {

         val cameraFacing = CameraCharacteristics.LENS_FACING_FRONT

         videoCapture = if (useCamera2()) {
             createCameraCapture(Camera2Enumerator(this),cameraFacing)
         } else {
             createCameraCapture(Camera1Enumerator(true),cameraFacing)
         }
         return videoCapture

     }

     private fun useCamera2(): Boolean {
         return Camera2Enumerator.isSupported(this)
     }

     private fun switchCamera() {

         if (videoCapture is CameraVideoCapturer) {
             val cameraVideoCapture = videoCapture as CameraVideoCapturer
             cameraVideoCapture.switchCamera(null)
         }

     }

     private fun createCameraCapture(enumerator: CameraEnumerator, cameraFacing : Int): VideoCapturer? {

         val deviceNames = enumerator.deviceNames
         for (deviceName in deviceNames) {
             if (enumerator.isFrontFacing(deviceName) && cameraFacing == CameraCharacteristics.LENS_FACING_FRONT ||
                 enumerator.isBackFacing(deviceName) && cameraFacing == CameraCharacteristics.LENS_FACING_BACK
             ) {
                 val videoCapture: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                 if (videoCapture != null) {
                     return videoCapture
                 }
             }
         }
         for (deviceName in deviceNames) {
             if (!enumerator.isFrontFacing(deviceName) && cameraFacing == CameraCharacteristics.LENS_FACING_FRONT ||
                 !enumerator.isBackFacing(deviceName) && cameraFacing == CameraCharacteristics.LENS_FACING_BACK) {
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
         val rtcConfig = RTCConfiguration(iceServers)
         val pcConstraints = MediaConstraints()
         val pcObserver: PeerConnection.Observer = object : PeerConnection.Observer {
             override fun onSignalingChange(signalingState: SignalingState) {
                 Log.e("TAG", "onSignalingChange: ")
             }

             override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {
                 Log.e("TAG", "onIceConnectionChange: ")
             }

             override fun onIceConnectionReceivingChange(b: Boolean) {
                 Log.e("TAG", "onIceConnectionReceivingChange: ")
             }

             override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
                 Log.e("TAG", "onIceGatheringChange: " + iceGatheringState.name)
             }

             override fun onIceCandidate(iceCandidate: IceCandidate) {

                 val candidate = JSONObject()

                 candidate.put("candidate", iceCandidate.sdp)
                 candidate.put("sdpMid", iceCandidate.sdpMid)
                 candidate.put("sdpMLineIndex", iceCandidate.sdpMLineIndex)


                 val webRtcMessage = JSONObject().apply {

                     put("ice",candidate)
                     put("uuid",AppPreferencesDelegates.get().channelId)
                     put("dest",remoteId)
                     put("channelID",remoteId)

                 }

                 SocketManager.mSocket?.emit("webrtcMessage",webRtcMessage)


             }

             override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                 Log.e("TAG", "onIceCandidatesRemoved: ")
             }

             override fun onAddStream(mediaStream: MediaStream) {
                 Log.e("TAG", "onAddStream: " + mediaStream.videoTracks.size)
                 val remoteVideoTrack = mediaStream.videoTracks[0]
 //                val remoteVideoTrack3 = mediaStream.videoTracks[1]
                 val remoteAudioTrack = mediaStream.audioTracks[0]
 //                val remoteAudioTrack3 = mediaStream.audioTracks[1]
                 remoteAudioTrack.setEnabled(true)
                 remoteVideoTrack.setEnabled(true)
                 remoteVideoTrack.addSink(binding.surfaceView2)
                /* remoteAudioTrack3.setEnabled(true)
                remoteVideoTrack3.setEnabled(true)
                remoteVideoTrack3.addSink(binding.surfaceView3)*/
                startCallDurationTimer()
                binding.durationText.visibility = View.VISIBLE
            }

            override fun onRemoveStream(mediaStream: MediaStream) {
                Log.e("TAG", "onRemoveStream: ")
            }

            override fun onDataChannel(dataChannel: DataChannel) {
                Log.e("TAG", "onDataChannel: ")
            }

            override fun onRenegotiationNeeded() {
                Log.e("TAG", "onRenegotiationNeeded: ")

            }

            override fun onAddTrack(rtpReceiver: RtpReceiver, mediaStreams: Array<MediaStream>) {

            }
        }
        return factory?.createPeerConnection(rtcConfig, pcConstraints, pcObserver)
    }

    private fun doCall() {

        val sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToCreateVideo", "true"))
        sdpMediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToCreateAudio", "true"))

        peerConnection?.createOffer(object : CustomSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {

                peerConnection?.setLocalDescription(CustomSdpObserver(), sessionDescription)
                val signalDataJson = JSONObject()
                signalDataJson.put("type", sessionDescription.type.canonicalForm())
                signalDataJson.put("sdp", sessionDescription.description)

                Log.e("TAG", "peerConnection---$signalDataJson")

                val webRtcMessage = JSONObject().apply {

                    put("sdp", signalDataJson)
                    put("uuid", AppPreferencesDelegates.get().channelId)
                    put("dest", remoteId)
                    put("channelID", remoteId)

                }

                SocketManager.mSocket?.emit("webrtcMessage", webRtcMessage)


            }

            override fun onCreateFailure(s: String) {
                Log.e("sdpMediaConstraints-Failure", s)
            }

            override fun onSetSuccess() {
                super.onSetSuccess()
            }
        }, sdpMediaConstraints)
    }

    private fun handleRemoteVideoOffer(offer : String?) {

        val observer: SdpObserver = object : SdpObserver {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peerConnection?.setLocalDescription(this, sessionDescription)
                val signalDataJson = JSONObject()
                signalDataJson.put("type", sessionDescription.type.canonicalForm())
                signalDataJson.put("sdp", sessionDescription.description)

                Log.e("TAG", "peerConnection-+-$signalDataJson")

                val webRtcMessage = JSONObject().apply {

                    put("sdp", signalDataJson)
                    put("uuid", AppPreferencesDelegates.get().channelId)
                    put("dest", remoteId)
                    put("channelID", remoteId)

                }

                SocketManager.mSocket?.emit("webrtcMessage", webRtcMessage)



            }

            override fun onSetSuccess() {

                Log.e(
                    "TAG",
                    "Set description was successful"
                )
            }

            override fun onCreateFailure(s: String) {
                Log.e(
                    "TAG",
                    "Failed to create local answer error:$s"
                )
            }

            override fun onSetFailure(s: String) {
                Log.e(
                    "TAG",
                    "Failed to set description error:$s"
                )
            }
        }
        val sessionDescription = SessionDescription(SessionDescription.Type.OFFER, offer)
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        peerConnection?.setRemoteDescription(observer, sessionDescription)
        peerConnection?.createAnswer(observer, mediaConstraints)


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

    private fun createAnswerFromRemoteOffer(remoteOffer: String?) {

        val observer: SdpObserver = object : SdpObserver {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peerConnection?.setLocalDescription(this, sessionDescription)
                Log.e("TAG", "Local answer created")
            }

            override fun onSetSuccess() {

                Log.e(
                    "TAG",
                    "Set description was successful"
                )
            }

            override fun onCreateFailure(s: String) {
                Log.e(
                    "TAG",
                    "Failed to create local answer error:$s"
                )
            }

            override fun onSetFailure(s: String) {
                Log.e(
                    "TAG",
                    "Failed to set description error:$s"
                )
            }
        }
        val sessionDescription = SessionDescription(SessionDescription.Type.ANSWER, remoteOffer)
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        peerConnection?.setRemoteDescription(observer, sessionDescription)

    }

    @Throws(JSONException::class)
    fun addRemoteIceCandidate(iceCandidateData: JSONObject) {
        Log.e("TAG", "Check $iceCandidateData")
        val sdpMid = iceCandidateData.getString("sdpMid")
        val sdpMLineIndex = iceCandidateData.getInt("sdpMLineIndex")
        val sdp = iceCandidateData.getString("candidate")
        val iceCandidate = IceCandidate(sdpMid, sdpMLineIndex, sdp)
        Log.e(
            "TAG",
            "add remote candidate $iceCandidate"
        )
        peerConnection?.addIceCandidate(iceCandidate)
    }

    private fun stop() {

        if (localAudioTrack != null) {
            localAudioTrack = null
        }
        if (localAudioTrack != null) {
            localAudioTrack?.dispose()
        }
        if (videoCapture != null) {
            try {
                videoCapture?.stopCapture()
                videoCapture?.dispose()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        binding.surfaceView.release()
        binding.surfaceView2.release()
        binding.surfaceView3.release()
        if (peerConnection != null) {
            peerConnection?.close()
        }

        finish()
    }

    private fun startCallDurationTimer() {
        handler.post(object : Runnable {
            override fun run() {
                updateCallDuration()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun updateCallDuration() {
        callDurationInSeconds++
        val minutes = callDurationInSeconds / 60
        val seconds = callDurationInSeconds % 60
        val formattedDuration = String.format("%02d:%02d", minutes, seconds)
        binding.durationText.text = formattedDuration
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        binding.durationText.visibility = View.INVISIBLE
    }




    /*@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onCameraPermission() {

        Dexter.withContext(this@AppGroupVideoCallingActivity)
            .withPermissions(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                    if (permission?.areAllPermissionsGranted() == true) {

                        init()

                    } else {
                        AppPermissionDialog.showPermission(
                            this@AppGroupVideoCallingActivity,
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

    fun init() {

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

        binding.llVideoCallCut.setOnClickListener {

            try {

                val jsonObj = JSONObject()
                jsonObj.put("id", remoteId)
                SocketManager.mSocket?.emit("endCall", jsonObj)
                stop()
                finish()

                viewModel.callEnd(AppPreferencesDelegates.get().isCallId)


            } catch (e: Exception) {
                Log.e("TAG", "error:" + e.message)
            }

        }

        binding.llVideoCall.setOnClickListener {

            if (isSpeakerMode) {
                isSpeakerMode = false
                binding.speaker.setImageResource(R.drawable.ic_baseline_hearing_24)
                rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.EARPIECE)
            } else {
                isSpeakerMode = true
                binding.speaker.setImageResource(R.drawable.ic_baseline_speaker_up_24)
                rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
            }

        }

        binding.llMute.setOnClickListener {

            if (isMute) {
                isMute = false
                binding.mute.setImageResource(R.drawable.ic_baseline_mic_24)
                localAudioTrack?.setEnabled(true)
            } else {
                isMute = true
                binding.mute.setImageResource(R.drawable.ic_baseline_mic_off_24)
                localAudioTrack?.setEnabled(false)
            }

        }

        initializeSurfaceViews()

        initializePeerConnectionFactory()

        createVideoTrackFromCameraAndShowIt()

        initializePeerConnections()

        startStreamingVideo()

        if (callReceive == false) {
            doCall()
        }

        rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.EARPIECE)
        *//*initializePeerConnections()

        startStreamingVideo()*//*


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
        val initializationOptions =
            PeerConnectionFactory.InitializationOptions.builder(this).setEnableInternalTracer(true)
                .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        factory = PeerConnectionFactory.builder().setVideoEncoderFactory(
            DefaultVideoEncoderFactory(
                rootEglBase?.eglBaseContext,
                true,
                true
            )
        )
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase?.eglBaseContext))
            .createPeerConnectionFactory()
    }

    private fun createVideoTrackFromCameraAndShowIt() {

        val audioConstraints = MediaConstraints()
        val videoCapture: VideoCapturer? = createVideoCapture()
        var videoSource: VideoSource? = null

        val surfaceTextureHelper =
            SurfaceTextureHelper.create("CaptureThread", rootEglBase?.eglBaseContext)
        videoSource = factory?.createVideoSource(videoCapture?.isScreencast == true)
        videoCapture?.initialize(surfaceTextureHelper, this, videoSource?.capturerObserver)

        videoTrackFromCamera = factory?.createVideoTrack("localVideoTrack", videoSource)
        videoTrackFromCamera?.setEnabled(true)

        videoCapture?.startCapture(1024, 720, 30)

        //create an AudioSource instance
        audioSource = factory?.createAudioSource(audioConstraints)
        localAudioTrack = factory?.createAudioTrack("audio101", audioSource)

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

        val cameraFacing = CameraCharacteristics.LENS_FACING_FRONT

        videoCapture = if (useCamera2()) {
            createCameraCapture(Camera2Enumerator(this),cameraFacing)
        } else {
            createCameraCapture(Camera1Enumerator(true),cameraFacing)
        }
        return videoCapture

    }

    private fun useCamera2(): Boolean {
        return Camera2Enumerator.isSupported(this)
    }

    private fun switchCamera() {

        if (videoCapture is CameraVideoCapturer) {
            val cameraVideoCapture = videoCapture as CameraVideoCapturer
            cameraVideoCapture.switchCamera(null)
        }

    }

    private fun switchCamera(cameraID: Int) {

        if (videoCapture is CameraVideoCapturer) {
            val cameraVideoCapture = videoCapture as CameraVideoCapturer
            cameraVideoCapture.switchCamera(null)
        }

    }

    private fun createCameraCapture(enumerator: CameraEnumerator,cameraFacing : Int): VideoCapturer? {

        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName) && cameraFacing == CameraCharacteristics.LENS_FACING_FRONT ||
                enumerator.isBackFacing(deviceName) && cameraFacing == CameraCharacteristics.LENS_FACING_BACK
            ) {
                val videoCapture: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapture != null) {
                    return videoCapture
                }
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName) && cameraFacing == CameraCharacteristics.LENS_FACING_FRONT ||
                !enumerator.isBackFacing(deviceName) && cameraFacing == CameraCharacteristics.LENS_FACING_BACK) {
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
                Log.e("TAG", "onSignalingChange: ")
            }

            override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {

                if (iceConnectionState.name == "DISCONNECTED") {
                    finish()
                }

            }

            override fun onIceConnectionReceivingChange(b: Boolean) {
                Log.e("TAG", "onIceConnectionReceivingChange: ")
            }

            override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
                Log.e("TAG", "onIceGatheringChange: " + iceGatheringState.name)
            }

            override fun onIceCandidate(iceCandidate: IceCandidate) {

                val candidate = JSONObject()

                candidate.put("candidate", iceCandidate.sdp)
                candidate.put("sdpMid", iceCandidate.sdpMid)
                candidate.put("sdpMLineIndex", iceCandidate.sdpMLineIndex)
//                candidate.put("usernameFragment", userFragment)


                val webRtcMessage = JSONObject().apply {

                    put("ice", candidate)
                    put("uuid", AppPreferencesDelegates.get().channelId)
                    put("dest", remoteId)
                    put("channelID", remoteId)

                }

                SocketManager.mSocket?.emit("webrtcMessage", webRtcMessage)

            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                Log.e("TAG", "onIceCandidatesRemoved: ")
            }

            override fun onAddStream(mediaStream: MediaStream) {
                Log.e("TAG", "onAddStream: " + mediaStream.audioTracks.size)
                val remoteVideoTrack = mediaStream.videoTracks[0]
                val remoteAudioTrack = mediaStream.audioTracks[0]
                remoteAudioTrack.setEnabled(true)
                remoteVideoTrack.setEnabled(true)
                remoteVideoTrack.addSink(binding.surfaceView2)
                startCallDurationTimer()
                binding.durationText.visibility = View.VISIBLE
                startCallDurationTimer()

            }

            override fun onRemoveStream(mediaStream: MediaStream) {
                Log.e("TAG", "onRemoveStream: ")
            }

            override fun onDataChannel(dataChannel: DataChannel) {
                Log.e("TAG", "onDataChannel: ")
            }

            override fun onRenegotiationNeeded() {
                Log.e("TAG", "onRenegotiationNeeded: ")

            }

            override fun onAddTrack(rtpReceiver: RtpReceiver, mediaStreams: Array<MediaStream>) {

            }
        }
        return factory?.createPeerConnection(rtcConfig, pcConstraints, pcObserver)
    }

    private fun doCall() {

        val sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "OfferToCreateVideo",
                "true"
            )
        )
        sdpMediaConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "OfferToCreateAudio",
                "true"
            )
        )

        peerConnection?.createOffer(object : CustomSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {

                peerConnection?.setLocalDescription(CustomSdpObserver(), sessionDescription)
                val signalDataJson = JSONObject()
                signalDataJson.put("type", sessionDescription.type.canonicalForm())
                signalDataJson.put("sdp", sessionDescription.description)

                Log.e("TAG", "peerConnection---$signalDataJson")

                val webRtcMessage = JSONObject().apply {

                    put("sdp", signalDataJson)
                    put("uuid", AppPreferencesDelegates.get().channelId)
                    put("dest", remoteId)
                    put("channelID", remoteId)

                }

                SocketManager.mSocket?.emit("webrtcMessage", webRtcMessage)

            }

            override fun onCreateFailure(s: String) {
                Log.e("sdpMediaConstraints-Failure", s)
            }

            override fun onSetSuccess() {
                super.onSetSuccess()
            }
        }, sdpMediaConstraints)
    }

    private fun handleRemoteVideoOffer(offer: String?) {

        val observer: SdpObserver = object : SdpObserver {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peerConnection?.setLocalDescription(this, sessionDescription)
                val signalDataJson = JSONObject()
                signalDataJson.put("type", sessionDescription.type.canonicalForm())
                signalDataJson.put("sdp", sessionDescription.description)

                Log.e("TAG", "peerConnection-+-$signalDataJson")

                val webRtcMessage = JSONObject().apply {

                    put("sdp", signalDataJson)
                    put("uuid", AppPreferencesDelegates.get().channelId)
                    put("dest", remoteId)
                    put("channelID", remoteId)

                }

                SocketManager.mSocket?.emit("webrtcMessage", webRtcMessage)

            }

            override fun onSetSuccess() {

                Log.e(
                    "TAG",
                    "Set description was successful"
                )
            }

            override fun onCreateFailure(s: String) {
                Log.e(
                    "TAG",
                    "Failed to create local answer error:$s"
                )
            }

            override fun onSetFailure(s: String) {
                Log.e(
                    "TAG",
                    "Failed to set description error:$s"
                )
            }
        }
        val sessionDescription = SessionDescription(SessionDescription.Type.OFFER, offer)
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        peerConnection?.setRemoteDescription(observer, sessionDescription)
        peerConnection?.createAnswer(observer, mediaConstraints)


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
            *//*if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init()
            } else {
                onCameraPermission()
            }*//*
        }
    }

    private fun onCallReceive() {}

    private fun extractUsernameFragment(sdp: String): String {
        val lines = sdp.split("\r\n")
        for (line in lines) {
            if (line.startsWith("a=ice-ufrag:")) {
                return line.substring("a=ice-ufrag:".length)
            }
        }
        return ""
    }

    private fun createAnswerFromRemoteOffer(remoteOffer: String?) {

        val observer: SdpObserver = object : SdpObserver {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peerConnection?.setLocalDescription(this, sessionDescription)
                Log.e("TAG", "Local answer created")
            }

            override fun onSetSuccess() {

                Log.e(
                    "TAG",
                    "Set description was successful"
                )
            }

            override fun onCreateFailure(s: String) {
                Log.e(
                    "TAG",
                    "Failed to create local answer error:$s"
                )
            }

            override fun onSetFailure(s: String) {
                Log.e(
                    "TAG",
                    "Failed to set description error:$s"
                )
            }
        }
        val sessionDescription = SessionDescription(SessionDescription.Type.ANSWER, remoteOffer)
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        peerConnection?.setRemoteDescription(observer, sessionDescription)

    }

    @Throws(JSONException::class)
    fun addRemoteIceCandidate(iceCandidateData: JSONObject) {
        Log.e("TAG", "Check $iceCandidateData")
        val sdpMid = iceCandidateData.getString("sdpMid")
        val sdpMLineIndex = iceCandidateData.getInt("sdpMLineIndex")
        val sdp = iceCandidateData.getString("candidate")
        val iceCandidate = IceCandidate(sdpMid, sdpMLineIndex, sdp)
        Log.e(
            "TAG",
            "add remote candidate $iceCandidate"
        )
        peerConnection?.addIceCandidate(iceCandidate)
    }

    private fun stop() {

        if (localAudioTrack != null) {
            localAudioTrack = null
        }

        if (localAudioTrack != null) {
            localAudioTrack = null
        }

        if (videoCapture != null) {
            try {
                videoCapture?.stopCapture()
                videoCapture?.dispose()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        if (peerConnection != null) {
            peerConnection?.close()
        }

        finish()
    }

    private fun startCallDurationTimer() {
        handler.post(object : Runnable {
            override fun run() {
                updateCallDuration()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun updateCallDuration() {
        callDurationInSeconds++
        val minutes = callDurationInSeconds / 60
        val seconds = callDurationInSeconds % 60
        val formattedDuration = String.format("%02d:%02d", minutes, seconds)
        binding.durationText.text = formattedDuration
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }*/


}