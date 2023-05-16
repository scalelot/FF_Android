package com.festum.festumfield.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.festum.festumfield.Activity.ChatingActivity;
import com.festum.festumfield.Model.ListChat.ListChatsModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_IMAGE_SENT = 1;
    private static final int TYPE_PRODUCT_SENT = 2;
    private static final int TYPE_MESSAGE_RECEIVED = 3;
    private static final int TYPE_IMAGE_RECEIVED = 4;
    private static final int TYPE_PRODUCT_RECEIVED = 5;

    private LayoutInflater inflater;
    private List<JSONObject> chatMessages = new ArrayList<>();
    Activity activity;


    public MessageAdapter(ChatingActivity chatingActivity,LayoutInflater inflater) {
        this.activity = chatingActivity;
        this.inflater = inflater;
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView messageTxt, txt_time;
        RelativeLayout relative;
        View mView;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);

            messageTxt = itemView.findViewById(R.id.sentTxt);
            txt_time = itemView.findViewById(R.id.txt_time);
            relative = itemView.findViewById(R.id.relative);
            mView = itemView;
            itemView.setOnLongClickListener(this);
            relative.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            }
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView right_send_image;
        RelativeLayout send_relative;
        View mView;

        public SentImageHolder(@NonNull View itemView) {
            super(itemView);

            right_send_image = itemView.findViewById(R.id.right_send_image);
            send_relative = itemView.findViewById(R.id.send_relative);
            mView = itemView;
            itemView.setOnLongClickListener(this);
            send_relative.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            }
        }
    }

    private class SentProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView pro_chat_image;
        View mView;
        RelativeLayout relative;
        TextView txt_pro_name, txt_pro_des, txt_pro_price, txt_product;

        public SentProductHolder(View itemView) {
            super(itemView);

            pro_chat_image = itemView.findViewById(R.id.pro_chat_image);
            txt_pro_name = itemView.findViewById(R.id.txt_pro_name);
            txt_pro_des = itemView.findViewById(R.id.txt_pro_des);
            txt_pro_price = itemView.findViewById(R.id.txt_pro_price);
            txt_product = itemView.findViewById(R.id.txt_product);
            relative = itemView.findViewById(R.id.relative);
            mView = itemView;
            itemView.setOnLongClickListener(this);
            relative.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView rec_time, receivedText, recive_name;
        View mView;
        CircleImageView rec_profile_pic;
        RelativeLayout relative;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            rec_time = itemView.findViewById(R.id.rec_time);
            recive_name = itemView.findViewById(R.id.recive_name);
            receivedText = itemView.findViewById(R.id.receivedText);
            relative = itemView.findViewById(R.id.relative);
            rec_profile_pic = itemView.findViewById(R.id.rec_profile_pic);
            mView = itemView;
            itemView.setOnLongClickListener(this);
            relative.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            }
        }
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        View mView;
        TextView nameTxt;
        RelativeLayout relative;

        public ReceivedImageHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            relative = itemView.findViewById(R.id.relative);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            mView = itemView;
            itemView.setOnLongClickListener(this);
            relative.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            }
        }
    }

    private class ReceivedProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        CircleImageView recvice_profile_pic;
        ImageView pro_recvice_image, recvice_seen;
        View mView;
        RelativeLayout relative;
        TextView txt_recvice_name, txt_recvice_des, txt_recvice_price;
        TextView recvice_time, recvice_product, recviceName;

        public ReceivedProductHolder(@NonNull View itemView) {
            super(itemView);

            recvice_profile_pic = itemView.findViewById(R.id.recvice_profile_pic);
            recviceName = itemView.findViewById(R.id.recviceName);
            pro_recvice_image = itemView.findViewById(R.id.pro_recvice_image);
            txt_recvice_name = itemView.findViewById(R.id.txt_recvice_name);
            txt_recvice_des = itemView.findViewById(R.id.txt_recvice_des);
            txt_recvice_price = itemView.findViewById(R.id.txt_recvice_price);
            recvice_product = itemView.findViewById(R.id.recvice_product);
            recvice_seen = itemView.findViewById(R.id.recvice_seen);
            recvice_time = itemView.findViewById(R.id.recvice_time);
            relative = itemView.findViewById(R.id.relative);
            mView = itemView;
            itemView.setOnLongClickListener(this);
            relative.setOnClickListener(this);
        }


        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        JSONObject message = chatMessages.get(position);

        try {
            if (message.getBoolean("isSent")) {

                if (!chatMessages.get(position).getString("message").isEmpty())
                    return TYPE_MESSAGE_SENT;
                else if (!chatMessages.get(position).getString("image").isEmpty())
                    return TYPE_IMAGE_SENT;
                else return TYPE_PRODUCT_SENT;

            } else if (message.getBoolean("isRecive")) {

                if (!chatMessages.get(position).getString("message").isEmpty())
                    return TYPE_MESSAGE_RECEIVED;
                else if (!chatMessages.get(position).getString("image").isEmpty())
                    return TYPE_IMAGE_RECEIVED;
                else return TYPE_PRODUCT_RECEIVED;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {

            case TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent_message, parent, false);
                return new SentMessageHolder(view);

            case TYPE_MESSAGE_RECEIVED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_message, parent, false);
                return new ReceivedMessageHolder(view);

            case TYPE_IMAGE_SENT:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent_image, parent, false);
                return new SentImageHolder(view);

            case TYPE_IMAGE_RECEIVED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_photo, parent, false);
                return new ReceivedImageHolder(view);

            case TYPE_PRODUCT_RECEIVED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recive_product, parent, false);
                return new ReceivedProductHolder(view);

            case TYPE_PRODUCT_SENT:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_product, parent, false);
                return new SentProductHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject message = chatMessages.get(position);
        try {
            if (message.getBoolean("isSent")) {

                if (!chatMessages.get(position).getString("message").isEmpty()) {

                    SentMessageHolder messageHolder = (SentMessageHolder) holder;
                    String str = message.getString("message");
                    if (!str.isEmpty()) {
                        messageHolder.messageTxt.setText(str);
                    }
                    String strTime = String.valueOf(message.getString("Sendtime"));
                    long timestamp = Long.parseLong(strTime) * 1000L;
                    if (String.valueOf(timestamp) != null) {
                        String time = getDate(timestamp);
                        messageHolder.txt_time.setText(time);
                    }

                    if (ChatingActivity.isInActionMode) {
                        if (ChatingActivity.selectionList.contains(chatMessages.get(position))) {
                            messageHolder.mView.setBackgroundResource(R.color.green);
                            messageHolder.relative.setBackgroundColor(activity.getResources().getColor(R.color.green));
                        }
                    }
                } else if (!chatMessages.get(position).getString("image").isEmpty()) {
                    SentImageHolder imageHolder = (SentImageHolder) holder;

                    Glide.with(activity).load(Constans.Display_Image_URL + message.getString("image")).placeholder(R.drawable.ic_user_img).into(imageHolder.right_send_image);

                    if (ChatingActivity.isInActionMode) {
                        if (ChatingActivity.selectionList.contains(chatMessages.get(position))) {
                            imageHolder.mView.setBackgroundResource(R.color.green);
                            imageHolder.send_relative.setBackgroundColor(activity.getResources().getColor(R.color.green));
                        }
                    }
                } else {
                    SentProductHolder sentProductHolder = (SentProductHolder) holder;
                    sentProductHolder.txt_pro_name.setText(message.getString("pro_name"));
                    sentProductHolder.txt_pro_des.setText(message.getString("pro_des"));
                    sentProductHolder.txt_pro_price.setText("$"+message.getString("pro_price")+"."+"00");
                    sentProductHolder.txt_product.setText(message.getString("pro_message"));
                    Glide.with(activity).load(Constans.Display_Image_URL + message.getString("pro_img")).placeholder(R.drawable.ic_user_img).into(sentProductHolder.pro_chat_image);
//                    JSONObject json = new JSONObject(String.valueOf(message.getJSONObject("product")));
//
//                    sentProductHolder.txt_pro_name.setText(json.getString("pro_name"));
//                    sentProductHolder.txt_pro_des.setText(json.getString("pro_des"));
//                    sentProductHolder.txt_pro_price.setText("$"+json.getString("pro_price")+"."+"00");
//                    sentProductHolder.txt_product.setText(json.getString("pro_message"));
//                    Glide.with(activity).load(Constans.Display_Image_URL + json.getString("pro_img")).placeholder(R.drawable.ic_user_img).into(sentProductHolder.pro_chat_image);

                    if (ChatingActivity.isInActionMode) {
                        if (ChatingActivity.selectionList.contains(message)) {
                            sentProductHolder.mView.setBackgroundResource(R.color.green);
                            sentProductHolder.relative.setBackgroundColor(activity.getResources().getColor(R.color.green));
                        }
                    }
                }

            } else if (message.getBoolean("isRecive")) {

                if (!chatMessages.get(position).getString("message").isEmpty()) {

                    ReceivedMessageHolder receivedMessageHolder = (ReceivedMessageHolder) holder;
                    String str = message.getString("message");
                    String str1 = message.getString("name");
                    if (!str.isEmpty()) {
                        receivedMessageHolder.receivedText.setText(str);
                        receivedMessageHolder.recive_name.setText(str1);
                    }

                    String strTime = String.valueOf(message.getString("recivetime"));
                    long timestamp = Long.parseLong(strTime) * 1000L;
                    if (String.valueOf(timestamp) != null) {
                        String time = getDate(timestamp);
                        receivedMessageHolder.rec_time.setText(time);
                    }

                    if (ChatingActivity.isInActionMode) {
                        if (ChatingActivity.selectionList.contains(chatMessages.get(position))) {
                            receivedMessageHolder.mView.setBackgroundResource(R.color.selected_item);
                            receivedMessageHolder.relative.setBackgroundColor(activity.getResources().getColor(R.color.green));

                        }
                    }

                } else if (!chatMessages.get(position).getString("image").isEmpty()) {
                    ReceivedImageHolder imageHolder = (ReceivedImageHolder) holder;
                    imageHolder.nameTxt.setText(message.getString("name"));

                    String img = message.getString("image");

                    Glide.with(activity).load(Constans.Display_Image_URL + img).placeholder(R.drawable.ic_user_img).into(imageHolder.imageView);

                    if (ChatingActivity.isInActionMode) {
                        if (ChatingActivity.selectionList.contains(chatMessages.get(position))) {
                            imageHolder.mView.setBackgroundResource(R.color.selected_item);
                            imageHolder.relative.setBackgroundColor(activity.getResources().getColor(R.color.green));

                        }
                    }
                } else {
                    ReceivedProductHolder receivedProductHolder = (ReceivedProductHolder) holder;
                    receivedProductHolder.txt_recvice_name.setText(message.getString("pro_name"));
                    receivedProductHolder.txt_recvice_des.setText(message.getString("pro_des"));
                    receivedProductHolder.txt_recvice_price.setText("$"+message.getString("pro_price")+"."+"00");
                    receivedProductHolder.recvice_product.setText(message.getString("pro_message"));
                    Glide.with(activity).load(Constans.Display_Image_URL + message.getString("pro_img")).placeholder(R.drawable.ic_user_img).into(receivedProductHolder.pro_recvice_image);

                    if (ChatingActivity.isInActionMode) {
                        if (ChatingActivity.selectionList.contains(chatMessages.get(position))) {
                            receivedProductHolder.mView.setBackgroundResource(R.color.selected_item);
                            receivedProductHolder.relative.setBackgroundColor(activity.getResources().getColor(R.color.green));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getDate(long timeStamp) {
        try {
            DateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    private Bitmap getBitmapFromString(String image) {

        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void addItem(JSONObject jsonObject) {
        chatMessages.add(jsonObject);
        notifyDataSetChanged();
    }

    public void removeData(ArrayList<ListChatsModel> list) {
        for (ListChatsModel model : list) {
            chatMessages.remove(model);
        }
        notifyDataSetChanged();
    }
}