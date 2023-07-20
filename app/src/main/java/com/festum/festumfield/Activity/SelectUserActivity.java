package com.festum.festumfield.Activity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.festum.festumfield.Adapter.UserSelectAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.Model.SelecetdUserModel;
import com.festum.festumfield.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SelectUserActivity extends BaseActivity {

    RecyclerView user_recycler;
    ImageView ic_back_arrow;
    SharedPreferences sharedPreferences;
    UserSelectAdapter userSelectAdapter;
    String[] user_name = {"John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan"};
    CheckBox img_select_all;
    TextView txt_people_count;
    FloatingActionButton fb_map;
    ArrayList<SelecetdUserModel> selecetdUserModelArrayList = new ArrayList<>();
    ArrayList<SelecetdUserModel> selectedarraylist = new ArrayList<>();
    ArrayList<SelecetdUserModel> arraylist = new ArrayList<>();
    RelativeLayout iv_filter;
    String str_count;
    SharedPreferences.Editor editor;

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

        sharedPreferences = getSharedPreferences("countUser", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userSelectAdapter.setOnItemClickListener(new UserSelectAdapter.ClickListener() {
            @Override
            public void onItemClick(SelecetdUserModel model, boolean isAddTo, int position) {
                if (isAddTo) {
                    selectedarraylist.add(model);

                } else {
                    selectedarraylist.remove(model);
                }

                txt_people_count.setText(String.valueOf(selectedarraylist.size()));
                userSelectAdapter.notifyDataSetChanged();


//                Log.e(TAG, "onItemClick: ", );

//                if (isAddTo) {
//                    selectedarraylist.add(model);
//                } else {
//                    selectedarraylist.remove(model);
//                    img_select_all.setChecked(false);
//                }
//
//                str_count = String.valueOf(selectedarraylist.size() + " People Selected");
//                editor.putInt("Count", selectedarraylist.size());
//                userSelectAdapter.notifyDataSetChanged();
            }
        });


        img_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    for (int i = 0; i < selecetdUserModelArrayList.size(); i++) {
                        if (!isChecked) {
                            txt_people_count.setText("");
                            userSelectAdapter.selectAllItem(false);
                            userSelectAdapter.notifyDataSetChanged();
                        } else {
                            selectedarraylist.clear();
                            userSelectAdapter.selectAllItem(true);
                            txt_people_count.setText(String.valueOf(user_name.length));
                            str_count = String.valueOf(user_name.length + " People Selected");
                            editor.putInt("Count", user_name.length);
                            userSelectAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fb_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.apply();
                startActivity(new Intent(SelectUserActivity.this, ChooseUserPromotionActivity.class));
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

        AppCompatButton btnCancel = bottomSheetDialog.findViewById(R.id.btnClear);
        AppCompatButton btnApply = bottomSheetDialog.findViewById(R.id.btnApply);

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

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
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