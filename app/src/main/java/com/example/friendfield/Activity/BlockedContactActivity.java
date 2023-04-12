package com.example.friendfield.Activity;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.friendfield.Adapter.BlockedContactAdapter;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.Model.BlockedFriends.BlockedFriendRegisterModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlockedContactActivity extends BaseActivity {

    RecyclerView recyclerView;
    BlockedContactAdapter blockeContactAdapter;
    ImageView ic_back;
    NestedScrollView nestedScroll;
    RelativeLayout emptyLay;
    ProgressBar idPBLoading;
    ArrayList<BlockedFriendRegisterModel> blockedFriendRegisterModels = new ArrayList<>();
    int page = 1, limit = 10;
    String searchData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_contact);

        recyclerView = findViewById(R.id.recyclerView);
        ic_back = findViewById(R.id.ic_back_arrow);
        nestedScroll = findViewById(R.id.nestedScroll);
        emptyLay = findViewById(R.id.emptyLay);
        idPBLoading = findViewById(R.id.idPBLoading);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getBlockedFriends(page, limit, searchData);

        nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    idPBLoading.setVisibility(View.VISIBLE);
                    getBlockedFriends(page, limit, searchData);
                }
            }
        });
    }

    private void getBlockedFriends(int page, int limit, String search) {
        if (page > limit) {
            Toast.makeText(getApplicationContext(), "That's all the data..", Toast.LENGTH_SHORT).show();
            idPBLoading.setVisibility(View.GONE);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("page", page);
            jsonObject.put("limit", limit);
            jsonObject.put("search", search);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.all_block_friends, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    idPBLoading.setVisibility(View.GONE);
                    Log.e("AllBlockFriends=>", response.toString());
                    try {
                        JSONObject dataJsonObject = response.getJSONObject("Data");

                        JSONArray data_array = dataJsonObject.getJSONArray("docs");

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject jsonObject = data_array.getJSONObject(i);

                            BlockedFriendRegisterModel blockedFriendRegisterModel = new Gson().fromJson(jsonObject.toString(), BlockedFriendRegisterModel.class);
                            blockedFriendRegisterModels.add(blockedFriendRegisterModel);
                        }

                        if (!blockedFriendRegisterModels.isEmpty()) {
                            emptyLay.setVisibility(View.GONE);
                            nestedScroll.setVisibility(View.VISIBLE);
                        } else {
                            emptyLay.setVisibility(View.VISIBLE);
                            nestedScroll.setVisibility(View.GONE);
                        }

                        blockeContactAdapter = new BlockedContactAdapter(BlockedContactActivity.this, blockedFriendRegisterModels);
                        LinearLayoutManager manager = new LinearLayoutManager(BlockedContactActivity.this);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(blockeContactAdapter);
                        blockeContactAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("AllBlockFriends_error=>" + error.toString());
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
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BlockedContactActivity.this, SettingActivity.class));
        finish();
    }
}