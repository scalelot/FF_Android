package com.festum.festumfield.verstion.firstmodule.screens.main.streaming

import android.Manifest
import android.util.Log
import android.view.View
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ActivityWebrtcBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.webrtc.Connection
import com.festum.festumfield.verstion.firstmodule.sources.webrtc.ConnectionListener
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaStreamTrack
import org.webrtc.RendererCommon.RendererEvents
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class WebrtcActivity : BaseActivity<ChatViewModel>(), ConnectionListener {

    private lateinit var binding: ActivityWebrtcBinding

    private val CAMERA_AND_MIC = 1001

    private var remoteId: String? = null

    private var mConnection: Connection? = null

    var remoteUserName = ""
    override fun getContentView(): View {
        binding = ActivityWebrtcBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        remoteId = intent.getStringExtra("toUser")

        val webRtcMessage = JSONObject().apply {

            put("displayName", AppPreferencesDelegates.get().userName)
            put("uuid", AppPreferencesDelegates.get().channelId)
            put("dest", "all")
            put("channelID", remoteId)

        }

        SocketManager.mSocket?.emit("webrtcMessage", webRtcMessage)


        SocketManager.mSocket?.on("webrtcMessage") { args ->

            val receiverData = args[0] as JSONObject

            handleWebSocketMessage(receiverData)

        }

        mConnection = Connection.initialize(this, this)

        mConnection?.initializeMediaDevices(this, binding.localRenderer)

        mConnection?.createPeerConnection()

        mConnection?.createOffer()


//        initializeLogoutButton()

    }

    override fun setupObservers() {

    }

    private fun connectToWebsocketServer() {

//        requestCameraAndMicAccess()

    }

    private fun initializeLogoutButton() {

    }

    private fun getUserMedia() {
    }

    @AfterPermissionGranted(1001)
    private fun requestCameraAndMicAccess() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *permissions)) {
            Log.d("TAG", "media permissions granted")
            runOnUiThread { getUserMedia() }
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.request_camera_mic_permissions_text),
                CAMERA_AND_MIC,
                *permissions
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /*private fun sendSocketMessage(action: String, data: JSONObject) {
        try {
            SocketManager.mSocket?.emit(action,data)
        } catch (je: JSONException) {
            Log.e("TAG", je.toString())
        }
    }*/

    override fun onIceCandidateReceived(iceCandidate: IceCandidate?) {

        Log.e("TAG", "onIceCandidateReceived" + iceCandidate?.sdp)

        try {
            val candidate = JSONObject()
            candidate.put("candidate", iceCandidate?.sdp)
            candidate.put("sdpMid", iceCandidate?.sdpMid)
            candidate.put("sdpMLineIndex", iceCandidate?.sdpMLineIndex)

            val webRtcMessage = JSONObject().apply {

                put("ice", candidate)
                put("uuid", AppPreferencesDelegates.get().channelId)
                put("dest", remoteId)
                put("channelID", remoteId)

            }

            SocketManager.mSocket?.emit("webRtcMessage", webRtcMessage)

        } catch (je: JSONException) {
            Log.e("TAG", "Failed to handle onIceCandidate event", je)
        }
    }

    override fun onAddStream(mediaStreamTrack: MediaStreamTrack?) {

        mediaStreamTrack?.setEnabled(true)

        Log.e("TAG", "add video")

        if (mediaStreamTrack?.kind() == "video") {

            val videoTrack = mediaStreamTrack as VideoTrack
            runOnUiThread {
                val eglContext = EglBase.create().eglBaseContext
                binding.remoteRenderer.init(eglContext, object : RendererEvents {
                    override fun onFirstFrameRendered() {}
                    override fun onFrameResolutionChanged(i: Int, i1: Int, i2: Int) {}
                })
                videoTrack.addSink(binding.remoteRenderer)
            }
        }
    }

    override fun onLocalOffer(offer: SessionDescription?) {

        Log.e("TAG", "onLocalOffer" + offer?.description.toString())

        try {
            val sdp = JSONObject()
            sdp.put("type", "offer")
            sdp.put("sdp", offer?.description)

            val webRtcMessage = JSONObject().apply {

                put("sdp", sdp)
                put("uuid", AppPreferencesDelegates.get().channelId)
                put("dest", remoteId)
                put("channelID", remoteId)

            }

            SocketManager.mSocket?.emit("webRtcMessage", webRtcMessage)

        } catch (je: JSONException) {
            Log.e("TAG", "Failed to handle onLocalOffer", je)

        }
    }

    override fun onLocalAnswer(answer: SessionDescription?) {

        Log.e("TAG", "onLocalAnswer" + answer?.type)

        try {
            val sdp = JSONObject()
            sdp.put("type", "answer")
            sdp.put("sdp", answer?.description)

            val webRtcMessage = JSONObject().apply {

                put("sdp", sdp)
                put("uuid", remoteId)
                put("dest", AppPreferencesDelegates.get().channelId)
                put("channelID", AppPreferencesDelegates.get().channelId)

            }

            SocketManager.mSocket?.emit("webRtcMessage", webRtcMessage)

        } catch (je: JSONException) {
            Log.e("TAG", "Failed to handle onLocalAnswer", je)
        }

    }

    private fun handleWebSocketMessage(message: JSONObject) {


        Log.e("TAG", "message:---- $remoteUserName")

        if (message.optString("displayName").isNotEmpty()) {

            remoteUserName = message.optString("displayName")

            Log.e("TAG", "displayName:---- $remoteUserName")

        }

        if (message.optJSONObject("sdp") != null) {

            val sdpOffer = message.optJSONObject("sdp")

            Log.e("TAG", "sdpOffer:---- $sdpOffer")

            when (sdpOffer.optString("type")) {

                "offer" -> {
                    mConnection?.createAnswerFromRemoteOffer(remoteId)
                    Log.e("TAG", "offer:---- $remoteId")
                }

                "answer" -> {

                    mConnection?.createAnswerFromRemoteOffer(sdpOffer.optString("sdp"))

                    Log.e("TAG", "answer:----  " + sdpOffer.optString("sdp"))

                }

                else -> Log.e("TAG", "WebSocket unknown action")
            }

        }

        if (message.optJSONObject("ice") != null) {

            val iceCandidate = message.optJSONObject("ice")

            mConnection?.addRemoteIceCandidate(iceCandidate)

            Log.e("TAG", "iceCandidate:----  $iceCandidate")

        }
    }

}