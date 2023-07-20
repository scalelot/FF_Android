package com.festum.festumfield;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.festum.festumfield.Activity.ChatingActivity;
import com.festum.festumfield.Activity.CreateNewGroupActivity;
import com.festum.festumfield.Activity.DisplayAllProductActivity;
import com.festum.festumfield.Activity.LikeAndCommentActivity;
import com.festum.festumfield.Activity.PromotionActivity;
import com.festum.festumfield.Activity.RequestActivity;
import com.festum.festumfield.Activity.ProfileActivity;
import com.festum.festumfield.Activity.SettingActivity;
import com.festum.festumfield.Activity.UserProfileActivity;
import com.festum.festumfield.Fragment.CallsFragment;
import com.festum.festumfield.Fragment.ChatFragment;
import com.festum.festumfield.Fragment.ContactFragment;
import com.festum.festumfield.Fragment.MapsFragment;
import com.festum.festumfield.Model.Profile.Register.GetPersonalProfileModel;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    TextView txt_chats, txt_find_friend, txt_calls, txt_contact_list, select;
    ImageView iv_chats;
    ImageView iv_find_friend;
    ImageView iv_calls;
    ImageView iv_contact_list;
    LinearLayout lin_chats;
    LinearLayout lin_find_friend;
    LinearLayout lin_calls;
    LinearLayout lin_contact_list;
    ImageView iv_likes;
    ImageView iv_promotion;
    ImageView iv_business_account, popup_btn;
    TextView noti_title, noti_message;
    ColorStateList def;
    TextView user_name;
    CircleImageView user_img;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (PackageManager.PERMISSION_GRANTED == 0) {
            ActivityCompat.requestPermissions(this, permissions(), 1);
        } else {
            getContactList();
        }

        txt_chats = findViewById(R.id.txt_chats);
        txt_find_friend = findViewById(R.id.txt_find_friend);
        txt_calls = findViewById(R.id.txt_calls);
        txt_contact_list = findViewById(R.id.txt_contact_list);

        iv_chats = findViewById(R.id.iv_chats);
        iv_find_friend = findViewById(R.id.iv_find_friend);
        iv_calls = findViewById(R.id.iv_calls);
        iv_contact_list = findViewById(R.id.iv_contact_list);

        lin_chats = findViewById(R.id.lin_chats);
        lin_find_friend = findViewById(R.id.lin_find_friend);
        lin_calls = findViewById(R.id.lin_calls);
        lin_contact_list = findViewById(R.id.lin_contact_list);

        user_name = findViewById(R.id.user_name);
        user_img = findViewById(R.id.user_img);
        iv_business_account = findViewById(R.id.iv_business_account);
        iv_likes = findViewById(R.id.iv_likes);
        iv_promotion = findViewById(R.id.iv_promotion);
        popup_btn = findViewById(R.id.popup_btn);
        noti_title = findViewById(R.id.noti_title);
        noti_message = findViewById(R.id.noti_message);

        def = txt_find_friend.getTextColors();
        Log.e("CountryCode==>", MyApplication.getCountryCode(getApplicationContext()));

        if (MyApplication.getuserName(getApplicationContext()).equals("")) {
            user_name.setText(MyApplication.getCountryCode(getApplicationContext()) + " " + MyApplication.getcontactNo(getApplicationContext()));
        } else {
            user_name.setText(MyApplication.getuserName(getApplicationContext()));
        }

        Log.e("AuthToken==>", MyApplication.getAuthToken(getApplicationContext()));

        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            }
        });

        iv_business_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DisplayAllProductActivity.class));
                finish();
            }
        });

        iv_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LikeAndCommentActivity.class));

            }
        });
        iv_promotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PromotionActivity.class));
                finish();
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new ChatFragment()).commit();

        lin_chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_chats.setTextColor(getResources().getColor(R.color.app_color));
                txt_find_friend.setTextColor(def);
                txt_calls.setTextColor(def);
                txt_contact_list.setTextColor(def);

                iv_chats.setColorFilter(getResources().getColor(R.color.app_color));
                iv_find_friend.setColorFilter(getResources().getColor(R.color.grey));
                iv_calls.setColorFilter(getResources().getColor(R.color.grey));
                iv_contact_list.setColorFilter(getResources().getColor(R.color.grey));

                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChatFragment()).commit();
            }
        });

        lin_find_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txt_find_friend.setTextColor(getResources().getColor(R.color.app_color));
                txt_chats.setTextColor(def);
                txt_calls.setTextColor(def);
                txt_contact_list.setTextColor(def);

                iv_find_friend.setColorFilter(getResources().getColor(R.color.app_color));
                iv_chats.setColorFilter(getResources().getColor(R.color.grey));
                iv_calls.setColorFilter(getResources().getColor(R.color.grey));
                iv_contact_list.setColorFilter(getResources().getColor(R.color.grey));

                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MapsFragment()).commit();
            }
        });

        lin_calls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_calls.setTextColor(getResources().getColor(R.color.app_color));
                txt_chats.setTextColor(def);
                txt_find_friend.setTextColor(def);
                txt_contact_list.setTextColor(def);

                iv_calls.setColorFilter(getResources().getColor(R.color.app_color));
                iv_find_friend.setColorFilter(getResources().getColor(R.color.grey));
                iv_chats.setColorFilter(getResources().getColor(R.color.grey));
                iv_contact_list.setColorFilter(getResources().getColor(R.color.grey));

                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CallsFragment()).commit();

            }
        });

        lin_contact_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_contact_list.setTextColor(getResources().getColor(R.color.app_color));
                txt_chats.setTextColor(def);
                txt_find_friend.setTextColor(def);
                txt_calls.setTextColor(def);

                iv_contact_list.setColorFilter(getResources().getColor(R.color.app_color));
                iv_find_friend.setColorFilter(getResources().getColor(R.color.grey));
                iv_calls.setColorFilter(getResources().getColor(R.color.grey));
                iv_chats.setColorFilter(getResources().getColor(R.color.grey));

                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ContactFragment()).commit();

            }
        });

        popup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, popup_btn);
                popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.requests) {
                            startActivity(new Intent(MainActivity.this, RequestActivity.class));
                            finish();
                            return true;
                        } else if (itemId == R.id.setting) {
                            startActivity(new Intent(MainActivity.this, SettingActivity.class));
                            finish();
                            return true;
                        } else if (itemId == R.id.new_broadcast) {
                            startActivity(new Intent(MainActivity.this, CreateNewGroupActivity.class));
                            finish();
                            return true;
                        } else if (itemId == R.id.new_group) {
                            startActivity(new Intent(MainActivity.this, CreateNewGroupActivity.class));
                            finish();
                            return true;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        if (getIntent().getExtras() != null) {
            try {
                if (getIntent().getExtras() != null) {
                    for (String key : getIntent().getExtras().keySet()) {
                        if (key.equals("title"))
                            noti_title.setText(getIntent().getExtras().getString(key));
                        else if (key.equals("message"))
                            noti_message.setText(getIntent().getExtras().getString(key));
                    }

                    if (noti_title.equals("Chat")) {
                        startActivity(new Intent(MainActivity.this, ChatingActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Error Get Data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.getMessage();
            }
        }

        if (getIntent().getExtras() != null) {
            checkFCMBundle(intent);
        }
    }

    @SuppressLint("Range")
    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i("ContactName=>", "Name: " + name);
                        Log.i("ContactNumber=>", "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }

    private void getAllPersonalData() {
        try {
//            FileUtils.DisplayLoading(MainActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
//                        FileUtils.DismissLoading(MainActivity.this);
                        Log.e("GetProfileData=>", response.toString());
                        JSONObject jsonObject = new JSONObject(String.valueOf(response));
                        GetPersonalProfileModel peronalInfoModel = new Gson().fromJson(response.toString(), GetPersonalProfileModel.class);

                        MyApplication.setuserName(getApplicationContext(), peronalInfoModel.getData().getFullName());
                        MyApplication.setChannelId(getApplicationContext(), peronalInfoModel.getData().getChannelID());

                        if (MyApplication.getuserName(getApplicationContext()).equals("")) {
                            user_name.setText(MyApplication.getCountryCode(getApplicationContext()) + " " + MyApplication.getcontactNo(getApplicationContext()));
                        } else {
                            user_name.setText(MyApplication.getuserName(getApplicationContext()));
                        }

                        String fullName = peronalInfoModel.getData().getFullName();
                        if (fullName.isEmpty()) {
                            CustomDialog();
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    FileUtils.DismissLoading(MainActivity.this);
                    Log.e("GetProfileDataError=>", error.toString());
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

            RequestQueue referenceQueue = Volley.newRequestQueue(getApplicationContext());
            referenceQueue.add(jsonObjectRequest);
        } catch (Exception e) {
//            FileUtils.DismissLoading(MainActivity.this);
            e.printStackTrace();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkFCMBundle(intent);
    }

    private void checkFCMBundle(Intent intent) {
        try {
            intent = getIntent();
            Bundle bundle = intent.getExtras();
            String str = bundle.getString("title");
            String str1 = bundle.getString("body");
            String push = bundle.getString("pushType");

            switch (push) {

                case "TYPE_ONE":
                    startActivity(new Intent(getApplicationContext(), ChatingActivity.class));
                    break;

            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static String[] storge_permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    String[] p;

    public String[] permissions() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                p = storge_permissions_33;
            } else {
                p = storge_permissions;
            }
        } catch (Exception e) {
            Log.e("Permission:==", e.toString());
        }
        return p;
    }

    public void CustomDialog() {

        Dialog dialog = new Dialog(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.create_profile_dialog, null);
        dialog.setContentView(view);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(back, 70);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);

        ImageView dialog_close = dialog.findViewById(R.id.dialog_close);
        AppCompatButton dialog_skip = dialog.findViewById(R.id.dialog_skip);
        AppCompatButton dialog_continue = dialog.findViewById(R.id.dialog_continue);

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) dialog.dismiss();
            }
        });

        dialog_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) dialog.dismiss();
            }
        });

        dialog_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) dialog.dismiss();

                if (!MyApplication.isPersonalProfileRegistered(getApplicationContext())) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "User profile already registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FileUtils.hideKeyboard(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllPersonalData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}