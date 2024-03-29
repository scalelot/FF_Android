package com.festum.festumfield.Activity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.festum.festumfield.Adapter.ProductDisplayAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.Model.BusinessInfo.BusinessInfoRegisterModel;
import com.festum.festumfield.Model.Product.ProductDetailsModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.festum.festumfield.verstion.firstmodule.screens.main.profile.CreateBusinessProfileActivity;
import com.festum.festumfield.verstion.firstmodule.screens.main.HomeActivity;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends BaseActivity {
    ImageView ic_back;
    ImageView img_business;
    TextView txt_business_name;
    TextView txt_business_des;
    RelativeLayout edt_business_img;
    LinearLayout lin_empty_view;
    LinearLayout lin_not_empty;
    ImageView iv_add_new_product;
    RecyclerView recycle_add_new_product;
    AppCompatButton btn_add_product;
    AppCompatButton btn_save;
    TextView txt_skip_for_now;
    LinearLayout lin_add_product;
    Context context;
    int page = 1, limit = Integer.MAX_VALUE;
    String searchData = "";
    NestedScrollView nestedScrollView;
    ProgressBar idPBLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        context = this;
        ic_back = findViewById(R.id.ic_back);
        img_business = findViewById(R.id.img_business);
        txt_business_name = findViewById(R.id.txt_business_name);
        txt_business_des = findViewById(R.id.txt_business_des);
        edt_business_img = findViewById(R.id.edt_business_img);
        lin_empty_view = findViewById(R.id.lin_empty_view);
        lin_not_empty = findViewById(R.id.lin_not_empty);
        iv_add_new_product = findViewById(R.id.iv_add_new_product);
        recycle_add_new_product = findViewById(R.id.recycle_add_new_product);
        btn_add_product = findViewById(R.id.btn_add_product);
        btn_save = findViewById(R.id.btn_save);
        txt_skip_for_now = findViewById(R.id.txt_skip_for_now);
        lin_add_product = findViewById(R.id.lin_add_product);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        idPBLoading = findViewById(R.id.idPBLoading);


        recycle_add_new_product.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddProductActivity.class));
            }
        });

        lin_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddProductActivity.class));
            }
        });

        txt_skip_for_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        edt_business_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateBusinessProfileActivity.class).putExtra("EditProfile", getResources().getString(R.string.edit_business_profile)));
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    idPBLoading.setVisibility(View.VISIBLE);
                    getProductItem(page, limit, searchData, "price", -1);
                }
            }
        });
    }

    public void getBusinessInfo() {
        FileUtils.DisplayLoading(ProductActivity.this);
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_business_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(ProductActivity.this);

                    Log.e("ProductGetBusinessInfo=>", response.toString());

                    BusinessInfoRegisterModel businessInfoRegisterModel = new Gson().fromJson(response.toString(), BusinessInfoRegisterModel.class);

                    txt_business_name.setText(businessInfoRegisterModel.getData().getName());
                    txt_business_des.setText(businessInfoRegisterModel.getData().getDescription());

                    Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + businessInfoRegisterModel.getData().getBusinessimage()).into(img_business);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(ProductActivity.this);
                    Log.e("ProductGetBusinessInfoError=>", error.toString());
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

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            FileUtils.DismissLoading(ProductActivity.this);
            e.printStackTrace();

        }
    }

    public void getProductItem(int page, int limit, String search, String short_field, int short_option) {
        if (page > limit) {
            Toast.makeText(this, "That's all the data..", Toast.LENGTH_SHORT).show();
            idPBLoading.setVisibility(View.GONE);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
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
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.list_product, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    idPBLoading.setVisibility(View.GONE);
                    Log.e("GetProductLists=>", response.toString());

                    ArrayList<ProductDetailsModel> productDetailsModelArrayList = new ArrayList<>();

                    try {
                        JSONObject dataJsonObject = response.getJSONObject("Data");

                        JSONArray data_array = dataJsonObject.getJSONArray("docs");

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject jsonObject = data_array.getJSONObject(i);

                            ProductDetailsModel productDetailsModel = new Gson().fromJson(jsonObject.toString(), ProductDetailsModel.class);
                            productDetailsModelArrayList.add(productDetailsModel);
                        }

                        if (productDetailsModelArrayList != null ) {
                            lin_not_empty.setVisibility(View.VISIBLE);
                            lin_empty_view.setVisibility(View.GONE);

                            btn_add_product.setVisibility(View.GONE);
                            txt_skip_for_now.setVisibility(View.GONE);
                            btn_save.setVisibility(View.VISIBLE);
                        } else {
                            lin_empty_view.setVisibility(View.VISIBLE);
                            lin_not_empty.setVisibility(View.GONE);

                            btn_add_product.setVisibility(View.VISIBLE);
                            txt_skip_for_now.setVisibility(View.VISIBLE);
                            btn_save.setVisibility(View.GONE);
                        }

                        ProductDisplayAdapter productDisplayAdapter = new ProductDisplayAdapter(ProductActivity.this, productDetailsModelArrayList);
                        recycle_add_new_product.setHasFixedSize(true);
                        recycle_add_new_product.setAdapter(productDisplayAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("GetProductListsError", error.toString());
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

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBusinessInfo();
        getProductItem(page, limit, searchData, "price", -1);
    }

    @Override
    public void onBackPressed() {
       /* startActivity(new Intent(ProductActivity.this, CreateBusinessProfileActivity.class));*/
        finish();
    }
}