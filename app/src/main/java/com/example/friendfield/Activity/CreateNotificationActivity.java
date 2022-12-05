package com.example.friendfield.Activity;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.Model.Business.Register.BusinessRegisterModel;
import com.example.friendfield.Model.Notification.CreateNotificationModel;
import com.example.friendfield.Model.Notification.NotificationModel;
import com.example.friendfield.Model.Product.ProductImagesModel;
import com.example.friendfield.Model.Product.ProductModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Const;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateNotificationActivity extends BaseActivity {

    ImageView back_arrow_ic, add_image, img;
    EditText edt_notification_title, edt_notification_link;
    TextInputEditText edt_notification_des;
    AppCompatButton btn_notification_done;
    RelativeLayout ic_edit_img;
    public static final int PICK_IMAGE = 1;
    Bitmap bitmap = null;
    boolean status = false;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_REQUEST = 1;
    Uri uri;
    Intent picIntent = null;
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
                    FileUtils.DisplayLoading(context);
                    if (edit_noti != null) {
                        updateNotification(notificationId, edt_notification_title.getText().toString().trim(), edt_notification_des.getText().toString().trim(), edt_notification_link.getText().toString().trim(), image_url);
                    } else {
                        createNotification(edt_notification_title.getText().toString().trim(), edt_notification_des.getText().toString().trim(), edt_notification_link.getText().toString().trim(), image_url);
                        Toast.makeText(CreateNotificationActivity.this, "Data add successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ic_edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(CreateNotificationActivity.this)
                        .crop()
                        .maxResultSize(1080, 1080)
                        .start(PICK_IMAGE);
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(CreateNotificationActivity.this)
                        .crop()
                        .maxResultSize(1080, 1080)
                        .start(PICK_IMAGE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try {
                Uri selectedImageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    Const.bitmap_business_profile_image = bitmap;
                    add_image.setImageBitmap(bitmap);
                    img = add_image;
                    if (img != null) {
                        status = true;
                        ic_edit_img.setVisibility(View.VISIBLE);
                    } else {
                        status = false;
                        ic_edit_img.setVisibility(View.GONE);
                    }

                    File file = new File(selectedImageUri.getPath());
                    uploadImage(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == GALLERY_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                if (data != null) {
//                    uri = data.getData();
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inJustDecodeBounds = true;
//                    try {
//                        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
//                        options.inSampleSize = calculateInSampleSize(options, 100, 100);
//                        options.inJustDecodeBounds = false;
//                        Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
//                        add_image.setImageBitmap(image);
//                        img = add_image;
//                        if (img != null) {
//                            status = true;
//                            ic_edit_img.setVisibility(View.VISIBLE);
//                        } else {
//                            status = false;
//                            ic_edit_img.setVisibility(View.GONE);
//                        }
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Cancelled",
//                            Toast.LENGTH_SHORT).show();
//                }
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(), "Cancelled",
//                        Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == CAMERA_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                if (data.hasExtra("data")) {
//                    bitmap = (Bitmap) data.getExtras().get("data");
//                    uri = getImageUri(CreateNotificationActivity.this, bitmap);
//                    File finalFile = new File(getRealPathFromUri(uri));
//                    add_image.setImageBitmap(bitmap);
//                } else if (data.getExtras() == null) {
//
//                    Toast.makeText(getApplicationContext(),
//                                    "No extras to retrieve!", Toast.LENGTH_SHORT)
//                            .show();
//
//                    BitmapDrawable thumbnail = new BitmapDrawable(
//                            getResources(), data.getData().getPath());
////                    pet_pic.setImageDrawable(thumbnail);
//
//                }
//
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(), "Cancelled",
//                        Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == PICK_IMAGE) {
//            if (resultCode == RESULT_OK) {
//                Uri selectedImageUri;
//                try {
//                    selectedImageUri = data.getData();
//                    Bitmap bitmap = null;
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                    add_image.setImageBitmap(bitmap);
//
//                    File file = new File(selectedImageUri.getPath());
//                    uploadImage(file);
////                FileUtils.personalProfileImageUpload(getApplicationContext(),file);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }else {
//                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
//
//            }
//
//        } else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void uploadImage(File file) {
        AndroidNetworking.upload(Constans.set_notification_banner)
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
                        try {
                            Log.e("LLL_N_image_up--->", response.toString());
                            ProductImagesModel productImagesModel = new Gson().fromJson(response.toString(), ProductImagesModel.class);

                            image_url = productImagesModel.getData().getKey();


                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("LLL_N_image_err--->", error.toString());

                    }
                });
    }

    public void createNotification(String title, String description, String link, String imageUrl) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("title", title);
        params.put("description", description);
        params.put("link", link);
        if (imageUrl.equals("")) {

        } else {
            params.put("imageUrl", imageUrl);
        }
        JsonObjectRequest request = null;
        try {
            request = new JsonObjectRequest(POST, Constans.create_notification, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(context);
                    Log.e("LLL_noti_res-->", response.toString());

                    CreateNotificationModel createNotificationModel = new Gson().fromJson(response.toString(), CreateNotificationModel.class);

                    onBackPressed();

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(getApplicationContext());
                    System.out.println("LLL_noti_err--> " + error.toString());
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

            queue.add(request);

        } catch (Exception e) {
            FileUtils.DismissLoading(CreateNotificationActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e("LLL_bserror-->", e.getMessage());
        }
    }

    public void updateNotification(String nid, String title, String description, String link, String imageUrl) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("notificationid", nid);
        params.put("title", title);
        params.put("description", description);
        params.put("link", link);
        if (imageUrl.equals("")) {

        } else {
            params.put("imageUrl", imageUrl);
        }
        JsonObjectRequest request = null;
        try {
            request = new JsonObjectRequest(POST, Constans.update_notification, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(context);
                    Log.e("LLL_noti_res-->", response.toString());

                    CreateNotificationModel createNotificationModel = new Gson().fromJson(response.toString(), CreateNotificationModel.class);

                    onBackPressed();

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(getApplicationContext());
                    System.out.println("LLL_noti_err--> " + error.toString());
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

            queue.add(request);

        } catch (Exception e) {
            FileUtils.DismissLoading(CreateNotificationActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e("LLL_bserror-->", e.getMessage());
        }
    }

    public void getNotification(String id) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, Constans.fetch_single_notification + "?nid=" + id, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    NotificationModel notificationModel = new Gson().fromJson(response.toString(), NotificationModel.class);

                    edt_notification_title.setText(notificationModel.getNotificationDetailsModel().getTitle());
                    edt_notification_des.setText(String.valueOf(notificationModel.getNotificationDetailsModel().getDescription()));
                    edt_notification_link.setText(notificationModel.getNotificationDetailsModel().getLink());

                    if (notificationModel.getNotificationDetailsModel().getImageUrl().equals("")) {
                        ic_edit_img.setVisibility(View.GONE);
                    } else {
                        image_url = notificationModel.getNotificationDetailsModel().getImageUrl();
                        Log.e("LLL_imge--->", image_url);
                        ic_edit_img.setVisibility(View.VISIBLE);
                        add_image.setEnabled(false);
                        Glide.with(getApplicationContext()).load(Constans.Display_Image_URL + notificationModel.getNotificationDetailsModel().getImageUrl()).into(add_image);

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(CreateNotificationActivity.this);
                    System.out.println("LLL_product---> " + error.toString());
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
            e.printStackTrace();
        }
    }

    private String getRealPathFromUri(Uri tempUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = this.getContentResolver().query(tempUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private Uri getImageUri(CreateNotificationActivity youractivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(youractivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreateNotificationActivity.this, PromotionActivity.class));
        finish();
    }
}