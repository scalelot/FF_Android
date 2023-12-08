package com.festum.festumfield.Activity;

import static com.android.volley.Request.Method.GET;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.festum.festumfield.Adapter.ImageSliderPagerAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.Model.ImageSliderModel;
import com.festum.festumfield.Model.Product.ProductDetailsModel;
import com.festum.festumfield.Model.Product.ProductModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends BaseActivity {
    ImageView ic_back, ic_delete, ic_p_edit;
    TextView txt_p_name, txt_p_price, txt_p_offer;
    TextView txt_p_des, txt_p_code;
    Context context;
    AppCompatButton btn_inquiry_message;
    ViewPager viewPager;
    ProductDetailsModel productDetailsModel;
    LinearLayout sliderDotspanel;
    String ProductId;
    String isFromUser;
    private int dotscount;
    private ImageView[] dots;
    ImageSliderPagerAdapter viewPagerAdapter;
    List<ImageSliderModel> imageSliderModels;
    String idsProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        context = this;
        ic_back = findViewById(R.id.ic_back);
        ic_delete = findViewById(R.id.ic_delete);
        txt_p_name = findViewById(R.id.txt_p_name);
        txt_p_price = findViewById(R.id.txt_p_price);
        txt_p_offer = findViewById(R.id.txt_p_offer);
        txt_p_des = findViewById(R.id.txt_p_des);
        txt_p_code = findViewById(R.id.txt_p_code);
        ic_p_edit = findViewById(R.id.ic_p_edit);
        btn_inquiry_message = findViewById(R.id.btn_inquiry_message);
        viewPager = findViewById(R.id.viewPager);
        sliderDotspanel = findViewById(R.id.SliderDots);

        isFromUser = getIntent().getStringExtra("product");
        productDetailsModel = (ProductDetailsModel) getIntent().getSerializableExtra("ProductDetails");
        ProductId = getIntent().getStringExtra("ProductDetailsId");

        getProductDetails(ProductId);

        if (isFromUser.equals("Product")) {
            btn_inquiry_message.setVisibility(View.GONE);
        } else {
            btn_inquiry_message.setVisibility(View.VISIBLE);
        }

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog();
            }
        });

        ic_p_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddProductActivity.class).putExtra("Edit_Pro", getResources().getString(R.string.edit_product)).putExtra("pro_id", ProductId));
                finish();
            }
        });

    }

    public void getProductDetails(String id) {

        try {
            FileUtils.DisplayLoading(ProductDetailsActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, Constans.fetch_single_product + "?pid=" + id, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(ProductDetailsActivity.this);
                    Log.e("GetProductDetail", response.toString());

                    ProductModel productModel = new Gson().fromJson(response.toString(), ProductModel.class);

                    txt_p_name.setText(productModel.getProductDetailsModel().getName());
                    txt_p_price.setText("$" + String.format("%,.0f", Float.valueOf(productModel.getProductDetailsModel().getPrice())) + ".00");

                    if (productModel.getProductDetailsModel().getOffer().equals("")) {
                        txt_p_offer.setVisibility(View.GONE);
                    } else {
                        txt_p_offer.setVisibility(View.VISIBLE);
                        txt_p_offer.setText(productModel.getProductDetailsModel().getOffer() + "% Off");
                    }

                    idsProduct = productModel.getProductDetailsModel().getId();
                    txt_p_des.setText(productModel.getProductDetailsModel().getDescription());
                    txt_p_code.setText(productModel.getProductDetailsModel().getItemCode());

                    imageSliderModels = new ArrayList<>();

                    for (int i = 0; i < productModel.getProductDetailsModel().getImages().size(); i++) {
                        imageSliderModels.add(new ImageSliderModel(productModel.getProductDetailsModel().getImages().get(i).toString()));
                    }

                    viewPagerAdapter = new ImageSliderPagerAdapter(ProductDetailsActivity.this, imageSliderModels);
                    viewPager.setAdapter(viewPagerAdapter);
                    dotscount = viewPagerAdapter.getCount();
                    dots = new ImageView[dotscount];

                    for (int i = 0; i < dotscount; i++) {
                        dots[i] = new ImageView(getApplicationContext());
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(8, 0, 8, 0);
                        sliderDotspanel.addView(dots[i], params);
                    }

                    dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            for (int i = 0; i < dotscount; i++) {
                                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                            }
                            dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                    String price = String.format("%,.0f", Float.valueOf(productModel.getProductDetailsModel().getPrice()));

                    /*btn_inquiry_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ChatingActivity.class);
                            intent.putExtra("product_img", String.valueOf(productModel.getProductDetailsModel().getImages().get(0)));
                            intent.putExtra("product_name", productModel.getProductDetailsModel().getName());
                            intent.putExtra("product_des", productModel.getProductDetailsModel().getDescription());
                            intent.putExtra("product_ids", productModel.getProductDetailsModel().getId());
                            intent.putExtra("product_price", price);
                            startActivity(intent);
                        }
                    });*/

                    btn_inquiry_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ChatingActivity.class);
                            intent.putExtra("productId", productModel.getProductDetailsModel().getId());
                            startActivityForResult(intent, IntentUtil.Companion.getPRODUCT_REQUEST_CODE());
                        }
                    });


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("GetProductDetailError", error.toString());
                    FileUtils.DismissLoading(ProductDetailsActivity.this);
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
            FileUtils.DismissLoading(ProductDetailsActivity.this);
            e.printStackTrace();
        }
    }
    public void deleteDialog() {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(ProductDetailsActivity.this).inflate(R.layout.delete_dialog_product, null);
        dialog.setContentView(view);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(back, 50);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);

        ImageView dialog_close = dialog.findViewById(R.id.dialog_close);
        AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        AppCompatButton btn_delete = dialog.findViewById(R.id.btn_delete);

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.DeleteProduct(context, Constans.remove_product, ProductId);
                dialog.dismiss();
                onBackPressed();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (isFromUser.equals("Product")) {
            /*startActivity(new Intent(getApplicationContext(), ProductActivity.class));*/
            finish();
        } else {
            /*startActivity(new Intent(getApplicationContext(), ChatingActivity.class));*/
            finish();
        }
    }
}