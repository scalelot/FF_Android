package com.example.friendfield.Activity;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.friendfield.Adapter.ChatViewPagerAdapter;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.Model.UserProfile.UserProfileRegisterModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatUserProfileActivity extends BaseActivity {

    ImageView ic_back;
    TextView u_name, u_nickname;
    LinearLayout l_product;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView ic_fb, ic_insta, ic_tw, ic_ld,ic_pinterest,ic_youtube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_profile);

        Window window = ChatUserProfileActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ChatUserProfileActivity.this, R.color.darkturquoise));

        ic_back = findViewById(R.id.ic_back);
        tabLayout = findViewById(R.id.chat_tabLayout);
        viewPager = findViewById(R.id.viewPager);
        l_product = findViewById(R.id.l_product);
        ic_fb = findViewById(R.id.ic_fb);
        ic_insta = findViewById(R.id.ic_insta);
        ic_tw = findViewById(R.id.ic_twitter);
        ic_ld = findViewById(R.id.ic_linkdin);
        ic_pinterest = findViewById(R.id.ic_pinterest);
        ic_youtube = findViewById(R.id.ic_youtube);

        u_name = findViewById(R.id.u_name);
        u_nickname = findViewById(R.id.u_nickname);

        getApiCalling();

        if (!MyApplication.isBusinessProfileRegistered(ChatUserProfileActivity.this)) {
            l_product.setVisibility(View.GONE);
        } else {
            l_product.setVisibility(View.VISIBLE);
        }

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        l_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatUserProfileActivity.this, ProductActivity.class));
                finish();
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.tab_personal_info)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.tab_business_info)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.tab_reels)));

        if (!MyApplication.isBusinessProfileRegistered(ChatUserProfileActivity.this)) {

            ChatViewPagerAdapter viewPagerAdapter = new ChatViewPagerAdapter(ChatUserProfileActivity.this, getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(viewPagerAdapter);

            //if business profile not created
            ((LinearLayout) Objects.requireNonNull(tabLayout.getTabAt(1)).view).setVisibility(View.GONE);

        } else {
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.darkturquoise));
            ChatViewPagerAdapter viewPagerAdapter = new ChatViewPagerAdapter(ChatUserProfileActivity.this, getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(viewPagerAdapter);
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(ChatUserProfileActivity.this, ChatingActivity.class));
        finish();
    }

    private void getApiCalling() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {

                UserProfileRegisterModel userProfileRegisterModel = new Gson().fromJson(response.toString(), UserProfileRegisterModel.class);
//                u_name.setText(userProfileRegisterModel.getUserProfileModel().getFullName());
///                u_nickname.setText(userProfileRegisterModel.getUserProfileModel().getNickName());

//                if (MyApplication.getuserName(getApplicationContext()).equals("")) {
//                    u_name.setText(MyApplication.getCountryCode(getApplicationContext()) + " " + MyApplication.getcontactNo(getApplicationContext()));
//                } else {
//                    u_name.setText(userProfileRegisterModel.getUserProfileModel().getFullName());
//                }
//
//                if (userProfileRegisterModel.getUserProfileModel().getFacebookLink() != null) {
//                    ic_fb.setVisibility(View.VISIBLE);
//                } else {
//                    ic_fb.setVisibility(View.GONE);
//                }
//
//                if (userProfileRegisterModel.getUserProfileModel().getInstagramLink() != null) {
//                    ic_insta.setVisibility(View.VISIBLE);
//                } else {
//                    ic_insta.setVisibility(View.GONE);
//                }
//
//                if (userProfileRegisterModel.getUserProfileModel().getTwitterLink() != null) {
//                    ic_tw.setVisibility(View.VISIBLE);
//                } else {
//                    ic_tw.setVisibility(View.GONE);
//                }
//
//                if (userProfileRegisterModel.getUserProfileModel().getLinkedinLink() != null) {
//                    ic_ld.setVisibility(View.VISIBLE);
//                } else {
//                    ic_ld.setVisibility(View.GONE);
//                }

//                ic_fb.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (u_name.equals("")) {
//                            Toast.makeText(ChatUserProfileActivity.this, "Enter Data", Toast.LENGTH_SHORT).show();
//                        } else {
//                            String url = userProfileRegisterModel.getUserProfileModel().getFacebookLink();
//                            if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
//                                Uri uri = Uri.parse(url);
//                                Intent i_fb = new Intent(Intent.ACTION_VIEW, uri);
//                                startActivity(i_fb);
//
//                            } else {
//                                Toast.makeText(ChatUserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//
//                ic_insta.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String url = userProfileRegisterModel.getUserProfileModel().getInstagramLink();
//                        if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
//                            Uri uri = Uri.parse(url);
//                            Intent i_insta = new Intent(Intent.ACTION_VIEW, uri);
//                            startActivity(i_insta);
//                        } else {
//                            Toast.makeText(ChatUserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//                ic_tw.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        String url = userProfileRegisterModel.getUserProfileModel().getTwitterLink();
//                        if (url.startsWith("https://") || url.startsWith("http://")) {
//                            Uri uri = Uri.parse(url);
//                            Intent i_twitter = new Intent(Intent.ACTION_VIEW, uri);
//                            startActivity(i_twitter);
//                        } else {
//                            Toast.makeText(ChatUserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//                ic_ld.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String url = userProfileRegisterModel.getUserProfileModel().getLinkedinLink();
//                        if (url.startsWith("https://") || url.startsWith("http://")) {
//                            Uri uri = Uri.parse(url);
//                            Intent i_linkedin = new Intent(Intent.ACTION_VIEW, uri);
//                            startActivity(i_linkedin);
//                        } else {
//                            Toast.makeText(ChatUserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("authorization", MyApplication.getAuthToken(ChatUserProfileActivity.this));
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ChatUserProfileActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

}