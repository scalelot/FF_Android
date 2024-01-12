package com.festum.festumfield.Activity;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.festum.festumfield.Model.Notification.CreateNotificationModel;
import com.festum.festumfield.Model.Notification.NotificationModel;
import com.festum.festumfield.Model.Product.ProductImagesModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.RealPathUtil;
import com.festum.festumfield.Utils.Const;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CreateNotificationActivity extends BaseActivity {

    ImageView back_arrow_ic, add_image;
    EditText edt_notification_title, edt_notification_link;
    TextInputEditText edt_notification_des;
    AppCompatButton btn_notification_done;
    RelativeLayout ic_edit_img;
    public static final int PICK_IMAGE = 1;
    boolean status = true;
    RequestQueue queue;
    Context context;
    String image_url = "";
    String edit_noti;
    TextView txt_title;
    String notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        ActivityCompat.requestPermissions(this, permissions(), 1);
        context = this;

        back_arrow_ic = findViewById(R.id.back_arrow_ic);
        add_image = findViewById(R.id.add_image);
        txt_title = findViewById(R.id.title);
        edt_notification_title = findViewById(R.id.edt_notification_title);
        edt_notification_des = findViewById(R.id.edt_notification_des);
        edt_notification_link = findViewById(R.id.edt_notification_link);
        btn_notification_done = findViewById(R.id.btn_notification_done);
        ic_edit_img = findViewById(R.id.ic_edit_img);
        queue = Volley.newRequestQueue(CreateNotificationActivity.this);

        edit_noti = getIntent().getStringExtra("Edit_Noti");
        notificationId = getIntent().getStringExtra("noti_id");

        if (edit_noti != null) {
            txt_title.setText(edit_noti);
            getNotification(notificationId);
        } else {
            txt_title.setText(getResources().getString(R.string.add_product));
        }

        back_arrow_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_notification_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edt_notification_title.getText().toString();
                String des = edt_notification_des.getText().toString();
                String link = edt_notification_link.getText().toString();

                if (status != true) {
                    Toast.makeText(CreateNotificationActivity.this, "Enter Image", Toast.LENGTH_SHORT).show();
                } else if (title.isEmpty()) {
                    edt_notification_title.setError("Enter Notification Title");
                } else if (des.isEmpty()) {
                    edt_notification_des.setError("Enter Notification Description");
                } else if (link.isEmpty()) {
                    edt_notification_link.setError("Enter Link");
                } else {
                    FileUtils.DisplayLoading(CreateNotificationActivity.this);
                    if (edit_noti != null) {
                        updateNotification(notificationId, edt_notification_title.getText().toString().trim(), edt_notification_des.getText().toString().trim(), edt_notification_link.getText().toString().trim(), image_url);
                    } else {
                        createNotification(edt_notification_title.getText().toString().trim(), edt_notification_des.getText().toString().trim(), edt_notification_link.getText().toString().trim(), image_url);
                    }
                }
            }
        });

        ic_edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

    }

    public static String[] storge_permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};

    public static String[] storge_permissions_33 = {android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO};
    String[] per;

    public String[] permissions() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                per = storge_permissions_33;
            } else {
                per = storge_permissions;
            }
        } catch (Exception e) {
            Log.e("CameraPermission:==", e.toString());
        }
        return per;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try {
                Uri selectImage = data.getData();
                Glide.with(CreateNotificationActivity.this).load(selectImage).placeholder(R.drawable.ic_user).into(add_image);

                String path = RealPathUtil.getRealPath(CreateNotificationActivity.this, selectImage);
                File file = new File(path);
                uploadImage(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImage(File file) {
        try {
            AndroidNetworking.upload(Constans.set_notification_banner).addMultipartFile("file", file).addHeaders("authorization", AppPreferencesDelegates.Companion.get().getToken()).setTag("uploadTest").setPriority(Priority.HIGH).build().setUploadProgressListener(new UploadProgressListener() {
                @Override
                public void onProgress(long bytesUploaded, long totalBytes) {
                }
            }).getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("NotiBanner=>", response.toString());
                        ProductImagesModel productImagesModel = new Gson().fromJson(response.toString(), ProductImagesModel.class);
                        image_url = productImagesModel.getData().getKey();

                        if (image_url != null) {
                            status = true;
                            ic_edit_img.setVisibility(View.VISIBLE);
                        } else {
                            status = false;
                            ic_edit_img.setVisibility(View.GONE);
                        }
                        getNotification(notificationId);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError error) {
                    Log.e("NotiBannerError=>", error.toString());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNotification(String title, String description, String link, String imageUrl) {

        JsonObjectRequest request = null;
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("title", title);
            params.put("description", description);
            params.put("link", link);
            if (imageUrl.equals("")) {

            } else {
                params.put("imageUrl", imageUrl);
            }
            request = new JsonObjectRequest(POST, Constans.create_notification, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(CreateNotificationActivity.this);
                    Log.e("CreateNotification=>", response.toString());

                    CreateNotificationModel createNotificationModel = new Gson().fromJson(response.toString(), CreateNotificationModel.class);
                    Toast.makeText(CreateNotificationActivity.this, "Data add successfully", Toast.LENGTH_SHORT).show();

                    Const.isUpdate = true;
                    onBackPressed();

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(CreateNotificationActivity.this);
                    System.out.println("CreateNotificationError=>" + error.toString());
                    error.printStackTrace();
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

            queue.add(request);

        } catch (Exception e) {
            FileUtils.DismissLoading(CreateNotificationActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void updateNotification(String nid, String title, String description, String link, String imageUrl) {
        JsonObjectRequest request = null;
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("notificationid", nid);
            params.put("title", title);
            params.put("description", description);
            params.put("link", link);
            if (imageUrl.equals("")) {

            } else {
                params.put("imageUrl", imageUrl);
            }
            request = new JsonObjectRequest(POST, Constans.update_notification, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(CreateNotificationActivity.this);
                    Log.e("UpdateNotification=>", response.toString());

                    CreateNotificationModel createNotificationModel = new Gson().fromJson(response.toString(), CreateNotificationModel.class);

                    startActivity(new Intent(CreateNotificationActivity.this, PromotionActivity.class));

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(CreateNotificationActivity.this);
                    System.out.println("UpdateNotificationError=>" + error.toString());
                    error.printStackTrace();
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

            queue.add(request);

        } catch (Exception e) {
            FileUtils.DismissLoading(CreateNotificationActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void getNotification(String id) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, Constans.fetch_single_notification + "?nid=" + id, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("GetSingleNotification=>", response.toString());
                    NotificationModel notificationModel = new Gson().fromJson(response.toString(), NotificationModel.class);

                    edt_notification_title.setText(notificationModel.getNotificationDetailsModel().getTitle());
                    edt_notification_des.setText(String.valueOf(notificationModel.getNotificationDetailsModel().getDescription()));
                    edt_notification_link.setText(notificationModel.getNotificationDetailsModel().getLink());

                    if (notificationModel.getNotificationDetailsModel().getImageUrl().equals("")) {
                        ic_edit_img.setVisibility(View.GONE);
                    } else {
                        image_url = notificationModel.getNotificationDetailsModel().getImageUrl();
                        ic_edit_img.setVisibility(View.VISIBLE);
                        add_image.setEnabled(false);
                        Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + notificationModel.getNotificationDetailsModel().getImageUrl()).into(add_image);

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    FileUtils.DismissLoading(CreateNotificationActivity.this);
                    System.out.println("GetSingleNotificationError=>" + error.toString());
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

            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}