package com.festum.festumfield.Activity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
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
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.festum.festumfield.Adapter.ViewPagerAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.Model.Profile.Register.GetPersonalProfileModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.RealPathUtil;
import com.festum.festumfield.Utils.Const;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends BaseActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    RelativeLayout edit_img;
    TextView u_name, u_nickname;
    CircleImageView user_profile_image;
    ImageView ic_back, ic_fb, ic_insta, ic_twitter, ic_linkedin, ic_pinterest, ic_youtube;
    Boolean isBusinessProfile = false;
    public static AppCompatButton btn_edit_profile;
    LinearLayout ll_business_product;
    int pos;
    private static final int PICK_IMAGE = 1;
    public static String[] storge_permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
    List<String> listPermissionsNeeded = new ArrayList<>();
    String perStr = "";
    int permissionCheck,permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        checkPermissions();
        permissionCheck = ContextCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        permission = ContextCompat.checkSelfPermission(UserProfileActivity.this, ACCESS_COARSE_LOCATION);
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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
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
                    if (isBusinessProfile) {
                        ll_business_product.setVisibility(View.VISIBLE);
                    } else {
                        ll_business_product.setVisibility(View.GONE);
                    }
                    btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (permissionCheck == 0 && permission == 0) {
                                Intent intent = new Intent(UserProfileActivity.this, BusinessProfileActivity.class);
                                intent.putExtra("EditProfile", getResources().getString(R.string.edit_business_profile));
                                startActivity(intent);
                            } else {
                                permissionDialog();
                            }
                        }
                    });
                } else {
                    btn_edit_profile.setText(getResources().getString(R.string.edit_personal_profile));
                    ll_business_product.setVisibility(View.GONE);
                    btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (permissionCheck == 0&& permission == 0) {
                                Intent intent = new Intent(UserProfileActivity.this, ProfileActivity.class);
                                intent.putExtra("EditProfile", getResources().getString(R.string.edit_personal_profile));
                                startActivity(intent);
                            } else {
                                permissionDialog();
                            }
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
//            FileUtils.DisplayLoading(UserProfileActivity.this);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    FileUtils.DismissLoading(UserProfileActivity.this);
                    GetPersonalProfileModel userProfileRegisterModel = new Gson().fromJson(response.toString(), GetPersonalProfileModel.class);

                    u_name.setText(userProfileRegisterModel.getData().getFullName());
                    u_nickname.setText(userProfileRegisterModel.getData().getNickName());

                    if (userProfileRegisterModel.getData().getProfileimage().equals("")) {
                        Log.e("GetPersonalInfo-->", "No Image Found");
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

                    isBusinessProfile = userProfileRegisterModel.getData().getIsBusinessProfileCreated();

//                    ic_fb.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (u_name.equals("")) {
//                                Toast.makeText(UserProfileActivity.this, "Enter Data", Toast.LENGTH_SHORT).show();
//                            } else {
//                                for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
//                                    if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Facebook")) {
//                                        String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
//                                        if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
//                                            Uri uri = Uri.parse(url);
//                                            Intent i_fb = new Intent(Intent.ACTION_VIEW, uri);
//                                            startActivity(i_fb);
//
//                                        } else {
//                                            Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    });
//
//                    ic_insta.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
//                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Instagram")) {
//
//                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
//
//                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
//                                        Uri uri = Uri.parse(url);
//                                        Intent i_insta = new Intent(Intent.ACTION_VIEW, uri);
//                                        startActivity(i_insta);
//                                    } else {
//                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }
//                    });
//
//                    ic_twitter.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
//                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Twitter")) {
//                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
//                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
//                                        Uri uri = Uri.parse(url);
//                                        Intent i_twitter = new Intent(Intent.ACTION_VIEW, uri);
//                                        startActivity(i_twitter);
//                                    } else {
//                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }
//                    });
//
//                    ic_linkedin.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
//                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Linkedin")) {
//                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
//                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
//                                        Uri uri = Uri.parse(url);
//                                        Intent i_linkedin = new Intent(Intent.ACTION_VIEW, uri);
//                                        startActivity(i_linkedin);
//                                    } else {
//                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }
//                    });
//
//                    ic_pinterest.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
//                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Pinterest")) {
//                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
//                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
//                                        Uri uri = Uri.parse(url);
//                                        Intent i_linkedin = new Intent(Intent.ACTION_VIEW, uri);
//                                        startActivity(i_linkedin);
//                                    } else {
//                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }
//                    });
//
//                    ic_youtube.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            for (int i = 0; i < userProfileRegisterModel.getData().getSocialMediaLinks().size(); i++) {
//                                if (userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getPlatform().equals("Youtube")) {
//                                    String url = userProfileRegisterModel.getData().getSocialMediaLinks().get(i).getLink();
//                                    if (url.startsWith("www") || url.startsWith("https://") || url.startsWith("http://")) {
//                                        Uri uri = Uri.parse(url);
//                                        Intent i_linkedin = new Intent(Intent.ACTION_VIEW, uri);
//                                        startActivity(i_linkedin);
//                                    } else {
//                                        Toast.makeText(UserProfileActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }
//                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    FileUtils.DismissLoading(UserProfileActivity.this);
                    Log.e("GetPersonalInfoError=>", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", AppPreferencesDelegates.Companion.get().getToken());
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(UserProfileActivity.this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
//            FileUtils.DismissLoading(UserProfileActivity.this);
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectImage = data.getData();
            Glide.with(UserProfileActivity.this).load(selectImage).placeholder(R.drawable.ic_user).into(user_profile_image);

            String path = RealPathUtil.getRealPath(UserProfileActivity.this, selectImage);
            File file = new File(path);
            personalProfileImageUpload(file);
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void personalProfileImageUpload(File file) {
        AndroidNetworking.upload(Constans.set_profile_pic).addMultipartFile("file", file).addHeaders("authorization", AppPreferencesDelegates.Companion.get().getToken()).setTag("uploadTest").setPriority(Priority.HIGH).build().setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
                // do anything with progress
            }
        }).getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("ProfileImgUpload=>", response.toString());
                getApiCalling();
            }

            @Override
            public void onError(ANError error) {
                Log.e("ProfileImgUploadError=>", error.toString());

            }
        });
    }

    private boolean checkPermissions() {
        int result = 0;

        listPermissionsNeeded.clear();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for (String p : storge_permissions_33) {
                result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);

                }
            }
        } else {
            for (String p : storge_permissions) {
                result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);

                }
            }
        }
        Log.e("checkPermissions:", String.valueOf(listPermissionsNeeded));
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                    permissionDialog();
                }
            }
        }
    }

    public void permissionDialog() {
        Dialog dialog = new Dialog(UserProfileActivity.this);
        View view = LayoutInflater.from(UserProfileActivity.this).inflate(R.layout.permission_dialog, null);
        dialog.setContentView(view);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(back, 70);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView dis_txt = dialog.findViewById(R.id.dis_txt);
        AppCompatButton dialog_allow = dialog.findViewById(R.id.dialog_allow);

        dis_txt.setText("To serve you best user experience we need permissions.");
        dialog_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(i);
                dialog.dismiss();
            }
        });

        dialog.show();
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
                    if (permissionCheck == 0&& permission == 0) {
                        Intent intent = new Intent(UserProfileActivity.this, BusinessProfileActivity.class);
                        intent.putExtra("EditProfile", getResources().getString(R.string.edit_business_profile));
                        startActivity(intent);
                    } else {
                        permissionDialog();
                    }
                }
            });
        } else {
            btn_edit_profile.setText(getResources().getString(R.string.edit_personal_profile));
            btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (permissionCheck == 0&& permission == 0) {
                        Intent intent = new Intent(UserProfileActivity.this, ProfileActivity.class);
                        intent.putExtra("EditProfile", getResources().getString(R.string.edit_personal_profile));
                        startActivity(intent);
                    } else {
                        permissionDialog();
                    }
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