package com.festum.festumfield.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.festum.festumfield.Model.ReelsModel;
import com.example.friendfield.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReelsVideoAdapter extends RecyclerView.Adapter<ReelsVideoAdapter.ViewHolder> {

    List<ReelsModel> reelsModelList;
    RecyclerView recycler_reels;
    ReelsCommentAdapter adapter;
    LinearLayoutManager manager;
    LinearLayout ll_delete_reels, ll_edit_reels, ll_copy_link, ll_share_reels;
    String[] name = {"Hello", "World", "Welcome", "Hunter", "Bryan", "Hello", "World", "Welcome", "Hunter", "Bryan"};

    public ReelsVideoAdapter(List<ReelsModel> reelsModelList) {
        this.reelsModelList = reelsModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reels_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setVideoData(reelsModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return reelsModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        CircleImageView img_user;
        TextView user_name, txt_time, txt_detalis, txt_like_count, txt_comment_count;
        ImageView img_like_boder, img_comment, img_send, img_more;
        LinearLayout ll_likes, ll_comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            img_user = itemView.findViewById(R.id.img_user);
            user_name = itemView.findViewById(R.id.user_name);
            txt_time = itemView.findViewById(R.id.txt_time);
            txt_detalis = itemView.findViewById(R.id.txt_detalis);
            txt_like_count = itemView.findViewById(R.id.txt_like_count);
            txt_comment_count = itemView.findViewById(R.id.txt_comment_count);
            img_more = itemView.findViewById(R.id.img_more);
            ll_likes = itemView.findViewById(R.id.ll_likes);
            ll_comment = itemView.findViewById(R.id.ll_comment);
        }

        public void setVideoData(ReelsModel reelsModel) {
            user_name.setText(reelsModel.getName());

            ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext(), R.style.CustomBottomSheetDialogTheme);
                    View inflate = LayoutInflater.from(v.getContext()).inflate(R.layout.reels_comment_dialog, null);
                    bottomSheetDialog.setContentView(inflate);

                    recycler_reels = bottomSheetDialog.findViewById(R.id.recycler_reels);
                    adapter = new ReelsCommentAdapter(v.getContext(), name);
                    manager = new LinearLayoutManager(v.getContext());
                    recycler_reels.setLayoutManager(manager);
                    recycler_reels.setAdapter(adapter);

                    bottomSheetDialog.show();
                }
            });

            img_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext(), R.style.CustomBottomSheetDialogTheme);
                    View inflate = LayoutInflater.from(v.getContext()).inflate(R.layout.reels_more_dialog, null);
                    bottomSheetDialog.setContentView(inflate);

                    ll_delete_reels = bottomSheetDialog.findViewById(R.id.ll_delete_reels);
                    ll_edit_reels = bottomSheetDialog.findViewById(R.id.ll_edit_reels);
                    ll_copy_link = bottomSheetDialog.findViewById(R.id.ll_copy_link);
                    ll_share_reels = bottomSheetDialog.findViewById(R.id.ll_share_reels);
                    bottomSheetDialog.show();
                }
            });
        }

    }
}
