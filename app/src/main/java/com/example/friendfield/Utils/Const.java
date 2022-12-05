package com.example.friendfield.Utils;

import android.graphics.Bitmap;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class Const {

    public static Double longitude;
    public static Double lattitude;

    public static Double b_longitude;
    public static Double b_lattitude;

    public static LocationManager locationManager;
    public static Marker mCurrLocationMarker;
    public static LatLng latLngvalue;

    public static Bitmap bitmap_business_profile_image;
    public static Bitmap bitmap_profile_image;

    public static ArrayList<String> taglist = new ArrayList<>();
    public static String tag_str = null;


}
