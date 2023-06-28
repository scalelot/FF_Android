package com.festum.festumfield.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.festum.festumfield.Activity.ChatingActivity;
import com.festum.festumfield.Model.ListChat.ListChatsModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_IMAGE_SENT = 1;
    private static final int TYPE_PRODUCT_SENT = 2;
    List<JSONObject> chatMessages;
    Activity activity;

    public MessageAdapter(ChatingActivity chatingActivity, List<JSONObject> objectList) {
        this.activity = chatingActivity;
        this.chatMessages = objectList;
    }

    private class SendMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView sendTxt, sendTxtTime, reciveTxt, reciveTxtTime, reciveUserName;
        RelativeLayout rl_right,rl_left;
        LinearLayout textRelative;
        CircleImageView reciveImgUser;
        View mView;

        public SendMessageHolder(@NonNull View itemView) {
            super(itemView);

            sendTxt = itemView.findViewById(R.id.sendTxt);
            sendTxtTime = itemView.findViewById(R.id.sendTxtTime);
            reciveTxt = itemView.findViewById(R.id.reciveTxt);
            reciveTxtTime = itemView.findViewById(R.id.reciveTxtTime);
            reciveUserName = itemView.findViewById(R.id.reciveUserName);
            reciveImgUser = itemView.findViewById(R.id.reciveImgUser);
            textRelative = itemView.findViewById(R.id.textRelative);
            rl_right = itemView.findViewById(R.id.rl_right);
            rl_left = itemView.findViewById(R.id.rl_left);

            mView = itemView;
            itemView.setOnLongClickListener(this);
            textRelative.setOnClickListener(this);
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

    private class SendImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView sendImage, reciveImg;
        RelativeLayout relativeRight, relativeLeft;
        LinearLayout imageRelative;
        TextView sendImgTime, reciveImgTime, reciveUserTxt;
        CircleImageView reciveUserImg;
        View mView;

        public SendImageHolder(@NonNull View itemView) {
            super(itemView);

            sendImage = itemView.findViewById(R.id.sendImage);
            reciveImg = itemView.findViewById(R.id.reciveImg);
            imageRelative = itemView.findViewById(R.id.imageRelative);
            sendImgTime = itemView.findViewById(R.id.sendImgTime);
            reciveImgTime = itemView.findViewById(R.id.reciveImgTime);
            reciveUserTxt = itemView.findViewById(R.id.reciveUserTxt);
            reciveUserImg = itemView.findViewById(R.id.reciveUserImg);
            relativeRight = itemView.findViewById(R.id.relativeRight);
            relativeLeft = itemView.findViewById(R.id.relativeLeft);

            mView = itemView;
            itemView.setOnLongClickListener(this);
            imageRelative.setOnClickListener(this);
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

    private class SendProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView sendProname, sendProdes, sendProprice, sendMessage, sendProtime;
        ImageView sendProImage;
        RelativeLayout sendRelative, reciveRelative;
        LinearLayout productRelative;
        CircleImageView recvice_profile_pic;
        TextView recviceProUserName, recviceProName, recviceDes, recvicePrice, recviceMessage, recviceProTime;
        ImageView recvice_image;
        View mView;

        public SendProductHolder(View itemView) {
            super(itemView);

            sendProname = itemView.findViewById(R.id.sendProname);
            sendProdes = itemView.findViewById(R.id.sendProdes);
            sendProprice = itemView.findViewById(R.id.sendProprice);
            sendMessage = itemView.findViewById(R.id.sendMessage);
            sendProtime = itemView.findViewById(R.id.sendProtime);
            sendProImage = itemView.findViewById(R.id.sendProImage);
            productRelative = itemView.findViewById(R.id.productRelative);
            recvice_profile_pic = itemView.findViewById(R.id.recvice_profile_pic);
            recviceProUserName = itemView.findViewById(R.id.recviceProUserName);
            recviceProName = itemView.findViewById(R.id.recviceProName);
            recviceDes = itemView.findViewById(R.id.recviceDes);
            recvicePrice = itemView.findViewById(R.id.recvicePrice);
            recviceMessage = itemView.findViewById(R.id.recviceMessage);
            recviceProTime = itemView.findViewById(R.id.recviceProTime);
            recvice_image = itemView.findViewById(R.id.recvice_image);
            sendRelative = itemView.findViewById(R.id.sendRelative);
            reciveRelative = itemView.findViewById(R.id.reciveRelative);

            mView = itemView;
            itemView.setOnLongClickListener(this);
            productRelative.setOnClickListener(this);
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
        try {
            if (!chatMessages.get(position).getString("message").isEmpty())
                return TYPE_MESSAGE_SENT;
            else if (!chatMessages.get(position).getString("image").isEmpty())
                return TYPE_IMAGE_SENT;
            else return TYPE_PRODUCT_SENT;
        } catch (Exception e) {
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
                return new SendMessageHolder(view);

            case TYPE_IMAGE_SENT:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent_image, parent, false);
                return new SendImageHolder(view);

            case TYPE_PRODUCT_SENT:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_product, parent, false);
                return new SendProductHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject message = chatMessages.get(position);

        try {
            if (!chatMessages.get(position).getString("message").isEmpty()) {
                SendMessageHolder sendMessageHolder = (SendMessageHolder) holder;
                sendMessageHolder.mView.setBackgroundResource(R.color.app_bg);
                if (!message.getBoolean("isSent")) {
                    sendMessageHolder.rl_right.setVisibility(View.GONE);
                    sendMessageHolder.rl_left.setVisibility(View.VISIBLE);

                    sendMessageHolder.reciveTxt.setText(message.getString("message"));
                    sendMessageHolder.reciveUserName.setText(message.getString("name"));
                    String strTime = message.getString("recivetime");
                    sendMessageHolder.reciveTxtTime.setText(getDate(strTime));

                    String pImg = message.getString("userProfileImg");

                    Glide.with(activity).load(Constans.Display_Image_URL + pImg).placeholder(R.drawable.ic_user_img).into(sendMessageHolder.reciveImgUser);
                } else {
                    sendMessageHolder.rl_right.setVisibility(View.VISIBLE);
                    sendMessageHolder.rl_left.setVisibility(View.GONE);


                    sendMessageHolder.sendTxt.setText(message.getString("message"));
                    String strTime = message.getString("Sendtime");
                    sendMessageHolder.sendTxtTime.setText(getDate(strTime));
                }

                if (ChatingActivity.isInActionMode) {
                    boolean flag1 = ChatingActivity.selectionList.contains(chatMessages.get(position));
                    if (flag1 == true) {
                        sendMessageHolder.mView.setBackgroundResource(R.color.selected_item);
                    }
                }
            } else if (!chatMessages.get(position).getString("image").isEmpty()) {
                SendImageHolder sendImageHolder = (SendImageHolder) holder;
                sendImageHolder.mView.setBackgroundResource(R.color.app_bg);
                if (!message.getBoolean("isSent")) {
                    sendImageHolder.relativeRight.setVisibility(View.GONE);
                    sendImageHolder.relativeLeft.setVisibility(View.VISIBLE);

                    sendImageHolder.reciveUserTxt.setText(message.getString("name"));

                    String img = message.getString("image");
                    String pImg = message.getString("userProfileImg");

                    Glide.with(activity).load(Constans.Display_Image_URL + img).placeholder(R.drawable.ic_user_img).into(sendImageHolder.reciveImg);
                    Glide.with(activity).load(Constans.Display_Image_URL + pImg).placeholder(R.drawable.ic_user_img).into(sendImageHolder.reciveUserImg);

                    String strTime = String.valueOf(message.getString("recivetime"));
                    sendImageHolder.reciveImgTime.setText(getDate(strTime));

                } else {
                    sendImageHolder.relativeRight.setVisibility(View.VISIBLE);
                    sendImageHolder.relativeLeft.setVisibility(View.GONE);

                    if (message.getString("image").startsWith("/storage")) {
                        Glide.with(activity).load(message.getString("image")).placeholder(R.drawable.ic_user_img).into(sendImageHolder.sendImage);
                    } else {
                        Glide.with(activity).load(Constans.Display_Image_URL + message.getString("image")).placeholder(R.drawable.ic_user_img).into(sendImageHolder.sendImage);

                    }
                    String strTime = String.valueOf(message.getString("Sendtime"));
                    sendImageHolder.sendImgTime.setText(getDate(strTime));
                }

                if (ChatingActivity.isInActionMode) {
                    boolean flag1 = ChatingActivity.selectionList.contains(chatMessages.get(position));
                    if (flag1 == true) {
                        sendImageHolder.mView.setBackgroundResource(R.color.selected_item);
                    }
                }
            } else {
                SendProductHolder sendProductHolder = (SendProductHolder) holder;
                sendProductHolder.mView.setBackgroundResource(R.color.app_bg);

                if (!message.getBoolean("isSent")) {
                    sendProductHolder.sendRelative.setVisibility(View.GONE);
                    sendProductHolder.reciveRelative.setVisibility(View.VISIBLE);

                    if (!message.getString("pro_name").isEmpty()) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int marginTopDp = 20;
                        int marginRightPx = (int) (marginTopDp * activity.getResources().getDisplayMetrics().density + 0.5f);
                        int marginTopPx = (int) (10 * activity.getResources().getDisplayMetrics().density + 0.5f);
                        layoutParams.setMargins(0, marginTopPx, marginRightPx, 0);
                        sendProductHolder.itemView.setLayoutParams(layoutParams);
                        sendProductHolder.recviceProName.setText(message.getString("pro_name"));
                        sendProductHolder.recviceDes.setText(message.getString("pro_des"));
                        sendProductHolder.recviceMessage.setText(message.getString("pro_message"));
                        sendProductHolder.recvicePrice.setText("$" + message.getString("pro_price") + "." + "00");
                        Picasso.get().load(Constans.Display_Image_URL + message.getString("pro_img")).placeholder(R.drawable.ic_user_img).into(sendProductHolder.recvice_image);
                        Picasso.get().load(Constans.Display_Image_URL + message.getString("userProfileImg")).placeholder(R.drawable.ic_user_img).into(sendProductHolder.recvice_profile_pic);

//                        String strTime = String.valueOf(message.getString("Sendtime"));
//                        sendProductHolder.recviceProTime.setText(getDate(strTime));
                    } else {
                        sendProductHolder.itemView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                    }

                } else {
                    sendProductHolder.sendRelative.setVisibility(View.VISIBLE);
                    sendProductHolder.reciveRelative.setVisibility(View.GONE);

                    if (!message.getString("pro_name").isEmpty()) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int marginTopDp = 20;
                        int marginRightPx = (int) (marginTopDp * activity.getResources().getDisplayMetrics().density + 0.5f);
                        int marginTopPx = (int) (10 * activity.getResources().getDisplayMetrics().density + 0.5f);
                        layoutParams.setMargins(0, marginTopPx, marginRightPx, 0);
                        sendProductHolder.itemView.setLayoutParams(layoutParams);
                        sendProductHolder.sendProname.setText(message.getString("pro_name"));
                        sendProductHolder.sendProdes.setText(message.getString("pro_des"));
                        sendProductHolder.sendMessage.setText(message.getString("pro_message"));
                        sendProductHolder.sendProprice.setText("$" + message.getString("pro_price") + "." + "00");
                        Glide.with(activity).load(Constans.Display_Image_URL + message.getString("pro_img")).placeholder(R.drawable.ic_user_img).into(sendProductHolder.sendProImage);

//                        String strTime = String.valueOf(message.getString("recivetime"));
//                        sendProductHolder.sendProtime.setText(getDate(strTime));
                    } else {
                        sendProductHolder.itemView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                    }

                }
                if (ChatingActivity.isInActionMode) {
                    boolean flag1 = ChatingActivity.selectionList.contains(chatMessages.get(position));
                    if (flag1 == true) {
                        sendProductHolder.mView.setBackgroundResource(R.color.selected_item);
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getDate(String timeStamp) {
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.valueOf(timeStamp) * 1000L);
            String date = DateFormat.format("hh:mm a", cal).toString();
            return date;
        } catch (Exception ex) {
            return "00:00";
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