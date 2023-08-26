package com.festum.festumfield.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FileUtils {


    public static LottieAnimationView animationView;
    public static Dialog mDialog;

    public static void DisplayLoading(Context context) {
        mDialog = new Dialog(context);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.progress_dialog);
        mDialog.setCancelable(false);

        animationView = mDialog.findViewById(R.id.animationView);

        animationView.playAnimation();

        mDialog.show();
    }

    public static void DismissLoading(Context context) {

        if (mDialog.isShowing()) mDialog.dismiss();
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static File commonDocumentDirPath(String FolderName) {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FolderName);
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {

            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success) {
                dir = null;
            }
        }
        return dir;
    }

    public static void personalProfileImageUpload(Context context, File file) {
        AndroidNetworking.upload(Constans.set_profile_pic).addMultipartFile("file", file).addHeaders("authorization", AppPreferencesDelegates.Companion.get().getToken()).setTag("uploadTest").setPriority(Priority.HIGH).build().setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
                // do anything with progress
            }
        }).getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                // do anything with response
                Log.e("LLL_image_up--->", response.toString());
            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("LLL_image_err--->", error.toString());

            }
        });
    }


    public static File commonDirPath(String filename) {
//    public static String commonDirPath(String filename) {
        String outputpath = null;
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/FriendField/");
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/FriendField/");
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success) {
                dir = null;
            }
        }
//        outputpath=dir.getPath();
        return dir;
    }

    public static String getAddressFromLatLng(Context context, LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

//            edt_business_address.setText(address);
            Log.e("LLL_address-->", address);
            Log.e("LLL_city-->", city);
            Log.e("LLL_state-->", state);
            Log.e("LLL_country-->", country);
            Log.e("LLL_postalCode-->", postalCode);
            Log.e("LLL_knownName-->", knownName);
            Log.e("LLL_add-->", addresses.toString());
            return addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAddressFromLatLng(Context context, double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

//            edt_business_address.setText(address);
            Log.e("LLL_address-->", address);
            Log.e("LLL_city-->", city);
            Log.e("LLL_state-->", state);
            Log.e("LLL_country-->", country);
            Log.e("LLL_postalCode-->", postalCode);
            Log.e("LLL_knownName-->", knownName);
            Log.e("LLL_add-->", addresses.toString());
            return addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("Range")
    public static long getDuration(Context context, Uri uri) {
        long duration = 0L;
        Cursor cursor = MediaStore.Video.query(context.getContentResolver(), uri, new String[]{MediaStore.Video.VideoColumns.DURATION});
        if (cursor != null) {
            cursor.moveToFirst();
            duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));
            cursor.close();
        }
        return duration;
    }

    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }

    }

    public static void DeleteProduct(Context context, String url, String product_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("pid", product_id);

        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("LLL_delete_pro-->", response.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("LLL_delete_err-->", "Error: " + error + "\nStatus Code " + error.networkResponse.statusCode + "\nCause " + error.getCause() + "\nnetworkResponse " + error.networkResponse.data.toString() + "\nmessage" + error.getMessage());

                    NetworkResponse response = error.networkResponse;

                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            JSONObject obj = new JSONObject(res);
                            Log.e("LLL_dell11-->", obj.toString());

                        } catch (UnsupportedEncodingException | JSONException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        } // returned data is not JSONObject?

                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
//                    map.put("Content-Type", "application/json");
                    map.put("authorization", AppPreferencesDelegates.Companion.get().getToken());
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isValidPhoneNumber(String phone) {

        if (!phone.trim().equals("") && phone.length() >= 10) {
            return Patterns.PHONE.matcher(phone).matches();
        }

        return false;
    }


}
