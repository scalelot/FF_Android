package com.festum.festumfield.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.festum.festumfield.verstion.firstmodule.screens.dialog.CodeDialog;
import com.festum.festumfield.verstion.firstmodule.sources.local.model.PhoneCodeModel;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.festum.festumfield.verstion.firstmodule.utils.CountryCityUtils;
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class LoginActivity extends BaseActivity implements CodeDialog.CountyPickerItems {

    CountryCodePicker ccp;
    EditText edtPhone;
    AppCompatButton btn_continue;
    String countycode;

    TextView codeFlag,countryCode;

    LinearLayout linearLayout;

    PhoneCodeModel selectedPhoneModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*ccp = findViewById(R.id.ccp);*/
        edtPhone = findViewById(R.id.edtPhone);
        btn_continue = findViewById(R.id.btn_continue);
        codeFlag = findViewById(R.id.codeFlag);
        countryCode = findViewById(R.id.countryCode);
        linearLayout = findViewById(R.id.linear);

        //Firebase GetToken
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
               AppPreferencesDelegates.Companion.get().setFcmToken(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", e.toString());
            }
        });

        /*countycode = ccp.getDefaultCountryCode();




        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countycode = ccp.getSelectedCountryCode();

            }
        });*/

        try {
            getCountryCode();
        } catch (JSONException e) {
            Log.e("TAG", "onCreate:--+-- " + e.getLocalizedMessage() );
            throw new RuntimeException(e);
        }

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileUtils.hideKeyboard(LoginActivity.this);
                    String newToken = AppPreferencesDelegates.Companion.get().getFcmToken();
                    String phoneNumber = edtPhone.getText().toString().trim();

                    Log.e("TAG", "onClick:--- " + countycode );
                    if (phoneNumber.isEmpty()) {
                        edtPhone.setError("Enter Mobile Number");
                    } else {
                        if (isValidPhoneNumber(phoneNumber)) {
                            boolean status = validateUsing_libphonenumber(countycode, phoneNumber);
                            if (status) {
                                SendOtp(countycode, edtPhone.getText().toString(), newToken);
                                FileUtils.DisplayLoading(LoginActivity.this);
                                Button b = (Button) view;
                                b.setEnabled(false);
                            } else {
                                edtPhone.setError("Invalid Phone Number");
                            }
                        } else {
                            edtPhone.setError("Invalid Phone Number");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    private boolean validateUsing_libphonenumber(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(this);
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            return true;
        } else {
            edtPhone.setError("Invalid Phone Number");
            return false;
        }
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
        AppCompatButton button1 = (AppCompatButton) findViewById(R.id.btn_continue);
        button1.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @SuppressLint("SetTextI18n")
    public void getCountryCode() throws JSONException {
        ArrayList<PhoneCodeModel> phoneModelList = new ArrayList<>();
        JSONObject obj = new JSONObject(Objects.requireNonNull(FileUtil.Companion.loadJSONFromAsset(this)));

        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String keyStr = keys.next();
            try {
                String keyValue = obj.getString(keyStr);
                PhoneCodeModel code = new PhoneCodeModel(keyStr, keyValue);
                phoneModelList.add(code);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (selectedPhoneModel == null) {
            selectedPhoneModel = phoneModelList.get(86);
        }

        countryCode.setText("+ " + selectedPhoneModel.getValue());
        countryCode.setText(countryCode.getText().toString());
        countycode = selectedPhoneModel.getValue();

        codeFlag.setText(CountryCityUtils.Companion.getFlagId(CountryCityUtils.Companion.firstTwo(
                Objects.requireNonNull(selectedPhoneModel.getKey()).toLowerCase(Locale.getDefault())
        )));

        CodeDialog phoneCodeDialog = new CodeDialog(this, phoneModelList, this);

        linearLayout.setOnClickListener(view -> {
            if (!phoneCodeDialog.isAdded()) {
                phoneCodeDialog.show(getSupportFragmentManager(), null);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void pickCountry(@NonNull PhoneCodeModel countries) {

        selectedPhoneModel = countries;
        countryCode.setText("+ " + countries.getValue());
        countycode = countries.getValue();
        codeFlag.setText(
                CountryCityUtils.Companion.getFlagId(
                        CountryCityUtils.Companion.firstTwo(
                                Objects.requireNonNull(countries.getKey()).toLowerCase(Locale.getDefault())
                        )
                )
        );

    }

}