package com.festum.festumfield.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.festum.festumfield.Activity.BlockedContactActivity;
import com.festum.festumfield.Model.BlockedFriends.BlockedFriendRegisterModel;
import com.festum.festumfield.Model.BlockedFriends.BlockedFriendsModel;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockedContactAdapter extends RecyclerView.Adapter<BlockedContactAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<BlockedFriendRegisterModel> blockedFriendRegisterModels;
    LayoutInflater inflater;

    public BlockedContactAdapter(BlockedContactActivity blockedContactActivity, ArrayList<BlockedFriendRegisterModel> blockedFriendRegisterModels) {
        this.activity = blockedContactActivity;
        this.blockedFriendRegisterModels = blockedFriendRegisterModels;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.blocked_contact_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(activity).load(Constans.Display_Image_URL + blockedFriendRegisterModels.get(position).getProfileimage()).placeholder(R.drawable.ic_user_img).into(holder.circleImageView);

        holder.txt_contact.setText(blockedFriendRegisterModels.get(position).getContactNo());

        String strIds = blockedFriendRegisterModels.get(position).getRequestId();

        holder.btn_unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUnblockUser(strIds);
                BlockedContactActivity.removeAt(holder.getAdapterPosition());
            }
        });
    }



    private void setUnblockUser(String strIds) {
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("friendrequestid", strIds);
        stringHashMap.put("status", "unblocked");

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constans.set_friends_unblock, new JSONObject(stringHashMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("UnBlockUser=>", response.toString());

                    BlockedFriendsModel blockedFriendsModel = new Gson().fromJson(response.toString(), BlockedFriendsModel.class);
                    Toast.makeText(activity, "Unblock user Successfully!!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("UnBlockUser_Error=>", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("authorization", MyApplication.getAuthToken(activity));
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return blockedFriendRegisterModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txt_contact;
        AppCompatButton btn_unblock;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.contact_user);
            txt_contact = itemView.findViewById(R.id.txt_contact);
            btn_unblock = itemView.findViewById(R.id.btn_unblock);
        }
    }
}