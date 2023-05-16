package com.festum.festumfield.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.festum.festumfield.Activity.CreateNotificationActivity;
import com.festum.festumfield.Adapter.PromotionNotificationAdapter;
import com.festum.festumfield.Model.Notification.NotificationDetailsModel;
import com.festum.festumfield.MyApplication;
import com.example.friendfield.R;
import com.festum.festumfield.Utils.Constans;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RunningPromotionFragment extends Fragment {

    AppCompatButton btn_create_notification;
    RecyclerView recycler_noti;
    NestedScrollView nestedScrollView;
    ArrayList<NotificationDetailsModel> arraylist = new ArrayList<>();
    int page = 1, limit = 10;
    String searchData = "";
    ProgressBar idPBLoading;
    RelativeLayout emptyLay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_running_promotion, container, false);

        btn_create_notification = inflate.findViewById(R.id.btn_create_notification);
        recycler_noti = inflate.findViewById(R.id.recycler_noti);
        nestedScrollView = inflate.findViewById(R.id.nestedScrollView);
        idPBLoading = inflate.findViewById(R.id.idPBLoading);
        emptyLay = inflate.findViewById(R.id.emptyLay);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler_noti.setLayoutManager(linearLayoutManager);

        if (!arraylist.isEmpty()) {
            arraylist.clear();
        }

        btn_create_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateNotificationActivity.class));
                getActivity().finish();
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    idPBLoading.setVisibility(View.VISIBLE);
                    getNotificationItem(page, limit, searchData, "", "title", 1);
                }
            }
        });

        getNotificationItem(page, limit, searchData, "", "title", 1);

        return inflate;
    }

    public void getNotificationItem(int page, int limit, String search, String status, String short_field, int short_option) {
        if (page > limit) {
            Toast.makeText(getActivity(), "That's all the data..", Toast.LENGTH_SHORT).show();
            idPBLoading.setVisibility(View.GONE);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("page", page);
            jsonObject.put("limit", limit);
            jsonObject.put("search", search);
            jsonObject.put("status", status);
            jsonObject.put("sortfield", short_field);
            jsonObject.put("sortoption", short_option);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.fetch_notification, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    idPBLoading.setVisibility(View.GONE);
                    Log.e("FetchNotifi=>", response.toString());
                    try {
                        JSONObject dataJsonObject = response.getJSONObject("Data");
                        JSONArray data_array = dataJsonObject.getJSONArray("docs");
                        int adPos = 0;

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject jsonObject = data_array.getJSONObject(i);

                            NotificationDetailsModel notificationDetailsModel = new Gson().fromJson(jsonObject.toString(), NotificationDetailsModel.class);
                            if (adPos == 1) {
                                arraylist.add(null);
                                adPos = 0;
                            }
                            arraylist.add(notificationDetailsModel);
                            adPos++;
                        }

                        if (!arraylist.isEmpty()) {
                            emptyLay.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.VISIBLE);
                        } else {
                            emptyLay.setVisibility(View.VISIBLE);
                            nestedScrollView.setVisibility(View.GONE);
                        }

                        Log.e("LLL_product_listsize-->", String.valueOf(arraylist.size()));

                        PromotionNotificationAdapter notificationAdapter = new PromotionNotificationAdapter(getActivity(), arraylist);
                        recycler_noti.setAdapter(notificationAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("FetchNotifi_Error=>", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", MyApplication.getAuthToken(getContext()));
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}