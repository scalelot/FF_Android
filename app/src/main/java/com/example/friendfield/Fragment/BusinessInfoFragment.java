package com.example.friendfield.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.example.friendfield.Activity.BusinessProfileActivity;
import com.example.friendfield.Activity.UserProfileActivity;
import com.example.friendfield.Model.BusinessInfo.BusinessInfoRegisterModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Const;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BusinessInfoFragment extends Fragment {

    RelativeLayout edt_img;
    CircleImageView cir_business_img;
    private static final int PICK_IMAGE1 = 100;
    TextView txt_business_name, txt_category, txt_subcategory, txt_discription, txt_business_location, txt_business_category, txt_business_subcategory, txt_business_brochure;
    AppCompatButton btn_create_profile;
    LinearLayout create_profile, ll_create_profile;
    RelativeLayout rel_brochure;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_info, container, false);

        edt_img = view.findViewById(R.id.edt_img);
        cir_business_img = view.findViewById(R.id.cir_business_img);
        txt_business_name = view.findViewById(R.id.text_business_name);
        txt_category = view.findViewById(R.id.txt_category);
        txt_subcategory = view.findViewById(R.id.txt_subcategory);
        txt_discription = view.findViewById(R.id.txt_discription);
        txt_business_location = view.findViewById(R.id.txt_business_location);
        txt_business_category = view.findViewById(R.id.txt_business_category);
        txt_business_subcategory = view.findViewById(R.id.txt_business_subcategory);
        btn_create_profile = view.findViewById(R.id.btn_create_profile);
        create_profile = view.findViewById(R.id.create_profile);
        ll_create_profile = view.findViewById(R.id.ll_create_profile);
        rel_brochure = view.findViewById(R.id.rel_brochure);
        txt_business_brochure = view.findViewById(R.id.txt_business_brochure);

//        if (!MyApplication.isBusinessProfileRegistered(BusinessInfoFragment.this.getContext())) {
//            create_profile.setVisibility(View.VISIBLE);
//            ll_create_profile.setVisibility(View.GONE);
//        } else {
//            create_profile.setVisibility(View.GONE);
//            ll_create_profile.setVisibility(View.VISIBLE);
//        }

        if (Const.bitmap_business_profile_image != null) {
            cir_business_img.setImageBitmap(Const.bitmap_business_profile_image);
        }

        edt_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btn_create_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BusinessProfileActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!MyApplication.isBusinessProfileRegistered(getActivity())) {
            getApiCalling();
//            create_profile.setVisibility(View.VISIBLE);
//            ll_create_profile.setVisibility(View.GONE);
//        } else {
//            ll_create_profile.setVisibility(View.VISIBLE);
//            create_profile.setVisibility(View.GONE);
//        }
    }

    private void getApiCalling() {

        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_business_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Log.e("BusinessInfo=>", response.toString());
                        BusinessInfoRegisterModel businessInfoRegisterModel = new Gson().fromJson(response.toString(), BusinessInfoRegisterModel.class);

                        Double longitude = businessInfoRegisterModel.getData().getLocation().getCoordinates().get(0);
                        Double latitude = businessInfoRegisterModel.getData().getLocation().getCoordinates().get(1);

                        LatLng latLng = new LatLng(latitude, longitude);

                        txt_business_name.setText(businessInfoRegisterModel.getData().getName());
                        txt_category.setText(businessInfoRegisterModel.getData().getCategory());
                        txt_subcategory.setText(businessInfoRegisterModel.getData().getSubCategory());
                        txt_discription.setText(businessInfoRegisterModel.getData().getDescription());
                        txt_business_location.setText(FileUtils.getAddressFromLatLng(getContext(), latLng));
                        txt_business_category.setText(businessInfoRegisterModel.getData().getInterestedCategory());
                        txt_business_subcategory.setText(businessInfoRegisterModel.getData().getInterestedSubCategory());
                        txt_business_brochure.setText(businessInfoRegisterModel.getData().getBrochure());

                        ll_create_profile.setVisibility(View.GONE);
                        create_profile.setVisibility(View.VISIBLE);
                    } else {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("BusinessInfo_Error=>", error.toString());
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

    private void openGallery() {
        ImagePicker.Companion.with(getActivity())
                .crop()
//                        .galleryOnly()
                .maxResultSize(1080, 1080)
                .start(PICK_IMAGE1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE1) {

            try {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
                    cir_business_img.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}