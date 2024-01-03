package com.festum.festumfield.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.festum.festumfield.Activity.CreateNotificationActivity;
import com.festum.festumfield.Adapter.PromotionHistoryAdapter;
import com.festum.festumfield.R;

import java.util.ArrayList;

public class HistoryPromotionFragment extends Fragment {

    AppCompatButton btn_create_notification;
    RecyclerView recycler_noti;
    String[] txt_userName = {"Hello World", "World", "Hello World", "World", "Hello World"};
    int[] img_noti = {R.drawable.img_1, R.drawable.img_1, R.drawable.img_1, R.drawable.img_1, R.drawable.img_1};
    ArrayList<String> arraylist = new ArrayList<String>();
    ArrayList<Integer> imagearraylist = new ArrayList<Integer>();
    RelativeLayout emptyLay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_history_promotion, container, false);

        btn_create_notification = inflate.findViewById(R.id.btn_create_notification);
        recycler_noti = inflate.findViewById(R.id.recycler_noti);
        emptyLay = inflate.findViewById(R.id.emptyLay);

        btn_create_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateNotificationActivity.class));
                getActivity().finish();
            }
        });

        int adPos = 0;
        for (int i = 0; i < txt_userName.length; i++) {
            if (adPos == 1) {
                arraylist.add(null);
                adPos = 0;
            }
            arraylist.add(txt_userName[i]);
            adPos++;
        }


        int adPos1 = 0;
        for (int i = 0; i < img_noti.length; i++) {
            if (adPos1 == 1) {
                imagearraylist.add(null);
                adPos1 = 0;
            }
            imagearraylist.add(img_noti[i]);
            adPos1++;
        }


        PromotionHistoryAdapter promotionHistoryAdapter = new PromotionHistoryAdapter(getActivity(), arraylist, imagearraylist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler_noti.setLayoutManager(linearLayoutManager);
        recycler_noti.setAdapter(promotionHistoryAdapter);

        recycler_noti.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                emptyLay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
//                emptyLay.setVisibility(View.VISIBLE);
            }
        });

        return inflate;
    }
}