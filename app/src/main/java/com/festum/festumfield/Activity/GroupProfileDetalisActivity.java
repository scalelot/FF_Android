package com.festum.festumfield.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.festum.festumfield.Adapter.GroupProfileAdapter;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.Model.GroupUserModel;
import com.example.friendfield.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.kyleduo.switchbutton.SwitchButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfileDetalisActivity extends AppCompatActivity {

    ImageView back_arrow, img_search, iv_search, iv_clear_text;
    AppCompatButton btn_group_info;
    RecyclerView recycler_group;
    RelativeLayout rl_permission, edit_img;
    EditText edt_search_text;
    LinearLayout ll_serch_edit;
    TextView txt_people, txt_show, txt_less;
    GroupProfileAdapter groupProfileAdapter;
    CircleImageView group_profile_image;
    private List<GroupUserModel> modelList = new ArrayList<>();
    String[] name = {"Hunter Bryan", "Devon Lane", "Cameron Williamson", "Hunter Bryan", "Devon Lane", "Cameron Williamson", "Hunter Bryan", "Devon Lane", "Cameron Williamson", "Hunter Bryan", "Devon Lane", "Cameron Williamson"};
    private static final int PICK_IMAGE = 100;
    int counter = 1;
    Bitmap bitmap = null;
    String uri;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile_detalis);

        btn_group_info = findViewById(R.id.btn_group_info);
        recycler_group = findViewById(R.id.recycler_group);
        txt_people = findViewById(R.id.txt_people);
        rl_permission = findViewById(R.id.rl_permission);
        txt_show = findViewById(R.id.txt_show);
        txt_less = findViewById(R.id.txt_less);
        back_arrow = findViewById(R.id.back_arrow);
        group_profile_image = findViewById(R.id.group_profile_image);
        edit_img = findViewById(R.id.edit_img);
        img_search = findViewById(R.id.img_search);
        ll_serch_edit = findViewById(R.id.ll_serch_edit);
        iv_search = findViewById(R.id.iv_search);
        iv_clear_text = findViewById(R.id.iv_clear_text);
        edt_search_text = findViewById(R.id.edt_search_text);

        uri = getIntent().getStringExtra("imgUri");
        if (uri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(GroupProfileDetalisActivity.this.getContentResolver(), Uri.parse(uri));
                group_profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            group_profile_image.setImageResource(R.drawable.ic_group_profile);
        }

        txt_people.setText(String.valueOf(name.length + " peoples"));

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        rl_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorizedDialog();
            }
        });

        btn_group_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupProfileDetalisActivity.this, EditGroupDetalisActivity.class));
                finish();
            }
        });

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter == 1) {
                    ll_serch_edit.setVisibility(View.VISIBLE);
                    counter++;
                } else if (counter == 2) {
                    ll_serch_edit.setVisibility(View.GONE);
                    counter = 1;
                }
            }
        });

        edt_search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        edt_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    iv_clear_text.setVisibility(View.GONE);
                    iv_search.setVisibility(View.VISIBLE);
                } else {
                    iv_clear_text.setVisibility(View.VISIBLE);
                    iv_search.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                filter(text);

                iv_search.setVisibility(View.GONE);
                iv_clear_text.setVisibility(View.VISIBLE);
            }
        });

        iv_clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search_text.setText("");
                iv_clear_text.setVisibility(View.GONE);
                iv_search.setVisibility(View.VISIBLE);
            }
        });

        for (int i = 0; i < name.length; ++i) {
            GroupUserModel elements = new GroupUserModel();
            elements.setName(name[i]);

            modelList.add(elements);
        }

        recycler_group.setHasFixedSize(false);
        final LinearLayoutManager LLM = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycler_group.setLayoutManager(LLM);

        if (modelList.size() > 3) {
            groupProfileAdapter = new GroupProfileAdapter(GroupProfileDetalisActivity.this, modelList.subList(0, 3));
            recycler_group.setAdapter(groupProfileAdapter);
            txt_show.setVisibility(View.VISIBLE);
            txt_less.setVisibility(View.GONE);
            txt_show.setOnClickListener(v -> {
                groupProfileAdapter = new GroupProfileAdapter(GroupProfileDetalisActivity.this, modelList);
                recycler_group.setAdapter(groupProfileAdapter);
                txt_show.setVisibility(View.GONE);
                txt_less.setVisibility(View.VISIBLE);
            });
            txt_less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modelList.size() > 3) {
                        groupProfileAdapter = new GroupProfileAdapter(GroupProfileDetalisActivity.this, modelList.subList(0, 3));
                        recycler_group.setAdapter(groupProfileAdapter);
                        txt_show.setVisibility(View.VISIBLE);
                        txt_less.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            groupProfileAdapter = new GroupProfileAdapter(GroupProfileDetalisActivity.this, modelList);
            recycler_group.setAdapter(groupProfileAdapter);
            txt_show.setVisibility(View.GONE);
            txt_less.setVisibility(View.GONE);
        }


    }

    private void openGallery() {
        ImagePicker.Companion.with(GroupProfileDetalisActivity.this)
                .crop()
                .maxResultSize(1080, 1080)
                .start(PICK_IMAGE);

    }


    public void authorizedDialog() {
        Dialog dialog = new Dialog(GroupProfileDetalisActivity.this);
        View view = LayoutInflater.from(GroupProfileDetalisActivity.this).inflate(R.layout.access_permission_dialog, null);
        dialog.setContentView(view);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(colorDrawable, 80);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);


        ImageView btn_close = dialog.findViewById(R.id.btn_close);

        TextView txt_user = dialog.findViewById(R.id.txt_user);
        TextView txt_ph_mo = dialog.findViewById(R.id.txt_ph_mo);
        TextView txt_email = dialog.findViewById(R.id.txt_email);

        SwitchButton name_switch = dialog.findViewById(R.id.name_switch);
        SwitchButton number_switch = dialog.findViewById(R.id.number_switch);
        SwitchButton email_switch = dialog.findViewById(R.id.email_switch);
        SwitchButton media_switch = dialog.findViewById(R.id.media_switch);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try {
                Uri selectedImageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    group_profile_image.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void filter(String text) {
        ArrayList<GroupUserModel> filteredlist = new ArrayList<>();

        for (GroupUserModel item : modelList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            groupProfileAdapter.filterList(filteredlist);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GroupProfileDetalisActivity.this, MainActivity.class));
        finish();
    }
}