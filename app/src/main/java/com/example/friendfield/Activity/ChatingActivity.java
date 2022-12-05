package com.example.friendfield.Activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.friendfield.Adapter.MessageAdapter;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.MainActivity;
import com.example.friendfield.Model.BusinessInfo.BusinessInfoRegisterModel;
import com.example.friendfield.Model.ListChat.ListChatsModel;
import com.example.friendfield.Model.PersonalInfo.PeronalRegisterModel;
import com.example.friendfield.Model.ReceiveFriendsList.ReceiveFriendsRegisterModel;
import com.example.friendfield.Model.SendMessage.SendAllModelData;
import com.example.friendfield.Model.SendMessage.SendTextModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatingActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    ImageView hp_back_arrow, img_video_call, img_contact, iv_close;
    ImageView iv_pro_image, img_gallery, img_camera, img_product;
    CircleImageView img_user;
    EditText edt_chating;
    TextView u_name, txt_online, txt_pro_name, txt_pro_des, txt_pro_price;
    RelativeLayout btn_send, rel_replay, rl_user;
    RecyclerView chat_recycler;
    String toUserIds, txt_name, p_name, p_des, userName;
    String loginUserId = "", p_price, edt_str, pro_img;
    Uri imageUri, uri;
    MessageAdapter messageAdapter;
    ListChatsModel listChatsModel;
    ArrayList<ListChatsModel> listChatsModelArrayList = new ArrayList<>();
    protected static final int GALLERY_REQUEST = 1;
    private static final int PERMISSION_CODE = 1000;
    private int IMAGE_CAPTURE_CODE = 1001;
    int page = 1, limit = 20;
    NestedScrollView nestedScrollView;
    List<String> send_message;
    List<String> form_id;
    List<String> to_id;
    List<String> time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        hp_back_arrow = findViewById(R.id.hp_back_arrow);
        img_video_call = findViewById(R.id.img_video_call);
        img_contact = findViewById(R.id.img_contact);
        iv_close = findViewById(R.id.iv_close);
        iv_pro_image = findViewById(R.id.iv_pro_image);
        img_gallery = findViewById(R.id.img_gallery);
        img_camera = findViewById(R.id.img_camera);
        img_user = findViewById(R.id.img_user);
        edt_chating = findViewById(R.id.edt_chating);
        u_name = findViewById(R.id.u_name);
        txt_online = findViewById(R.id.txt_online);
        btn_send = findViewById(R.id.btn_send);
        chat_recycler = findViewById(R.id.chat_recycler);
        rel_replay = findViewById(R.id.rel_replay);
        txt_pro_name = findViewById(R.id.txt_pro_name);
        txt_pro_des = findViewById(R.id.txt_pro_des);
        txt_pro_price = findViewById(R.id.txt_pro_price);
        rl_user = findViewById(R.id.rl_user);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        img_product = findViewById(R.id.img_product);

        toUserIds = getSharedPreferences("ToUserIds", MODE_PRIVATE).getString("ids", null);
        userName = getSharedPreferences("ToUserIds", MODE_PRIVATE).getString("userName", null);

        if (!MyApplication.isBusinessProfileRegistered(ChatingActivity.this)) {
            img_product.setVisibility(View.VISIBLE);
        } else {
            img_product.setVisibility(View.GONE);
        }

        txt_name = getIntent().getStringExtra("UserName");
        pro_img = getIntent().getStringExtra("product_img");
        p_name = getIntent().getStringExtra("product_name");
        p_des = getIntent().getStringExtra("product_des");
        p_price = getIntent().getStringExtra("product_price");

        if (p_name == null && p_des == null && p_price == null) {
            rel_replay.setVisibility(View.GONE);
        } else {
            Glide.with(this).load(Constans.Display_Image_URL + pro_img).placeholder(R.drawable.ic_user_img).into(iv_pro_image);

            txt_pro_name.setText(p_name);
            txt_pro_des.setText(p_des);
            txt_pro_price.setText(p_price);
            rel_replay.setVisibility(View.VISIBLE);
        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rel_replay.setVisibility(View.GONE);
            }
        });

        u_name.setText(userName);

        hp_back_arrow.setOnClickListener(this);
        img_video_call.setOnClickListener(this);
        img_contact.setOnClickListener(this);
        rl_user.setOnClickListener(this);
        img_camera.setOnClickListener(this);
        img_gallery.setOnClickListener(this);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
//                    loaderLay.setVisibility(View.VISIBLE);
                    getChatList(toUserIds, page, limit);
                }
            }
        });

        img_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatingActivity.this, ChatProductSelectActivity.class));
                finish();
            }
        });

        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hp_back_arrow:
                startActivity(new Intent(ChatingActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.img_video_call:
                startActivity(new Intent(ChatingActivity.this, VideoCallActivity.class));
                finish();
                break;
            case R.id.img_contact:
                startActivity(new Intent(ChatingActivity.this, ChattingAudioCallActivity.class));
                finish();
                break;
            case R.id.rl_user:
                startActivity(new Intent(ChatingActivity.this, ChatUserProfileActivity.class));
                finish();
                break;
            case R.id.img_camera:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE);
                } else {
                    openCamera();
                }
            case R.id.img_gallery:
                ImagePicker.Companion.with(ChatingActivity.this)
                        .crop()
                        .galleryOnly()
                        .maxResultSize(1080, 1080)
                        .start(GALLERY_REQUEST);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        edt_str = s.toString().trim();

        if (edt_str.isEmpty()) {
            resetMessageEdit();
        }
    }

    private void resetMessageEdit() {
        edt_chating.removeTextChangedListener(this);
        edt_chating.setText("");
        edt_chating.addTextChangedListener(this);
    }

    private void initView() {

        messageAdapter = new MessageAdapter(getLayoutInflater());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        chat_recycler.setLayoutManager(linearLayoutManager);
        chat_recycler.setAdapter(messageAdapter);

        edt_chating.addTextChangedListener(this);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("to", toUserIds);
                    jsonObject.put("message", edt_chating.getText().toString().trim());
                    jsonObject.put("isSent", true);
                    jsonObject.put("isRecive", false);

                    if (edt_chating.getText().toString().equals("")) {
                        Toast.makeText(ChatingActivity.this, "Enter Text", Toast.LENGTH_SHORT);
                    } else {
                        sendMessage(toUserIds, edt_chating.getText().toString().trim());
                        messageAdapter.addItem(jsonObject);
                    }
                    chat_recycler.smoothScrollToPosition(chat_recycler.getAdapter().getItemCount());
                    resetMessageEdit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendMessage(String toUserIds, String toString) {
        AndroidNetworking.post(Constans.set_chat_message)
                .addBodyParameter("to", toUserIds)
                .addBodyParameter("message", toString)
                .addHeaders("authorization", MyApplication.getAuthToken(getApplicationContext()))
                .setPriority(Priority.HIGH)
                .setTag("UploadTest")
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("LLL_send_message--->", response.toString());
//                        edt_chating.getText().clear();
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("LLL_send_error--->", error.toString());
                    }
                });
    }

    public void getChatList(String toUserIds, int page, int limit) {
        if (page > limit) {
            Toast.makeText(getApplicationContext(), "That's all the data..", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", toUserIds);
            jsonObject.put("page", page);
            jsonObject.put("limit", limit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = null;
        {
            try {
                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.list_chat_message, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("LLL_list_message", response.toString());
                        listChatsModelArrayList.clear();
                        form_id = new ArrayList<>();
                        to_id = new ArrayList<>();
                        send_message = new ArrayList<>();
                        time = new ArrayList<>();
                        try {
                            JSONObject dataJsonObject = response.getJSONObject("Data");

                            JSONArray data_array = dataJsonObject.getJSONArray("docs");

                            for (int i = 0; i < data_array.length(); i++) {
                                JSONObject jsonObject = data_array.getJSONObject(i);
                                listChatsModel = new Gson().fromJson(jsonObject.toString(), ListChatsModel.class);
                                listChatsModelArrayList.add(listChatsModel);
                            }

                            for (int index = 0; index < listChatsModelArrayList.size(); index++) {
                                send_message.add(listChatsModelArrayList.get(index).getSendAllModelData().getText().getMessage());
                                time.add(String.valueOf(listChatsModelArrayList.get(index).getFrom().getId()));
                                try {
                                    JSONObject contact = new JSONObject();
                                    contact.put("message", send_message.get(index));
                                    contact.put("isRecive", false);
                                    contact.put("isSent", true);
                                    messageAdapter.addItem(contact);
                                    chat_recycler.smoothScrollToPosition(chat_recycler.getAdapter().getItemCount());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
//
//                            for (int i = 0; i < time.size(); i++) {
//                                if (time.get(i).contains(loginUserId)) {
//                                    form_id.add(listChatsModelArrayList.get(i).getSendAllModelData().getText().getMessage());
//                                    Log.e("list_message", String.valueOf(form_id));
//                                } else if (!time.get(i).contains(loginUserId)) {
//                                    to_id.add(listChatsModelArrayList.get(i).getSendAllModelData().getText().getMessage());
//                                }
//                            }
//
//                            if (!form_id.isEmpty()) {
//                                try {
//                                    for (int i = 0; i < form_id.size(); i++) {
//                                        JSONObject contact = new JSONObject();
//                                        contact.put("message", form_id.get(i));
//                                        contact.put("isRecive", false);
//                                        contact.put("isSent", true);
//                                        messageAdapter.addItem(contact);
//                                        chat_recycler.smoothScrollToPosition(chat_recycler.getAdapter().getItemCount());
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            } else if (!to_id.isEmpty()) {
//                                try {
//                                    for (int i = 0; i < to_id.size(); i++) {
//                                        JSONObject contact = new JSONObject();
//                                        contact.put("message", to_id.get(i));
//                                        contact.put("isRecive", true);
//                                        contact.put("isSent", false);
//                                        messageAdapter.addItem(contact);
//                                        chat_recycler.smoothScrollToPosition(chat_recycler.getAdapter().getItemCount());
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("LLL_list_message_error", error.toString());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("authorization", MyApplication.getAuthToken(getApplicationContext()));
                        return map;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getPersonalInfo() {
        FileUtils.DisplayLoading(ChatingActivity.this);
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(ChatingActivity.this);
                    Log.e("Chat_PersonalInfo", response.toString());
                    BusinessInfoRegisterModel businessInfoRegisterModel = new Gson().fromJson(response.toString(), BusinessInfoRegisterModel.class);
                    loginUserId = businessInfoRegisterModel.getData().getId();
                    Log.e("Ids", loginUserId);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(ChatingActivity.this);
                    Log.e("Chat_Personal_Error", error.toString());
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

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            FileUtils.DismissLoading(ChatingActivity.this);
            e.printStackTrace();

        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST) {
            if (data != null) {
                uri = data.getData();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                File file = new File(uri.getPath());

                uploadImage(file);
//                    try {
//                        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
//                        options.inSampleSize = calculateInSampleSize(options, 100, 100);
//                        options.inJustDecodeBounds = false;
//                        Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
////                        img_add_image.setImageBitmap(image);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Cancelled",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImage(File file) {
        AndroidNetworking.upload(Constans.set_chat_message)
                .addMultipartFile("file", file)
                .addHeaders("authorization", MyApplication.getAuthToken(getApplicationContext()))
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("LLL_chat_pset--->", response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error

                        Toast.makeText(ChatingActivity.this, "Not Upload Image", Toast.LENGTH_SHORT).show();
                        Log.e("LLL_chat_perr--->", error.toString());

                    }
                });
    }

//    public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//        return inSampleSize;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        getPersonalInfo();
        getChatList(toUserIds, page, limit);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChatingActivity.this, MainActivity.class));
    }
}