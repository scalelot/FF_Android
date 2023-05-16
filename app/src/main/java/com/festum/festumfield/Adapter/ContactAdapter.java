package com.festum.festumfield.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.festum.festumfield.Fragment.ContactFragment;
import com.festum.festumfield.Model.AllMyFriends.AllFriendsRegisterModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyDataViewHolder> {

    Fragment fragment;
    ArrayList<AllFriendsRegisterModel> registerModels;

    public ContactAdapter(ContactFragment contactFragment, ArrayList<AllFriendsRegisterModel> allFriendsModelsList) {
        this.fragment = contactFragment;
        this.registerModels = allFriendsModelsList;
    }


    @NonNull
    @Override
    public MyDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list, parent, false);
        return new MyDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataViewHolder holder, int position) {
        Glide.with(fragment).load(Constans.Display_Image_URL + registerModels.get(position).getProfileimage()).placeholder(R.drawable.ic_user_img).into(holder.img_user);
        holder.txt_user_name.setText(registerModels.get(position).getFullName());

        holder.btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fragment.getContext(), "Invite", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return registerModels.size();
    }

    public void fliterList(ArrayList<AllFriendsRegisterModel> allFriendsRegisterModels) {
        registerModels = allFriendsRegisterModels;
        notifyDataSetChanged();
    }

    class MyDataViewHolder extends RecyclerView.ViewHolder {

        AppCompatButton btn_invite;
        TextView txt_user_name, txt_message;
        CircleImageView img_user;

        public MyDataViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_invite = itemView.findViewById(R.id.btn_invite);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            txt_message = itemView.findViewById(R.id.txt_message);
            img_user = itemView.findViewById(R.id.img_user);
        }
    }

}
