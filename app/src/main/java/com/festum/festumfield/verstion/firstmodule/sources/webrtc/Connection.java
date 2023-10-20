package com.festum.festumfield.verstion.firstmodule.sources.webrtc;

import android.content.Context;
import android.util.Log;

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
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;

public class Connection implements PeerConnection.Observer {

    private static final String MEDIA_STREAM_ID = "ARDAMS";
    private static final String VIDEO_TRACK_ID = "ARDAMSv0";
    private static final String AUDIO_TRACK_ID = "ARDAMSa0";
    private static final int VIDEO_HEIGHT = 250;
    private static final int VIDEO_WIDTH = 250;
    private static final int VIDEO_FPS = 30;

    private static final String STUN_SERVER_URL = "stun:stun.l.google.com:19302";
    private static final String STUN_SERVER_URL_SECOND = "stun:stun.stunprotocol.org:3478";

    private static Connection INSTANCE = null;
    private final PeerConnectionFactory mFactory;
    private PeerConnection mPeerConnection;
    private MediaStream mMediaStream;
    private VideoCapturer mVideoCapturer;
    private final ConnectionListener mListener;

    private Connection(final Context context, final ConnectionListener listener) {
        final PeerConnectionFactory.InitializationOptions options = PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions();
        final EglBase.Context eglContext = EglBase.create().getEglBaseContext();
        final VideoEncoderFactory encoderFactory = new SoftwareVideoEncoderFactory();
        final VideoDecoderFactory decoderFactory = new SoftwareVideoDecoderFactory();

        PeerConnectionFactory.initialize(options);
        mFactory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
        mListener = listener;
    };

    public static synchronized Connection initialize(final Context context, final ConnectionListener listener) {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new Connection(context, listener);
        return INSTANCE;
    }

    public void initializeMediaDevices(final Context context, final SurfaceViewRenderer localRenderer) throws Exception {
        mMediaStream = mFactory.createLocalMediaStream(MEDIA_STREAM_ID);

        mVideoCapturer = createVideoCapturer(context);

        final VideoSource videoSource = mFactory.createVideoSource(false);
        final EglBase.Context eglContext = EglBase.create().getEglBaseContext();
        final SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("captureThread", eglContext);

        // Video capturer and localRenderer needs to be initialized
        mVideoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
        localRenderer.init(eglContext, new RendererCommon.RendererEvents() {
            @Override
            public void onFirstFrameRendered() {
//                Log.e("TAG", "onFirstFrameRendered");
            }

            @Override
            public void onFrameResolutionChanged(int i, int i1, int i2) {
//                Log.e("TAG", "Frame resolution changed");
            }
        });

        mVideoCapturer.startCapture(VIDEO_WIDTH, VIDEO_HEIGHT, VIDEO_FPS);

        final VideoTrack videoTrack = mFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrack.setEnabled(true);
        videoTrack.addSink(localRenderer);

        final AudioSource audioSource = mFactory.createAudioSource(new MediaConstraints());
        final AudioTrack audioTrack = mFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        audioTrack.setEnabled(true);

        mMediaStream.addTrack(videoTrack);
        mMediaStream.addTrack(audioTrack);
//        Log.e("TAG", "media devices initialized");
    }

    public void createOffer() {
        final MediaConstraints mediaConstraints = new MediaConstraints();

        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));


        for (final MediaStreamTrack videoTrack: mMediaStream.videoTracks) {
            mPeerConnection.addTrack(videoTrack);
        }

        for (final MediaStreamTrack audioTrack: mMediaStream.audioTracks) {
            mPeerConnection.addTrack(audioTrack);
        }

        mPeerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
//                Log.e("TAG", "Local offer created:" + sessionDescription.description);
                mPeerConnection.setLocalDescription(this, sessionDescription);
            }

            @Override
            public void onSetSuccess() {
//                Log.e("TAG", "Local description set success");

                mListener.onLocalOffer(mPeerConnection.getLocalDescription());
            }

            @Override
            public void onCreateFailure(String s) {
//                Log.e("TAG", "Failed to create local offer error:" + s);
            }

            @Override
            public void onSetFailure(String s) {
//                Log.e("TAG", "Failed to set local description error:" + s);
            }
        }, mediaConstraints);
    }

    public void createAnswerFromRemoteOffer(final String remoteOffer) {
        // Reuse the SdpObserver
        final SdpObserver observer = new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
//                Log.e("TAG", "Local answer created");
                mListener.onLocalAnswer(sessionDescription);
                mPeerConnection.setLocalDescription(this,sessionDescription);
            }

            @Override
            public void onSetSuccess() {
//                Log.e("TAG", "Set description was successful");
            }

            @Override
            public void onCreateFailure(String s) {
//                Log.e("TAG", "Failed to create local answer error:" + s);
            }

            @Override
            public void onSetFailure(String s) {
//                Log.e("TAG", "Failed to set description error:" + s);
            }
        };

        final SessionDescription sessionDescription = new SessionDescription(SessionDescription.Type.ANSWER, remoteOffer);
        final MediaConstraints mediaConstraints = new MediaConstraints();

        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));

        mPeerConnection.setRemoteDescription(observer, sessionDescription);
        mPeerConnection.createAnswer(observer, mediaConstraints);
    }

    public void addRemoteIceCandidate(final JSONObject iceCandidateData) throws JSONException {
//        Log.e("TAG", "Check " + iceCandidateData.toString());
        final String sdpMid = iceCandidateData.getString("sdpMid");
        final int sdpMLineIndex = iceCandidateData.getInt("sdpMLineIndex");
        String sdp = iceCandidateData.getString("candidate");

        final IceCandidate iceCandidate = new IceCandidate(sdpMid, sdpMLineIndex, sdp);
//        Log.e("TAG", "add remote candidate " + iceCandidate.toString());
        mPeerConnection.addIceCandidate(iceCandidate);
    }

    public void close() {
        for (AudioTrack audioTrack : mMediaStream.audioTracks) {
            audioTrack.setEnabled(false);
        }

        for (VideoTrack videoTrack : mMediaStream.videoTracks) {
            videoTrack.setEnabled(false);
        }

        try {
            mVideoCapturer.stopCapture();
        } catch (InterruptedException ie) {
//            Log.e("TAG", "Failed to stop capture", ie);
        }

        mPeerConnection.close();
        mFactory.dispose();
    }

    private VideoCapturer createVideoCapturer(final Context context) throws Exception {
        final boolean isUseCamera2 = Camera2Enumerator.isSupported(context);
        final CameraEnumerator cameraEnumerator = isUseCamera2 ? new Camera2Enumerator(context) : new Camera1Enumerator(true);

        final String[] deviceNames = cameraEnumerator.getDeviceNames();

        for (final String deviceName : deviceNames) {
//            Log.e("TAG", "Found device: " + deviceName);
            if (cameraEnumerator.isFrontFacing(deviceName)) {
//                Log.e("TAG", "Found front device");
                return cameraEnumerator.createCapturer(deviceName, null);
            }
        }

        throw new Exception("Failed to get camera device");
    }

    public void createPeerConnection() {
        if (mPeerConnection != null) return;

        final ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder(STUN_SERVER_URL_SECOND).createIceServer());
        iceServers.add(PeerConnection.IceServer.builder(STUN_SERVER_URL).createIceServer());

        mPeerConnection = mFactory.createPeerConnection(iceServers, this);
//        Log.e("TAG", "Peer Connection created");
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
//        Log.e("TAG", "onAddStream");
    }

    @Override
    public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {
//        Log.e("TAG", "onAddTrack");
        mListener.onAddStream(receiver.track());
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
//        Log.e("TAG", "onIceConnectionReceivingChange");
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
//        Log.e("TAG", "onIceGatheringChange state=" + iceGatheringState.toString());
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
//        Log.e("TAG", "onDataChannel");
    }

    @Override
    public void onRenegotiationNeeded() {
//        Log.e("TAG", "onRenegotiationNeeded");
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
//        Log.e("TAG", "onIceCandidate");

        mListener.onIceCandidateReceived(iceCandidate);
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
//        Log.e("TAG", "onSignalingChange state=" + signalingState.toString());
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
//        Log.e("TAG", "onIceCandidatesRemoved");
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
//        Log.e("TAG", "onIceConnectionChange state=" + iceConnectionState.toString());
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
//        Log.e("TAG", "onRemoveStream");
    }
}
