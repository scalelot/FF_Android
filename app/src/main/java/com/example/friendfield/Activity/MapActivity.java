package com.example.friendfield.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;

import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.engine.Resource;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapActivity extends BaseActivity {

    TextView txt_location, textView, latlng;
    ImageView map_backarrow;
    Marker marker;
    RelativeLayout mMapViewRoot;
    MapView mGoogleMapView;
    GoogleMap mGoogleMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    View view_marker;
    PopupWindow popview;
    private static final int DURATION = 3000;
    Place place;
    String latitude = "", longitude = "";
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    String apiKeys = "AIzaSyAP9ViAFSCQHr4i_DjkbKcj0Lj2BarZNIk";
    List<Place.Field> fields;
    String TAG = "MapActivity";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map_backarrow = findViewById(R.id.map_backarrow);
        txt_location = findViewById(R.id.txt_location);
        textView = (TextView) findViewById(R.id.text);
        latlng = (TextView) findViewById(R.id.latlng);

        queue = Volley.newRequestQueue(MapActivity.this);
        Places.initialize(getApplicationContext(), apiKeys);

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(MapActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });

        map_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mMapViewRoot = (RelativeLayout) findViewById(R.id.mapview_root);
        final View transparentView = View.inflate(getApplicationContext(), R.layout.transparent_layout, mMapViewRoot);

        view_marker = transparentView.findViewById(R.id.view_marker);
        mGoogleMapView = findViewById(R.id.map);
        mGoogleMapView.onCreate(mapViewBundle);
        mGoogleMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mGoogleMap = googleMap;

                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                LatLng m1 = new LatLng(21.224740, 72.808710);

                MarkerOptions options = new MarkerOptions().position(m1);
                Bitmap bitmap = createUserBitmap();
                if (bitmap != null) {
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    options.anchor(0.5f, 0.907f);
                    marker = mGoogleMap.addMarker(options);
                }

                LatLng m2 = new LatLng(21.224461, 72.830391);

                MarkerOptions options1 = new MarkerOptions().position(m2);
                Bitmap bitmap1 = createUserBitmap();
                if (bitmap1 != null) {
                    options1.icon(BitmapDescriptorFactory.fromBitmap(bitmap1));
                    options1.anchor(0.5f, 0.907f);
                    marker = mGoogleMap.addMarker(options1);
                }

                LatLng m3 = new LatLng(21.225170, 72.819500);

                MarkerOptions options3 = new MarkerOptions().position(m3);
                Bitmap bitmap3 = createUserBitmap();
                if (bitmap3 != null) {
                    options3.icon(BitmapDescriptorFactory.fromBitmap(bitmap3));
                    options3.anchor(0.5f, 0.907f);
                    marker = mGoogleMap.addMarker(options3);
                }

                mGoogleMap.addMarker(new MarkerOptions().position(m3).title("Marker in Sydney"));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(m3));

                showRipples(m3);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showRipples(m3);
                    }
                }, DURATION - 500);

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Projection projection = mGoogleMap.getProjection();
                        Point viewPosition = projection.toScreenLocation(marker.getPosition());

                        transparentView.setLeft(viewPosition.x);
                        transparentView.setTop(viewPosition.y);

                        PopupDialog();
                        return false;
                    }
                });
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(m3));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(m3)
                        .zoom(20).build();

                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            txt_location.setText(getResources().getString(R.string.location_ust_be_on));
            Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
    }

    public void findLocationorName(String latitude, String longitude) {
        HashMap<String, String> map = new HashMap<>();
        map.put("latitude", latitude);
        map.put("longitude", longitude);

        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.set_find_friends_location_or_name, new JSONObject(map), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(getApplicationContext());
                    Log.e("LL_findfriends-->", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(getApplicationContext());
                    Log.e("LL_location_friends-->", error.toString());
                    error.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("authorization", MyApplication.getAuthToken(getApplicationContext()));
                    return map;
                }
            };
            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + "," + place.getLatLng());
                Log.i(TAG, "LatLng: " + place.getLatLng());
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
//                textView.setText(place.getAddress());

                findLocationorName(latitude,longitude);

                mGoogleMap.clear();
                mGoogleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));




            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private Bitmap createUserBitmap() {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getResources().getDrawable(R.drawable.cricle_bg);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_business_profile);
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
        } catch (Throwable t) {
            t.printStackTrace();
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
        popview = new PopupWindow(MapActivity.this);
        View view = LayoutInflater.from(MapActivity.this).inflate(R.layout.map_request_dialog, null);
        popview.setContentView(view);
        popview.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popview.setFocusable(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextInputEditText textInputEditText = view.findViewById(R.id.input_edt);
        AppCompatButton appCompatButton = view.findViewById(R.id.map_send);
        popview.setOutsideTouchable(false);
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textInputEditText.getText().toString().isEmpty()) {
                    Toast.makeText(MapActivity.this, "" + textInputEditText.getText().toString(), Toast.LENGTH_SHORT).show();
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

    private void showRipples(LatLng latLng) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setSize(500, 500);
        d.setColor(Color.rgb(90, 200, 210));
        d.setStroke(0, Color.TRANSPARENT);

        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth()
                , d.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        final int radius = 1000;

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

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleMapView.onResume();
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}