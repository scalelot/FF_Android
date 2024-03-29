package com.festum.festumfield.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.festum.festumfield.Adapter.TagAdapter;
import com.festum.festumfield.Model.AllMyFriends.AllFriendsRegisterModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.festum.festumfield.verstion.firstmodule.screens.main.HomeActivity;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatPersnoalInfoFragment extends Fragment {

    TextView txt_about, text_birth, text_gender, txt_email, txt_number;
    RelativeLayout rl_authorized, clear_chats, block_hunter, report_hunter;
    RecyclerView recyper_tag;
    NestedScrollView chatNested;
    String str, strIds;
    int page = 1, limit = 10;
    String searchData = "";
    boolean nameData, numData, emailData, dobData, genderData, mediaData, videoData, audioData;
    SwitchButton name_switch, number_switch, email_switch, birthday_switch, gender_switch, media_switch, video_switch, audio_switch;
    public static ArrayList<String> tags = new ArrayList<>();
    public static ArrayList<String> taglist = new ArrayList<>();
    ArrayList<AllFriendsRegisterModel> allUserDataList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_chat_persnoal_info, container, false);

        chatNested = inflate.findViewById(R.id.chatNested);
        txt_about = inflate.findViewById(R.id.txt_about);
        text_birth = inflate.findViewById(R.id.text_birth);
        text_gender = inflate.findViewById(R.id.text_gender);
        txt_email = inflate.findViewById(R.id.txt_email);
        txt_number = inflate.findViewById(R.id.txt_number);
        rl_authorized = inflate.findViewById(R.id.rl_authorized);
        clear_chats = inflate.findViewById(R.id.clear_chats);
        block_hunter = inflate.findViewById(R.id.block_hunter);
        report_hunter = inflate.findViewById(R.id.report_hunter);
        recyper_tag = inflate.findViewById(R.id.recyper_tag);

        strIds = getActivity().getSharedPreferences("ToUserIds", 0).getString("ids", null);
        recyper_tag.setLayoutManager(new FlexboxLayoutManager(getContext()));
        chatNested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    getUserDatas(page, limit, searchData);
                }
            }
        });

        rl_authorized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorizedDialog();
            }
        });

        block_hunter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtils.DisplayLoading(ChatPersnoalInfoFragment.this.getContext());
                str = "blocked";
                set_unfriendorblock(strIds, str);
            }
        });

        return inflate;
    }

    public void getUserDatas(int page, int limit, String searchData) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("page", page);
            jsonObject.put("limit", limit);
            jsonObject.put("search", searchData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.all_myfriend, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("AllUserData:-", response.toString());
                    try {
                        JSONObject jsonObject1 = response.getJSONObject("Data");
                        JSONArray data_array = jsonObject1.getJSONArray("docs");
                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject jsonObject = data_array.getJSONObject(i);

                            AllFriendsRegisterModel productDetailsModel = new Gson().fromJson(jsonObject.toString(), AllFriendsRegisterModel.class);
                            allUserDataList.add(productDetailsModel);
                        }

                        if (!taglist.isEmpty()) {
                            taglist.clear();
                        }

                        for (int j = 0; j < allUserDataList.size(); j++) {
                            if (allUserDataList.get(j).getId().equals(strIds)){
                                txt_about.setText(allUserDataList.get(j).getAboutUs());
                                text_birth.setText(allUserDataList.get(j).getDob());
                                text_gender.setText(allUserDataList.get(j).getGender());
                                txt_email.setText(allUserDataList.get(j).getEmailId());
                                txt_number.setText(allUserDataList.get(j).getContactNo());
                                for (int k=0;k<allUserDataList.get(j).getHobbies().size();k++){
                                    taglist.add(allUserDataList.get(j).getHobbies().get(k));
                                }
                            }
                        }

                        TagAdapter tagAdapter = new TagAdapter(getActivity(), taglist);
                        recyper_tag.setAdapter(tagAdapter);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("AllUserData:-", String.valueOf(error));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("authorization", AppPreferencesDelegates.Companion.get().getToken());
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void authorizedDialog() {
        Dialog dialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.authorized_dialog, null);
        dialog.setContentView(view);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(colorDrawable, 80);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);

        dialog.setCanceledOnTouchOutside(false);

        ImageView btn_close = dialog.findViewById(R.id.btn_close);

        TextView txt_user = dialog.findViewById(R.id.txt_user);
        TextView txt_ph_mo = dialog.findViewById(R.id.txt_ph_mo);
        TextView txt_email = dialog.findViewById(R.id.txt_email);

        name_switch = dialog.findViewById(R.id.name_switch);
        number_switch = dialog.findViewById(R.id.number_switch);
        email_switch = dialog.findViewById(R.id.email_switch);
        birthday_switch = dialog.findViewById(R.id.birthday_switch);
        gender_switch = dialog.findViewById(R.id.gender_switch);
        media_switch = dialog.findViewById(R.id.media_switch);
        video_switch = dialog.findViewById(R.id.video_switch);
        audio_switch = dialog.findViewById(R.id.audio_switch);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.DisplayLoading(ChatPersnoalInfoFragment.this.getContext());
                setauthorizedDialog(strIds);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setauthorizedDialog(String strIds) {
        JSONObject js = new JSONObject();
        try {
            js.put("friendid", strIds);

            JSONObject jsonobject_one = new JSONObject();

            if (name_switch.isChecked()) {
                nameData = true;
                jsonobject_one.put("fullname", nameData);
            } else {
                nameData = false;
                jsonobject_one.put("fullname", nameData);
            }

            if (number_switch.isChecked()) {
                numData = true;
                jsonobject_one.put("contactnumber", numData);
            } else {
                numData = false;
                jsonobject_one.put("contactnumber", numData);
            }

            if (email_switch.isChecked()) {
                emailData = true;
                jsonobject_one.put("email", emailData);
            } else {
                emailData = false;
                jsonobject_one.put("email", emailData);
            }

            if (birthday_switch.isChecked()) {
                dobData = true;
                jsonobject_one.put("dob", dobData);
            } else {
                dobData = false;
                jsonobject_one.put("dob", dobData);
            }

            if (gender_switch.isChecked()) {
                genderData = true;
                jsonobject_one.put("gender", genderData);
            } else {
                genderData = false;
                jsonobject_one.put("gender", genderData);
            }

            if (media_switch.isChecked()) {
                mediaData = true;
                jsonobject_one.put("socialmedia", mediaData);
            } else {
                mediaData = false;
                jsonobject_one.put("socialmedia", mediaData);
            }

            if (video_switch.isChecked()) {
                videoData = true;
                jsonobject_one.put("videocall", videoData);
            } else {
                videoData = false;
                jsonobject_one.put("videocall", videoData);
            }

            if (audio_switch.isChecked()) {
                audioData = true;
                jsonobject_one.put("audiocall", audioData);
            } else {
                audioData = false;
                jsonobject_one.put("audiocall", audioData);
            }
            js.put("authorized_permissions", jsonobject_one);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.set_authorized_permissions, js, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(ChatPersnoalInfoFragment.this.getContext());
                    Log.e("AuthorizedPermission:--", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(ChatPersnoalInfoFragment.this.getContext());
                    Log.e("AuthorizedPermissionError:--", error.toString());
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> map = new HashMap<>();
                    map.put("authorization",AppPreferencesDelegates.Companion.get().getToken());
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            FileUtils.DismissLoading(ChatPersnoalInfoFragment.this.getContext());
            e.printStackTrace();
        }
    }

    public void set_unfriendorblock(String strIds, String str) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("friendid", strIds);
            hashMap.put("status", str);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.set_friends_unfriendorblock, new JSONObject(hashMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(ChatPersnoalInfoFragment.this.getContext());
                    Log.e("BlockFriends:--", response.toString());
                    startActivity(new Intent(ChatPersnoalInfoFragment.this.getContext(), HomeActivity.class));
                    getActivity().finish();
                    Toast.makeText(getContext(), "Block Successfully!!!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(ChatPersnoalInfoFragment.this.getContext());
                    System.out.println("BlockFriendsError:--" + error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("authorization", AppPreferencesDelegates.Companion.get().getToken());
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            FileUtils.DismissLoading(ChatPersnoalInfoFragment.this.getContext());
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDatas(page, limit, searchData);
    }
}