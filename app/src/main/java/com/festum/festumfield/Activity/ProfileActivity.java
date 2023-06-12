package com.festum.festumfield.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.Model.Profile.Register.GetPersonalProfileModel;
import com.festum.festumfield.Model.Profile.Register.ProfileRegisterModel;
import com.festum.festumfield.Model.UserProfile.SocialMediaLink;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.TagView.ContactsCompletionView;
import com.festum.festumfield.TagView.Person;
import com.festum.festumfield.TagView.PersonAdapter;
import com.festum.festumfield.TagView.TokenCompleteTextView;
import com.festum.festumfield.Utils.Const;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.bendik.simplerangeview.SimpleRangeView;

public class ProfileActivity extends BaseActivity implements OnMapReadyCallback, LocationListener, TokenCompleteTextView.TokenListener<Person> {

    RadioGroup radioGender;
    RadioButton selectBtn;
    EditText edt_name, edt_nickname, edt_emailId, edt_fb, edt_insta, edt_twitter, edt_linkdin, edt_address, edt_aboutUs, edt_pintrest, edt_youtube;
    AppCompatButton btn_save, btn_business;
    ImageView ic_back;
    LinearLayout ll_chk;
    LocationManager locationManager;
    private static final int REQUEST_CODE = 101;
    SeekBar seekbar_range;
    CircleImageView profile_image;
    private static final int PICK_IMAGE = 100;
    RelativeLayout edit_profile;
    TextView t_km, title, txt_min_age, txt_max_age, gender;
    int p = 0, text_km = 0, txt_min = 0, txt_max = 0;
    SimpleRangeView rangeBar;
    String yourRd = "MALE";
    String hello = "";
    String profile_title = "";
    ImageView iv_location;
    LatLng latLng;
    MapView mapview;
    Spinner gen_spinner;
    private GoogleMap map;
    public Criteria criteria;
    public String bestProvider;
    String longitude = null;
    String lattitude = null;
    RelativeLayout rl_map;
    RelativeLayout relative_map;
    EditText edt_dob;
    ImageView img_calender;
    Boolean isSaveAndCreateBusiness = false;
    ContactsCompletionView completionView;
    ArrayAdapter<Person> adapter;
    Person[] people;
    SocialMediaLink socialMediaLink;
    ArrayList<SocialMediaLink> socialMediaLinkArrayList = new ArrayList<>();
    ArrayList<String> hobbiesArrayList = new ArrayList<>();
    JSONArray hobbiesJsonArray = new JSONArray();
    String gen_spinner_value = "";
    String selectedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edt_name = findViewById(R.id.edt_name);
        edt_nickname = findViewById(R.id.edt_nickname);
        edt_emailId = findViewById(R.id.edt_emailId);
        edt_fb = findViewById(R.id.edt_fb);
        edt_insta = findViewById(R.id.edt_insta);
        edt_twitter = findViewById(R.id.edt_twitter);
        edt_linkdin = findViewById(R.id.edt_linkdin);
        edt_pintrest = findViewById(R.id.edt_pintrest);
        edt_youtube = findViewById(R.id.edt_youtube);
        edt_aboutUs = findViewById(R.id.edt_aboutUs);
        btn_save = findViewById(R.id.btn_save);
        ic_back = findViewById(R.id.ic_back);
        btn_business = findViewById(R.id.btn_business);
        ll_chk = findViewById(R.id.ll_chk);
        radioGender = findViewById(R.id.radioGender);
        seekbar_range = findViewById(R.id.seekbar_range);
        profile_image = findViewById(R.id.profile_image);
        edit_profile = findViewById(R.id.edit_profile);
        t_km = findViewById(R.id.t_km);
        txt_min_age = findViewById(R.id.txt_min_age);
        rangeBar = findViewById(R.id.rangeSeekbar);
        title = findViewById(R.id.title);
        txt_max_age = findViewById(R.id.txt_max_age);
        edt_address = findViewById(R.id.edt_address);
        gender = findViewById(R.id.gender);
        iv_location = findViewById(R.id.iv_location);
        mapview = findViewById(R.id.mapview);
        rl_map = findViewById(R.id.rl_map);
        relative_map = findViewById(R.id.relative_map);
        gen_spinner = findViewById(R.id.gen_spinner);
        edt_dob = findViewById(R.id.edt_dob);
        img_calender = findViewById(R.id.img_calender);

        ActivityCompat.requestPermissions(this, permissions(), 1);

        people = Person.samplePeople();
        adapter = new PersonAdapter(this, R.layout.person_layout, people);

        AndroidNetworking.initialize(getApplicationContext());

        completionView = (ContactsCompletionView) findViewById(R.id.tagView);
        completionView.setAdapter(adapter);
        completionView.setThreshold(1);
        completionView.setTokenListener(ProfileActivity.this);
        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
        completionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ((TextView) findViewById(R.id.textValue)).setText(editable.toString());

            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }

        profile_title = getIntent().getStringExtra("EditProfile");

        rangeBar.setOnChangeRangeListener(new SimpleRangeView.OnChangeRangeListener() {
            @Override
            public void onRangeChanged(@NonNull SimpleRangeView simpleRangeView, int i, int i1) {
                txt_min_age.setText(String.valueOf(i));
                txt_max_age.setText(String.valueOf(i1));
                txt_min = i;
                txt_max = i1;
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edt_dob.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        img_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTime.show();
            }
        });

        rangeBar.setOnTrackRangeListener(new SimpleRangeView.OnTrackRangeListener() {
            @Override
            public void onStartRangeChanged(@NonNull SimpleRangeView simpleRangeView, int i) {
                txt_min_age.setText(String.valueOf(i));
            }

            @Override
            public void onEndRangeChanged(@NonNull SimpleRangeView simpleRangeView, int i) {
                txt_max_age.setText(String.valueOf(i));
            }
        });

        rangeBar.setOnRangeLabelsListener(new SimpleRangeView.OnRangeLabelsListener() {
            @Nullable
            @Override
            public String getLabelTextForPosition(@NonNull SimpleRangeView simpleRangeView, int i, @NonNull SimpleRangeView.State state) {
                return String.valueOf(i);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        gen_spinner.setAdapter(adapter);

        gen_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gen_spinner_value = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsLocationActivity.class).putExtra("isProfileLocation", true));

            }
        });

        seekbar_range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                p = i;
                seekBar.setProgress(i);
                t_km.setText(String.valueOf(p));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                t_km.setText(String.valueOf(p));
                text_km = p;
            }
        });

        relative_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsLocationActivity.class).putExtra("isProfileLocation", true));
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_name.getText().toString().isEmpty()) {
                    edt_name.setError(getResources().getString(R.string.enter_full_name));
                } else if (edt_nickname.getText().toString().isEmpty()) {
                    edt_nickname.setError(getResources().getString(R.string.enter_nickname));
                } else if (edt_emailId.getText().toString().isEmpty()) {
                    edt_emailId.setError(getResources().getString(R.string.enter_emailid));
                } else {
                    FileUtils.DisplayLoading(ProfileActivity.this);
                    if (profile_title != null) {
                        patchApivolley(edt_name.getText().toString().trim(), edt_nickname.getText().toString().trim(), edt_emailId.getText().toString().trim(), edt_dob.getText().toString(), gen_spinner_value, edt_aboutUs.getText().toString().trim(), hobbiesJsonArray, Const.longitude, Const.lattitude, text_km, yourRd, txt_min, txt_max, edt_fb.getText().toString().trim(), edt_insta.getText().toString().trim(), edt_twitter.getText().toString().trim(), edt_linkdin.getText().toString().trim(), edt_pintrest.getText().toString().trim(), edt_youtube.getText().toString().trim());
                    } else {
                        postApivolley(edt_name.getText().toString().trim(), edt_nickname.getText().toString().trim(), edt_emailId.getText().toString().trim(), edt_dob.getText().toString(), gen_spinner_value, edt_aboutUs.getText().toString().trim(), Const.longitude, Const.lattitude, text_km, yourRd, txt_min, txt_max, edt_fb.getText().toString().trim(), edt_insta.getText().toString().trim(), edt_twitter.getText().toString().trim(), edt_linkdin.getText().toString().trim(), edt_pintrest.getText().toString().trim(), edt_youtube.getText().toString().trim());

                    }
                }
            }
        });

        btn_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSaveAndCreateBusiness = true;

                if (edt_name.getText().toString().isEmpty()) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.enter_full_name), Toast.LENGTH_SHORT).show();
                    edt_name.setError(getResources().getString(R.string.enter_full_name));
                } else if (edt_nickname.getText().toString().isEmpty()) {
                    edt_nickname.setError(getResources().getString(R.string.enter_nickname));
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.enter_nickname), Toast.LENGTH_SHORT).show();
                } else if (edt_emailId.getText().toString().isEmpty()) {
                    edt_emailId.setError(getResources().getString(R.string.enter_emailid));
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.enter_emailid), Toast.LENGTH_SHORT).show();
                } else {
                    FileUtils.DisplayLoading(ProfileActivity.this);
                    postApivolley(edt_name.getText().toString().trim(), edt_nickname.getText().toString().trim(), edt_emailId.getText().toString().trim(), edt_dob.getText().toString(), gen_spinner_value, edt_aboutUs.getText().toString().trim(), Const.longitude, Const.lattitude, text_km, yourRd, txt_min, txt_max, edt_fb.getText().toString().trim(), edt_insta.getText().toString().trim(), edt_twitter.getText().toString().trim(), edt_linkdin.getText().toString().trim(), edt_pintrest.getText().toString().trim(), edt_youtube.getText().toString().trim());
                }
            }
        });


        radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                selectBtn = findViewById(radioGroup.getCheckedRadioButtonId());
                yourRd = selectBtn.getText().toString();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        mapview.getMapAsync(this);

        if (profile_title != null) {
            title.setText(profile_title);
            fetchgetApi();
            btn_business.setVisibility(View.GONE);
        } else {
            title.setText(getResources().getString(R.string.create_personal_profile));
        }

        if (Const.longitude != null) {
            if (map != null) {
                map.clear();
                if (Const.mCurrLocationMarker != null) {
                    Const.mCurrLocationMarker.remove();
                }
                LatLng userLocation = new LatLng(Double.valueOf(Const.lattitude), Double.valueOf(Const.longitude));
                map.addMarker(new MarkerOptions().position(userLocation));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
            }
        } else {
            fetchLocation();
        }

    }

    private void patchApivolley(String p_name, String pu_name, String p_email, String dob, String gender, String about_us, JSONArray hobbiesJsonArray, Double longitude, Double lattitude, Integer text_km, String yourRd, Integer txt_min, Integer txt_max, String fb_link, String insta_link, String tw_link, String ld_link, String pins_link, String youtube_link) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("fullName", p_name);
            jsonBody.put("userName", pu_name);
            jsonBody.put("nickName", pu_name);
            jsonBody.put("emailId", p_email);
            jsonBody.put("aboutUs", about_us);
            jsonBody.put("longitude", longitude);
            jsonBody.put("latitude", lattitude);
            jsonBody.put("areaRange", text_km.toString().trim());
            jsonBody.put("dob", dob);
            jsonBody.put("gender", gender);
            jsonBody.put("interestedin", yourRd.toUpperCase());
            jsonBody.put("targetAudienceAgeMin", txt_min.toString().trim());
            jsonBody.put("targetAudienceAgeMax", txt_max.toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!hobbiesArrayList.isEmpty()) {
            for (int i = 0; i < hobbiesArrayList.size(); i++) {
                hobbiesJsonArray.put(hobbiesArrayList.get(i));
            }
            try {
                jsonBody.put("hobbies", hobbiesJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!fb_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Facebook", fb_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!insta_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Instagram", insta_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!tw_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Twitter", tw_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!ld_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Linkedin", ld_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!pins_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Pinterest", pins_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!youtube_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Youtube", youtube_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < socialMediaLinkArrayList.size(); i++) {
            jsonArray.put(socialMediaLinkArrayList.get(i).getJSONObject());
        }

        try {
            jsonBody.put("socialMediaLinks", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.profile_register, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(ProfileActivity.this);
                    Log.e("UpdateProfile=>", response.toString());
                    ProfileRegisterModel profileRegisterModel = new Gson().fromJson(response.toString(), ProfileRegisterModel.class);

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(ProfileActivity.this);
                    System.out.println("UpdateProfileError=>" + error.toString());
                    error.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Data Not Submit" + error, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", MyApplication.getAuthToken(getApplicationContext()));
                    return map;
                }

            };
        } catch (Exception e) {
            FileUtils.DismissLoading(ProfileActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    private void postApivolley(String f_name, String u_name, String u_email, String dob, String gender, String about_us, Double longitude, Double lattitude, Integer text_km, String yourRd, Integer txt_min, Integer txt_max, String fb_link, String insta_link, String tw_link, String ld_link, String pins_link, String youtube_link) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("fullName", f_name);
            jsonBody.put("userName", u_name);
            jsonBody.put("nickName", u_name);
            jsonBody.put("emailId", u_email);
            jsonBody.put("aboutUs", about_us);
            jsonBody.put("longitude", longitude);
            jsonBody.put("latitude", lattitude);
            jsonBody.put("areaRange", text_km.toString().trim());
            jsonBody.put("dob", dob);
            jsonBody.put("gender", gender);
            jsonBody.put("interestedin", yourRd.toUpperCase());
            jsonBody.put("targetAudienceAgeMin", txt_min.toString().trim());
            jsonBody.put("targetAudienceAgeMax", txt_max.toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!hobbiesArrayList.isEmpty()) {
            for (int i = 0; i < hobbiesArrayList.size(); i++) {
                hobbiesJsonArray.put(hobbiesArrayList.get(i));
            }
            try {
                jsonBody.put("hobbies", hobbiesJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!fb_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Facebook", fb_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!insta_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Instagram", insta_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!tw_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Twitter", tw_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!ld_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Linkedin", ld_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!pins_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Pinterest", pins_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        if (!youtube_link.equals("")) {
            socialMediaLink = new SocialMediaLink("Youtube", youtube_link);
            socialMediaLinkArrayList.add(socialMediaLink);
        } else {

        }

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < socialMediaLinkArrayList.size(); i++) {
            jsonArray.put(socialMediaLinkArrayList.get(i).getJSONObject());
        }

        try {
            jsonBody.put("socialMediaLinks", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.profile_register, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(ProfileActivity.this);
                    Log.e("ProfileRegister=>", response.toString());
                    ProfileRegisterModel profileRegisterModel = new Gson().fromJson(response.toString(), ProfileRegisterModel.class);
                    Toast.makeText(ProfileActivity.this, profileRegisterModel.getMessage(), Toast.LENGTH_SHORT).show();

                    if (isSaveAndCreateBusiness) {
                        startActivity(new Intent(ProfileActivity.this, BusinessProfileActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(ProfileActivity.this);
                    System.out.println("ProfileRegisterError=>" + error.toString());
                    Toast.makeText(ProfileActivity.this, "Data Not Submit" + error, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", MyApplication.getAuthToken(getApplicationContext()));
                    return map;
                }

            };
        } catch (Exception e) {
            FileUtils.DismissLoading(ProfileActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    private void fetchgetApi() {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            FileUtils.DisplayLoading(ProfileActivity.this);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(ProfileActivity.this);
                    Log.e("GetProfileRegister=>", response.toString());

                    GetPersonalProfileModel profileRegisterModel = new Gson().fromJson(response.toString(), GetPersonalProfileModel.class);

                    Double longitude = profileRegisterModel.getData().getLocationModel().getCoordinates().get(0);
                    Double latitude = profileRegisterModel.getData().getLocationModel().getCoordinates().get(1);

                    LatLng latLng1 = new LatLng(latitude, longitude);
                    latLng = latLng1;
                    String getadd = FileUtils.getAddressFromLatLng(getApplicationContext(), latitude, longitude);

                    edt_address.setText(getadd);
                    edt_name.setText(profileRegisterModel.getData().getFullName());
                    edt_nickname.setText(profileRegisterModel.getData().getNickName());
                    edt_emailId.setText(profileRegisterModel.getData().getEmailId());
                    edt_aboutUs.setText(profileRegisterModel.getData().getAboutUs());
                    edt_dob.setText(profileRegisterModel.getData().getDob());

                    for (int i = 0; i < profileRegisterModel.getData().getHobbies().size(); i++) {
                        completionView.addObjectAsync(new Person(profileRegisterModel.getData().getHobbies().get(i)));
                    }

                    t_km.setText(String.valueOf(profileRegisterModel.getData().getAreaRange()));
                    txt_min_age.setText(String.valueOf(profileRegisterModel.getData().getTargetAudienceAgeMin()));
                    txt_max_age.setText(String.valueOf(profileRegisterModel.getData().getTargetAudienceAgeMax()));

                    for (int i = 0; i < profileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
                        if (profileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Facebook")) {
                            edt_fb.setText(profileRegisterModel.getData().getSocialMediaLinks().get(i).getLink());

                        } else if (profileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Instagram")) {
                            edt_insta.setText(profileRegisterModel.getData().getSocialMediaLinks().get(i).getLink());

                        } else if (profileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Twitter")) {
                            edt_twitter.setText(profileRegisterModel.getData().getSocialMediaLinks().get(i).getLink());

                        } else if (profileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Linkedin")) {
                            edt_linkdin.setText(profileRegisterModel.getData().getSocialMediaLinks().get(i).getLink());

                        } else if (profileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Pinterest")) {
                            edt_pintrest.setText(profileRegisterModel.getData().getSocialMediaLinks().get(i).getLink());

                        } else if (profileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Youtube")) {
                            edt_youtube.setText(profileRegisterModel.getData().getSocialMediaLinks().get(i).getLink());

                        }
                    }

                    gen_spinner_value = profileRegisterModel.getData().getGender();
                    ArrayAdapter<String> spinnerAdap = (ArrayAdapter<String>) gen_spinner.getAdapter();
                    int spinnerPosition = spinnerAdap.getPosition(gen_spinner_value);
                    gen_spinner.setSelection(spinnerPosition);

                    seekbar_range.setProgress(Integer.parseInt(String.valueOf(profileRegisterModel.getData().getAreaRange())));
                    gender.setText(profileRegisterModel.getData().getInterestedin());
                    rangeBar.setStart(profileRegisterModel.getData().getTargetAudienceAgeMin());
                    rangeBar.setEnd(profileRegisterModel.getData().getTargetAudienceAgeMax());

                    MyApplication.setBusinessProfileRegistered(ProfileActivity.this, profileRegisterModel.getData().getIsBusinessProfileCreated());


                    if (profileRegisterModel.getData().getProfileimage().equals("")) {
                        profile_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
                    } else {
                        Glide.with(ProfileActivity.this).asBitmap().load(Constans.Display_Image_URL + profileRegisterModel.getData().getProfileimage()).placeholder(R.drawable.ic_user).into(profile_image);
                    }

                    hello = gender.getText().toString();
                    if (hello.equals("Male")) {
                        ((RadioButton) radioGender.getChildAt(0)).setChecked(true);
                    } else if (hello.equals("FEMALE")) {
                        ((RadioButton) radioGender.getChildAt(1)).setChecked(true);
                    } else if (hello.equals("OTHER")) {
                        ((RadioButton) radioGender.getChildAt(2)).setChecked(true);
                    }

                    yourRd = hello;
                    text_km = Integer.parseInt(t_km.getText().toString());
                    txt_min = profileRegisterModel.getData().getTargetAudienceAgeMin();
                    txt_max = profileRegisterModel.getData().getTargetAudienceAgeMax();

                    map.addMarker(new MarkerOptions().position(latLng1));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 12));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(ProfileActivity.this);
                    Log.e("GetProfileRegisterError=>", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", MyApplication.getAuthToken(getApplicationContext()));
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            FileUtils.DismissLoading(ProfileActivity.this);
            e.printStackTrace();
        }
    }

    public static String[] storge_permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};

    public static String[] storge_permissions_33 = {android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO};
    String[] per;

    public String[] permissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                per = storge_permissions_33;
            } else {
                per = storge_permissions;
            }
        } catch (Exception e) {
            Log.e("CameraPermission:==", e.toString());
        }
        return per;
    }

    private void openGallery() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            Uri selectedImageUri;
            try {
                selectedImageUri = data.getData();
                selectedImage = selectedImageUri.getPath();
                Bitmap bitmap = null;
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                Const.bitmap_profile_image = bitmap;
                profile_image.setImageBitmap(bitmap);

                File file = new File(selectedImageUri.getPath());

                FileUtils.personalProfileImageUpload(getApplicationContext(), file);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    public String getPath(Uri uri) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = String.valueOf(location.getLongitude());
        lattitude = String.valueOf(location.getLatitude());

        LatLng userLocation;
        if (Const.longitude != null) {
            userLocation = new LatLng(Const.lattitude, Const.longitude);

        } else {
            userLocation = new LatLng(Double.valueOf(lattitude), Double.valueOf(longitude));

        }
        map.addMarker(new MarkerOptions().position(userLocation));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
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

    public void fetchLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                return;
            } else {

            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

        } catch (Exception e) {
            Log.e("FetchLocation_Error=>", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
            map = googleMap;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);

                criteria = new Criteria();
                bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    Log.e("TAG", "GPS is on");
                    Const.lattitude = location.getLatitude();
                    Const.longitude = location.getLongitude();
                    Log.e("LLL_Latitude: ", +location.getLatitude() + ", Longitude:" + location.getLongitude());

                    centreMapOnLocation(location, bestProvider);
                } else {
                    locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                }

                mapview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), MapsLocationActivity.class).putExtra("isProfileLocation", true));
                    }
                });

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng arg0) {
                        startActivity(new Intent(getApplicationContext(), MapsLocationActivity.class).putExtra("isProfileLocation", true));

                        android.util.Log.i("onMapClick", "Horray!");
                    }
                });
            } else {

            }
        } catch (Exception e) {
            Log.e("OnMapError=>", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

    public void centreMapOnLocation(Location location, String title) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(userLocation);
        markerOptions.draggable(true);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

    }

    private void updateTokenConfirmation() {
        if (!hobbiesArrayList.isEmpty()) {
            hobbiesArrayList.clear();
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        for (Object token : completionView.getObjects()) {
            sb.append(token.toString());
            sb.append(",");

            hobbiesArrayList.add(token.toString());
        }
        Const.tag_str = sb.toString();
    }

    @Override
    public void onTokenAdded(Person token) {
        ((TextView) findViewById(R.id.lastEvent)).setText("Added: " + token);
        updateTokenConfirmation();
    }

    @Override
    public void onTokenRemoved(Person token) {
        ((TextView) findViewById(R.id.lastEvent)).setText("Removed: " + token);
        updateTokenConfirmation();
    }

    @Override
    public void onTokenIgnored(Person token) {
        ((TextView) findViewById(R.id.lastEvent)).setText("Ignored: " + token);
        updateTokenConfirmation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Const.longitude != null) {
            if (map != null) {
                map.clear();
                if (Const.mCurrLocationMarker != null) {
                    Const.mCurrLocationMarker.remove();
                }
                LatLng userLocation = new LatLng(Const.lattitude, Const.longitude);
                map.addMarker(new MarkerOptions().position(userLocation));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        Log.e("OnClickMap=>", "false");
                    }
                });
            }
        } else {
            fetchLocation();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}