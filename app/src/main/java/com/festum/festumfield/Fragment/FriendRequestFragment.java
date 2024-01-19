package com.festum.festumfield.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.festum.festumfield.Adapter.FriendsRequestAdapter;
import com.festum.festumfield.Model.Interface.FriendsRequestInterface;
import com.festum.festumfield.Model.ReceiveFriendsList.ReceiveFriendsRegisterModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
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
    RelativeLayout emptyLay;
    ProgressBar idPBLoading;
    ArrayList<ReceiveFriendsRegisterModel> receivefriendrequestsModelArrayList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        recycle_request = view.findViewById(R.id.recyler_request);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        emptyLay = view.findViewById(R.id.emptyLay);
        idPBLoading = view.findViewById(R.id.idPBLoading);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recycle_request.setLayoutManager(manager);

        getFriendsRequest(page, limit, searchData);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    idPBLoading.setVisibility(View.VISIBLE);
                    getFriendsRequest(page, limit, searchData);
                }
            }
        });

        recycle_request.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                emptyLay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                emptyLay.setVisibility(View.VISIBLE);
            }
        });



        return view;
    }

    public void getFriendsRequest(int page, int limit, String search) {
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
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.all_recived_friend_request, jsonObject, new Response.Listener<JSONObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(JSONObject response) {
                    idPBLoading.setVisibility(View.GONE);
                    Log.e("FriendsRequest-->", response.toString());
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

                        friendsRequestAdapter = new FriendsRequestAdapter(FriendRequestFragment.this, receivefriendrequestsModelArrayList, new FriendsRequestInterface() {
                            @Override
                            public void setIsResponse(boolean isResponse) {

                                if (isResponse){
                                    receivefriendrequestsModelArrayList.clear();
                                    getFriendsRequest(page, limit, searchData);
                                }

                            }

                        });

                        recycle_request.setAdapter(friendsRequestAdapter);
                        friendsRequestAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    idPBLoading.setVisibility(View.GONE);
                    Log.e("FriendsRequestError=>", error.toString());
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

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            idPBLoading.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


}