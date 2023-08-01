package com.festum.festumfield.Activity;

import static com.android.volley.Request.Method.GET;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import com.festum.festumfield.Adapter.MessageAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.Model.BusinessInfo.BusinessInfoRegisterModel;
import com.festum.festumfield.Model.ListChat.ListChatsModel;
import com.festum.festumfield.Model.Product.ProductModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.RealPathUtil;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatingActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private Toolbar toolbar;
    public static boolean isInActionMode = false;
    public static ArrayList<JSONObject> selectionList = new ArrayList<>();
    List<JSONObject> objectList = new ArrayList<>();
    ImageView hp_back_arrow, img_video_call, img_contact, iv_close;
    ImageView iv_pro_image, img_gallery, img_camera, img_product;
    CircleImageView img_user;
    EditText edt_chating;
    TextView u_name, txt_online, txt_pro_name, txt_pro_des, txt_pro_price;
    RelativeLayout btn_send, rel_replay, rl_user;
    RecyclerView chat_recycler;
    String toUserIds, txt_name, p_name, p_des, userName, p_ids;
    String loginUserId = "", p_price, edt_str, pro_img, p_img;
    MessageAdapter messageAdapter;
    ListChatsModel listChatsModel;
    List<ListChatsModel> listChatsModelArrayList = new ArrayList<>();
    protected static final int GALLERY_REQUEST = 1;
    int page = 1, limit = 10;
    NestedScrollView nestedScrollView;
    LinearLayoutManager linearLayoutManager;
    String proName, proDesc, proPrice, proImage;
    Socket mSocket;
    JSONObject send, recive, reciveMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        mSocket = MyApplication.mSocket;

        mSocket.on("callUser", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject js = (JSONObject) args[0];
                Log.e("CallAnswer:==", js.toString());
            }
        });

        getMessageRecive();

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

        initView();

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
                Intent intent = new Intent(ChatingActivity.this, ChatProductSelectActivity.class);
                intent.putExtra("friendid", toUserIds);
                startActivity(intent);
                finish();
            }
        });
    }

    public void getMessageRecive() {
        try {
            mSocket.on("newMessage", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject js = (JSONObject) args[0];
                            Log.e("NewMessage:==", js.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(js.toString());
                                String fromIds = jsonObject.getString("from");
                                String name = jsonObject.getString("customername");
                                JSONObject content = jsonObject.getJSONObject("content");
                                JSONObject txtJS = content.getJSONObject("text");
                                JSONObject medJS = content.getJSONObject("media");
                                JSONObject proJS = content.getJSONObject("product");

                                if (!fromIds.equals(loginUserId)) {
                                    reciveMessage = new JSONObject();

                                    //Get Message Only
                                    if (txtJS.length() != 0 && proJS.length() == 0) {
                                        String strTxt = txtJS.getString("message");
                                        reciveMessage.put("message", strTxt);
                                    } else {
                                        reciveMessage.put("message", "");
                                    }

                                    //Get Message Product
                                    if (txtJS.length() != 0 && proJS.length() != 0) {
                                        String strTxt = txtJS.getString("message");
                                        reciveMessage.put("pro_message", strTxt);
                                    } else {
                                        reciveMessage.put("pro_message", "");
                                    }

                                    //Get Image
                                    if (medJS.length() != 0) {
                                        String psthTxt = medJS.getString("path");
                                        reciveMessage.put("image", psthTxt);
                                    } else {
                                        reciveMessage.put("image", "");
                                    }

                                    //Get Product
                                    if (proJS.length() != 0) {
                                        String proIds = proJS.getString("productid");
                                        getReciveProduct(proIds);
                                        reciveMessage.put("pro_name", proName);
                                        reciveMessage.put("pro_des", proDesc);
                                        reciveMessage.put("pro_price", proPrice);
                                        reciveMessage.put("pro_img", proImage);
                                    } else {
                                        System.out.println("e");
                                        reciveMessage.put("pro_name", "");
                                        reciveMessage.put("pro_des", "");
                                        reciveMessage.put("pro_price", "");
                                        reciveMessage.put("pro_img", "");
                                        reciveMessage.put("pro_message", "");
                                    }

                                    reciveMessage.put("name", name);
                                    reciveMessage.put("userProfileImg", p_img);
                                    reciveMessage.put("isRecive", true);
                                    reciveMessage.put("isSent", false);

                                    if (fromIds.equals(toUserIds)) {
                                        objectList.add(reciveMessage);

                                        messageAdapter.notifyDataSetChanged();

                                        chat_recycler.scrollToPosition(messageAdapter.getItemCount() - 1);
                                    } else {
                                        System.out.println("UserId Wrong");
                                    }
                                } else {
                                    System.out.println("Error");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReciveProduct(String proIds) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, Constans.fetch_single_product + "?pid=" + proIds, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("GetSingleProduct=>", response.toString());

                    ProductModel productModel = new Gson().fromJson(response.toString(), ProductModel.class);

                    proName = productModel.getProductDetailsModel().getName();
                    proDesc = productModel.getProductDetailsModel().getDescription();
                    proPrice = String.valueOf(productModel.getProductDetailsModel().getPrice());
                    proImage = String.valueOf(productModel.getProductDetailsModel().getImages().get(0));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("GetSingleProductError=> " + error.toString());
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
            RequestQueue requestQueue = Volley.newRequestQueue(ChatingActivity.this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);

        messageAdapter = new MessageAdapter(ChatingActivity.this, objectList);
        chat_recycler.setAdapter(messageAdapter);
        chat_recycler.setHasFixedSize(true);
        chat_recycler.setLayoutManager(linearLayoutManager);
        chat_recycler.smoothScrollToPosition(0);

        edt_chating.addTextChangedListener(this);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
    }

    private void attemptSend() {
        String message = edt_chating.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            edt_chating.requestFocus();
            return;
        }
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

                Log.e("TAG", "attemptSend: " + jsonObject.toString());

                if (edt_chating.getText().toString().trim().equals("")) {
                    Toast.makeText(ChatingActivity.this, "Enter Text", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(toUserIds, edt_chating.getText().toString().trim());

                    objectList.add(jsonObject);
                    messageAdapter.notifyDataSetChanged();
                    chat_recycler.smoothScrollToPosition(messageAdapter.getItemCount());
                }
                resetMessageEdit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                String edt_mess = edt_chating.getText().toString().trim();
                if (p_ids != null && !TextUtils.isEmpty(edt_mess)) {
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

                    sendProduct(toUserIds, p_ids, edt_mess);

                    rel_replay.setVisibility(View.GONE);

                    objectList.add(jsonObject);

                    messageAdapter.notifyDataSetChanged();

                    linearLayoutManager.smoothScrollToPosition(chat_recycler, null, messageAdapter.getItemCount());
                } else {
                    Toast.makeText(ChatingActivity.this, "Enter Message", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                    Log.e("SendMessage=>", response.toString());
                }

                @Override
                public void onError(ANError error) {
                    Log.e("SendMessageError=>", error.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendProduct(String userIds, String toUserIds, String txt) {
        try {
            AndroidNetworking.post(Constans.set_chat_message).addBodyParameter("to", userIds).addBodyParameter("message", txt).addBodyParameter("product", toUserIds).addHeaders("authorization", MyApplication.getAuthToken(getApplicationContext())).setPriority(Priority.HIGH).setTag("UploadTest").build().setUploadProgressListener(new UploadProgressListener() {
                @Override
                public void onProgress(long bytesUploaded, long totalBytes) {
                }
            }).getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("SendProduct=>", response.toString());
                }

                @Override
                public void onError(ANError error) {
                    Log.e("SendProductError=>", error.toString());
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

                            for (int i = 0; i < listChatsModelArrayList.size(); i++) {
                                if (listChatsModelArrayList.get(i).getFrom().getId().equals(loginUserId)) {

                                    send = new JSONObject();
                                    if (listChatsModelArrayList.get(i).getContentType().equals("text")) {
                                        send.put("message", listChatsModelArrayList.get(i).getSendAllModelData().getText().getMessage());
                                    } else {
                                        send.put("message", "");
                                    }
                                    if (listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid() != null) {
                                        send.put("pro_name", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getName());
                                        send.put("pro_des", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getDescription());
                                        send.put("pro_price", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getPrice());
                                        send.put("pro_img", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getImages().get(0));
                                        send.put("pro_ids", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid());
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
                                    send.put("pro_message", listChatsModelArrayList.get(i).getSendAllModelData().getText().getMessage());
                                    send.put("Sendtime", listChatsModelArrayList.get(i).getCreatedAt());
                                    send.put("isSent", true);
                                    send.put("isRecive", false);
                                    objectList.add(send);
                                    messageAdapter.notifyDataSetChanged();
                                    linearLayoutManager.smoothScrollToPosition(chat_recycler, null, messageAdapter.getItemCount());
                                } else {

                                    recive = new JSONObject();
                                    if (listChatsModelArrayList.get(i).getContentType().equals("text")) {
                                        recive.put("message", listChatsModelArrayList.get(i).getSendAllModelData().getText().getMessage());
                                    } else {
                                        recive.put("message", "");
                                    }
                                    recive.put("name", userName);
                                    recive.put("pro_message", listChatsModelArrayList.get(i).getSendAllModelData().getText().getMessage());
                                    if (listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid() != null) {
                                        recive.put("pro_name", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getName());
                                        recive.put("pro_des", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getDescription());
                                        recive.put("pro_price", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getPrice());
                                        recive.put("pro_img", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid().getImages().get(0));
                                        recive.put("pro_ids", listChatsModelArrayList.get(i).getSendAllModelData().getProduct().getProductid());
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
                                    recive.put("recivetime", listChatsModelArrayList.get(i).getCreatedAt());
                                    recive.put("userProfileImg", p_img);
                                    recive.put("isRecive", true);
                                    recive.put("isSent", false);

                                    objectList.add(recive);
                                    messageAdapter.notifyDataSetChanged();
                                    linearLayoutManager.smoothScrollToPosition(chat_recycler, null, messageAdapter.getItemCount());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ListMessageError=>", error.toString());
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

    @SuppressLint("NotifyDataSetChanged")
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
        if (!selectionList.contains(objectList.get(position))) {
            selectionList.add(objectList.get(position));
        } else {
            selectionList.remove(objectList.get(position));
        }
        updateViewCounter();
    }

    private void updateViewCounter() {
        if (!selectionList.isEmpty()) {
            toolbar.getMenu().getItem(0).setVisible(true);
        } else {
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
        } else if (item.getItemId() == R.id.item_edit) {
        } else if (item.getItemId() == R.id.item_copy) {
            Log.d("====", "Copy");
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = null;
            try {
                clip = ClipData.newPlainText("label", selectionList.get(0).getString("message"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (clipboard == null || clip == null) ;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ChatingActivity.this, "Copied!!", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.item_right_for) {
            Log.d("====", "Right For");
        } else if (item.getItemId() == R.id.item_info) {
            try {
                System.out.println("All:=" + selectionList.get(0));
                if (!selectionList.get(0).getString("pro_name").isEmpty()) {
                    Intent intent = new Intent(ChatingActivity.this, MessageInfoActivity.class);
                    intent.putExtra("Name", selectionList.get(0).getString("pro_name"));
                    intent.putExtra("Des", selectionList.get(0).getString("pro_des"));
                    intent.putExtra("Price", selectionList.get(0).getString("pro_price"));
                    intent.putExtra("Image", selectionList.get(0).getString("pro_img"));
                    intent.putExtra("Delivered", selectionList.get(0).getString("Sendtime"));
                    intent.putExtra("Message", selectionList.get(0).getString("pro_message"));
                    startActivity(intent);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.hp_back_arrow) {
            startActivity(new Intent(ChatingActivity.this, MainActivity.class));
            finish();
        } else if (id == R.id.img_video_call) {
            Intent intent = new Intent(ChatingActivity.this,VideoCallReciveActivity.class);
            intent.putExtra("toUserId", toUserIds);
            intent.putExtra("userName", userName);
            intent.putExtra("loginUser", loginUserId);
            startActivity(intent);
            finish();
        } else if (id == R.id.img_contact) {
            startActivity(new Intent(ChatingActivity.this, ChattingAudioCallActivity.class));
            finish();
        } else if (id == R.id.rl_user) {
            startActivity(new Intent(ChatingActivity.this, ChatUserProfileActivity.class));
            finish();
        } else if (id == R.id.img_camera) {
            ImagePicker.with(ChatingActivity.this).crop().cameraOnly().compress(1024).maxResultSize(1080, 1080).start();
        } else if (id == R.id.img_gallery) {
            openCamera();
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

    private void openCamera() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == GALLERY_REQUEST) {
                Uri selectImage = data.getData();

                String path = RealPathUtil.getRealPath(ChatingActivity.this, selectImage);
                File file = new File(path);
                JSONObject jsonObject = new JSONObject();
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

                    objectList.add(jsonObject);

                    messageAdapter.notifyDataSetChanged();

                    chat_recycler.smoothScrollToPosition(objectList.size() - 1);
                }
            } else {
                Uri cameraUri = data.getData();
                String path = RealPathUtil.getRealPath(ChatingActivity.this, cameraUri);
                File file = new File(path);

                JSONObject jsonObject = new JSONObject();
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

                    objectList.add(jsonObject);

                    messageAdapter.notifyDataSetChanged();

                    chat_recycler.smoothScrollToPosition(objectList.size() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    Log.e("SendChatImage=>", response.toString());
                }

                @Override
                public void onError(ANError error) {
                    Toast.makeText(ChatingActivity.this, "Not Upload Image", Toast.LENGTH_SHORT).show();
                    Log.e("SendChatImageError=>", error.toString());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPersonalInfo() {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_personal_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("ChatPersonalInfo=>", response.toString());
                    BusinessInfoRegisterModel businessInfoRegisterModel = new Gson().fromJson(response.toString(), BusinessInfoRegisterModel.class);
                    loginUserId = businessInfoRegisterModel.getData().getId();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ChatPersonalInfoError=>", error.toString());
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

    @Override
    protected void onResume() {
        super.onResume();
        getPersonalInfo();
        getChatList(toUserIds, page, limit);
    }

    @Override
    public void onBackPressed() {
        if (isInActionMode) {
            clearActionMode();
            messageAdapter.notifyDataSetChanged();
        } else {
            startActivity(new Intent(ChatingActivity.this, MainActivity.class));
            finish();
        }
    }
}