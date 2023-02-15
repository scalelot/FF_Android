package com.example.friendfield.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;

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
    private static final int TYPE_PRODUCT_SENT = 4;
    private static final int TYPE_MESSAGE_RECEIVED = 1;
    private static final int TYPE_IMAGE_SENT = 2;
    private static final int TYPE_IMAGE_RECEIVED = 3;

    private LayoutInflater inflater;
    private List<JSONObject> messages = new ArrayList<>();
    Activity activity;

    public MessageAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView messageTxt, txt_time;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);

            messageTxt = itemView.findViewById(R.id.sentTxt);
            txt_time = itemView.findViewById(R.id.txt_time);
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {

        ImageView send_image;

        public SentImageHolder(@NonNull View itemView) {
            super(itemView);

            send_image = itemView.findViewById(R.id.send_image);
        }
    }

    private class SentProductHolder extends RecyclerView.ViewHolder {

        ImageView pro_chat_image;
        TextView txt_pro_name, txt_pro_des, txt_pro_price, txt_product;

        public SentProductHolder(@NonNull View itemView) {
            super(itemView);

            pro_chat_image = itemView.findViewById(R.id.pro_chat_image);
            txt_pro_name = itemView.findViewById(R.id.txt_pro_name);
            txt_pro_des = itemView.findViewById(R.id.txt_pro_des);
            txt_pro_price = itemView.findViewById(R.id.txt_pro_price);
            txt_product = itemView.findViewById(R.id.txt_product);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView rec_time, receivedText, recive_name;
        CircleImageView rec_profile_pic;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            rec_time = itemView.findViewById(R.id.rec_time);
            recive_name = itemView.findViewById(R.id.recive_name);
            receivedText = itemView.findViewById(R.id.receivedText);
            rec_profile_pic = itemView.findViewById(R.id.rec_profile_pic);
        }
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameTxt;

        public ReceivedImageHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            nameTxt = itemView.findViewById(R.id.nameTxt);

        }
    }

    @Override
    public int getItemViewType(int position) {

        JSONObject message = messages.get(position);

        try {
            if (message.getBoolean("isSent")) {

                if (message.has("message"))
                    return TYPE_MESSAGE_SENT;
                else if (message.has("product"))
                    return TYPE_PRODUCT_SENT;
                else
                    return TYPE_IMAGE_SENT;

            } else if (message.getBoolean("isRecive")) {

                if (message.has("message"))
                    return TYPE_MESSAGE_RECEIVED;
                else
                    return TYPE_IMAGE_RECEIVED;

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
                view = inflater.inflate(R.layout.item_sent_message, parent, false);
                return new SentMessageHolder(view);
            case TYPE_MESSAGE_RECEIVED:

                view = inflater.inflate(R.layout.item_received_message, parent, false);
                return new ReceivedMessageHolder(view);

            case TYPE_IMAGE_SENT:

                view = inflater.inflate(R.layout.item_sent_image, parent, false);
                return new SentImageHolder(view);

            case TYPE_IMAGE_RECEIVED:

                view = inflater.inflate(R.layout.item_received_photo, parent, false);
                return new ReceivedImageHolder(view);

            case TYPE_PRODUCT_SENT:

                view = inflater.inflate(R.layout.item_chat_product, parent, false);
                return new SentProductHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject message = messages.get(position);
        try {
            if (message.getBoolean("isSent")) {

                if (message.has("message")) {

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
                } else if (message.has("product")) {
                    SentProductHolder sentProductHolder = (SentProductHolder) holder;
                    sentProductHolder.txt_pro_name.setText(String.valueOf(message.getString("pro_name")));
                    sentProductHolder.txt_pro_des.setText(message.getString("pro_des"));
                    sentProductHolder.txt_pro_price.setText(message.getString("p_price"));
                    sentProductHolder.txt_product.setText(message.getString("pro_message"));
                    Glide.with(inflater.getContext()).load(Constans.Display_Image_URL + message.getString("pro_img")).placeholder(R.drawable.ic_user_img).into(sentProductHolder.pro_chat_image);
                } else {
                    SentImageHolder imageHolder = (SentImageHolder) holder;
                    Bitmap bitmap = BitmapFactory.decodeFile(message.getString("image"));

                    imageHolder.send_image.setImageBitmap(bitmap);
                }

            } else if (message.getBoolean("isRecive")) {

                if (message.has("message")) {

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

                } else {

                    ReceivedImageHolder imageHolder = (ReceivedImageHolder) holder;
                    imageHolder.nameTxt.setText(message.getString("name"));

//                    Bitmap bitmap = getBitmapFromString(message.getString("image"));
//                    imageHolder.imageView.setImageBitmap(bitmap);
                    String img = message.getString("image");

                    Glide.with(inflater.getContext()).load(Constans.Display_Image_URL + img).placeholder(R.drawable.ic_user_img).into(imageHolder.imageView);
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
        return messages.size();
    }

    public void addItem(JSONObject jsonObject) {
        messages.add(jsonObject);
        notifyDataSetChanged();
    }
}