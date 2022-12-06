package com.example.friendfield.Activity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.friendfield.R;
import com.example.friendfield.Utils.Const;
import com.example.friendfield.Utils.FileUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsLocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    Context context;
    public Criteria criteria;
    public String bestProvider;
    LocationManager locationManager;
    Boolean isProfileLocation;
    Boolean isBusinessLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);

        isProfileLocation = getIntent().getBooleanExtra("isProfileLocation", false);
        isBusinessLocation = getIntent().getBooleanExtra("isBusinessLocation", false);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);

                criteria = new Criteria();
                bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    Log.e("TAG", "GPS is on");

                    if (isProfileLocation) {
                        Const.lattitude = location.getLatitude();
                        Const.longitude = location.getLongitude();
                    } else {
                        Const.b_lattitude = location.getLatitude();
                        Const.b_longitude = location.getLongitude();
                    }
                    Log.e("LLL_map_Latitude1: ", +location.getLatitude() + ", Longitude:" + location.getLongitude());
                    centreMapOnLocation(location, bestProvider);

                } else {
                    locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                }

            } else {
                Toast.makeText(getApplicationContext(), "Accetta i permessi", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("LLL_bproerr1--->", e.getMessage());
            e.printStackTrace();
        }
    }

    public void centreMapOnLocation(Location location, String title) {
        if (Const.mCurrLocationMarker != null) {
            Const.mCurrLocationMarker.remove();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Const.mCurrLocationMarker = marker;
                Log.e("LLL_map_System out", "onMarkerEnd..." + marker.getPosition().latitude + "..." + marker.getPosition().longitude);
                LatLng latLng1 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                Const.latLngvalue = latLng1;

                if (isProfileLocation) {
                    Const.lattitude = marker.getPosition().latitude;
                    Const.longitude = marker.getPosition().longitude;
                } else {
                    Const.b_lattitude = marker.getPosition().latitude;
                    Const.b_longitude = marker.getPosition().longitude;
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                finish();
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if (Const.mCurrLocationMarker != null) {
                    Const.mCurrLocationMarker.remove();
                }
                Const.latLngvalue = latLng;

                LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng1);
                markerOptions.draggable(true);
                markerOptions.title(title);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                Const.mCurrLocationMarker = mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 12));
                Log.e("LLL_final_location-->", "latitude: " + latLng.latitude + " longitude: " + latLng.longitude);

                if (isProfileLocation) {
                    Const.lattitude = latLng.latitude;
                    Const.longitude = latLng.longitude;
                } else {
                    Const.b_lattitude = latLng.latitude;
                    Const.b_longitude =latLng.longitude;
                }

                finish();
            }
        });


        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(userLocation);
        markerOptions.draggable(true);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        Const.mCurrLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("LLL_map_Latitude: ", +location.getLatitude() + ", Longitude:" + location.getLongitude());

        if (isProfileLocation) {
            Const.lattitude = location.getLatitude();
            Const.longitude = location.getLongitude();
        } else {
            Const.b_lattitude = location.getLatitude();
            Const.b_longitude = location.getLongitude();
        }

        if (Const.mCurrLocationMarker != null) {
            Const.mCurrLocationMarker.remove();
        }
        Log.e("LLL_lati--: ", Const.longitude + " : longi : " + Const.lattitude);
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(userLocation);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        Const.mCurrLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

}