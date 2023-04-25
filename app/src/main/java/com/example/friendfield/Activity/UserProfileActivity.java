package com.example.friendfield.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.friendfield.Adapter.ViewPagerAdapter;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.MainActivity;
import com.example.friendfield.Model.Profile.Register.GetPersonalProfileModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Const;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends BaseActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    RelativeLayout edit_img;
    TextView u_name, u_nickname;
    CircleImageView user_profile_image;
    ImageView ic_back, ic_fb, ic_insta, ic_twitter, ic_linkedin, ic_pinterest, ic_youtube;

    public static AppCompatButton btn_edit_profile;
    LinearLayout ll_business_product;
    int pos;
    private static int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        edit_img = findViewById(R.id.edit_img);
        ic_back = findViewById(R.id.ic_back);
        user_profile_image = findViewById(R.id.user_profile_image);
        u_name = findViewById(R.id.u_name);
        u_nickname = findViewById(R.id.u_nickname);
        btn_edit_profile = findViewById(R.id.btn_edit_profile);
        ic_fb = findViewById(R.id.ic_fb);
        ic_insta = findViewById(R.id.ic_insta);
        ic_twitter = findViewById(R.id.ic_twitter);
        ic_linkedin = findViewById(R.id.ic_linkdin);
        ic_pinterest = findViewById(R.id.ic_pinterest);
        ic_youtube = findViewById(R.id.ic_youtube);

        ll_business_product = findViewById(R.id.ll_business_product);

        ll_business_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this, ProductActivity.class));
            }
        });

        if (Const.bitmap_profile_image != null) {
            user_profile_image.setImageBitmap(Const.bitmap_profile_image);
        }

        getApiCalling();

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Images"),CAMERA_PERMISSION_CODE);
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.tab_personal_info)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.tab_business_info)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.tab_reels)));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                pos = tab.getPosition();
                if (tab.getPosition() == 1) {
                    btn_edit_profile.setText(getResources().getString(R.string.edit_business_profile));
                    btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserProfileActivity.this, BusinessProfileActivity.class);
                            intent.putExtra("EditProfile", getResources().getString(R.string.edit_business_profile));
                            startActivity(intent);
                        }
                    });
                } else {
                    btn_edit_profile.setText(getResources().getString(R.string.edit_personal_profile));
                    btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserProfileActivity.this, ProfileActivity.class);
                            intent.putExtra("EditProfile", getResources().getString(R.string.edit_personal_profile));
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getApiCalling() {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    GetPersonalProfileModel userProfileRegisterModel = new Gson().fromJson(response.toString(), GetPersonalProfileModel.class);

                    u_name.setText(userProfileRegisterModel.getData().getFullName());
                    u_nickname.setText(userProfileRegisterModel.getData().getNickName());

                    if (userProfileRegisterModel.getData().getProfileimage().equals("")) {
                        Log.e("LLL_data-->", "No Image Found");
                        user_profile_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
                    } else {
                        Glide.with(UserProfileActivity.this).asBitmap().load(Constans.Display_Image_URL + userProfileRegisterModel.getData().getProfileimage()).placeholder(R.drawable.ic_user).into(user_profile_image);
                    }

                    for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
                        if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Facebook")) {
                            ic_fb.setVisibility(View.VISIBLE);
                        } else if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Instagram")) {
                            ic_insta.setVisibility(View.VISIBLE);
                        } else if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Twitter")) {
                            ic_twitter.setVisibility(View.VISIBLE);
                        } else if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Linkedin")) {
                            ic_linkedin.setVisibility(View.VISIBLE);
                        } else if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Pinterest")) {
                            ic_pinterest.setVisibility(View.VISIBLE);
                        } else if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Youtube")) {
                            ic_youtube.setVisibility(View.VISIBLE);
                        }
                    }


                    if (!userProfileRegisterModel.getData().getIsBusinessProfileCreated()) {
                        ll_business_product.setVisibility(View.GONE);
                    } else {
                        ll_business_product.setVisibility(View.VISIBLE);
                    }

                    ic_fb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (u_name.equals("")) {
                                Toast.makeText(UserProfileActivity.this, "Enter Data", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
                                    if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Facebook")) {

                                        String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
                                        if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
                                            Uri uri = Uri.parse(url);
                                            Intent i_fb = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(i_fb);

                                        } else {
                                            Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    });

                    ic_insta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Instagram")) {

                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();

                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
                                        Uri uri = Uri.parse(url);
                                        Intent i_insta = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(i_insta);
                                    } else {
                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });

                    ic_twitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Twitter")) {
                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
                                        Uri uri = Uri.parse(url);
                                        Intent i_twitter = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(i_twitter);
                                    } else {
                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });

                    ic_linkedin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Linkedin")) {
                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
                                        Uri uri = Uri.parse(url);
                                        Intent i_linkedin = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(i_linkedin);
                                    } else {
                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });

                    ic_pinterest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Pinterest")) {
                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
                                        Uri uri = Uri.parse(url);
                                        Intent i_linkedin = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(i_linkedin);
                                    } else {
                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });

                    ic_youtube.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Youtube")) {
                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
                                        Uri uri = Uri.parse(url);
                                        Intent i_linkedin = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(i_linkedin);
                                    } else {
                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
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
                    map.put("authorization", MyApplication.getAuthToken(UserProfileActivity.this));
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(UserProfileActivity.this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPathFromURI(Uri contentUri){
        String res = null;

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,proj,null,null,null);
        if (cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();

        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PERMISSION_CODE) {

            try {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    user_profile_image.setImageBitmap(bitmap);

                    File file = new File(selectedImageUri.getPath());
                    FileUtils.personalProfileImageUpload(getApplicationContext(), file);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getApiCalling();
        if (pos == 1) {
            btn_edit_profile.setText(getResources().getString(R.string.edit_business_profile));
            btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserProfileActivity.this, BusinessProfileActivity.class);
                    intent.putExtra("EditProfile", getResources().getString(R.string.edit_business_profile));
                    startActivity(intent);
                }
            });
        } else {
            btn_edit_profile.setText(getResources().getString(R.string.edit_personal_profile));
            btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserProfileActivity.this, ProfileActivity.class);
                    intent.putExtra("EditProfile", getResources().getString(R.string.edit_personal_profile));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
        finish();
    }
}