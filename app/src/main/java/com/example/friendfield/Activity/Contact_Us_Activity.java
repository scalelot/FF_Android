package com.example.friendfield.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.Model.ContactUs.ContactDataModel;
import com.example.friendfield.Model.ContactUs.ContactModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Contact_Us_Activity extends BaseActivity {

    EditText full_name, phone_number, email_id, description;
    AppCompatButton btn_send;
    ImageView ic_back_arrow, img_add_image;
    ContactModel contactDataModel;
    Uri uri;
    private Bitmap bitmap;
    public static final int PICK_IMAGE = 1;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        ActivityCompat.requestPermissions(this, permissions(), 1);

        ic_back_arrow = findViewById(R.id.ic_back_arrow);
        full_name = findViewById(R.id.full_name);
        phone_number = findViewById(R.id.phone_number);
        email_id = findViewById(R.id.email_id);
        description = findViewById(R.id.description);
        btn_send = findViewById(R.id.btn_send);
        img_add_image = findViewById(R.id.img_add_image);

        ic_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(Contact_Us_Activity.this).crop().maxResultSize(1080, 1080).start(PICK_IMAGE);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (full_name.getText().toString().trim().isEmpty()) {
                    full_name.setError(getResources().getString(R.string.enter_full_name));
                } else if (phone_number.getText().toString().trim().isEmpty()) {
                    phone_number.setError(getResources().getString(R.string.enter_phone_number));
                } else if (email_id.getText().toString().trim().isEmpty()) {
                    email_id.setError(getResources().getString(R.string.enter_emailid));
                } else if (description.getText().toString().trim().isEmpty()) {
                    description.setError(getResources().getString(R.string.enter_description));
                } else {
                    getContactApi(full_name.getText().toString().trim(), phone_number.getText().toString().trim(), email_id.getText().toString().trim(), description.getText().toString().trim());
                }
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
        }
        return per;
    }

    private void getContactApi(String name, String ph_number, String email, String des) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("fullName", name);
            hashMap.put("contactNo", ph_number);
            hashMap.put("emailId", email);
            hashMap.put("issue", des);

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.contact_us, new JSONObject(hashMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("ContactUs=>", response.toString());
                    ContactDataModel contactDataModel = new Gson().fromJson(response.toString(), ContactDataModel.class);
                    full_name.setText("");
                    phone_number.setText("");
                    email_id.setText("");
                    description.setText("");

                    Toast.makeText(Contact_Us_Activity.this, contactDataModel.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ContactUs_Error=>" + error.toString());
                    Toast.makeText(Contact_Us_Activity.this, "Data Not Submit" + error, Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", MyApplication.getAuthToken(getApplicationContext()));
                    return map;
                }
            };
        } catch (Exception e) {
            FileUtils.DismissLoading(Contact_Us_Activity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(Contact_Us_Activity.this);
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Contact_Us_Activity.this, SettingActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    try {
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                        options.inSampleSize = calculateInSampleSize(options, 100, 100);
                        options.inJustDecodeBounds = false;
                        Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                        img_add_image.setImageBitmap(image);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("data")) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    uri = getImageUri(Contact_Us_Activity.this, bitmap);
                    File finalFile = new File(getRealPathFromUri(uri));
                    System.out.println("file:----" + uri.toString());
                    img_add_image.setImageBitmap(bitmap);
                } else if (data.getExtras() == null) {

                    Toast.makeText(getApplicationContext(), "No extras to retrieve!", Toast.LENGTH_SHORT).show();

                    BitmapDrawable thumbnail = new BitmapDrawable(getResources(), data.getData().getPath());
//                    pet_pic.setImageDrawable(thumbnail);

                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private Uri getImageUri(Contact_Us_Activity youractivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(youractivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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
}