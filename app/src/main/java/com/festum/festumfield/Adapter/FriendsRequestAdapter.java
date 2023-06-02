package com.festum.festumfield.Adapter;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.festum.festumfield.Fragment.FriendRequestFragment;
import com.festum.festumfield.Model.ReceiveFriendsList.ReceiveFriendsRegisterModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.Utils.FileUtils;
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRequestAdapter extends RecyclerView.Adapter<FriendsRequestAdapter.MyDataHolder> {

    Fragment fragment;
    ArrayList<ReceiveFriendsRegisterModel> receiveFriendsRequestModels;
    LayoutInflater inflater;
    String freindsIds;
    String status;
    Dialog rejectdialog, acceptdialog;
    boolean nameData, numData, emailData, dobData, genderData, mediaData, videoData, audioData;
    SwitchButton name_switch, number_switch, email_switch, dob_switch, gender_switch, media_switch, video_switch, audio_switch;

    public FriendsRequestAdapter(FriendRequestFragment friendRequestFragment, ArrayList<ReceiveFriendsRegisterModel> receivefriendrequestsModelArrayList) {
        this.fragment = friendRequestFragment;
        this.receiveFriendsRequestModels = receivefriendrequestsModelArrayList;
        inflater = LayoutInflater.from(fragment.getContext());
    }

    @NonNull
    @Override
    public MyDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.request_item, parent, false);
        return new MyDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(fragment).load(Constans.Display_Image_URL + receiveFriendsRequestModels.get(position).getProfileimage()).placeholder(R.drawable.ic_user_img).into(holder.request_image);

        holder.text_username.setText(receiveFriendsRequestModels.get(position).getFullName());
        holder.text_dis.setText(receiveFriendsRequestModels.get(position).getRequestMessage());

        String fName = receiveFriendsRequestModels.get(position).getFullName();
        String num = receiveFriendsRequestModels.get(position).getContactNo();
        String email = receiveFriendsRequestModels.get(position).getEmailId();
        String dob = receiveFriendsRequestModels.get(position).getDob();
        String gen = receiveFriendsRequestModels.get(position).getGender();

        String strTime = String.valueOf(receiveFriendsRequestModels.get(position).getTimestamp());
        long timestamp = Long.parseLong(strTime) * 1000L;
        if (String.valueOf(timestamp) != null) {
            String time = getFormattedDate(timestamp);
            holder.recive_text_time.setText(time);
        }

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freindsIds = receiveFriendsRequestModels.get(position).getRequestId();
                acceptDialog(freindsIds, fName, num, email, dob, gen);
                removeAt(holder.getAdapterPosition());

            }
        });

        holder.btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freindsIds = receiveFriendsRequestModels.get(position).getRequestId();
                rejectDialog(freindsIds);
                removeAt(holder.getAdapterPosition());
            }
        });
    }

    private void removeAt(int adapterPosition) {
        receiveFriendsRequestModels.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyItemRangeChanged(adapterPosition, receiveFriendsRequestModels.size());
        notifyDataSetChanged();
    }

    public String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.US);
        Date netDate = (new Date(smsTimeInMilis));
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return sdf.format(netDate);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return android.text.format.DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return android.text.format.DateFormat.format("dd/MM/yy", smsTime).toString();
        }
    }

    @Override
    public int getItemCount() {
        return receiveFriendsRequestModels.size();
    }

    class MyDataHolder extends RecyclerView.ViewHolder {

        AppCompatButton btn_accept, btn_reject;
        TextView text_username, recive_text_time, text_dis;
        CircleImageView request_image;

        public MyDataHolder(@NonNull View itemView) {
            super(itemView);

            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_reject = itemView.findViewById(R.id.btn_reject);
            text_username = itemView.findViewById(R.id.text_username);
            recive_text_time = itemView.findViewById(R.id.recive_text_time);
            text_dis = itemView.findViewById(R.id.text_dis);
            request_image = itemView.findViewById(R.id.request_image);
        }
    }

    public void acceptDialog(String freindsIds, String fName, String num, String email, String dob, String gender) {
        acceptdialog = new Dialog(fragment.getActivity());
        View view = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.request_accept_dialog, null);
        acceptdialog.setContentView(view);
        acceptdialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(back, 50);
        acceptdialog.getWindow().setBackgroundDrawable(insetDrawable);
        acceptdialog.setCanceledOnTouchOutside(true);

        ImageView btn_close = acceptdialog.findViewById(R.id.btn_close);

        TextView txt_user = acceptdialog.findViewById(R.id.txt_user);
        TextView txt_ph_mo = acceptdialog.findViewById(R.id.txt_ph_mo);
        TextView txt_email = acceptdialog.findViewById(R.id.txt_email);
        TextView text_birthday = acceptdialog.findViewById(R.id.text_birthday);
        TextView text_gender = acceptdialog.findViewById(R.id.text_gender);

        txt_user.setText(fName);
        txt_ph_mo.setText(num);
        txt_email.setText(email);
        text_birthday.setText(dob);
        text_gender.setText(gender);

        name_switch = acceptdialog.findViewById(R.id.name_switch);
        number_switch = acceptdialog.findViewById(R.id.number_switch);
        email_switch = acceptdialog.findViewById(R.id.email_switch);
        dob_switch = acceptdialog.findViewById(R.id.dob_switch);
        gender_switch = acceptdialog.findViewById(R.id.gender_switch);
        media_switch = acceptdialog.findViewById(R.id.media_switch);
        video_switch = acceptdialog.findViewById(R.id.video_switch);
        audio_switch = acceptdialog.findViewById(R.id.audio_switch);

        AppCompatButton button_cancle = acceptdialog.findViewById(R.id.button_cancle);
        AppCompatButton button_accept = acceptdialog.findViewById(R.id.button_accpet);


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptdialog.dismiss();
            }
        });

        button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.DisplayLoading(fragment.getContext());
                status = "accepted";
                setFriendsResponseApi(freindsIds, status);
                acceptdialog.dismiss();
            }
        });

        button_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptdialog.dismiss();
            }
        });
        acceptdialog.show();

    }

    public void rejectDialog(String freindsIds) {
        rejectdialog = new Dialog(fragment.getActivity());
        View view = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.reject_dialog, null);
        rejectdialog.setContentView(view);

        rejectdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        rejectdialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(back, 50);
        rejectdialog.getWindow().setBackgroundDrawable(insetDrawable);

        ImageView dialog_close = rejectdialog.findViewById(R.id.dialog_close);
        AppCompatButton button_reject = rejectdialog.findViewById(R.id.button_reject);
        AppCompatButton button_block = rejectdialog.findViewById(R.id.button_block);

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectdialog.dismiss();
            }
        });

        button_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = "rejected";
                setFriendsResponseApi(freindsIds, status);
                rejectdialog.dismiss();
            }
        });

        button_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.DisplayLoading(fragment.getContext());
                status = "blocked";
                setFriendsResponseApi(freindsIds, status);
                rejectdialog.dismiss();
            }
        });

        rejectdialog.show();
    }


    private void setFriendsResponseApi(String freindsIds, String status) {
        JSONObject js = new JSONObject();
        try {
            js.put("friendrequestid", freindsIds);
            js.put("status", status);

            JSONObject jsonobject_one = new JSONObject();

            if (status == "rejected" && status == "blocked") {
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

                if (dob_switch.isChecked()) {
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.response_friend_request, js, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FileUtils.DismissLoading(fragment.getContext());
                    Log.e("LL_friends_response", response.toString());

                    FriendsRequestAdapter.this.notifyDataSetChanged();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FileUtils.DismissLoading(fragment.getContext());
                    System.out.println("friends_response_error:-- " + error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("authorization", MyApplication.getAuthToken(fragment.getContext()));
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(fragment.getContext());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtils.DismissLoading(fragment.getContext());
        }
    }
}
