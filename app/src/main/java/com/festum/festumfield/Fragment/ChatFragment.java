package com.festum.festumfield.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
//import android.widget.ProgressBar;
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
import com.festum.festumfield.Activity.MarketingNotificationActivity;
import com.festum.festumfield.Adapter.ChatUserAdapter;
import com.festum.festumfield.Model.AllMyFriends.AllFriendsRegisterModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Activity.ReelsActivity;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatFragment extends Fragment {

    RecyclerView chat_recyclerview;
    ChatUserAdapter chatUserAdapter;
    RelativeLayout ll_notification;
    FloatingActionButton fb_reels;
    RelativeLayout iv_filter, emptyLay;
    ProgressBar progressBar;
    EditText edt_search_text;
    ImageView iv_search;
    ImageView iv_clear_text;
    int page = 1, limit = 10;
    String searchData = "";
    NestedScrollView nestedScrollView;
    View view;
    ArrayList<AllFriendsRegisterModel> receivefriendrequestsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_chat, container, false);

        chat_recyclerview = view.findViewById(R.id.chat_recyclerview);
        ll_notification = view.findViewById(R.id.ll_notification);
        fb_reels = view.findViewById(R.id.fb_reels);
        iv_filter = view.findViewById(R.id.iv_filter);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        edt_search_text = view.findViewById(R.id.edt_search_text);
        iv_search = view.findViewById(R.id.iv_search);
        iv_clear_text = view.findViewById(R.id.iv_clear_text);
        emptyLay = view.findViewById(R.id.emptyLay);
        progressBar = view.findViewById(R.id.idPBLoading);

        getAllMyFriends(page, limit, searchData);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getAllMyFriends(page, limit, searchData);
                }
            }
        });

        iv_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MarketingNotificationActivity.class));
                getActivity().finish();
            }
        });

        fb_reels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ReelsActivity.class));
                getActivity().finish();
            }
        });

        edt_search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        edt_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    iv_clear_text.setVisibility(View.GONE);
                    iv_search.setVisibility(View.VISIBLE);
                } else {
                    iv_clear_text.setVisibility(View.VISIBLE);
                    iv_search.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                filter(text);
                iv_search.setVisibility(View.GONE);
                iv_clear_text.setVisibility(View.VISIBLE);
            }
        });

        iv_clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search_text.setText("");
                iv_clear_text.setVisibility(View.GONE);
                iv_search.setVisibility(View.VISIBLE);
            }
        });

        chat_recyclerview.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
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

    private void getAllMyFriends(int page, int limit, String search) {
        if (page > limit) {
            Toast.makeText(getContext(), "That's all the data..", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
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
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.all_myfriend, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("GetAllMyFriends:--", response.toString());
                    try {
                        JSONObject dataJsonObject = response.getJSONObject("Data");

                        JSONArray data_array = dataJsonObject.getJSONArray("docs");

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject jsonObject = data_array.getJSONObject(i);

                            AllFriendsRegisterModel productDetailsModel = new Gson().fromJson(jsonObject.toString(), AllFriendsRegisterModel.class);
                            receivefriendrequestsModelArrayList.add(productDetailsModel);
                        }

                        if (!receivefriendrequestsModelArrayList.isEmpty()) {
                            emptyLay.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.VISIBLE);
                        } else {
                            emptyLay.setVisibility(View.VISIBLE);
                            nestedScrollView.setVisibility(View.GONE);
                        }

                        chatUserAdapter = new ChatUserAdapter(ChatFragment.this, receivefriendrequestsModelArrayList);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        chat_recyclerview.setLayoutManager(manager);
                        chat_recyclerview.setAdapter(chatUserAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("GetAllMyFriendsError" + error.toString());
                    progressBar.setVisibility(View.GONE);

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
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);

        }
    }

    public void showFilterDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.filter_dialog);

        CheckBox check_contact_fnd = bottomSheetDialog.findViewById(R.id.check_contact_fnd);
        CheckBox check_app_fnd = bottomSheetDialog.findViewById(R.id.check_app_fnd);
        CheckBox check_receiver_fnd = bottomSheetDialog.findViewById(R.id.check_receiver_fnd);
        CheckBox check_sender_fnd = bottomSheetDialog.findViewById(R.id.check_sender_fnd);

        AppCompatButton btn_cancel = bottomSheetDialog.findViewById(R.id.btn_cancel);
        AppCompatButton btn_apply = bottomSheetDialog.findViewById(R.id.btn_apply);
        ImageView filter_close = bottomSheetDialog.findViewById(R.id.filter_close);

        filter_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        check_contact_fnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });

        check_app_fnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });

        check_receiver_fnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });

        check_sender_fnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });


        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.show();
    }

    private void filter(String text) {
        ArrayList<AllFriendsRegisterModel> filteredlist = new ArrayList<AllFriendsRegisterModel>();

        for (AllFriendsRegisterModel item : receivefriendrequestsModelArrayList) {
            if (item.getFullName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            chatUserAdapter.filterList(filteredlist);
        }
    }

}