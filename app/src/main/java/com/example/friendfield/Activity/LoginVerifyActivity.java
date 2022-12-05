package com.example.friendfield.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.MainActivity;
import com.example.friendfield.Model.SendOtpModel;
import com.example.friendfield.Model.Verifyotp.VerifyOtpModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class LoginVerifyActivity extends BaseActivity {
    ImageView iv_back;
    TextView txt_phone_number;

    TextView tv_timer;
    TextView txt_resend_code;
    Button btn_verify;
    String csrfToken;
    String OtpValue;
    String MobileNo;
    String c_code;
    RequestQueue queue;
    String cookie;
    OtpTextView otpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verify);

        queue = Volley.newRequestQueue(LoginVerifyActivity.this);

        iv_back = findViewById(R.id.iv_back);
        txt_phone_number = findViewById(R.id.txt_phone_number);

        tv_timer = findViewById(R.id.tv_timer);
        txt_resend_code = findViewById(R.id.txt_resend_code);
        btn_verify = findViewById(R.id.btn_verify);

        otpTextView = findViewById(R.id.otpTextView);

        csrfToken = getIntent().getStringExtra("csrfToken");
        c_code = getIntent().getStringExtra("c_code");
        MobileNo = getIntent().getStringExtra("m_no");
        Log.e("LLL_csrfto--->", csrfToken);

        txt_phone_number.setText(" +" + c_code + " " + MobileNo);

        MyApplication.setCountryCode(getApplicationContext(), " +" + c_code);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
//                otpTextView.setOTP(otp);
                OtpValue = otp;
                Log.e("LLL_onOtpCompleted=>", otp);
            }
        });

        showtimer();

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.hideKeyboard(LoginVerifyActivity.this);

                try {
                    if (!OtpValue.equals("")) {
                        FileUtils.DisplayLoading(LoginVerifyActivity.this);
                        btn_verify.setBackground(getResources().getDrawable(R.drawable.login_btn_bg));
                        VerifyOtp(OtpValue);
                    } else {
                        Toast.makeText(LoginVerifyActivity.this, "Please Enter Otp", Toast.LENGTH_LONG).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        txt_resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SendOtp(MobileNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void showtimer() {
        txt_resend_code.setEnabled(false);

        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tv_timer.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
            }

            public void onFinish() {
                tv_timer.setText("00:00");
                txt_resend_code.setEnabled(true);

            }
        }.start();

    }

    public void VerifyOtp(String otpval) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("otp", otpval);

        JsonObjectRequest request = null;
        try {
            request = new JsonObjectRequest(Request.Method.POST, Constans.verify_otp, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(LoginVerifyActivity.this);
                    Log.e("LLL_v_otp_res-->", response.toString());

                    VerifyOtpModel verifyOtpModel = new Gson().fromJson(response.toString(), VerifyOtpModel.class);

//                    if (verifyOtpModel.getStatusCode().equals("10001")) {
//
//                        Toast.makeText(LoginVerifyActivity.this, verifyOtpModel.getMessage(), Toast.LENGTH_LONG).show();
//                    } else {
                    Toast.makeText(LoginVerifyActivity.this, verifyOtpModel.getMessage(), Toast.LENGTH_LONG).show();

//                        MyApplication.setuserActive(getApplicationContext(), verifyOtpModel.getVerifyDataModel().getVerifyUserModel().getActive());
//                        MyApplication.setcontactNo(getApplicationContext(), verifyOtpModel.getVerifyDataModel().getVerifyUserModel().getContactNo());
//                        MyApplication.setuserName(getApplicationContext(), verifyOtpModel.getVerifyDataModel().getVerifyUserModel().getUserName());
//                        MyApplication.setAuthToken(getApplicationContext(), verifyOtpModel.getVerifyDataModel().getVerifyTokenModel().getAuthToken());
//                        MyApplication.setPersonalProfileRegistered(getApplicationContext(), verifyOtpModel.getVerifyDataModel().getVerifyUserModel().getIsPersonalProfileRegistered());
//                        MyApplication.setBusinessProfileRegistered(getApplicationContext(), verifyOtpModel.getVerifyDataModel().getVerifyUserModel().getIsBusinessProfileRegistered());
//                        Log.e("LLL_auth_token--->", verifyOtpModel.getVerifyDataModel().getVerifyTokenModel().getAuthToken());

                    MyApplication.setAuthToken(getApplicationContext(), "bearer " + verifyOtpModel.getData().getToken());
                    Log.e("LLL_auth_token--->", verifyOtpModel.getData().getToken());


                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

//                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(LoginVerifyActivity.this);
                    System.out.println("LLL_v_err--> " + error.toString());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
//                    map.put("auth-token", "bearer "+csrfToken);
                    map.put("authorization", "bearer " + csrfToken);
//                    map.put("Cookie", "auth-token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJfY29udGFjdE5vIjoiNzI4NDA3MDg2NiIsIl91c2VySWQiOiIyZWU2NDliOC0yNmFmLTQzNmMtOTBiZi1lYzYwM2Y5ZDRjNGMiLCJfbmV3VXNlciI6ZmFsc2UsImlhdCI6MTY0OTEzNDkxNX0.qiqAF6agReb3FZ3wHRyPzpR_N_M46ra9ogt07A_QLgbG_iy-avw4dfCOzaf2y74PJBcBJ0Lfc0cZXUoyJsDBfA");
                    return map;
                }

            };
            queue.add(request);

        } catch (Exception e) {
            FileUtils.DismissLoading(LoginVerifyActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void SendOtp(String phone_number) {

        showtimer();

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("contactNo", phone_number);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("countryCode", c_code);
        params.put("contactNo", phone_number);

        JsonObjectRequest request = null;
        try {
            request = new JsonObjectRequest(Request.Method.POST, Constans.send_otp, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("LLL_sendotp_res-->", response.toString());

                    SendOtpModel sendOtpModel = new Gson().fromJson(response.toString(), SendOtpModel.class);
                    Log.e("LLL_message-->", sendOtpModel.getMessage());

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginVerifyActivity.this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
                    Log.e("LLL_err-->", error.getMessage());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    return map;
                }

            };
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();

        }

        queue.add(request);

    }
}