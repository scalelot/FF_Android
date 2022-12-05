package com.example.friendfield.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.friendfield.Activity.ChatUserProfileActivity;
import com.example.friendfield.Activity.UserProfileActivity;
import com.example.friendfield.Fragment.BusinessInfoFragment;
import com.example.friendfield.Fragment.ChatBusinessInfoFragment;
import com.example.friendfield.Fragment.ChatPersnoalInfoFragment;
import com.example.friendfield.Fragment.MyReelsFragment;
import com.example.friendfield.Fragment.PersnoalInfoFragment;

import java.util.ArrayList;

public class ChatViewPagerAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public ChatViewPagerAdapter(ChatUserProfileActivity chatUserProfileActivity, FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        this.context = chatUserProfileActivity;
        this.totalTabs = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatPersnoalInfoFragment chatPersnoalInfoFragment = new ChatPersnoalInfoFragment();
                return chatPersnoalInfoFragment;
            case 1:
                ChatBusinessInfoFragment chatBusinessInfoFragment = new ChatBusinessInfoFragment();
                return chatBusinessInfoFragment;
            case 2:
                MyReelsFragment myReelsFragment = new MyReelsFragment();
                return myReelsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
