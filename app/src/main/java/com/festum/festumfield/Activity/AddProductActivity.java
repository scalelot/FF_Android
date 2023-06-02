package com.festum.festumfield.Activity;

import static com.android.volley.Request.Method.GET;

import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.Model.Product.CreateProductModel;
import com.festum.festumfield.Model.Product.ProductImagesModel;
import com.festum.festumfield.Model.Product.ProductModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.RealPathUtil;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends BaseActivity {
    ImageView ic_back;
    LinearLayout lin_add_images;
    ImageView img_add_image, img_product1, img_product2, img_product3;
    EditText edt_pro_name, edt_pro_price, edt_pro_des, edt_offer;
    EditText edt_pro_code, edt_category, edt_subcategory;
    AppCompatButton btn_save_product;
    RequestQueue queue;
    Context context;
    String edit_pro;
    TextView txt_title;
    String proId;
    RelativeLayout rl_ads;
    public static final int PICK_IMAGE = 1;
    public static final int PICK_IMAGE1 = 2;
    public static final int PICK_IMAGE2 = 3;
    public static final int PICK_IMAGE3 = 4;
    ArrayList<String> imagesArrayList = new ArrayList<>();
    JSONArray imageJsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        context = this;
        queue = Volley.newRequestQueue(AddProductActivity.this);

        ic_back = findViewById(R.id.ic_back);
        lin_add_images = findViewById(R.id.lin_add_images);
        img_add_image = findViewById(R.id.img_add_image);
        edt_pro_name = findViewById(R.id.edt_pro_name);
        edt_pro_price = findViewById(R.id.edt_pro_price);
        edt_pro_des = findViewById(R.id.edt_pro_des);
        edt_offer = findViewById(R.id.edt_offer);
        edt_pro_code = findViewById(R.id.edt_pro_code);
        img_product1 = findViewById(R.id.img_product1);
        img_product2 = findViewById(R.id.img_product2);
        img_product3 = findViewById(R.id.img_product3);

        edt_category = findViewById(R.id.edt_category);
        edt_subcategory = findViewById(R.id.edt_subcategory);
        btn_save_product = findViewById(R.id.btn_save_product);

        txt_title = findViewById(R.id.txt_title);
        rl_ads = findViewById(R.id.rl_ads);

        edit_pro = getIntent().getStringExtra("Edit_Pro");
        proId = getIntent().getStringExtra("pro_id");

        if (edit_pro != null) {
            txt_title.setText(edit_pro);
            getProductDetails(proId);
        } else {
            txt_title.setText(getResources().getString(R.string.add_product));
        }

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        img_product1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE1);
            }
        });
        img_product2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE2);
            }
        });
        img_product3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE3);
            }
        });

        btn_save_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_pro_name.getText().toString().equals("")) {
                    edt_pro_name.setError(getResources().getString(R.string.please_enter_pro_name));

                } else if (edt_pro_price.getText().toString().equals("")) {
                    edt_pro_price.setError(getResources().getString(R.string.please_enter_pro_price));

                } else if (edt_pro_des.getText().toString().equals("")) {
                    edt_pro_des.setError(getResources().getString(R.string.please_enter_pro_des));

                } else if (edt_category.getText().toString().equals("")) {
                    edt_category.setError(getResources().getString(R.string.please_enter_b_cat));

                } else if (edt_subcategory.getText().toString().equals("")) {
                    edt_subcategory.setError(getResources().getString(R.string.please_enter_b_subcat));

                } else if (edt_offer.getText().toString().equals("")) {
                    edt_offer.setError(getResources().getString(R.string.please_enter_pro_offer));

                } else if (edt_pro_code.getText().toString().equals("")) {
                    edt_pro_code.setError(getResources().getString(R.string.please_enter_pro_pcode));

                } else {
                    FileUtils.DisplayLoading(context);
                    if (edit_pro != null) {
                        updateProductInfo(proId, edt_pro_name.getText().toString().trim(), edt_pro_price.getText().toString().trim(), edt_pro_des.getText().toString().trim(), edt_category.getText().toString().trim(), edt_subcategory.getText().toString().trim(), edt_offer.getText().toString().trim(), edt_pro_code.getText().toString().trim(), imagesArrayList);
                    } else {
                        addProductInfo(edt_pro_name.getText().toString().trim(), edt_pro_price.getText().toString().trim(), edt_pro_des.getText().toString().trim(), edt_category.getText().toString().trim(), edt_subcategory.getText().toString().trim(), edt_offer.getText().toString().trim(), edt_pro_code.getText().toString().trim(), imagesArrayList);
                    }
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {

            try {
                Uri selectImage = data.getData();
                Glide.with(AddProductActivity.this).load(selectImage).placeholder(R.drawable.ic_user).into(img_add_image);

                String path = RealPathUtil.getRealPath(AddProductActivity.this, selectImage);
                File file = new File(path);
                uploadImage(0, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE1) {
            try {
                Uri selectImage = data.getData();
                Glide.with(AddProductActivity.this).load(selectImage).placeholder(R.drawable.ic_user).into(img_product1);

                String path = RealPathUtil.getRealPath(AddProductActivity.this, selectImage);
                File file = new File(path);
                uploadImage(1, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE2) {
            try {
                Uri selectImage = data.getData();
                Glide.with(AddProductActivity.this).load(selectImage).placeholder(R.drawable.ic_user).into(img_product2);

                String path = RealPathUtil.getRealPath(AddProductActivity.this, selectImage);
                File file = new File(path);
                uploadImage(2, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE3) {
            try {
                Uri selectImage = data.getData();
                Glide.with(AddProductActivity.this).load(selectImage).placeholder(R.drawable.ic_user).into(img_product3);

                String path = RealPathUtil.getRealPath(AddProductActivity.this, selectImage);
                File file = new File(path);
                uploadImage(3, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImage(int pos, File file) {
        try {
            FileUtils.DisplayLoading(AddProductActivity.this);
            AndroidNetworking.upload(Constans.set_product_image).addMultipartFile("file", file).addHeaders("authorization", MyApplication.getAuthToken(getApplicationContext())).setTag("uploadTest").setPriority(Priority.HIGH).build().setUploadProgressListener(new UploadProgressListener() {
                @Override
                public void onProgress(long bytesUploaded, long totalBytes) {
                }
            }).getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        FileUtils.DismissLoading(AddProductActivity.this);
                        Log.e("ProUploadImages=>", response.toString());
                        ProductImagesModel productImagesModel = new Gson().fromJson(response.toString(), ProductImagesModel.class);
                        if (edit_pro != null) {
                            if (pos >= imagesArrayList.size() || pos < 0) {
                                imagesArrayList.add(productImagesModel.getData().getKey());
                            } else {
                                imagesArrayList.set(pos, productImagesModel.getData().getKey());
                            }
                        } else {
                            imagesArrayList.add(productImagesModel.getData().getKey());
                        }
                        getProductDetails(proId);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError error) {
                    FileUtils.DismissLoading(  AddProductActivity.this);
                    Log.e("ProUploadImagesError=>", error.toString());

                }
            });
        } catch (Exception e) {
            FileUtils.DismissLoading(  AddProductActivity.this);
            Toast.makeText(context, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    public void addProductInfo(String pro_name, String pro_price, String pro_des, String category, String subCat, String pro_offer, String pro_itemcode, ArrayList<String> imagesArrayList) {
        JSONObject params = new JSONObject();
        try {
            params.put("name", pro_name);
            params.put("price", pro_price);
            params.put("description", pro_des);
            params.put("category", category);
            params.put("subCategory", subCat);
            params.put("offer", pro_offer);
            params.put("itemCode", pro_itemcode);
            if (!imagesArrayList.isEmpty()) {
                for (int i = 0; i < imagesArrayList.size(); i++) {
                    imageJsonArray.put(imagesArrayList.get(i));
                }
                try {
                    params.put("images", imageJsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            FileUtils.DismissLoading(  AddProductActivity.this);
            e.printStackTrace();
        }

        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.add_product, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(context);
                    Log.e("AddProduct=>", response.toString());

                    CreateProductModel createProductModel = new Gson().fromJson(response.toString(), CreateProductModel.class);

                    finish();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(context);
                    Log.e("AddProduct_Error=>", error.toString());
                    error.printStackTrace();

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
            queue.add(jsonObjectRequest);

        } catch (Exception e) {
            FileUtils.DismissLoading(AddProductActivity.this);
            e.printStackTrace();
        }
    }

    public void updateProductInfo(String pro_id, String pro_name, String pro_price, String pro_des, String category, String subCat, String pro_offer, String pro_itemcode, ArrayList<String> imagesArrayList) {
        JSONObject params = new JSONObject();
        try {
            params.put("productid", pro_id);
            params.put("name", pro_name);
            params.put("price", pro_price);
            params.put("description", pro_des);
            params.put("category", category);
            params.put("subCategory", subCat);
            params.put("offer", pro_offer);
            params.put("itemCode", pro_itemcode);
            if (!imagesArrayList.isEmpty()) {
                for (int i = 0; i < imagesArrayList.size(); i++) {
                    imageJsonArray.put(imagesArrayList.get(i));
                }
                try {
                    params.put("images", imageJsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.update_product, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(context);
                    Log.e("UpdateProduct=>", response.toString());

                    CreateProductModel createProductModel = new Gson().fromJson(response.toString(), CreateProductModel.class);

                    startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                    finish();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(context);
                    Log.e("UpdateProduct_Error=>", error.toString());
                    error.printStackTrace();

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
            queue.add(jsonObjectRequest);

        } catch (Exception e) {
            FileUtils.DismissLoading(AddProductActivity.this);
            e.printStackTrace();
        }
    }

    public void getProductDetails(String id) {
        try {
            FileUtils.DisplayLoading(AddProductActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, Constans.fetch_single_product + "?pid=" + id, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(AddProductActivity.this);
                    Log.e("FetchSingleProduct=>", response.toString());

                    ProductModel productModel = new Gson().fromJson(response.toString(), ProductModel.class);

                    edt_pro_name.setText(productModel.getProductDetailsModel().getName());
                    edt_pro_price.setText(String.valueOf(productModel.getProductDetailsModel().getPrice()));
                    edt_offer.setText(productModel.getProductDetailsModel().getOffer());
                    edt_category.setText(productModel.getProductDetailsModel().getCategory());
                    edt_subcategory.setText(productModel.getProductDetailsModel().getSubCategory());
                    edt_pro_des.setText(productModel.getProductDetailsModel().getDescription());
                    edt_pro_code.setText(productModel.getProductDetailsModel().getItemCode());

                    for (int i = 0; i < productModel.getProductDetailsModel().getImages().size(); i++) {
                        imagesArrayList.add(productModel.getProductDetailsModel().getImages().get(i).toString());
                    }

                    if (productModel.getProductDetailsModel().getImages().size() == 4) {
                        for (int i = 0; i < productModel.getProductDetailsModel().getImages().size(); i++) {
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(0)).into(img_add_image);
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(1)).into(img_product1);
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(2)).into(img_product2);
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(3)).into(img_product3);
                        }
                    } else if (productModel.getProductDetailsModel().getImages().size() == 3) {
                        for (int i = 0; i < productModel.getProductDetailsModel().getImages().size(); i++) {
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(0)).into(img_add_image);
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(1)).into(img_product1);
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(2)).into(img_product2);
                        }
                    } else if (productModel.getProductDetailsModel().getImages().size() == 2) {
                        for (int i = 0; i < productModel.getProductDetailsModel().getImages().size(); i++) {
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(0)).into(img_add_image);
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(1)).into(img_product1);
                        }
                    } else if (productModel.getProductDetailsModel().getImages().size() == 1) {
                        for (int i = 0; i < productModel.getProductDetailsModel().getImages().size(); i++) {
                            Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + productModel.getProductDetailsModel().getImages().get(0)).into(img_add_image);
                        }
                    } else {

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(AddProductActivity.this);
                    System.out.println("FetchSingleProduct_Error=> " + error.toString());
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

            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            FileUtils.DismissLoading(AddProductActivity.this);
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}