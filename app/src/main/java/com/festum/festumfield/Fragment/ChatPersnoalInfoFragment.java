package com.festum.festumfield.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;

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
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Const;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatPersnoalInfoFragment extends Fragment {

    TextView txt_about, text_birth, text_gender, txt_email, txt_number;
    RelativeLayout rl_authorized, clear_chats, block_hunter, report_hunter;
    RecyclerView recy_tag;
    String str, strIds;
    boolean nameData, numData, emailData, dobData, genderData, mediaData, videoData, audioData;
    SwitchButton name_switch, number_switch, email_switch, birthday_switch, gender_switch, media_switch, video_switch, audio_switch;
    public static ArrayList<String> taglist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_chat_persnoal_info, container, false);

        txt_about = inflate.findViewById(R.id.txt_about);
        text_birth = inflate.findViewById(R.id.text_birth);
        text_gender = inflate.findViewById(R.id.text_gender);
        txt_email = inflate.findViewById(R.id.txt_email);
        txt_number = inflate.findViewById(R.id.txt_number);
        rl_authorized = inflate.findViewById(R.id.rl_authorized);
        clear_chats = inflate.findViewById(R.id.clear_chats);
        block_hunter = inflate.findViewById(R.id.block_hunter);
        report_hunter = inflate.findViewById(R.id.report_hunter);
        recy_tag = inflate.findViewById(R.id.recy_tag);

        strIds = getActivity().getSharedPreferences("ToUserIds", 0).getString("ids", null);
        recy_tag.setLayoutManager(new FlexboxLayoutManager(getContext()));

        if (Const.tag_str != null && !Const.tag_str.isEmpty()) {
            String[] items = Const.tag_str.split(",");

            for (int i = 0; i < items.length; i++) {
                Const.taglist.add(items[i]);
            }

            TagAdapter tagAdapter = new TagAdapter(getActivity(), Const.taglist);
            recy_tag.setAdapter(tagAdapter);
        }


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
                    Toast.makeText(getContext(), "Authorized Permission Done", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(ChatPersnoalInfoFragment.this.getContext());
                    Log.e("AuthorizedPermissionError:--", error.toString());
                }
            });
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
                    startActivity(new Intent(ChatPersnoalInfoFragment.this.getContext(), MainActivity.class));
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
                    map.put("authorization", MyApplication.getAuthToken(getContext()));
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
    }
}