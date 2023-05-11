package com.example.friendfield.Activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
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
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.RealPathUtil;
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

    private Toolbar toolbar;
    public static boolean isInActionMode = false;
    public static ArrayList<ListChatsModel> selectionList = new ArrayList<>();
    ImageView hp_back_arrow, img_video_call, img_contact, iv_close;
    ImageView iv_pro_image, img_gallery, img_camera, img_product;
    CircleImageView img_user;
    EditText edt_chating;
    TextView u_name, txt_online, txt_pro_name, txt_pro_des, txt_pro_price;
    RelativeLayout btn_send, rel_replay, rl_user;
    RecyclerView chat_recycler;
    String toUserIds, txt_name, p_name, p_des, userName, p_ids;
    String loginUserId = "", p_price, edt_str, pro_img, p_img;
    Uri imageUri, uri;
    MessageAdapter messageAdapter;
    ListChatsModel listChatsModel;
    ArrayList<ListChatsModel> listChatsModelArrayList = new ArrayList<>();
    protected static final int GALLERY_REQUEST = 1;
    private static final int PERMISSION_CODE = 1000;
    private int IMAGE_CAPTURE_CODE = 1001;
    int page = 1, limit = 10;
    NestedScrollView nestedScrollView;
    List<String> send_message, send_time, send_pro_img;
    List<String> form_id, to_id;
    List<String> userIdList, recivetime;
    JSONObject send, recive, proJson;

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
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toUserIds = getSharedPreferences("ToUserIds", MODE_PRIVATE).getString("ids", null);
        userName = getSharedPreferences("ToUserIds", MODE_PRIVATE).getString("userName", null);
        p_img = getSharedPreferences("ToUserIds", MODE_PRIVATE).getString("images", null);

        Glide.with(this).load(Constans.Display_Image_URL + p_img).placeholder(R.drawable.ic_user_img).into(img_user);

        if (!MyApplication.isBusinessProfileRegistered(ChatingActivity.this)) {
            img_product.setVisibility(View.VISIBLE);
        } else {
            img_product.setVisibility(View.GONE);
        }

        txt_name = getIntent().getStringExtra("UserName");
        pro_img = getIntent().getStringExtra("product_img");
        p_name = getIntent().getStringExtra("product_name");
        p_des = getIntent().getStringExtra("product_des");
        p_ids = getIntent().getStringExtra("product_ids");
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getChatList(toUserIds, page, limit);
            }
        }, 200);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
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

    public void prepareToolbar(int position) {
        hp_back_arrow.setVisibility(View.GONE);
        rl_user.setVisibility(View.GONE);
        img_product.setVisibility(View.GONE);
        img_video_call.setVisibility(View.GONE);
        img_contact.setVisibility(View.GONE);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_action_mode);
        isInActionMode = true;
        messageAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        prepareSelection(position);
    }

    public void prepareSelection(int position) {

        if (!selectionList.contains(listChatsModelArrayList.get(position))) {
            selectionList.add(listChatsModelArrayList.get(position));
        } else {
            selectionList.remove(listChatsModelArrayList.get(position));
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            clearActionMode();
            messageAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.item_left_for) {
            Log.d("====", "Left For");
        } else if (item.getItemId() == R.id.item_delete) {
            Log.d("====", "Delete");
            isInActionMode = false;
            ((MessageAdapter) messageAdapter).removeData(selectionList);
            clearActionMode();
        } else if (item.getItemId() == R.id.item_edit) {
            if (selectionList.size() == 1) {
                final EditText editText = new EditText(this);
                new AlertDialog.Builder(this).setTitle("Edit").setView(editText).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ListChatsModel model = selectionList.get(0);
                        model.getSendAllModelData().getText().setMessage(editText.getText().toString());
                        isInActionMode = false;
                    }
                }).create().show();
            }
        } else if (item.getItemId() == R.id.item_copy) {
            Log.d("====", "Copy");
            ListChatsModel model = selectionList.get(0);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", model.getSendAllModelData().getText().getMessage());
            if (clipboard == null || clip == null) ;
            clipboard.setPrimaryClip(clip);

        } else if (item.getItemId() == R.id.item_right_for) {
            Log.d("====", "Right For");
        } else if (item.getItemId() == R.id.item_info) {
            Log.d("====", "Info");
        }
        return true;
    }

    public void clearActionMode() {
        isInActionMode = false;
        toolbar.getMenu().clear();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setTitle("");
        hp_back_arrow.setVisibility(View.VISIBLE);
        rl_user.setVisibility(View.VISIBLE);
        img_product.setVisibility(View.VISIBLE);
        img_video_call.setVisibility(View.VISIBLE);
        img_contact.setVisibility(View.VISIBLE);
        selectionList.clear();
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
                openCamera();
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this/*, RecyclerView.VERTICAL, true*/);
//        linearLayoutManager.setStackFromEnd(true);
        chat_recycler.setLayoutManager(linearLayoutManager);

        messageAdapter = new MessageAdapter(ChatingActivity.this, getLayoutInflater());
        chat_recycler.setAdapter(messageAdapter);

        edt_chating.addTextChangedListener(this);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rel_replay.getVisibility() != View.VISIBLE) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("to", toUserIds);
                        jsonObject.put("message", edt_chating.getText().toString().trim());
                        jsonObject.put("image", "");
                        jsonObject.put("pro_name", "");
                        jsonObject.put("pro_des", "");
                        jsonObject.put("pro_price", "");
                        jsonObject.put("pro_img", "");
                        jsonObject.put("pro_message", "");
                        jsonObject.put("isSent", true);
                        jsonObject.put("isRecive", false);

                        if (edt_chating.getText().toString().equals("")) {
                            Toast.makeText(ChatingActivity.this, "Enter Text", Toast.LENGTH_SHORT);
                        } else {
                            sendMessage(toUserIds, edt_chating.getText().toString().trim());
                            messageAdapter.addItem(jsonObject);

                            chat_recycler.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                        }
                        resetMessageEdit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("to", toUserIds);
                        jsonObject.put("productIds", p_ids);
                        jsonObject.put("pro_message", edt_chating.getText().toString().trim());
                        jsonObject.put("pro_name", p_name);
                        jsonObject.put("pro_img", pro_img);
                        jsonObject.put("pro_price", p_price);
                        jsonObject.put("pro_des", p_des);
                        jsonObject.put("isSent", true);
                        jsonObject.put("isRecive", false);
                        jsonObject.put("message", "");
                        jsonObject.put("image", "");
                        if (p_ids == null && edt_chating.getText().toString().equals("")) {
                            Toast.makeText(ChatingActivity.this, "Enter Text", Toast.LENGTH_SHORT);
                        } else {
                            sendProduct(toUserIds, p_ids, edt_chating.getText().toString().trim());
                            rel_replay.setVisibility(View.GONE);
                            messageAdapter.addItem(jsonObject);

                            chat_recycler.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendMessage(String toUserIds, String toString) {
        try {
            AndroidNetworking.post(Constans.set_chat_message).addBodyParameter("to", toUserIds).addBodyParameter("message", toString).addHeaders("authorization", MyApplication.getAuthToken(getApplicationContext())).setPriority(Priority.HIGH).setTag("UploadTest").build().setUploadProgressListener(new UploadProgressListener() {
                @Override
                public void onProgress(long bytesUploaded, long totalBytes) {
                }
            }).getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("ChatSendMessage=>", response.toString());
                }

                @Override
                public void onError(ANError error) {
                    Log.e("SendMessage_Error=>", error.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendProduct(String userIds, String toUserIds, String txt) {
        try {
            AndroidNetworking.post(Constans.set_chat_message).addBodyParameter("to", userIds).addBodyParameter("product", toUserIds).addHeaders("authorization", MyApplication.getAuthToken(getApplicationContext())).setPriority(Priority.HIGH).setTag("UploadTest").build().setUploadProgressListener(new UploadProgressListener() {
                @Override
                public void onProgress(long bytesUploaded, long totalBytes) {
                }
            }).getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("ProductSendMessage=>", response.toString());

                }

                @Override
                public void onError(ANError error) {
                    Log.e("ProductMessage_Error=>", error.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        form_id = new ArrayList<>();
        to_id = new ArrayList<>();
        send_message = new ArrayList<>();
        userIdList = new ArrayList<>();
        send_time = new ArrayList<>();
        send_pro_img = new ArrayList<>();
        recivetime = new ArrayList<>();
        send_message.clear();
        userIdList.clear();
        JsonObjectRequest jsonObjectRequest = null;
        {
            try {
                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.list_chat_message, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listChatsModelArrayList.clear();
                        Log.e("ListChat=>", response.toString());
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
                                userIdList.add(String.valueOf(listChatsModelArrayList.get(index).getFrom().getId()));
                            }

                            try {
                                for (int i = 0; i < userIdList.size(); i++) {
                                    if (userIdList.get(i).equals(loginUserId)) {
                                        form_id.add(listChatsModelArrayList.get(i).getSendAllModelData().getText().getMessage());
                                        send_time.add(String.valueOf(listChatsModelArrayList.get(i).getTimestamp()));
                                    } else {
                                        to_id.add(listChatsModelArrayList.get(i).getSendAllModelData().getText().getMessage());
                                        recivetime.add(String.valueOf(listChatsModelArrayList.get(i).getTimestamp()));
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println(e);
                            }

                            if (!form_id.isEmpty()) {
                                try {
                                    for (int i = 0; i < form_id.size(); i++) {
                                        send = new JSONObject();
                                        send.put("message", form_id.get(i));
                                        send.put("Sendtime", send_time.get(i));
                                        send.put("isRecive", false);
                                        send.put("isSent", true);
                                        if (listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid() != null) {
                                            send.put("pro_name", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getName());
                                            send.put("pro_des", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getDescription());
                                            send.put("pro_price", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getPrice());
                                            send.put("pro_img", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getImages().get(0));
                                            send.put("pro_message", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getDescription());
                                        } else {
                                            System.out.println("e");
                                            send.put("pro_name", "");
                                            send.put("pro_des", "");
                                            send.put("pro_price", "");
                                            send.put("pro_img", "");
                                            send.put("pro_message", "");
                                        }
                                        if (!listChatsModelArrayList.get(i).getSendAllModelData().getMedia().getPath().isEmpty()) {
                                            send.put("image", listChatsModelArrayList.get(i).getSendAllModelData().getMedia().getPath());
                                        } else {
                                            send.put("image", "");
                                        }
                                        messageAdapter.addItem(send);
                                        chat_recycler.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (!to_id.isEmpty()) {
                                try {
                                    for (int i = 0; i < to_id.size(); i++) {
                                        recive = new JSONObject();
                                        recive.put("message", to_id.get(i));
                                        recive.put("recivetime", recivetime.get(i));
                                        recive.put("name", userName);
                                        if (listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid() != null) {
                                            recive.put("pro_name", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getName());
                                            recive.put("pro_des", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getDescription());
                                            recive.put("pro_price", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getPrice());
                                            recive.put("pro_img", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getImages().get(0));
                                            recive.put("pro_message", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getDescription());
                                        } else {
                                            System.out.println("e");
                                            recive.put("pro_name", "");
                                            recive.put("pro_des", "");
                                            recive.put("pro_price", "");
                                            recive.put("pro_img", "");
                                            recive.put("pro_message", "");
                                        }
                                        if (!listChatsModelArrayList.get(i).getSendAllModelData().getMedia().getPath().isEmpty()) {
                                            recive.put("image", listChatsModelArrayList.get(i).getSendAllModelData().getMedia().getPath());
                                        } else {
                                            recive.put("image", "");
                                        }
                                        recive.put("isRecive", true);
                                        recive.put("isSent", false);
//                                        recive.put("message", to_id.get(i));
//                                        recive.put("image", listChatsModelArrayList.get(i).getSendAllModelData().getMedia().getPath());
//                                        recive.put("recivetime", recivetime.get(i));
//                                        recive.put("name", userName);
//                                        recive.put("isRecive", true);
//                                        recive.put("isSent", false);
//                                        if (listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid() != null) {
//                                            send.put("product", proJson);
//                                        }
                                        messageAdapter.addItem(recive);
                                        chat_recycler.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ListMessage_error=>", error.toString());
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
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Chat_PersonalInfo=>", response.toString());
                    BusinessInfoRegisterModel businessInfoRegisterModel = new Gson().fromJson(response.toString(), BusinessInfoRegisterModel.class);
                    loginUserId = businessInfoRegisterModel.getData().getId();
                    Log.e("Ids", loginUserId);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Chat_Personal_Error=>", error.toString());
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
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, "New Picture");
//        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
//        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
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
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri selectImage = data.getData();

            String path = RealPathUtil.getRealPath(ChatingActivity.this, selectImage);
            File file = new File(path);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("to", toUserIds);
                jsonObject.put("image", path);
                jsonObject.put("message", "");
                jsonObject.put("pro_name", "");
                jsonObject.put("pro_des", "");
                jsonObject.put("pro_price", "");
                jsonObject.put("pro_img", "");
                jsonObject.put("pro_message", "");
                jsonObject.put("isSent", true);
                jsonObject.put("isRecive", false);

                if (path == null) {
                    Toast.makeText(ChatingActivity.this, "Enter Images", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage(file);

                    messageAdapter.addItem(jsonObject);

                    chat_recycler.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(File file) {
        try {
            AndroidNetworking.upload(Constans.set_chat_message).addMultipartFile("file", file).addMultipartParameter("to", toUserIds).addHeaders("authorization", MyApplication.getAuthToken(getApplicationContext())).setTag("uploadTest").setPriority(Priority.HIGH).build().setUploadProgressListener(new UploadProgressListener() {
                @Override
                public void onProgress(long bytesUploaded, long totalBytes) {
                }
            }).getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    // do anything with response
                    Log.e("SendImage=>", response.toString());
                }

                @Override
                public void onError(ANError error) {
                    // handle error
                    Toast.makeText(ChatingActivity.this, "Not Upload Image", Toast.LENGTH_SHORT).show();
                    Log.e("SendImage_Error=>", error.toString());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPersonalInfo();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChatingActivity.this, MainActivity.class));
    }
}