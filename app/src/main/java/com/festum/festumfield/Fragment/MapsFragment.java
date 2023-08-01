package com.festum.festumfield.Fragment;

import static android.content.Context.LOCATION_SERVICE;

import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.festum.festumfield.Model.FindFriends.FindFriendsModel;
import com.festum.festumfield.Model.Profile.Register.GetPersonalProfileModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapsFragment extends Fragment {

    TextView txt_location, latlng, map_username;
    TextInputEditText textInputEditText;
    ImageView iv_search;
    RelativeLayout mMapViewRoot, rl_map;
    GoogleMap mGoogleMap;
    Marker marker;
    View view_marker, transparentView, viewdialog, view;
    PopupWindow popview;
    private static final int DURATION = 3000;
    Double latitude, longitude;
    List<Place.Field> fields;
    SearchView searchView;
    LatLng m3;
    String userIds;
    SupportMapFragment mapFragment;
    RequestQueue queue;
    List<String> nameList;
    List<String> profileimgList;
    List<String> userIdList;
    List<String> longList;
    List<String> latList;
    FindFriendsModel findFriendsModel;
    LatLng latLng;
    int i, areakm = 0;
    Address address;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_maps, container, false);

        mMapViewRoot = (RelativeLayout) view.findViewById(R.id.mapview_root);
        transparentView = View.inflate(getContext(), R.layout.transparent_layout, mMapViewRoot);
        viewdialog = LayoutInflater.from(getContext()).inflate(R.layout.map_request_dialog, null);

        view_marker = transparentView.findViewById(R.id.view_marker);
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(callback);

        txt_location = view.findViewById(R.id.txt_location);
        searchView = view.findViewById(R.id.searchView);
        latlng = view.findViewById(R.id.latlng);
        iv_search = view.findViewById(R.id.iv_search);
        rl_map = view.findViewById(R.id.rl_map);

        fetchgetApi();

        queue = Volley.newRequestQueue(getContext());

        MapsInitializer.initialize(getActivity());
        Places.initialize(getContext(), getContext().getResources().getString(R.string.google_maps_key));

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            txt_location.setText(getResources().getString(R.string.location_ust_be_on));
            Toast.makeText(getActivity(), "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                try {
                    List<Address> addressList = null;

                    if (location != null || location.equals("") || !location.isEmpty()) {

                        Geocoder geocoder = new Geocoder(MapsFragment.this.getContext());

                        addressList = geocoder.getFromLocationName(location, 1);
                        if (addressList.size() != 0) {
                            address = addressList.get(0);
                            latitude = address.getLatitude();
                            longitude = address.getLongitude();
//                            FileUtils.DisplayLoading(MapsFragment.this.getContext());
                            findLocationorName(latitude, longitude);
                            latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            Log.e("LatLog==>>", String.valueOf(latLng));
                        } else {
                            Toast.makeText(MapsFragment.this.getContext(), "Search Valid Place", Toast.LENGTH_SHORT).show();
                        }

                        if (latLng != null) {
                            showRipples(latLng);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showRipples(latLng);
                                }
                            }, DURATION - 500);

                            fetchgetApi();
                        } else {
                            Toast.makeText(getContext(), "Enter incorrect", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(getContext(), "Enter incorrect", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        findLocationorName(Double.valueOf("21.170240"), Double.valueOf("72.831062"));
        return view;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);

            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        }
    };

    public void findLocationorName(Double latitude, Double longitude) {
        JsonObjectRequest jsonObjectRequest;
        try {
            HashMap<String, String> maplatlog = new HashMap<>();
            maplatlog.put("latitude", String.valueOf(latitude));
            maplatlog.put("longitude", String.valueOf(longitude));
            maplatlog.put("search", "");
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.set_find_friends_location_or_name, new JSONObject(maplatlog), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    FileUtils.DismissLoading(MapsFragment.this.getContext());
                    Log.e("FindFirends-->", response.toString());

                    findFriendsModel = new Gson().fromJson(response.toString(), FindFriendsModel.class);

                    profileimgList = new ArrayList<>();
                    nameList = new ArrayList<>();
                    userIdList = new ArrayList<>();
                    longList = new ArrayList<>();
                    latList = new ArrayList<>();

                    for (i = 0; i < findFriendsModel.getData().size(); i++) {

                        profileimgList.add(findFriendsModel.getData().get(i).getProfileimage());
                        nameList.add(findFriendsModel.getData().get(i).getFullName());
                        userIdList.add(findFriendsModel.getData().get(i).getId());
                        longList.add(String.valueOf(findFriendsModel.getData().get(i).getLocation().getCoordinates().get(0)));
                        latList.add(String.valueOf(findFriendsModel.getData().get(i).getLocation().getCoordinates().get(1)));

                        Double longitude = findFriendsModel.getData().get(i).getLocation().getCoordinates().get(0);
                        Double latitude = findFriendsModel.getData().get(i).getLocation().getCoordinates().get(1);

                        String ids = findFriendsModel.getData().get(i).getId();

                        m3 = new LatLng(latitude, longitude);
                        MarkerOptions options3 = new MarkerOptions().position(m3);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(150));

                        Glide.with(getActivity()).asBitmap().centerCrop().apply(requestOptions).load(Constans.Display_Image_URL + profileimgList.get(i)).into(new CustomTarget<Bitmap>(150, 150) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).anchor(0.5f, 0.907f).icon(BitmapDescriptorFactory.fromBitmap(resource)));
                                marker.setTag(ids);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
                    }

                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(m3));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(m3).zoom(10.5F).build();

                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Projection projection = mGoogleMap.getProjection();
                            Point viewPosition = projection.toScreenLocation(marker.getPosition());
                            transparentView.setLeft(viewPosition.x);
                            transparentView.setTop(viewPosition.y);

                            String idName = String.valueOf(marker.getTag());
                            for (int i = 0; i < userIdList.size(); i++) {
                                if (userIdList.get(i).contains(idName)) {
                                    map_username = viewdialog.findViewById(R.id.map_username);
                                    map_username.setText(nameList.get(i));
                                    userIds = idName;
                                }
                            }

                            PopupDialog();
                            return false;
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    FileUtils.DismissLoading(MapsFragment.this.getContext());
                    Log.e("FindFirendsError-->", error.toString());
                    error.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("authorization", MyApplication.getAuthToken(getContext()));
                    return map;
                }
            };
            queue.add(jsonObjectRequest);
        } catch (Exception e) {
//            FileUtils.DismissLoading(MapsFragment.this.getContext());
            e.printStackTrace();
        }
    }

    private void fetchgetApi() {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("GetMapProfileInfo", response.toString());
                    GetPersonalProfileModel profileRegisterModel = new Gson().fromJson(response.toString(), GetPersonalProfileModel.class);

                    areakm = profileRegisterModel.getData().getAreaRange();
                    longitude = profileRegisterModel.getData().getLocationModel().getCoordinates().get(0);
                    latitude = profileRegisterModel.getData().getLocationModel().getCoordinates().get(1);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("GetMapProfileInfoError-->", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", MyApplication.getAuthToken(getContext()));
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PopupDialog() {
        popview = new PopupWindow(getActivity());
        popview.setContentView(viewdialog);
        popview.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popview.setFocusable(true);
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textInputEditText = viewdialog.findViewById(R.id.input_edt);
        AppCompatButton appCompatButton = viewdialog.findViewById(R.id.map_send);
        popview.setOutsideTouchable(false);
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textInputEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "" + textInputEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    setFriendsApi(userIds, textInputEditText.getText().toString());
                    textInputEditText.getText().clear();
                    popview.dismiss();
                } else {
                    textInputEditText.setError("Enter Message");
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                popview.showAsDropDown(view_marker, -250, -60);
            }
        }, 200);

    }

    public void setFriendsApi(String userIds, String message) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            HashMap<String, String> hMap = new HashMap<>();
            hMap.put("receiverid", userIds);
            hMap.put("message", message);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.set_new_friend_request, new JSONObject(hMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("SendFirendRequest:--", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("SendFirendRequestError", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("authorization", MyApplication.getAuthToken(getContext()));
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRipples(LatLng latLng) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setSize(1000, 1000);
        d.setColor(Color.rgb(90, 200, 210));
        d.setStroke(0, Color.TRANSPARENT);

        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        int area = areakm * 1000;
        Log.e("hello", String.valueOf(area));
        final int radius = area;

        final GroundOverlay circle = mGoogleMap.addGroundOverlay(new GroundOverlayOptions().position(latLng, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(bitmap)));

        PropertyValuesHolder radiusHolder = PropertyValuesHolder.ofFloat("radius", 0, radius);
        PropertyValuesHolder transparencyHolder = PropertyValuesHolder.ofFloat("transparency", 0, 1);

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setValues(radiusHolder, transparencyHolder);
        valueAnimator.setDuration(DURATION);
        valueAnimator.setEvaluator(new FloatEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedRadius = (float) valueAnimator.getAnimatedValue("radius");
                float animatedAlpha = (float) valueAnimator.getAnimatedValue("transparency");
                circle.setDimensions(animatedRadius * 2);
                circle.setTransparency(animatedAlpha);
            }
        });

        valueAnimator.start();
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?").setCancelable(false).setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapFragment != null)
            getFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchgetApi();
    }
}