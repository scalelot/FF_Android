package com.example.friendfield.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendfield.Fragment.CallsFragment;
import com.example.friendfield.R;
import com.example.friendfield.TagView.TokenTextView;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyDataHolder> {

    Context context;
    int[] call_img;
    LayoutInflater inflater;
    ArrayList<String> user_name;
    ArrayList<String> arraylist;

    public TagAdapter(Context context, ArrayList<String> user_name) {
        this.context = context;
        this.user_name = user_name;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(this.user_name);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_token, parent, false);
        return new MyDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataHolder holder, int position) {
        holder.name.setText(user_name.get(position));
    }

    @Override
    public int getItemCount() {
        return user_name.size();
    }

    class MyDataHolder extends RecyclerView.ViewHolder {

      TokenTextView name;

        public MyDataHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);

        }
    }

}
