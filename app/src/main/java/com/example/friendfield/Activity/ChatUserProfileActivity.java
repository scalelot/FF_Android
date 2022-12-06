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
import com.bumptech.glide.Glide;
import com.example.friendfield.Adapter.ChatViewPagerAdapter;
import com.example.friendfield.Adapter.TagAdapter;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.Model.Profile.Register.GetPersonalProfileModel;
import com.example.friendfield.Model.UserProfile.UserProfileRegisterModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserProfileActivity extends BaseActivity {

    ImageView ic_back;
    TextView u_name, u_nickname, chat_name;
    LinearLayout l_product;
    TabLayout tabLayout;
    ViewPager viewPager;
    String chatImg, chatFname, chatNick;
    CircleImageView user_profile_image;
    ImageView ic_fb, ic_insta, ic_tw, ic_ld, ic_pinterest, ic_youtube;

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
        chat_name = findViewById(R.id.chat_name);
        u_nickname = findViewById(R.id.u_nickname);
        ic_fb = findViewById(R.id.ic_fb);
        ic_insta = findViewById(R.id.ic_insta);
        ic_tw = findViewById(R.id.ic_twitter);
        ic_ld = findViewById(R.id.ic_linkdin);
        ic_pinterest = findViewById(R.id.ic_pinterest);
        ic_youtube = findViewById(R.id.ic_youtube);
        user_profile_image = findViewById(R.id.user_profile_image);

        chatImg = getSharedPreferences("ToUserIds", MODE_PRIVATE).getString("images", null);
        chatFname = getSharedPreferences("ToUserIds", MODE_PRIVATE).getString("userName", null);
        chatNick = getSharedPreferences("ToUserIds", MODE_PRIVATE).getString("nickName", null);

        Glide.with(this).load(Constans.Display_Image_URL + chatImg).placeholder(R.drawable.ic_user_img).into(user_profile_image);
        chat_name.setText(chatFname);
        u_nickname.setText(chatNick);

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
        startActivity(new Intent(ChatUserProfileActivity.this, ChatingActivity.class));
    }

}