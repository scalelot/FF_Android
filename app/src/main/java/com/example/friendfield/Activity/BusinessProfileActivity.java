package com.example.friendfield.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
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
import com.example.friendfield.Model.BusinessInfo.BusinessInfoRegisterModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Const;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;
import com.example.friendfield.VolleyMultipartRequest;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BusinessProfileActivity extends BaseActivity implements OnMapReadyCallback, LocationListener {
    String selectedImage = "";
    private GoogleMap map;
    ImageView ic_back, img_add_brochure, iv_location, img;
    CircleImageView business_profile_image;
    RelativeLayout edt_img, rl_map, relative_busi_map, rl_upload, rl_add_brochur;
    LinearLayout lin_add_images;
    EditText edt_subcategory, edt_interested_subcategory, edt_interested_category;
    EditText edt_category, edt_description, edt_bussiness_name, edt_business_address;
    AppCompatButton btn_next, btn_save, btn_change;
    File myFile;
    LocationManager locationManager;
    RequestQueue queue;
    Context context;
    public Criteria criteria;
    public String bestProvider;
    LatLng latLng;
    String edit_profile = "";
    TextView tv_title, edt_brochure;
    MapView mapview;
    String longitude, uriString;
    String lattitude;
    String path = null;
    public static final int PICK_FILE = 99;
    PdfRenderer renderer;
    int total_pages = 0;
    int display_page = 0;
    String finalDisplayName;
    public static final int PICK_IMAGE = 1;
    private static final int REQUEST_CODE = 101;
    private RequestQueue rQueue;
    private ArrayList<HashMap<String, String>> arraylist;
    String url = "https://www.google.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);

        context = this;
        queue = Volley.newRequestQueue(BusinessProfileActivity.this, new HurlStack());

        ic_back = findViewById(R.id.ic_back);
        business_profile_image = findViewById(R.id.business_profile_image);
        edt_bussiness_name = findViewById(R.id.edt_bussiness_name);
        edt_img = findViewById(R.id.edt_img);
        edt_category = findViewById(R.id.edt_category);
        edt_subcategory = findViewById(R.id.edt_subcategory);
        edt_description = findViewById(R.id.edt_description);
        edt_interested_subcategory = findViewById(R.id.edt_interested_subcategory);
        edt_interested_category = findViewById(R.id.edt_interested_category);
        btn_next = findViewById(R.id.btn_next);
        rl_map = findViewById(R.id.rl_map);
        edt_business_address = findViewById(R.id.edt_business_address);
        iv_location = findViewById(R.id.iv_location);
        tv_title = findViewById(R.id.tv_title);
        btn_save = findViewById(R.id.btn_save);
        mapview = findViewById(R.id.mapview);
        relative_busi_map = findViewById(R.id.relative_busi_map);
        img_add_brochure = findViewById(R.id.img_add_brochure);
        rl_upload = findViewById(R.id.rl_upload);
        lin_add_images = findViewById(R.id.lin_add_images);
        rl_add_brochur = findViewById(R.id.rl_add_brochur);
        btn_change = findViewById(R.id.btn_change);
        edt_brochure = findViewById(R.id.edt_brochure);

        edit_profile = getIntent().getStringExtra("edit_profile");

        edt_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        rl_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPdf();
            }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPdf();
            }
        });

        String strName = getSharedPreferences("name", MODE_PRIVATE).getString("displayName", null);
        edt_brochure.setText(strName);

        ic_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsLocationActivity.class).putExtra("isBusinessLocation", true));

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_bussiness_name.getText().toString().equals("")) {
                    edt_bussiness_name.setError(getResources().getString(R.string.please_enter_bname));
                } else if (edt_category.getText().toString().equals("")) {
                    edt_category.setError(getResources().getString(R.string.please_enter_bname));
                } else if (edt_subcategory.getText().toString().equals("")) {
                    edt_subcategory.setError(getResources().getString(R.string.please_enter_b_subcat));
                } else if (edt_description.getText().toString().equals("")) {
                    edt_description.setError(getResources().getString(R.string.please_enter_b_des));
                } else if (Const.b_longitude == null) {
                    Toast.makeText(context, getResources().getString(R.string.please_enter_b_location), Toast.LENGTH_SHORT).show();
                } else if (Const.b_lattitude == null) {
                    Toast.makeText(context, getResources().getString(R.string.please_enter_b_location), Toast.LENGTH_SHORT).show();
                } else if (edt_interested_category.getText().toString().equals("")) {
                    edt_interested_category.setError(getResources().getString(R.string.please_enter_interested_category));
                } else if (edt_interested_subcategory.getText().toString().equals("")) {
                    edt_interested_subcategory.setError(getResources().getString(R.string.please_enter_interested_subcategory));
                } else {
                    FileUtils.DisplayLoading(context);
                    CreateBusinessAccount(Request.Method.POST, Constans.business_register, edt_bussiness_name.getText().toString().trim(), edt_category.getText().toString().trim(), edt_subcategory.getText().toString().trim(), edt_description.getText().toString().trim(), String.valueOf(Const.b_longitude), String.valueOf(Const.b_lattitude), edt_interested_category.getText().toString().trim(), edt_interested_subcategory.getText().toString().trim());
                }
            }
        });

        relative_busi_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsLocationActivity.class).putExtra("isBusinessLocation", true));
            }
        });

        if (img_add_brochure.getDrawable() == null) {
            rl_add_brochur.setVisibility(View.GONE);
            rl_upload.setVisibility(View.VISIBLE);
        } else {
            rl_add_brochur.setVisibility(View.VISIBLE);
            rl_upload.setVisibility(View.GONE);
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_bussiness_name.getText().toString().equals("")) {
                    edt_bussiness_name.setError(getResources().getString(R.string.please_enter_bname));
                } else if (edt_category.getText().toString().equals("")) {
                    edt_category.setError(getResources().getString(R.string.please_enter_bname));
                } else if (edt_subcategory.getText().toString().equals("")) {
                    edt_subcategory.setError(getResources().getString(R.string.please_enter_b_subcat));
                } else if (edt_description.getText().toString().equals("")) {
                    edt_description.setError(getResources().getString(R.string.please_enter_b_des));
                } else if (String.valueOf(Const.b_longitude) == null) {
                    Toast.makeText(context, getResources().getString(R.string.please_enter_b_location), Toast.LENGTH_SHORT).show();
                } else if (String.valueOf(Const.b_lattitude) == null) {
                    Toast.makeText(context, getResources().getString(R.string.please_enter_b_location), Toast.LENGTH_SHORT).show();
                } else {
                    if (img_add_brochure.getDrawable() == null) {
                        FileUtils.DisplayLoading(context);
                        rl_add_brochur.setVisibility(View.GONE);
                        rl_upload.setVisibility(View.VISIBLE);
                    } else {
                        rl_add_brochur.setVisibility(View.VISIBLE);
                        rl_upload.setVisibility(View.GONE);
                    }
                    CreateBusinessAccount(Request.Method.POST, Constans.business_register, edt_bussiness_name.getText().toString().trim(), edt_category.getText().toString().trim(), edt_subcategory.getText().toString().trim(), edt_description.getText().toString().trim(), String.valueOf(Const.b_longitude), String.valueOf(Const.b_lattitude), edt_interested_category.getText().toString().trim(), edt_interested_subcategory.getText().toString().trim());

                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        mapview.getMapAsync(this);

        if (!MyApplication.isBusinessProfileRegistered(BusinessProfileActivity.this)) {
            tv_title.setText(getResources().getString(R.string.edit_business_profile));
            if (Const.bitmap_business_profile_image != null) {
                business_profile_image.setImageBitmap(Const.bitmap_business_profile_image);
            }
            getBusinessProfileInfo();
            btn_save.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
        } else {
            tv_title.setText(getResources().getString(R.string.create_business_profile));
            btn_save.setVisibility(View.GONE);
            btn_next.setVisibility(View.VISIBLE);
        }

        if (Const.b_longitude != null) {
            if (map != null) {
                map.clear();

                if (Const.mCurrLocationMarker != null) {
                    Const.mCurrLocationMarker.remove();
                }
                LatLng userLocation = new LatLng(Const.b_lattitude, Const.b_longitude);
                map.addMarker(new MarkerOptions().position(userLocation));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
                edt_business_address.setText(FileUtils.getAddressFromLatLng(getApplicationContext(), Const.latLngvalue));

            }
        } else {
            fetchLocation();
        }
    }

    public void CreateBusinessAccount(int method, String url, String name, String category, String subcategory, String des, String longi, String latti, String interestedCategory, String interestedSubCategory) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("category", category);
        params.put("subCategory", subcategory);
        params.put("description", des);
        params.put("longitude", longi);
        params.put("latitude", latti);
        params.put("interestedCategory", interestedCategory);
        params.put("interestedSubCategory", interestedSubCategory);

        JsonObjectRequest request = null;
        try {
            request = new JsonObjectRequest(method, url, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(context);
                    Log.e("BusinessRegister=>", response.toString());

                    BusinessRegisterModel businessRegisterModel = new Gson().fromJson(response.toString(), BusinessRegisterModel.class);

                    MyApplication.setBusinessProfileRegistered(BusinessProfileActivity.this, businessRegisterModel.getIsSuccess());
                    finish();

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(getApplicationContext());
                    System.out.println("BusinessRegister_Error=>" + error.toString());
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
            FileUtils.DismissLoading(BusinessProfileActivity.this);
            Toast.makeText(this, getResources().getString(R.string.something_want_to_wrong), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void getBusinessProfileInfo() {
        FileUtils.DisplayLoading(BusinessProfileActivity.this);
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constans.fetch_business_info, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(BusinessProfileActivity.this);

                    Log.e("FetchBusinessInfo=>", response.toString());
                    BusinessInfoRegisterModel businessInfoRegisterModel = new Gson().fromJson(response.toString(), BusinessInfoRegisterModel.class);

                    Double logitude = businessInfoRegisterModel.getData().getLocation().getCoordinates().get(0);
                    Double latitiude = businessInfoRegisterModel.getData().getLocation().getCoordinates().get(1);


                    LatLng latLng1 = new LatLng(latitiude, logitude);
                    latLng = latLng1;

                    edt_bussiness_name.setText(businessInfoRegisterModel.getData().getName());
                    edt_category.setText(businessInfoRegisterModel.getData().getCategory());
                    edt_subcategory.setText(businessInfoRegisterModel.getData().getSubCategory());
                    edt_description.setText(businessInfoRegisterModel.getData().getDescription());

                    if (businessInfoRegisterModel.getData().getBusinessimage().equals("")) {
                        business_profile_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
                    } else {
                        Glide.with(BusinessProfileActivity.this).asBitmap().load(Constans.Display_Image_URL + businessInfoRegisterModel.getData().getBusinessimage()).placeholder(R.drawable.user_accept_dialog).into(business_profile_image);
                    }

                    String getadd = FileUtils.getAddressFromLatLng(getApplicationContext(), latLng);

                    edt_business_address.setText(getadd);

                    map.addMarker(new MarkerOptions().position(latLng1));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 12));

                    edt_interested_category.setText(businessInfoRegisterModel.getData().getInterestedCategory());
                    edt_interested_subcategory.setText(businessInfoRegisterModel.getData().getInterestedSubCategory());

                    rl_upload.setVisibility(View.GONE);
                    rl_add_brochur.setVisibility(View.VISIBLE);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("FetchBusiness_Error=>", error.toString());
                    FileUtils.DismissLoading(BusinessProfileActivity.this);
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
            e.printStackTrace();
            FileUtils.DismissLoading(BusinessProfileActivity.this);

        }
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_FILE);
    }


    private void openGallery() {
        ImagePicker.Companion.with(BusinessProfileActivity.this)
                .crop()
                .maxResultSize(1080, 1080)
                .start(PICK_IMAGE);
    }

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {

            try {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    Const.bitmap_business_profile_image = bitmap;
                    business_profile_image.setImageBitmap(bitmap);

                    File file = new File(selectedImageUri.getPath());

                    uploadImage(file);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_FILE) {
            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver()
                            .openFileDescriptor(uri, "r");
                    renderer = new PdfRenderer(parcelFileDescriptor);
                    total_pages = renderer.getPageCount();
                    display_page = 0;
                    _display(display_page);
                    cursor = this.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.d("PDFName=>", displayName);
                        SharedPreferences sharedPreferences = getSharedPreferences("name", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("displayName", displayName);
                        editor.commit();

                        edt_brochure.setText(displayName);

                        uploadPDF(displayName, uri);
                        lin_add_images.setVisibility(View.GONE);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
                Log.d("PDFName=>", displayName);
            }
        }
    }

    private void uploadPDF(final String pdfname, Uri pdffile) {

        InputStream iStream = null;
        try {

            iStream = getContentResolver().openInputStream(pdffile);
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constans.set_Setbrochure_pdf,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("PdfUpload=>", new String(response.data));
                            rQueue.getCache().clear();
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(new String(response.data));
                                Toast.makeText(getApplicationContext(), jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();

                                jsonObject.toString().replace("\\\\", "");

                                if (jsonObject.getString("IsSuccess").equals("true")) {
                                    Log.d("come::: >>>  ", "yessssss");
                                    arraylist = new ArrayList<HashMap<String, String>>();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("PdfUpload_Error=>" + error.getMessage());
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("file", new DataPart(pdfname, inputData, "application/pdf"));
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("authorization", MyApplication.getAuthToken(getApplicationContext()));
                    return headers;
                }
            };

            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(BusinessProfileActivity.this);
            rQueue.add(volleyMultipartRequest);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void _display(int _n) {
        if (renderer != null) {
            PdfRenderer.Page page = renderer.openPage(_n);
            Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            img_add_brochure.setImageBitmap(mBitmap);
            page.close();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void uploadImage(File file) {
        AndroidNetworking.upload(Constans.set_business_profile_pic)
                .addMultipartFile("file", file)
                .addHeaders("authorization", MyApplication.getAuthToken(getApplicationContext()))
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("BusinessProImage=>", response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("BusinProImage_Error--->", error.toString());
                    }
                });
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e("Latitude: ", +location.getLatitude() + ", Longitude:" + location.getLongitude());
        longitude = String.valueOf(location.getLongitude());
        lattitude = String.valueOf(location.getLatitude());

        LatLng userLocation;
        if (Const.b_longitude != null) {
            userLocation = new LatLng(Const.b_lattitude, Const.b_longitude);

        } else if (latLng != null) {
            userLocation = new LatLng(Double.valueOf(latLng.latitude), Double.valueOf(latLng.longitude));

        } else {
            userLocation = new LatLng(Double.valueOf(lattitude), Double.valueOf(longitude));

        }
        map.addMarker(new MarkerOptions().position(userLocation));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    public void fetchLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                return;
            } else {
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        } catch (Exception e) {
            Log.e("LLL_bproerr--->", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                    selectPdf();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
            map = googleMap;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);

                criteria = new Criteria();
                bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    Log.e("TAG", "GPS is on");
                    Const.b_lattitude = location.getLatitude();
                    Const.b_longitude = location.getLongitude();
                    Log.e("LLL_Latitude: ", +location.getLatitude() + ", Longitude:" + location.getLongitude());

                    centreMapOnLocation(location, bestProvider);

                } else {
                    locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                }

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng arg0) {
                        startActivity(new Intent(getApplicationContext(), MapsLocationActivity.class).putExtra("isBusinessLocation", true));
                    }
                });

                mapview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), MapsLocationActivity.class).putExtra("isBusinessLocation", true));

                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Accetta i permessi", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("LLL_bproerr1--->", e.getMessage());
            e.printStackTrace();
        }

    }

    public void centreMapOnLocation(Location location, String title) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(userLocation);
        markerOptions.draggable(true);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Const.b_longitude != null) {
            if (map != null) {
                map.clear();
                if (Const.mCurrLocationMarker != null) {
                    Const.mCurrLocationMarker.remove();
                }
                LatLng userLocation = new LatLng(Const.b_lattitude, Const.b_longitude);
                map.addMarker(new MarkerOptions().position(userLocation));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        Log.e("LLL_onclick_map==>", "false");
                    }
                });
            }
        } else {
            fetchLocation();
        }
    }
}