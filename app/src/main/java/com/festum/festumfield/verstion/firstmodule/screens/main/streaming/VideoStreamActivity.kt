package com.festum.festumfield.verstion.firstmodule.screens.main.streaming

import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.codewithkael.webrtcprojectforrecord.models.IceCandidateModel
import com.festum.festumfield.databinding.ActivityVideoStreamBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.webrtc.utils.PeerConnectionObserver
import com.festum.festumfield.verstion.firstmodule.sources.webrtc.utils.RTCAudioManager
import com.festum.festumfield.verstion.firstmodule.sources.webrtc.utils.RTCClient
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription

@AndroidEntryPoint
class VideoStreamActivity : BaseActivity<ChatViewModel>() {

    private lateinit var binding: ActivityVideoStreamBinding

    private var remoteId: String? = null
    private var remoteUser: String? = null
    private var rtcClient : RTCClient?=null

    private val rtcAudioManager by lazy { RTCAudioManager.create(this) }

    override fun getContentView(): View {
        binding = ActivityVideoStreamBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        remoteId = intent.getStringExtra("remoteChannelId")
        remoteUser = intent.getStringExtra("remoteUser")
        val bundle = intent.extras
        val callReceive = bundle?.getBoolean("callReceive")

        PermissionX.init(this)
            .permissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            ).request{ allGranted, _ ,_ ->
                if (allGranted){
                    if (callReceive == true){

                        callReceive()

                        Log.e("TAG", "callReceive:----- $callReceive")

                    } else {

                        calling()

                        Log.e("TAG", "calling:----- $callReceive")

                    }

                    webRtcMessage()

                } else {
                    Toast.makeText(this,"you should accept all permissions", Toast.LENGTH_LONG).show()
                }
            }

        Log.e("TAG", "setupUi:----- $remoteId")
        Log.e("TAG", "setupUi:----- $remoteUser")
        Log.e("TAG", "setupUi:----- $callReceive")



    }

    override fun setupObservers() {

    }

    private fun callReceive(){

        val webRtcMessage = JSONObject().apply {

            put("displayName", AppPreferencesDelegates.get().userName)
            put("uuid", AppPreferencesDelegates.get().channelId)
            put("dest", "all")
            put("channelID", remoteId?.lowercase())

        }

        SocketManager.mSocket?.emit("webrtcMessage", webRtcMessage)

    }

    private fun calling() {

        val webRtcMessage = JSONObject().apply {

            put("displayName",remoteUser)
            put("uuid", AppPreferencesDelegates.get().channelId)
            put("dest","all")
            put("channelID", remoteId?.lowercase())

        }

        SocketManager.mSocket?.emit("webrtcMessage",webRtcMessage)

    }

    private fun webRtcMessage() {

        SocketManager.mSocket?.on("webrtcMessage"){ args ->

            val receiverData = args[0] as JSONObject

            if (receiverData.optString("displayName").isNotEmpty()){

                runOnUiThread {
                    binding.apply {
                        rtcClient?.initializeSurfaceView(localView)
                        rtcClient?.initializeSurfaceView(remoteView)
                        rtcClient?.startLocalVideo(localView)
                    }


                }

            }

            if (receiverData.optJSONObject("ice") != null){

                val gson = Gson()

                try {
                    val receivingCandidate = gson.fromJson(gson.toJson(""),
                        IceCandidateModel::class.java)
                    rtcClient?.addIceCandidate(IceCandidate(receivingCandidate.sdpMid,
                        Math.toIntExact(receivingCandidate.sdpMLineIndex.toLong()),receivingCandidate.sdpCandidate))
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

            if (receiverData.optString("type").isNotEmpty() || receiverData.optString("sdp").isNotEmpty()){

                Log.e("TAG", "callingSdp:----------$receiverData")

                /*"answer_received" ->{

                    val session = SessionDescription(
                        SessionDescription.Type.ANSWER,
                        message.data.toString()
                    )
                    rtcClient?.onRemoteSessionReceived(session)
                    runOnUiThread {
                        binding.remoteViewLoading.visibility = View.GONE
                    }
                }
                "offer_received" ->{
                    runOnUiThread {
                        setIncomingCallLayoutVisible()
                        binding.incomingNameTV.text = "${message.name.toString()} is calling you"
                        binding.acceptButton.setOnClickListener {
                            setIncomingCallLayoutGone()
                            setCallLayoutVisible()
                            setWhoToCallLayoutGone()

                            binding.apply {
                                rtcClient?.initializeSurfaceView(localView)
                                rtcClient?.initializeSurfaceView(remoteView)
                                rtcClient?.startLocalVideo(localView)
                            }
                            val session = SessionDescription(
                                SessionDescription.Type.OFFER,
                                message.data.toString()
                            )
                            rtcClient?.onRemoteSessionReceived(session)
                            rtcClient?.answer(message.name!!)
                            target = message.name!!
                            binding.remoteViewLoading.visibility = View.GONE

                        }
                        binding.rejectButton.setOnClickListener {
                            setIncomingCallLayoutGone()
                        }

                    }

                }*/

            }

        }

    }

    private fun init(){


        rtcClient = RTCClient(application,remoteUser,SocketManager, object : PeerConnectionObserver() {
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                rtcClient?.addIceCandidate(p0)
                val candidate = hashMapOf(
                    "sdpMid" to p0?.sdpMid,
                    "sdpMLineIndex" to p0?.sdpMLineIndex,
                    "sdpCandidate" to p0?.sdp
                )

                /*socketRepository?.sendMessageToSocket(
                    MessageModel("ice",userName,target,candidate)
                )*/

               /* val candidate = JSONObject()

                candidate.put("candidate", iceCandidate.sdp)
                candidate.put("sdpMid", "0")
                candidate.put("sdpMLineIndex", 1)
                candidate.put("usernameFragment", userFragment)*/

                val webRtcMessage = JSONObject().apply {

                    put("ice",candidate)
                    put("uuid",AppPreferencesDelegates.get().channelId)
                    put("dest",remoteId)
                    put("channelID",remoteId)

                }

                SocketManager.mSocket?.emit("webRtcMessage",webRtcMessage)

            }

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                p0?.videoTracks?.get(0)?.addSink(binding.remoteView)
                Log.d("TAG", "onAddStream: $p0")

            }
        })
        rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)

    }



}