package com.festum.festumfield.Fragment;

import android.os.Bundle;

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
import com.festum.festumfield.Adapter.SendRequestAdapter;
import com.festum.festumfield.Model.ReceiveFriendsList.ReceiveFriendsRegisterModel;
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

public class SendRequestFragment extends Fragment {

    RecyclerView send_recylerview;
    SendRequestAdapter sendRequestAdapter;
    int page = 1, limit = 10;
    String searchData = "";
    NestedScrollView nestedScrollView;
    ArrayList<ReceiveFriendsRegisterModel> receivefriendrequestsModelArrayList = new ArrayList<>();
    RelativeLayout emptyLay;
    ProgressBar idPBLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_request, container, false);

        send_recylerview = view.findViewById(R.id.send_recylerview);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        emptyLay = view.findViewById(R.id.emptyLay);
        idPBLoading = view.findViewById(R.id.idPBLoading);

        getSendFriendsRequest(page, limit, searchData);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    idPBLoading.setVisibility(View.VISIBLE);
                    getSendFriendsRequest(page, limit, searchData);
                }
            }
        });

        return view;
    }

    private void getSendFriendsRequest(int page, int limit, String search) {
        if (page > limit) {
            Toast.makeText(getContext(), "That's all the data..", Toast.LENGTH_SHORT).show();
            idPBLoading.setVisibility(View.GONE);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("page", page);
            jsonObject.put("limit", limit);
            jsonObject.put("search", search);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.all_send_friend_request, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    idPBLoading.setVisibility(View.GONE);
                    Log.e("SendFriendsRequest-->", response.toString());
                    try {
                        JSONObject dataJsonObject = response.getJSONObject("Data");

                        JSONArray data_array = dataJsonObject.getJSONArray("docs");

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject jsonObject = data_array.getJSONObject(i);

                            ReceiveFriendsRegisterModel productDetailsModel = new Gson().fromJson(jsonObject.toString(), ReceiveFriendsRegisterModel.class);
                            receivefriendrequestsModelArrayList.add(productDetailsModel);
                        }

                        if (!receivefriendrequestsModelArrayList.isEmpty()) {
                            emptyLay.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.VISIBLE);
                        } else {
                            emptyLay.setVisibility(View.VISIBLE);
                            nestedScrollView.setVisibility(View.GONE);
                        }

                        sendRequestAdapter = new SendRequestAdapter(SendRequestFragment.this, receivefriendrequestsModelArrayList);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        send_recylerview.setLayoutManager(manager);
                        send_recylerview.setAdapter(sendRequestAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("SendFriendsRequestError", error.toString());
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