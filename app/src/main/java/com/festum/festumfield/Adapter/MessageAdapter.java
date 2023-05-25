package com.festum.festumfield.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
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
    List<JSONObject> chatMessages;
    Activity activity;

    public MessageAdapter(ChatingActivity chatingActivity, List<JSONObject> objectList) {
        this.activity = chatingActivity;
        this.chatMessages = objectList;
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
            ((ChatingActivity) activity).prepareToolbar(getAbsoluteAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAbsoluteAdapterPosition());
                notifyItemChanged(getAbsoluteAdapterPosition());
            }
        }

    }

    private class SentImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView right_send_image;
        RelativeLayout send_relative;
        TextView img_right_time;
        View mView;

        public SentImageHolder(@NonNull View itemView) {
            super(itemView);

            right_send_image = itemView.findViewById(R.id.right_send_image);
            send_relative = itemView.findViewById(R.id.send_relative);
            img_right_time = itemView.findViewById(R.id.img_right_time);
            mView = itemView;
            itemView.setOnLongClickListener(this);
            send_relative.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAbsoluteAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAbsoluteAdapterPosition());
                notifyItemChanged(getAbsoluteAdapterPosition());
            }
        }
    }

    private class SentProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView pro_chat_image;
        View mView;
        RelativeLayout relative;
        TextView txt_pro_name, txt_pro_des, txt_pro_price, txt_product;
        TextView pro_right_time;

        public SentProductHolder(View itemView) {
            super(itemView);

            pro_chat_image = itemView.findViewById(R.id.pro_chat_image);
            txt_pro_name = itemView.findViewById(R.id.txt_pro_name);
            txt_pro_des = itemView.findViewById(R.id.txt_pro_des);
            txt_pro_price = itemView.findViewById(R.id.txt_pro_price);
            txt_product = itemView.findViewById(R.id.txt_product);
            relative = itemView.findViewById(R.id.relative);
            pro_right_time = itemView.findViewById(R.id.pro_right_time);
            mView = itemView;
            itemView.setOnLongClickListener(this);
            relative.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAbsoluteAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAbsoluteAdapterPosition());
                notifyItemChanged(getAbsoluteAdapterPosition());
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView left_mess_time, receivedText, recive_name;
        View mView;
        CircleImageView rec_profile_pic;
        RelativeLayout relative;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            left_mess_time = itemView.findViewById(R.id.left_mess_time);
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
            ((ChatingActivity) activity).prepareToolbar(getAbsoluteAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAbsoluteAdapterPosition());
                notifyItemChanged(getAbsoluteAdapterPosition());
            }
        }
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        View mView;
        TextView nameTxt,img_left_time;
        RelativeLayout relative;

        public ReceivedImageHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            relative = itemView.findViewById(R.id.relative);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            img_left_time = itemView.findViewById(R.id.img_left_time);
            itemView.setOnLongClickListener(this);
            relative.setOnClickListener(this);
        }


        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAbsoluteAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAbsoluteAdapterPosition());
                notifyItemChanged(getAbsoluteAdapterPosition());
            }
        }
    }

    private class ReceivedProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        CircleImageView recvice_profile_pic;
        ImageView pro_recvice_image, recvice_seen;
        View mView;
        RelativeLayout relative;
        TextView txt_recvice_name, txt_recvice_des, txt_recvice_price;
        TextView recvice_left_time, recvice_product, recviceName;

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
            recvice_left_time = itemView.findViewById(R.id.recvice_left_time);
            relative = itemView.findViewById(R.id.relative);
            mView = itemView;
            itemView.setOnLongClickListener(this);
            relative.setOnClickListener(this);
        }


        @Override
        public boolean onLongClick(View view) {
            ((ChatingActivity) activity).prepareToolbar(getAbsoluteAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {

            if (ChatingActivity.isInActionMode) {
                ((ChatingActivity) activity).prepareSelection(getAbsoluteAdapterPosition());
                notifyItemChanged(getAbsoluteAdapterPosition());
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

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_product, parent, false);
                return new SentProductHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
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

                    messageHolder.mView.setBackgroundResource(R.color.white);

                    if (ChatingActivity.isInActionMode) {
                        boolean flag1 = ChatingActivity.selectionList.contains(chatMessages.get(position));
                        if (flag1 == true) {
                            messageHolder.mView.setBackgroundResource(R.color.selected_item);
                        }
                    }
                } else if (!chatMessages.get(position).getString("image").isEmpty()) {
                    SentImageHolder imageHolder = (SentImageHolder) holder;

                    Glide.with(activity).load(Constans.Display_Image_URL + message.getString("image")).placeholder(R.drawable.ic_user_img).into(imageHolder.right_send_image);

                    imageHolder.mView.setBackgroundResource(R.color.white);

                    String strTime = String.valueOf(message.getString("Sendtime"));
                    long timestamp = Long.parseLong(strTime) * 1000L;

                    if (String.valueOf(timestamp) != null) {
                        String time = getDate(timestamp);
                        imageHolder.img_right_time.setText(time);
                    }

                    if (ChatingActivity.isInActionMode) {
                        boolean flag1 = ChatingActivity.selectionList.contains(chatMessages.get(position));
                        if (flag1 == true) {
                            imageHolder.mView.setBackgroundResource(R.color.selected_item);
                        }
                    }

                } else {
                    SentProductHolder sentProductHolder = (SentProductHolder) holder;
                    String ids = message.getString("pro_name");

                    sentProductHolder.mView.setBackgroundResource(R.color.white);

                    if (!ids.isEmpty()) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int marginTopDp = 20;
                        int marginRightPx = (int) (marginTopDp * activity.getResources().getDisplayMetrics().density + 0.5f);
                        int marginTopPx = (int) (10 * activity.getResources().getDisplayMetrics().density + 0.5f);
                        layoutParams.setMargins(0, marginTopPx, marginRightPx, 0);
                        sentProductHolder.itemView.setLayoutParams(layoutParams);
                        sentProductHolder.txt_pro_name.setText(message.getString("pro_name"));
                        sentProductHolder.txt_pro_des.setText(message.getString("pro_des"));
                        sentProductHolder.txt_pro_price.setText("$" + message.getString("pro_price") + "." + "00");
                        sentProductHolder.txt_product.setText(message.getString("pro_message"));
                        String str = message.getString("pro_message");
                        System.out.println("Messages:==" + str);
                        Glide.with(activity).load(Constans.Display_Image_URL + message.getString("pro_img")).placeholder(R.drawable.ic_user_img).into(sentProductHolder.pro_chat_image);
                    } else {
                        sentProductHolder.itemView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                    }

                    String strTime = String.valueOf(message.getString("Sendtime"));
                    long timestamp = Long.parseLong(strTime) * 1000L;

                    if (String.valueOf(timestamp) != null) {
                        String time = getDate(timestamp);
                        sentProductHolder.pro_right_time.setText(time);
                    }

                    if (ChatingActivity.isInActionMode) {
                        boolean flag1 = ChatingActivity.selectionList.contains(chatMessages.get(position));
                        if (flag1 == true) {
                            sentProductHolder.mView.setBackgroundResource(R.color.selected_item);
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
                        receivedMessageHolder.left_mess_time.setText(time);
                    }

                    receivedMessageHolder.mView.setBackgroundResource(R.color.white);

                    if (ChatingActivity.isInActionMode) {
                        boolean flag1 = ChatingActivity.selectionList.contains(chatMessages.get(position));
                        if (flag1 == true) {
                            receivedMessageHolder.mView.setBackgroundResource(R.color.selected_item);
                        }
                    }

                } else if (!chatMessages.get(position).getString("image").isEmpty()) {
                    ReceivedImageHolder imageHolder = (ReceivedImageHolder) holder;
                    imageHolder.nameTxt.setText(message.getString("name"));

                    String img = message.getString("image");

                    Glide.with(activity).load(Constans.Display_Image_URL + img).placeholder(R.drawable.ic_user_img).into(imageHolder.imageView);

                    imageHolder.mView.setBackgroundResource(R.color.white);

                    String strTime = String.valueOf(message.getString("Sendtime"));
                    long timestamp = Long.parseLong(strTime) * 1000L;

                    if (String.valueOf(timestamp) != null) {
                        String time = getDate(timestamp);
                        imageHolder.img_left_time.setText(time);
                    }

                    if (ChatingActivity.isInActionMode) {
                        boolean flag1 = ChatingActivity.selectionList.contains(chatMessages.get(position));
                        if (flag1 == true) {
                            imageHolder.mView.setBackgroundResource(R.color.selected_item);
                        }
                    }

                } else {
                    ReceivedProductHolder receivedProductHolder = (ReceivedProductHolder) holder;
                    String ids = message.getString("pro_name");
                    receivedProductHolder.mView.setBackgroundResource(R.color.white);
                    if (!ids.isEmpty()) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int marginTopDp = 20;
                        int marginRightPx = (int) (marginTopDp * activity.getResources().getDisplayMetrics().density + 0.5f);
                        int marginTopPx = (int) (10 * activity.getResources().getDisplayMetrics().density + 0.5f);
                        layoutParams.setMargins(0, marginTopPx, marginRightPx, 0);
                        receivedProductHolder.txt_recvice_name.setText(message.getString("pro_name"));
                        receivedProductHolder.txt_recvice_des.setText(message.getString("pro_des"));
                        receivedProductHolder.txt_recvice_price.setText("$" + message.getString("pro_price") + "." + "00");
                        receivedProductHolder.recvice_product.setText(message.getString("pro_message"));
                        Glide.with(activity).load(Constans.Display_Image_URL + message.getString("pro_img")).placeholder(R.drawable.ic_user_img).into(receivedProductHolder.pro_recvice_image);
                    } else {
                        receivedProductHolder.itemView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                    }

                    String strTime = String.valueOf(message.getString("Sendtime"));
                    long timestamp = Long.parseLong(strTime) * 1000L;

                    if (String.valueOf(timestamp) != null) {
                        String time = getDate(timestamp);
                        receivedProductHolder.recvice_left_time.setText(time);
                    }

                    if (ChatingActivity.isInActionMode) {
                        for (int i = 0; i < chatMessages.size(); i++) {
                            boolean flag1 = ChatingActivity.selectionList.contains(message.getString("pro_ids"));
                            if (flag1 == true) {
                                receivedProductHolder.mView.setBackgroundResource(R.color.selected_item);
                            }
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

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void removeData(ArrayList<ListChatsModel> list) {
        for (ListChatsModel model : list) {
            chatMessages.remove(model);
        }
        notifyDataSetChanged();
    }
}