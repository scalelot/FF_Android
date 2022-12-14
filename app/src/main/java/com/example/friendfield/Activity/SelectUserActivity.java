package com.example.friendfield.Activity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.friendfield.Adapter.UserSelectAdapter;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.Model.SelecetdUserModel;
import com.example.friendfield.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SelectUserActivity extends BaseActivity {

    RecyclerView user_recycler;
    UserSelectAdapter userSelectAdapter;
    String[] user_name = {"John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan"};
    CheckBox img_select_all;
    TextView txt_people_count;
    FloatingActionButton fb_map;
    ImageView ic_back_arrow;
    ArrayList<SelecetdUserModel> selecetdUserModelArrayList = new ArrayList<>();
    ArrayList<SelecetdUserModel> selectedarraylist = new ArrayList<>();
    boolean flagSelectAll = false;
    RelativeLayout iv_filter;
    String str_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        user_recycler = findViewById(R.id.user_recycler);
        fb_map = findViewById(R.id.fb_map);
        ic_back_arrow = findViewById(R.id.ic_back_arrow);
        img_select_all = findViewById(R.id.img_select_all);
        iv_filter = findViewById(R.id.iv_filter);
        txt_people_count = findViewById(R.id.txt_people_count);

        for (int i = 0; i < user_name.length; i++) {
            SelecetdUserModel selecetdUserModel = new SelecetdUserModel(user_name[i]);
            selecetdUserModelArrayList.add(selecetdUserModel);
        }

        userSelectAdapter = new UserSelectAdapter(this, selecetdUserModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        user_recycler.setLayoutManager(linearLayoutManager);
        user_recycler.setAdapter(userSelectAdapter);

        ic_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userSelectAdapter.setOnItemClickListener(new UserSelectAdapter.ClickListener() {
            @Override
            public void onItemClick(SelecetdUserModel model, boolean isAddTo) {
                if (isAddTo) {
                    selectedarraylist.add(model);
                } else {
                    selectedarraylist.remove(model);
                }
                userSelectAdapter.notifyDataSetChanged();
                txt_people_count.setText(String.valueOf(selectedarraylist.size()));
                str_count = String.valueOf(selectedarraylist.size()+ " People Selected");
            }
        });

        img_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    for (int i = 0; i < selecetdUserModelArrayList.size(); i++) {
                        if (selecetdUserModelArrayList.get(i).isSelected) {
                            flagSelectAll = true;
                            txt_people_count.setText("");
                            userSelectAdapter.selectAllItem(false);
                            userSelectAdapter.notifyDataSetChanged();
                        } else {
                            userSelectAdapter.selectAllItem(true);
                            txt_people_count.setText(String.valueOf(user_name.length));
                            str_count = String.valueOf(user_name.length+" People Selected");
                            userSelectAdapter.notifyDataSetChanged();
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fb_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectUserActivity.this, ChooseUserPromotionActivity.class).putExtra("CountUser", str_count));
                finish();
            }
        });


        iv_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
    }

    public void showFilterDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SelectUserActivity.this, R.style.AppBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.selectuser_filter_dialog);

        CheckBox check_contact_fnd = bottomSheetDialog.findViewById(R.id.check_contact_fnd);
        CheckBox check_app_fnd = bottomSheetDialog.findViewById(R.id.check_app_fnd);
        CheckBox check_excel_fnd = bottomSheetDialog.findViewById(R.id.check_excel_fnd);
        ImageView dialog_close = bottomSheetDialog.findViewById(R.id.dialog_close);

        AppCompatButton btn_cancel = bottomSheetDialog.findViewById(R.id.btn_cancel);
        AppCompatButton btn_apply = bottomSheetDialog.findViewById(R.id.btn_apply);

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        check_contact_fnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });

        check_app_fnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });

        check_excel_fnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SelectUserActivity.this, ChooseUserPromotionActivity.class));
        finish();
    }
}