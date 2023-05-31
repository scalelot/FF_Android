package com.festum.festumfield;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

//import com.bokecc.camerafilter.LocalVideoFilter;
import com.festum.festumfield.Activity.ChatingActivity;
import com.festum.festumfield.Utils.Constans;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.CacheEvictor;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.WebSocket;

public class MyApplication extends Application implements LifecycleObserver {

    public static final String CHANNEL_ID = "exampleChannel";
    public static SimpleCache simpleCache;
    public static Context context;
    private static int stateCounter;
    public static final String TAG = MyApplication.class.getSimpleName();
    public static Socket mSocket;
    SharedPreferences sharedPreferences;
    private static MyApplication singleton = null;
    public static MyApplication getInstance() {

        if (singleton == null) {
            singleton = new MyApplication();
        }
        return singleton;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        stateCounter = 0;

        LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024);
        DatabaseProvider databaseProvider = (DatabaseProvider) (new ExoDatabaseProvider((Context) this));
        if (simpleCache == null) {
            simpleCache = new SimpleCache(this.getCacheDir(), (CacheEvictor) leastRecentlyUsedCacheEvictor, databaseProvider);
        }

        createNotificationChannel();

        //ReelsActivity SharedPreference
        sharedPreferences = getSharedPreferences("Reels", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        sharedPreferences = getSharedPreferences("countUser", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.clear();
        editor1.commit();

        //Socket
        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.reconnection = true;
        options.reconnectionDelay = 2000;
        options.reconnectionDelayMax = 5000;

        try {
            mSocket = IO.socket("https://api.festumfield.com", options);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.io().on(Manager.EVENT_TRANSPORT, onTransport);


            if(!mSocket.connected()){
                mSocket.connect();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    private final Emitter.Listener onTransport = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Transport transport = (Transport)args[0];
            transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    @SuppressWarnings("unchecked")
                    Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                    String authToken = "";
                    headers.put("authorization", Collections.singletonList(authToken));
                }
            }).on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                }
            });
        }
    };

    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "Socket Connected!");
        }
    };

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onConnectError");
        }
    };
    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onDisconnect");
            if(!mSocket.connected()){
                mSocket.connect();
            }
        }
    };


    public static boolean isApplicationOnBackground() {
        return stateCounter == 0;
    }

    public static void activityStarted() {
        stateCounter++;
    }

    public static void activityStopped() {
        stateCounter--;
    }


    public static Context getContext() {
        return context;
    }

    public static void setCountryCode(Context context, String code) {
        if (code == null) {
            return;
        }

        // Save in the preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (null == sharedPreferences) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CountryCode", code);
        editor.commit();
    }

    public static String getCountryCode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String cookie = sharedPreferences.getString("CountryCode", "");

        return cookie;
    }

    public static void setuserActive(Context context, Boolean useractive) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (null == sharedPreferences) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isUserActive", useractive);
        editor.commit();
    }

    public static String getcontactNo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String cookie = sharedPreferences.getString("contactNo", "");
        return cookie;
    }

    public static void setuserName(Context context, String contactNo) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (null == sharedPreferences) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", contactNo);
        editor.commit();
    }

    public static String getuserName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String cookie = sharedPreferences.getString("userName", "");
        return cookie;
    }

    public static void setAuthToken(Context context, String authToken) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (null == sharedPreferences) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("AuthToken", authToken);
        editor.commit();
    }

    public static String getAuthToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String cookie = sharedPreferences.getString("AuthToken", "");
        return cookie;
    }

    public static void setBusinessProfileRegistered(Context context, Boolean useractive) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (null == sharedPreferences) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isBusinessProfileRegistered", useractive);
        editor.commit();
    }

    public static Boolean isBusinessProfileRegistered(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Boolean useractive = sharedPreferences.getBoolean("isBusinessProfileRegistered", false);

        return useractive;
    }

    public static Boolean isPersonalProfileRegistered(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Boolean useractive = sharedPreferences.getBoolean("isPersonalProfileRegistered", false);

        return useractive;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Example Channel", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        Log.i(TAG, "App in background");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        try {
            Log.i(TAG, "App in foreground");
            SharedPreferences sharedPreferences = getSharedPreferences("isMySwitchChecked", MODE_PRIVATE);
            boolean bool = sharedPreferences.getBoolean("key", false);
            if (bool == true) {
                MyApplication.activityStarted();
                BaseActivity.biometricPrompt.authenticate(BaseActivity.promptInfo);
            }
        } catch (Exception e) {
            System.out.println("Error ");
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreateEvent() {
        Log.i(TAG, "ON_CREATE Event");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResumeEvent() {
        System.out.println("Hello");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPauseEvent() {
        Log.i(TAG, "ON_PAUSE event");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroyEvent() {
        Log.i(TAG, "ON_DESTROY event");
    }

}
