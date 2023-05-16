package com.festum.festumfield.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.Model.ContactUs.ContactDataModel;
import com.festum.festumfield.Model.ContactUs.ContactModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);            }
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
        if (requestCode == PICK_IMAGE) {
            try {
                Uri selectImage = data.getData();
                img_add_image.setImageURI(selectImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
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