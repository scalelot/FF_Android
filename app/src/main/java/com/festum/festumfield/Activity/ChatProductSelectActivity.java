package com.festum.festumfield.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.festum.festumfield.Adapter.ChatProductListAdapter;
import com.festum.festumfield.Model.BusinessInfo.BusinessInfoRegisterModel;
import com.festum.festumfield.Model.Product.ProductDetailsModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatProductSelectActivity extends AppCompatActivity {

    ImageView ic_product_back;
    RecyclerView recycle_chat_product;
    NestedScrollView nestedScrollView;
    RelativeLayout emptyLay, loaderLay;
    ChatProductListAdapter chatProductListAdapter;
    ArrayList<ProductDetailsModel> productDetailsModelArrayList = new ArrayList<>();
    int page = 1, limit = 10;
    String searchData = "";
    String friendid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_product_select);

        ic_product_back = findViewById(R.id.ic_product_back);
        recycle_chat_product = findViewById(R.id.recycle_chat_product);
        emptyLay = findViewById(R.id.emptyLay);
        loaderLay = findViewById(R.id.loaderLay);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        friendid = getIntent().getStringExtra("friendid");

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    loaderLay.setVisibility(View.VISIBLE);
                    getProductItem(friendid, page, limit, searchData, "name", -1);
                }
            }
        });
    }

    public void getProductItem(String friendid, int page, int limit, String search, String short_field, int short_option) {
        if (page > limit) {
            Toast.makeText(this, "That's all the data..", Toast.LENGTH_SHORT).show();
            loaderLay.setVisibility(View.GONE);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("friendid", friendid);
            jsonObject.put("page", page);
            jsonObject.put("limit", limit);
            jsonObject.put("search", search);
            jsonObject.put("sortfield", short_field);
            jsonObject.put("sortoption", short_option);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.get_friends_product_list, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("GetProductItem=>", response.toString());
                    loaderLay.setVisibility(View.GONE);
                    try {
                        JSONObject dataJsonObject = response.getJSONObject("Data");

                        JSONArray data_array = dataJsonObject.getJSONArray("docs");

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject jsonObject = data_array.getJSONObject(i);

                            ProductDetailsModel productDetailsModel = new Gson().fromJson(jsonObject.toString(), ProductDetailsModel.class);
                            productDetailsModelArrayList.add(productDetailsModel);
                        }

                        if (!productDetailsModelArrayList.isEmpty()) {
                            emptyLay.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.VISIBLE);
                        } else {
                            emptyLay.setVisibility(View.VISIBLE);
                            nestedScrollView.setVisibility(View.GONE);
                        }

                        chatProductListAdapter = new ChatProductListAdapter(ChatProductSelectActivity.this, productDetailsModelArrayList);
                        GridLayoutManager linearLayoutManager = new GridLayoutManager(ChatProductSelectActivity.this, 2);
                        recycle_chat_product.setLayoutManager(linearLayoutManager);
                        recycle_chat_product.setAdapter(chatProductListAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("GetProductItemError=>", error.toString());
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
    protected void onResume() {
        super.onResume();
        getProductItem(friendid, page, limit, searchData, "price", -1);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChatProductSelectActivity.this, ChatingActivity.class));
    }
}