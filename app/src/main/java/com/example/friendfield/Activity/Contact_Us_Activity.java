package com.example.friendfield.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.Model.ContactUs.ContactDataModel;
import com.example.friendfield.Model.ContactUs.ContactModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Contact_Us_Activity extends BaseActivity {

    EditText full_name, phone_number, email_id, description;
    AppCompatButton btn_send;
    ImageView ic_back_arrow, img_add_image;
    ContactModel contactDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        ic_back_arrow = findViewById(R.id.ic_back_arrow);
        full_name = findViewById(R.id.full_name);
        phone_number = findViewById(R.id.phone_number);
        email_id = findViewById(R.id.email_id);
        description = findViewById(R.id.description);
        btn_send = findViewById(R.id.btn_send);
        img_add_image = findViewById(R.id.img_add_image);

        ic_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (full_name.getText().toString().trim().isEmpty()) {
                    full_name.setError(getResources().getString(R.string.enter_full_name));
                } else if (phone_number.getText().toString().trim().isEmpty()) {
                    phone_number.setError(getResources().getString(R.string.enter_phone_number));
                } else if (email_id.getText().toString().trim().isEmpty()) {
                    email_id.setError(getResources().getString(R.string.enter_emailid));
                } else if (description.getText().toString().trim().isEmpty()) {
                    description.setError(getResources().getString(R.string.enter_description));
                } else {
                    getContactApi(full_name.getText().toString().trim(), phone_number.getText().toString().trim(), email_id.getText().toString().trim(), description.getText().toString().trim());
                }
            }
        });
    }

    private void getContactApi(String name, String ph_number, String email, String des) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("fullName", name);
        hashMap.put("contactNo", ph_number);
        hashMap.put("emailId", email);
        hashMap.put("issue", des);

        JsonObjectRequest jsonObjectRequest = null;
        try {

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.contact_us, new JSONObject(hashMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("ContactUs=>", response.toString());
                    ContactDataModel contactDataModel = new Gson().fromJson(response.toString(), ContactDataModel.class);
                    full_name.setText("");
                    phone_number.setText("");
                    email_id.setText("");
                    description.setText("");

                    Toast.makeText(Contact_Us_Activity.this, contactDataModel.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("CContactUs_Error=>" + error.toString());
                    Toast.makeText(Contact_Us_Activity.this, "Data Not Submit" + error, Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", MyApplication.getAuthToken(getApplicationContext()));
                    return map;
                }
            };
        } catch (Exception e) {
            FileUtils.DismissLoading(Contact_Us_Activity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(Contact_Us_Activity.this);
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Contact_Us_Activity.this, SettingActivity.class));
        finish();
    }
}