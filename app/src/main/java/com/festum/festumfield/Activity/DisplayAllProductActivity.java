package com.festum.festumfield.Activity;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.festum.festumfield.Adapter.DisplayAllProductAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.Model.Product.ProductDetailsModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayAllProductActivity extends BaseActivity {
    ImageView ic_back;
    RecyclerView recyclerview_product;
    ArrayList<ProductDetailsModel> productDetailsModelArrayList = new ArrayList<>();
    EditText edt_search_text;
    ImageView iv_search;
    ImageView iv_clear_text;
    int page = 1, limit = 10;
    String searchData = "";
    ProgressBar idPBLoading;
    NestedScrollView nestedScrollView;
    RelativeLayout emptyLay;
    DisplayAllProductAdapter productDisplayAdapter;

    GridLayoutManager gridLayoutManager;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_product);

        ic_back = findViewById(R.id.ic_back);
        recyclerview_product = findViewById(R.id.recyclerview_product);
        edt_search_text = findViewById(R.id.edt_search_text);
        iv_search = findViewById(R.id.iv_search);
        iv_clear_text = findViewById(R.id.iv_clear_text);
        nestedScrollView = findViewById(R.id.nestedScrollView1);
        idPBLoading = findViewById(R.id.idPBLoading);
        emptyLay = findViewById(R.id.emptyLay);

        if (!productDetailsModelArrayList.isEmpty()) {
            productDetailsModelArrayList.clear();
        }

        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);

        recyclerview_product.setLayoutManager(gridLayoutManager);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edt_search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        edt_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    iv_clear_text.setVisibility(View.GONE);
                    iv_search.setVisibility(View.VISIBLE);
                } else {
                    iv_clear_text.setVisibility(View.VISIBLE);
                    iv_search.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                filter(text);
                iv_search.setVisibility(View.GONE);
                iv_clear_text.setVisibility(View.VISIBLE);
            }
        });

        iv_clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search_text.setText("");
                iv_clear_text.setVisibility(View.GONE);
                iv_search.setVisibility(View.VISIBLE);
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
                    Log.e("DisAllProduct=>", response.toString());
                    idPBLoading.setVisibility(View.GONE);
                    try {
                        JSONObject dataJsonObject = response.getJSONObject("Data");

                        JSONArray data_array = dataJsonObject.getJSONArray("docs");

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject jsonObject = data_array.getJSONObject(i);

                            ProductDetailsModel productDetailsModel = new Gson().fromJson(jsonObject.toString(), ProductDetailsModel.class);
                            productDetailsModelArrayList.add(productDetailsModel);

                            if (!productDetailsModelArrayList.isEmpty()) {
                                emptyLay.setVisibility(View.GONE);
                                nestedScrollView.setVisibility(View.VISIBLE);
                            } else {
                                emptyLay.setVisibility(View.VISIBLE);
                                nestedScrollView.setVisibility(View.GONE);
                            }

                            productDisplayAdapter = new DisplayAllProductAdapter(DisplayAllProductActivity.this, productDetailsModelArrayList);
                            recyclerview_product.setHasFixedSize(true);
                            recyclerview_product.setAdapter(productDisplayAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DisAllProduct_Error=>", error.toString());
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

    private void filter(String text) {
        ArrayList<ProductDetailsModel> filteredlist = new ArrayList<ProductDetailsModel>();

        for (ProductDetailsModel item : productDetailsModelArrayList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            productDisplayAdapter.filterList(filteredlist);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProductItem(page, limit, searchData, "price", -1);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DisplayAllProductActivity.this, MainActivity.class));
        finish();
    }
}