package com.festum.festumfield.Adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.festum.festumfield.Fragment.SendRequestFragment;
import com.festum.festumfield.Model.ReceiveFriendsList.ReceiveFriendsRegisterModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendRequestAdapter extends RecyclerView.Adapter<SendRequestAdapter.MyViewHolder> {

    Fragment fragment;
    ArrayList<ReceiveFriendsRegisterModel> receiveFriendsRequestModels;
    LayoutInflater inflater;
    String status;

    public SendRequestAdapter(SendRequestFragment sendRequestFragment, ArrayList<ReceiveFriendsRegisterModel> receivefriendrequestsModelArrayList) {
        this.fragment = sendRequestFragment;
        this.receiveFriendsRequestModels = receivefriendrequestsModelArrayList;
        inflater = LayoutInflater.from(fragment.getContext());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.send_request_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.text_username.setText(receiveFriendsRequestModels.get(position).getFullName());
        holder.text_dis.setText(receiveFriendsRequestModels.get(position).getRequestMessage());

        status = receiveFriendsRequestModels.get(position).getStatus();
        if (status.equals("sent")) {
            holder.txt_request_check.setText(fragment.getContext().getResources().getString(R.string.pending));
            holder.txt_request_check.setTextColor(fragment.getResources().getColor(R.color.app_color));
        }
        String strTime = String.valueOf(receiveFriendsRequestModels.get(position).getTimestamp());
        /*long timestamp = Long.parseLong(strTime) * 1000L;
        if (String.valueOf(timestamp) != null) {
            String time = getFormattedDate(timestamp);
            holder.send_text_time.setText(time);
        }*/
        holder.send_text_time.setText(getTimeInMillis(receiveFriendsRequestModels.get(position).getTimestamp()));
        Glide.with(fragment).load(Constans.Display_Image_URL + receiveFriendsRequestModels.get(position).getProfileimage()).placeholder(R.drawable.ic_user_img).into(holder.send_img);

    }

    @Override
    public int getItemCount() {
        return receiveFriendsRequestModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView send_img;
        TextView text_username, send_text_time, text_dis, txt_request_check;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            send_img = itemView.findViewById(R.id.send_image);
            text_username = itemView.findViewById(R.id.text_username);
            send_text_time = itemView.findViewById(R.id.send_text_time);
            text_dis = itemView.findViewById(R.id.text_dis);
            txt_request_check = itemView.findViewById(R.id.txt_request_check);
        }
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

    public static String getTimeInMillis(long smsTimeInMillis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMillis);
        Calendar now = Calendar.getInstance();
        String timeFormatString = "hh:mm aa";
        String dateTimeFormatString = "dd/MM/yyyy";
        String dayFormatString = "EEEE";

        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) >= 2
                || now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) <= 6) {
            return "" + DateFormat.format(dayFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MM-dd-yyyy", smsTime).toString();
        }
    }
}
