package com.example.friendfield.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.friendfield.Activity.UserProfileActivity;
import com.example.friendfield.Fragment.BusinessInfoFragment;
import com.example.friendfield.Fragment.MyReelsFragment;
import com.example.friendfield.Fragment.PersnoalInfoFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public ViewPagerAdapter(UserProfileActivity userProfileActivity, FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        this.context = userProfileActivity;
        this.totalTabs = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PersnoalInfoFragment persnoalInfoFragment = new PersnoalInfoFragment();
                return persnoalInfoFragment;
            case 1:
                BusinessInfoFragment businessInfoFragment = new BusinessInfoFragment();
                return businessInfoFragment;
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
