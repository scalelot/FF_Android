package com.example.friendfield.Fragment;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.example.friendfield.Adapter.ProductDisplayAdapter;
import com.example.friendfield.Adapter.FriendsRequestAdapter;
import com.example.friendfield.Model.FindFriends.FindFriendsModel;
import com.example.friendfield.Model.FindFriends.FindFriendsRegisterModel;
import com.example.friendfield.Model.ReceiveFriendsList.ReceiveFriendsRegisterModel;
import com.example.friendfield.Model.ReceiveFriendsList.ReceiveFriendsRegisterModel;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendRequestFragment extends Fragment {

    FriendsRequestAdapter friendsRequestAdapter;
    RecyclerView recycle_request;
    int page = 1, limit = 10;
    String searchData = "";
    NestedScrollView nestedScrollView;
    RelativeLayout emptyLay,loaderLay;
    ArrayList<ReceiveFriendsRegisterModel> receivefriendrequestsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        recycle_request = view.findViewById(R.id.recyler_request);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        emptyLay = view.findViewById(R.id.emptyLay);
        loaderLay = view.findViewById(R.id.loaderLay);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    loaderLay.setVisibility(View.VISIBLE);
                    getFriendsRequest(page, limit, searchData);
                }
            }
        });

        return view;
    }

    public void getFriendsRequest(int page, int limit, String search) {
        if (page > limit) {
            Toast.makeText(getContext(), "That's all the data..", Toast.LENGTH_SHORT).show();
            loaderLay.setVisibility(View.GONE);
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
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.all_recived_friend_request, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    loaderLay.setVisibility(View.GONE);
                    Log.e("friends_request_dis-->", response.toString());
                    receivefriendrequestsModelArrayList.clear();
                    try {
                        JSONObject dataJsonObject = response.getJSONObject("Data");

                        JSONArray data_array = dataJsonObject.getJSONArray("docs");

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject jsonObject = data_array.getJSONObject(i);

                            ReceiveFriendsRegisterModel productDetailsModel = new Gson().fromJson(jsonObject.toString(), ReceiveFriendsRegisterModel.class);
                            receivefriendrequestsModelArrayList.add(productDetailsModel);
                        }

                        Log.e("friends_request_all-->", String.valueOf(receivefriendrequestsModelArrayList.size()));

                        if (!receivefriendrequestsModelArrayList.isEmpty()) {
                            emptyLay.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.VISIBLE);
                        } else {
                            emptyLay.setVisibility(View.VISIBLE);
                            nestedScrollView.setVisibility(View.GONE);
                        }

                        friendsRequestAdapter = new FriendsRequestAdapter(FriendRequestFragment.this, receivefriendrequestsModelArrayList);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        recycle_request.setLayoutManager(manager);
                        recycle_request.setAdapter(friendsRequestAdapter);
                        friendsRequestAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("friends_request_error=>", error.toString());
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
//            FileUtils.DismissLoading(ProductActivity.this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriendsRequest(page, limit, searchData);
    }
}