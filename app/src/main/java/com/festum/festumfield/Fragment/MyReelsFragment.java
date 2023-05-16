package com.festum.festumfield.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.festum.festumfield.Adapter.ReelsAdapter;
import com.example.friendfield.R;

import java.util.ArrayList;

public class MyReelsFragment extends Fragment {
    RecyclerView recycler_reels;
    int[] img_noti = {R.drawable.img_1, R.drawable.img_1, R.drawable.img_1, R.drawable.img_1, R.drawable.img_1, R.drawable.img_1, R.drawable.img_1, R.drawable.img_1, R.drawable.img_1, R.drawable.img_1};
    ArrayList<Integer> imagearraylist = new ArrayList<Integer>();
    ReelsAdapter reelsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_reels, container, false);

        if (!imagearraylist.isEmpty()) {
            imagearraylist.clear();
        }

        int adPos1 = 0;
        for (int i = 0; i < img_noti.length; i++) {
            if (adPos1 == 4) {
                imagearraylist.add(null);
                adPos1 = 0;
            }
            imagearraylist.add(img_noti[i]);
            adPos1++;
        }


        recycler_reels = view.findViewById(R.id.recycler_reels);
        recycler_reels.setLayoutManager(new GridLayoutManager(getContext(), 3));

        reelsAdapter = new ReelsAdapter(getActivity(), imagearraylist);
        recycler_reels.setAdapter(reelsAdapter);


        return view;
    }
}