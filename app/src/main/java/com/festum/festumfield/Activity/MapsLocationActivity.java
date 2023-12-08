package com.festum.festumfield.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Const;
import com.festum.festumfield.verstion.firstmodule.screens.adapters.PlaceAutocompleteAdapter;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity.AutoSuggestAdapter;
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity.OnSearchCity;
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity.ResponseSearch;
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity.RetrofitClient;
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@AndroidEntryPoint
public class MapsLocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, OnSearchCity {

    private GoogleMap mMap;
    public Criteria criteria;
    public String bestProvider;
    LocationManager locationManager;
    Boolean isProfileLocation;
    Boolean isBusinessLocation;


    private AutoSuggestAdapter autoSuggestAdapter;
    private ArrayList<ResponseSearch> responseSearch;

    private AutoCompleteTextView autoCompleteTextView;
    private PlaceAutocompleteAdapter adapter;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);

        isProfileLocation = getIntent().getBooleanExtra("isProfileLocation", false);
        isBusinessLocation = getIntent().getBooleanExtra("isBusinessLocation", false);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*autoCompleteTextView = findViewById(R.id.searchTextView);

        ResponseSearch responseSearch1 = new ResponseSearch();
        responseSearch1.setLocalizedName("Surat");

        ArrayList<ResponseSearch> responseSearchList = new ArrayList<>();

        autoSuggestAdapter = new AutoSuggestAdapter(this, R.layout.list_item_search_auto, R.id.text1, responseSearchList, this);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchLocation(autoCompleteTextView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });*/

        if (AppPreferencesDelegates.Companion.get().isNightModeOn()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        String apiKey = getResources().getString(R.string.google_maps_key_new_one);
        Places.initialize(getApplicationContext(), apiKey);

        placesClient = Places.createClient(this);

        autoCompleteTextView = findViewById(R.id.searchTextView);
        adapter = new PlaceAutocompleteAdapter(this, this);

        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (query.length() >= 2) {
                    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                    FindAutocompletePredictionsRequest predictionsRequest =
                            FindAutocompletePredictionsRequest.builder()
                                    .setTypeFilter(TypeFilter.CITIES)
                                    .setSessionToken(token)
                                    .setQuery(query)
                                    .build();

                    placesClient.findAutocompletePredictions(predictionsRequest)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    adapter.setPredictions(task.getResult());
                                } else {
                                    Log.e("TAG", "Prediction fetching task unsuccessful");
                                }
                            });
                } else {
                    adapter.clearPredictions();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


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
                    Log.e("Gps=>", "GPS is on");

                    if (isProfileLocation) {
                        Const.lattitude = location.getLatitude();
                        Const.longitude = location.getLongitude();
                    }

                    if (isBusinessLocation){
                        Const.b_lattitude = location.getLatitude();
                        Const.b_longitude = location.getLongitude();
                    }
                    Log.e("MapLatLog: ", +location.getLatitude() + ", Longitude:" + location.getLongitude());
                    centreMapOnLocation(location, bestProvider);

                } else {
                    locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                }

            } else {
                Toast.makeText(getApplicationContext(), "Access Permission", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
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
                Log.e("MapClick=>", "onMarkerEnd..." + marker.getPosition().latitude + "..." + marker.getPosition().longitude);
                LatLng latLng1 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                Const.latLngvalue = latLng1;

                if (isProfileLocation) {
                    Const.lattitude = marker.getPosition().latitude;
                    Const.longitude = marker.getPosition().longitude;
                } if (isBusinessLocation) {
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
                Log.e("FinalLocation-->", "latitude: " + latLng.latitude + " longitude: " + latLng.longitude);

                if (isProfileLocation) {
                    Const.lattitude = latLng.latitude;
                    Const.longitude = latLng.longitude;
                } if(isBusinessLocation) {
                    Const.b_lattitude = latLng.latitude;
                    Const.b_longitude = latLng.longitude;
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 8));

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("LocationChange=>: ", +location.getLatitude() + ", Longitude:" + location.getLongitude());

        if (isProfileLocation) {
            Const.lattitude = location.getLatitude();
            Const.longitude = location.getLongitude();
        } if(isBusinessLocation) {
            Const.b_lattitude = location.getLatitude();
            Const.b_longitude = location.getLongitude();
        }

        if (Const.mCurrLocationMarker != null) {
            Const.mCurrLocationMarker.remove();
        }
        Log.e("LocationChangeLatLog--: ", Const.longitude + " : longi : " + Const.lattitude);
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

    @Override
    public void onSearchCity(@NonNull String city) {

        List<Address> addressList = null;

        if (!city.isEmpty()) {
            autoCompleteTextView.dismissDropDown();

            Geocoder geocoder = new Geocoder(this);

            try {
                addressList = geocoder.getFromLocationName(city, 1);

                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();

                    if (latitude != 0 && longitude != 0) {
                        LatLng latLng = new LatLng(latitude, longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12f).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        autoCompleteTextView.setText("");
                        DeviceUtils.INSTANCE.hideKeyboard(this);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}