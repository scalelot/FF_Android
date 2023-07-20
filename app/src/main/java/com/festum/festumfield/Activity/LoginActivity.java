package com.festum.festumfield.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.Model.SendOtpModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    CountryCodePicker ccp;
    EditText edtPhone;
    AppCompatButton btn_continue;
    String countycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ccp = findViewById(R.id.ccp);
        edtPhone = findViewById(R.id.edtPhone);
        btn_continue = findViewById(R.id.btn_continue);

        //Firebase GetToken
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e("TAG", s);
                MyApplication.setNotificationToken(LoginActivity.this, s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", e.toString());
            }
        });

        countycode = ccp.getDefaultCountryCode();

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countycode = ccp.getSelectedCountryCode();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.hideKeyboard(LoginActivity.this);
                String newToken = MyApplication.getNotificationToken(LoginActivity.this);

                if (!edtPhone.getText().toString().equals("")) {
                    if (FileUtils.isValidPhoneNumber(edtPhone.getText().toString())) {
                        SendOtp(countycode, edtPhone.getText().toString(), newToken);
                        FileUtils.DisplayLoading(LoginActivity.this);
                    } else {
                        edtPhone.setError(getResources().getString(R.string.please_enter_mno));
                    }
                } else {
                    edtPhone.setError(getResources().getString(R.string.please_enter_mno_error));
                }
            }
        });

    }

    public void SendOtp(String countyCode, String phone_number, String newToken) {
        JsonObjectRequest request = null;
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("countryCode", countyCode);
            params.put("contactNo", phone_number);
            params.put("fcmtoken", newToken);
            request = new JsonObjectRequest(Request.Method.POST, Constans.send_otp, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(LoginActivity.this);
                    Log.e("LoginSendOtp=>", response.toString());

                    SendOtpModel sendOtpModel = new Gson().fromJson(response.toString(), SendOtpModel.class);

                    startActivity(new Intent(getApplicationContext(), LoginVerifyActivity.class).putExtra("csrfToken", sendOtpModel.getData().getToken()).putExtra("Country_code", countycode).putExtra("ph_number", edtPhone.getText().toString()));
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(LoginActivity.this);
                    System.out.println("LoginSendOtpError=>" + error.toString());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(request);
        } catch (Exception e) {
            FileUtils.DismissLoading(LoginActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtPhone.setText("");
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}