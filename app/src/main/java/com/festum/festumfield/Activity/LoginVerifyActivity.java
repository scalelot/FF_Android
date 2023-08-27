package com.festum.festumfield.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.Model.SendOtpModel;
import com.festum.festumfield.Model.Verifyotp.VerifyOtpModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.festum.festumfield.verstion.firstmodule.FestumApplicationClass;
import com.festum.festumfield.verstion.firstmodule.screens.main.HomeActivity;
import static com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates.*;

import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class LoginVerifyActivity extends BaseActivity {
    ImageView iv_back;
    LinearLayout ll_linear;
    TextView txt_phone_number;
    TextView tv_timer;
    TextView txt_resend_code;
    AppCompatButton btn_verify;
    String csrfToken;
    String OtpValue;
    String MobileNo;
    String c_code;
    RequestQueue queue;
    OtpTextView otpTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verify);

        queue = Volley.newRequestQueue(LoginVerifyActivity.this);

        iv_back = findViewById(R.id.iv_back);
        txt_phone_number = findViewById(R.id.txt_phone_number);
        ll_linear = findViewById(R.id.ll_linear);

        tv_timer = findViewById(R.id.tv_timer);
        txt_resend_code = findViewById(R.id.txt_resend_code);
        btn_verify = findViewById(R.id.btn_verify);

        otpTextView = findViewById(R.id.otpTextView);

        csrfToken = getIntent().getStringExtra("csrfToken");
        c_code = getIntent().getStringExtra("Country_code");
        MobileNo = getIntent().getStringExtra("ph_number");

        txt_phone_number.setText(" +" + c_code + " " + MobileNo);

        /*MyApplication.setCountryCode(getApplicationContext(), "+" + c_code);*/

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
                OtpValue = otp;
                if (otp.isEmpty()) {
                    btn_verify.setBackground(getResources().getDrawable(R.drawable.verify_btn_bg));
                } else {
                    btn_verify.setBackground(getResources().getDrawable(R.drawable.login_btn_bg));
                }
            }
        });

        showtimer();

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.hideKeyboard(LoginVerifyActivity.this);
                if (OtpValue != null && OtpValue.length() > 3){
                    FileUtils.DisplayLoading(LoginVerifyActivity.this);
                    VerifyOtp(OtpValue);
                }else{
                    Snackbar snackbar = Snackbar.make(ll_linear, "Please Enter Otp", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
//                if (!OtpValue.equals("")) {
//                    FileUtils.DisplayLoading(LoginVerifyActivity.this);
//                    VerifyOtp(OtpValue);
//                } else {
//                    Snackbar snackbar = Snackbar.make(ll_linear, "Please Enter Otp", Snackbar.LENGTH_SHORT);
//                    snackbar.show();
//                }
            }
        });

        txt_resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SendOtp(MobileNo);
                    FileUtils.DisplayLoading(LoginVerifyActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
        JsonObjectRequest request = null;
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("otp", otpval);
            request = new JsonObjectRequest(Request.Method.POST, Constans.verify_otp, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(LoginVerifyActivity.this);
                    Log.e("LoginVerifyOtp=>", response.toString());

                    VerifyOtpModel verifyOtpModel = new Gson().fromJson(response.toString(), VerifyOtpModel.class);

                    Toast.makeText(LoginVerifyActivity.this, verifyOtpModel.getMessage(), Toast.LENGTH_LONG).show();

//                    MyApplication.setAuthToken(getApplicationContext(), "bearer " + verifyOtpModel.getData().getToken());

                    AppPreferencesDelegates.Companion.get().setToken("bearer " + verifyOtpModel.getData().getToken());



//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                    /*FestumApplicationClass festumApplicationClass = new FestumApplicationClass();
                    festumApplicationClass.initializeSocket();*/

                    finish();

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(LoginVerifyActivity.this);
                    Snackbar snackbar = Snackbar.make(ll_linear, "Please Enter Valid Otp", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", "bearer " + csrfToken);
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

    public void SendOtp(String phone_number) {
        showtimer();

        JsonObjectRequest request = null;
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("countryCode", c_code);
            params.put("contactNo", phone_number);
            request = new JsonObjectRequest(Request.Method.POST, Constans.send_otp, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(LoginVerifyActivity.this);
                    Log.e("LoginVerifySendOtp=>", response.toString());

                    SendOtpModel sendOtpModel = new Gson().fromJson(response.toString(), SendOtpModel.class);

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(LoginVerifyActivity.this);
                    Toast.makeText(LoginVerifyActivity.this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
                    Log.e("LoginVerifySendOtpError=>", error.getMessage());
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
            FileUtils.DismissLoading(LoginVerifyActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
        }

        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginVerifyActivity.this, LoginActivity.class));
        finish();
    }
}