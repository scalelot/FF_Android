package com.example.friendfield.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.friendfield.Adapter.TagAdapter;
import com.example.friendfield.Model.Profile.Register.GetPersonalProfileModel;
import com.example.friendfield.Model.Profile.Register.ProfileRegisterModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Const;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PersnoalInfoFragment extends Fragment {

    TextView txt_number, txt_mail, txt_location, txt_range, text_interes_in, txt_age;
    TextView txt_about;
    TextView txt_birth;
    TextView txt_gender;
    public static SwitchButton switch_face_id;
    RecyclerView recy_tag;
    View view;

    SharedPreferences prefs;
    boolean value = false;
    String key = "key";
    String sharedPrefName = "isMySwitchChecked";
    public static ArrayList<String> taglist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_persnoal_info, container, false);

        txt_number = view.findViewById(R.id.txt_number);
        txt_mail = view.findViewById(R.id.txt_email);
        txt_location = view.findViewById(R.id.text_location);
        txt_range = view.findViewById(R.id.text_rage);
        text_interes_in = view.findViewById(R.id.text_interes_in);
        txt_age = view.findViewById(R.id.text_age);
        recy_tag = view.findViewById(R.id.recy_tag);
        txt_about = view.findViewById(R.id.txt_about);
        switch_face_id = view.findViewById(R.id.switch_face_id);
        txt_birth = view.findViewById(R.id.txt_birth);
        txt_gender = view.findViewById(R.id.txt_gender);

        recy_tag.setLayoutManager(new FlexboxLayoutManager(getContext()));

        prefs = getActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        value = prefs.getBoolean(key, value);

        switch_face_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switch_face_id.isChecked()) {

                    prefs = getActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
                    prefs.edit().putBoolean(key, true).commit();
                    onResume();

                } else {

                    prefs = getActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
                    prefs.edit().putBoolean(key, false).commit();
                    onPause();

                }
            }
        });

        switch_face_id.setChecked(prefs.getBoolean(key, false));

        getAllData();
        return view;
    }

    private void getAllData() {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        Log.e("FetchPersonal=>", response.toString());
                        JSONObject jsonObject = new JSONObject(String.valueOf(response));
                        GetPersonalProfileModel peronalInfoModel = new Gson().fromJson(response.toString(), GetPersonalProfileModel.class);

                        Double longitude = peronalInfoModel.getData().getLocationModel().getCoordinates().get(0);
                        Double latitude = peronalInfoModel.getData().getLocationModel().getCoordinates().get(1);

                        LatLng latLng = new LatLng(latitude, longitude);

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginUserIds", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("loginIds", peronalInfoModel.getData().getId());
                        editor.apply();
                        editor.commit();

                        txt_number.setText(peronalInfoModel.getData().getContactNo());
                        txt_mail.setText(peronalInfoModel.getData().getEmailId());
                        txt_birth.setText(peronalInfoModel.getData().getDob());
                        txt_gender.setText(peronalInfoModel.getData().getGender());

                        txt_about.setText(peronalInfoModel.getData().getAboutUs());

                        if (!taglist.isEmpty()) {
                            taglist.clear();
                        }
                        for (int i = 0; i < peronalInfoModel.getData().getHobbies().size(); i++) {
                            taglist.add(peronalInfoModel.getData().getHobbies().get(i));
                        }
                        TagAdapter tagAdapter = new TagAdapter(getActivity(), taglist);
                        recy_tag.setAdapter(tagAdapter);

                        txt_range.setText(peronalInfoModel.getData().getAreaRange().toString());
                        text_interes_in.setText(peronalInfoModel.getData().getInterestedin());
                        txt_age.setText(peronalInfoModel.getData().getTargetAudienceAgeMin() + "-" + peronalInfoModel.getData().getTargetAudienceAgeMax());
                        txt_location.setText(FileUtils.getAddressFromLatLng(getContext(), latLng));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("FetchPersonal_Error=>", error.toString());
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

            RequestQueue referenceQueue = Volley.newRequestQueue(getContext());
            referenceQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllData();
    }
}
