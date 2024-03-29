package com.festum.festumfield.Activity;

import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class ChangeNumberActivity extends BaseActivity {

    RelativeLayout relativeChangeNum;
    ImageView back_arrow;
    EditText edt_old_number, edt_new_number;
    AppCompatButton btn_change_number;
    CountryCodePicker ccp_old, ccp_new;
    String code_old, code_new, OtpValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number);

        relativeChangeNum = findViewById(R.id.relativeChangeNum);
        back_arrow = findViewById(R.id.back_arrow);
        edt_old_number = findViewById(R.id.edt_old_number);
        edt_new_number = findViewById(R.id.edt_new_number);
        btn_change_number = findViewById(R.id.btn_change_number);
        ccp_old = findViewById(R.id.ccp_old);
        ccp_new = findViewById(R.id.ccp_new);


        code_old = ccp_old.getDefaultCountryCode();
        code_new = ccp_old.getDefaultCountryCode();

        ccp_old.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                code_old = ccp_old.getSelectedCountryCode();
            }
        });

        ccp_new.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                code_new = ccp_new.getSelectedCountryCode();
            }
        });

        btn_change_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldNum = edt_old_number.getText().toString().trim();
                String newNum = edt_new_number.getText().toString().trim();
                if (oldNum.isEmpty()) {
                    edt_old_number.setError("Enter Old Number");
                } else if (newNum.isEmpty()) {
                    edt_new_number.setError("Enter New Number");
                } else {
                    if (isValidPhoneNumber(oldNum, newNum)) {
                        boolean statusOld = validateUsing_libphonenumber(code_old, oldNum);
                        boolean statusNew = validateUsing_newphonenumber(code_new, newNum);
                        if (!statusOld) {
                            edt_old_number.setError("Invalid Phone Number");
                        } else if (!statusNew) {
                            edt_new_number.setError("Invalid Phone Number");
                        } else {
                            ChangeNumberDialog();
                        }
                    }
                }
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber, CharSequence newNum) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        } else if (!TextUtils.isEmpty(newNum)) {
            return Patterns.PHONE.matcher(newNum).matches();
        }
        return false;
    }

    private boolean validateUsing_libphonenumber(String code_old, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(this);
        String codeOld = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(code_old));
        Phonenumber.PhoneNumber phoneNumber = null;
        Phonenumber.PhoneNumber newPhone = null;
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, codeOld);
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            return true;
        } else {
            edt_old_number.setError("Invalid Phone Number");
            return false;
        }
    }

    private boolean validateUsing_newphonenumber(String code_new, String newNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(this);
        String codeOld = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(code_new));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneNumberUtil.parse(newNumber, codeOld);
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            return true;
        } else {
            edt_new_number.setError("Invalid Phone Number");
            return false;
        }
    }

    public void changeNumber(String edt_old, String code_old, String edt_new, String code_new) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("oldcontactNo", edt_old);
            map.put("oldcountryCode", code_old);
            map.put("newcontactNo", edt_new);
            map.put("newcountryCode", code_new);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.change_number, new JSONObject(map), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    FileUtils.DismissLoading(ChangeNumberActivity.this);
                    Log.e("ChangeNumber=>", response.toString());
                    ChangeNumberVerify();
                    edt_old_number.setText("");
                    edt_new_number.setText("");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ChangeNumberError=>", error.toString());
                    Snackbar.make(relativeChangeNum, "This Number is not change.", Snackbar.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("authorization", AppPreferencesDelegates.Companion.get().getToken());
                    return hashMap;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
//            FileUtils.DismissLoading(ChangeNumberActivity.this);
            e.printStackTrace();
        }
    }

    public void ChangeNumberDialog() {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.change_number_dialog, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(back, 30);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);
        dialog.setCanceledOnTouchOutside(false);

        ImageView dialog_close = dialog.findViewById(R.id.close);
        AppCompatButton dialog_skip = dialog.findViewById(R.id.dialog_no);
        AppCompatButton dialog_continue = dialog.findViewById(R.id.dialog_yes);

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FileUtils.DismissLoading(ChangeNumberActivity.this);
                if (!edt_old_number.getText().toString().isEmpty() && !edt_new_number.getText().toString().isEmpty()) {
                    changeNumber(edt_old_number.getText().toString().trim(), code_old, edt_new_number.getText().toString().trim(), code_new);
                    dialog.cancel();
                } else {
                    dialog.cancel();
                }
            }
        });
        dialog.show();
    }

    public void ChangeNumberVerify() {
        Dialog dialog = new Dialog(ChangeNumberActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.change_number_verify_dialog, null);
        dialog.setContentView(view);

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(back, 50);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);

        AppCompatButton btn_verify = dialog.findViewById(R.id.btn_verify);
        TextView txt_error_msg = dialog.findViewById(R.id.txt_error_msg);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        OtpTextView otpTextView = dialog.findViewById(R.id.otpTextView);

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                OtpValue = otp;
                Log.e("CPHOtp=>", otp);
            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FileUtils.hideKeyboard(ChangeNumberActivity.this);
                try {
                    if (OtpValue != null && OtpValue.length() > 3) {
                        txt_error_msg.setVisibility(View.GONE);
//                        FileUtils.DisplayLoading(ChangeNumberActivity.this);
                        btn_verify.setBackground(getResources().getDrawable(R.drawable.login_btn_bg));
                        newVerifyOtp(OtpValue);
                        dialog.cancel();
                    } else {
                        txt_error_msg.setVisibility(View.VISIBLE);
                        txt_error_msg.setText("Please Enter Otp");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    public void newVerifyOtp(String otpValue) {
        JsonObjectRequest request = null;
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("otp", otpValue);
            request = new JsonObjectRequest(Request.Method.POST, Constans.verify_otp_new, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    FileUtils.DismissLoading(ChangeNumberActivity.this);
                    Log.e("ChangeNumOtp=>", response.toString());
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    FileUtils.DismissLoading(ChangeNumberActivity.this);
                    System.out.println("ChangeNumOtpError=>" + error.toString());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("authorization", AppPreferencesDelegates.Companion.get().getToken());
                    return map;
                }

            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);

        } catch (Exception e) {
//            FileUtils.DismissLoading(ChangeNumberActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChangeNumberActivity.this, SettingActivity.class));
        finish();
    }
}