package com.festum.festumfield.verstion.firstmodule.screens.webrtc;

import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;
import static io.socket.client.Socket.EVENT_CONNECT;
import static io.socket.client.Socket.EVENT_DISCONNECT;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.databinding.ActivitySamplePeerConnectionBinding;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
//import org.webrtc.VideoRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import io.socket.client.IO;
import io.socket.client.Socket;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CompleteActivity extends AppCompatActivity {
    private static final String TAG = "CompleteActivity";
    private static final int RC_CALL = 111;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;

    private Socket socket;
    private boolean isInitiator;
    private boolean isChannelReady;
    private boolean isStarted;


    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    MediaConstraints sdpConstraints;
    VideoSource videoSource;
    VideoTrack localVideoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;
    SurfaceTextureHelper surfaceTextureHelper;

    private ActivitySamplePeerConnectionBinding binding;
    private PeerConnection peerConnection;
    private EglBase rootEglBase;
    private PeerConnectionFactory factory;
    private VideoTrack videoTrackFromCamera;

    private String remoteId;
    private String remoteUser;
    private Boolean callReceive;

    //Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySamplePeerConnectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        remoteId = getIntent().getStringExtra("remoteChannelId");
        remoteUser = getIntent().getStringExtra("remoteUser");
        Bundle bundle = getIntent().getExtras();
        callReceive = bundle != null ? bundle.getBoolean("callReceive") : null;

        start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        if (socket != null) {
//            sendMessage("bye");
            socket.disconnect();
        }
        super.onDestroy();
    }

    @AfterPermissionGranted(RC_CALL)
    private void start() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            connectToSignallingServer();

            initializeSurfaceViews();

            initializePeerConnectionFactory();

            createVideoTrackFromCameraAndShowIt();

            initializePeerConnections();

            startStreamingVideo();

        } else {

            EasyPermissions.requestPermissions(this, "Need some permissions", RC_CALL, perms);

        }
    }

    private void connectToSignallingServer() {

        Objects.requireNonNull(SocketManager.INSTANCE.getMSocket()).on("all", args -> {
            Log.e("TAG", "connectToSignallingServer: full");
            isInitiator = true;
        }).on("full", args -> {
            Log.e("TAG", "connectToSignallingServer: full");
        }).on("join", args -> {
            Log.e("TAG", "connectToSignallingServer: join");
            Log.e("TAG", "connectToSignallingServer: Another peer made a request to join room");
            Log.e("TAG", "connectToSignallingServer: This peer is the initiator of room");
            isChannelReady = true;
        }).on("joined", args -> {
            Log.e("TAG", "connectToSignallingServer: joined");
            isChannelReady = true;
        }).on("log", args -> {
            for (Object arg : args) {
                Log.e("TAG", "connectToSignallingServer: " + String.valueOf(arg));
            }
        }).on("message", args -> {
            Log.e("TAG", "connectToSignallingServer: got a message");
        }).on("webrtcMessage", args -> {

            JSONObject webrtcMessage = (JSONObject) args[0];

            String displayName = webrtcMessage.optString("displayName");
            String uuid = webrtcMessage.optString("uuid");
            String dest = webrtcMessage.optString("dest");
            String channelID = webrtcMessage.optString("channelID");


            Log.e("TAG", "channelID" + webrtcMessage);


            isInitiator = uuid.equalsIgnoreCase(remoteId);

            isChannelReady = true;

            try {
                if (uuid.equalsIgnoreCase(remoteId)) {

                    maybeStart();

                    Log.e("TAG", "remoteId----" + remoteId);
                } else {
                    JSONObject message = (JSONObject) args[0];
                    Log.e("TAG", "connectToSignallingServer: got message " + message);
                    if (message.getString("type").equals("offer")) {
                        Log.e("TAG", "connectToSignallingServer: received an offer " + isInitiator + " " + isStarted);
                        if (!isInitiator && !isStarted) {
                            maybeStart();
                        }
                        peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, message.getString("sdp")));
                        doAnswer();
                    }
                    if (message.getString("type").equals("answer") && isStarted) {
                        peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, message.getString("sdp")));
                    }
                    if (message.getString("type").equals("candidate") && isStarted) {
                        Log.e("TAG", "connectToSignallingServer: receiving candidates");
                        IceCandidate candidate = new IceCandidate(message.getString("sdpMid"), message.getInt("sdpMLineIndex"), message.getString("candidate"));
                        peerConnection.addIceCandidate(candidate);
                    }
                    else if (message.equals("bye") && isStarted) {
//                        handleRemoteHangup();
                }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).on(EVENT_DISCONNECT, args -> {
            Log.e("TAG", "connectToSignallingServer: disconnect");
        });

    }
//MirtDPM4
    private void doAnswer() {
        peerConnection.createAnswer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                JSONObject sdp = new JSONObject();


                JSONObject message = new JSONObject();
                try {
                    message.put("type", "answer");
                    message.put("sdp", sessionDescription.description);
//                    message.put("sdp",sdp);
                    message.put("uuid", remoteId.toLowerCase());
                    message.put("dest",AppPreferencesDelegates.Companion.get().getChannelId());
                    message.put("channelID",AppPreferencesDelegates.Companion.get().getChannelId());
                    sendMessage(message);

                } catch (JSONException e) {
                    Log.e("TAG", "onCreateSuccess:--- " + e.toString() );
                    e.printStackTrace();
                }
            }
        }, new MediaConstraints());
    }

    private void maybeStart() {
        Log.e("TAG", "maybeStart: " + isStarted + " " + isChannelReady);
        if (!isStarted && isChannelReady) {
            isStarted = true;
            if (isInitiator) {
                doCall();
            }
        }

    }

    private void doCall() {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();

        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        peerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.e("TAG", "onCreateSuccess: ");
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                JSONObject sdp = new JSONObject();
                JSONObject message = new JSONObject();

                Log.e("TAG", "onCreateSuccess:--- " + sessionDescription.description);
                try {
                    sdp.put("type", "offer");
                    sdp.put("sdp", sessionDescription.description);
//                    message.put("sdp",sdp);
                    message.put("uuid", AppPreferencesDelegates.Companion.get().getChannelId());
                    message.put("dest",remoteId.toLowerCase());
                    message.put("channelID",remoteId.toLowerCase());
                    sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, sdpMediaConstraints);
    }

    private void sendMessage(Object message) {
        Objects.requireNonNull(SocketManager.INSTANCE.getMSocket()).emit("webrtcMessage", message);
    }

    private void initializeSurfaceViews() {
        rootEglBase = EglBase.create();
        binding.surfaceView.init(rootEglBase.getEglBaseContext(), null);
        binding.surfaceView.setEnableHardwareScaler(true);
        binding.surfaceView.setMirror(true);

        binding.surfaceView2.init(rootEglBase.getEglBaseContext(), null);
        binding.surfaceView2.setEnableHardwareScaler(true);
        binding.surfaceView2.setMirror(true);

        //add one more
    }

    private void initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        factory = new PeerConnectionFactory(null);
        factory.setVideoHwAccelerationOptions(rootEglBase.getEglBaseContext(), rootEglBase.getEglBaseContext());
    }

    private void createVideoTrackFromCameraAndShowIt() {
        audioConstraints = new MediaConstraints();
        VideoCapturer videoCapturer = createVideoCapturer();
        VideoSource videoSource = factory.createVideoSource(videoCapturer);
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackFromCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrackFromCamera.setEnabled(true);
        videoTrackFromCamera.addRenderer(new VideoRenderer(binding.surfaceView));

        //create an AudioSource instance
        audioSource = factory.createAudioSource(audioConstraints);
        localAudioTrack = factory.createAudioTrack("audio101", audioSource);

    }

    private void initializePeerConnections() {
        peerConnection = createPeerConnection(factory);
    }

    private void startStreamingVideo() {
        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(videoTrackFromCamera);
        mediaStream.addTrack(localAudioTrack);
        peerConnection.addStream(mediaStream);

//        sendMessage("got user media");
    }

    private PeerConnection createPeerConnection(PeerConnectionFactory factory) {
        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        String URL1 = "stun:stun.l.google.com:19302";
        String URL2 = "stun:stun.stunprotocol.org:3478";
        iceServers.add(new PeerConnection.IceServer(URL1));
        iceServers.add(new PeerConnection.IceServer(URL2));

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        MediaConstraints pcConstraints = new MediaConstraints();

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.e("TAG", "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.e("TAG", "onIceConnectionChange: ");
            }


            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.e("TAG", "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.e("TAG", "onIceGatheringChange: ");
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.e("TAG", "onIceCandidate: ");
                JSONObject ice = new JSONObject();
                JSONObject message = new JSONObject();

                try {

//                    message.put("type", "candidate");
                    message.put("candidate", iceCandidate.sdp);
                    message.put("sdpMid", iceCandidate.sdpMid);
                    message.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
                    message.put("iec", ice);
                    message.put("uuid", AppPreferencesDelegates.Companion.get().getChannelId());
                    message.put("dest",remoteId.toLowerCase());
                    message.put("channelID",remoteId.toLowerCase());

                    Log.e("TAG", "onIceCandidate: sending candidate " + message);
                    sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.e("TAG", "onIceCandidatesRemoved: ");
            }


            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.e("TAG", "onAddStream: " + mediaStream.videoTracks.size());
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                AudioTrack remoteAudioTrack = mediaStream.audioTracks.get(0);
                remoteAudioTrack.setEnabled(true);
                remoteVideoTrack.setEnabled(true);
                remoteVideoTrack.addRenderer(new VideoRenderer(binding.surfaceView2));

            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.e("TAG", "onRemoveStream: ");
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.e("TAG", "onDataChannel: ");
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.e("TAG", "onRenegotiationNeeded: ");
            }


        };

        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this);
    }

}
