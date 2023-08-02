package com.festum.festumfield;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainPackageConfig;
import com.facebook.react.shell.MainReactPackage;

import java.util.ArrayList;
import java.util.Arrays;

public class PackageList {

    Application application;
    ReactNativeHost reactNativeHost;
    MainPackageConfig mConfig;

    public PackageList(Application application) {
        this(application, null);
    }

    public PackageList(ReactNativeHost reactNativeHost, MainPackageConfig config) {
        this.reactNativeHost = reactNativeHost;
        mConfig = config;
    }

    public PackageList(Application application, MainPackageConfig config) {
        this.reactNativeHost = null;
        this.application = application;
        mConfig = config;
    }

    public ReactNativeHost getReactNativeHost() {
        return this.reactNativeHost;
    }

    public Resources getResources() {
        return application.getResources();
    }

    public Context getApplicationContext() {
        return application.getApplicationContext();
    }

    public ArrayList<ReactPackage> getPackages() {
        return new ArrayList<>(Arrays.<ReactPackage>asList(
                new MainReactPackage(mConfig)
        ));
    }
}
