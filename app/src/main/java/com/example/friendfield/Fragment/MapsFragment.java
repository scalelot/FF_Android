package com.example.friendfield.Fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.friendfield.Activity.ProfileActivity;
import com.example.friendfield.Model.FindFriends.FindFriendsModel;
import com.example.friendfield.Model.PersonalInfo.PeronalRegisterModel;
import com.example.friendfield.Model.Profile.Register.GetPersonalProfileModel;
import com.example.friendfield.Model.UserProfile.UserProfileRegisterModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.TagView.Person;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapsFragment extends Fragment {

    TextView txt_location, latlng, map_username;
    TextInputEditText textInputEditText;
    ImageView map_backarrow, iv_search, iv_clear_text;
    RelativeLayout mMapViewRoot;
    GoogleMap mGoogleMap;
    Marker marker;
    View view_marker, transparentView, viewdialog, view;
    PopupWindow popview;
    private static final int DURATION = 3000;
    Place place;
    Double latitude, longitude;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    String apiKeys = "AIzaSyAP9ViAFSCQHr4i_DjkbKcj0Lj2BarZNIk";
    List<Place.Field> fields;
    String TAG = "MapActivity";
    EditText textView;
    LatLng m3;
    String userIds;
    SupportMapFragment mapFragment;
    RequestQueue queue;
    List<String> nameList;
    List<String> profileimgList;
    List<String> userIdList;
    FindFriendsModel findFriendsModel;
    Bitmap result;
    int i, areakm = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_maps, container, false);

        //InfoWindow Layout View
        viewdialog = LayoutInflater.from(getActivity()).inflate(R.layout.map_request_dialog, null);

        map_backarrow = view.findViewById(R.id.map_backarrow);
        txt_location = view.findViewById(R.id.txt_location);
        textView = view.findViewById(R.id.text);
        latlng = view.findViewById(R.id.latlng);
        iv_search = view.findViewById(R.id.iv_search);
        iv_clear_text = view.findViewById(R.id.iv_clear_text);

        queue = Volley.newRequestQueue(getContext());

        MapsInitializer.initialize(getActivity());
        Places.initialize(getContext(), apiKeys);

        PlacesClient placesClient = Places.createClient(getContext());

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(getActivity());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });

        iv_clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
                iv_clear_text.setVisibility(View.GONE);
                iv_search.setVisibility(View.VISIBLE);
            }
        });

        mMapViewRoot = (RelativeLayout) view.findViewById(R.id.mapview_root);
        transparentView = View.inflate(getContext(), R.layout.transparent_layout, mMapViewRoot);

        view_marker = transparentView.findViewById(R.id.view_marker);
        if (getActivity() != null) {
            mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            if (mapFragment != null) {
                mapFragment.getMapAsync(callback);
            }
        }

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            txt_location.setText(getResources().getString(R.string.location_ust_be_on));
            Toast.makeText(getActivity(), "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }

        fetchgetApi();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapFragment != null)
            getFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + "," + place.getLatLng());
                Log.i(TAG, "LatLng: " + place.getLatLng());
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                textView.setText(place.getAddress());

                findLocationorName(latitude, longitude);

                LatLng latLng = new LatLng(latitude, longitude);

                if (latLng != null) {
                    showRipples(latLng);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showRipples(latLng);
                        }
                    }, DURATION - 500);

                    fetchgetApi();

                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    public void findLocationorName(Double latitude, Double longitude) {
        HashMap<String, String> maplatlog = new HashMap<>();
        maplatlog.put("latitude", String.valueOf(latitude));
        maplatlog.put("longitude", String.valueOf(longitude));
        maplatlog.put("search", "");

        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.set_find_friends_location_or_name, new JSONObject(maplatlog), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("LL_findfriends-->", response.toString());

                    findFriendsModel = new Gson().fromJson(response.toString(), FindFriendsModel.class);

                    profileimgList = new ArrayList<>();
                    nameList = new ArrayList<>();
                    userIdList = new ArrayList<>();

                    for (i = 0; i < findFriendsModel.getData().size(); i++) {

                        profileimgList.add(findFriendsModel.getData().get(i).getProfileimage());
                        nameList.add(findFriendsModel.getData().get(i).getFullName());
                        userIdList.add(findFriendsModel.getData().get(i).getId());

                        Double longitude = findFriendsModel.getData().get(i).getLocation().getCoordinates().get(0);
                        Double latitude = findFriendsModel.getData().get(i).getLocation().getCoordinates().get(1);

                        String ids = findFriendsModel.getData().get(i).getId();

                        m3 = new LatLng(latitude, longitude);
                        MarkerOptions options3 = new MarkerOptions().position(m3);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(150));

                        Glide.with(getActivity())
                                .asBitmap()
                                .centerCrop()
                                .apply(requestOptions)
                                .load(Constans.Display_Image_URL + profileimgList.get(i))
                                .into(new CustomTarget<Bitmap>(150, 150) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        marker = mGoogleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latitude, longitude))
                                                .anchor(0.5f, 0.907f)
                                                .icon(BitmapDescriptorFactory.fromBitmap(resource)));
                                        marker.setTag(ids);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });


                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(m3));
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(m3)
                                .zoom(20).build();

                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

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
                    Log.e("LL_location_friends-->", error.toString());
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
            e.printStackTrace();
        }
    }

    private void fetchgetApi() {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    GetPersonalProfileModel profileRegisterModel = new Gson().fromJson(response.toString(), GetPersonalProfileModel.class);

                    areakm = profileRegisterModel.getData().getAreaRange();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LL_location_area-->", error.toString());
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

    private Bitmap createUserBitmap() {
        result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
        result.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(result);
        Drawable drawable = getResources().getDrawable(R.drawable.cricle_bg);
        drawable.setBounds(0, 0, dp(62), dp(76));
        drawable.draw(canvas);

        Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RectF bitmapRect = new RectF();
        canvas.save();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_dark);
        if (bitmap != null) {
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Matrix matrix = new Matrix();
            float scale = dp(52) / (float) bitmap.getWidth();
            matrix.postTranslate(dp(5), dp(5));
            matrix.postScale(scale, scale);
            roundPaint.setShader(shader);
            shader.setLocalMatrix(matrix);
            bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
            canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
        }
        canvas.restore();
        try {
            canvas.setBitmap(null);
        } catch (Exception e) {
        }
        return result;
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
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
        HashMap<String, String> hMap = new HashMap<>();
        hMap.put("receiverid", userIds);
        hMap.put("message", message);

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.set_new_friend_request, new JSONObject(hMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Sendrequest:--", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LL_friends_error", error.toString());
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

        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth()
                , d.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        int area = areakm * 1000;
        Log.e("hello", String.valueOf(area));
        final int radius = area;

        final GroundOverlay circle = mGoogleMap.addGroundOverlay(new GroundOverlayOptions()
                .position(latLng, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(bitmap)));

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
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false).setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {

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
}