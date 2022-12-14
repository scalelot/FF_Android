package com.example.friendfield.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendfield.Adapter.UserSelectionAdapter;
import com.example.friendfield.Adapter.UserSelectionfinalAdapter;
import com.example.friendfield.BaseActivity;
import com.example.friendfield.Model.CreateGroupModel;
import com.example.friendfield.R;
import com.example.friendfield.Utils.EqualSpacingItemDecoration;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupTitleActivity extends BaseActivity {
    ImageView ic_back;
    TextView txt_title;
    TextView txt_selected_count;
    CircleImageView cir_group_image;
    RelativeLayout edt_img;
    EditText et_group_title;
    EditText edt_description;
    TextView txt_participant;
    RecyclerView recy_selected_list;
    ImageView iv_done;
    ArrayList<CreateGroupModel> selectedArrayList = new ArrayList<>();
    UserSelectionfinalAdapter userSelectionAdapter;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_title);

        ic_back = findViewById(R.id.ic_back);
        txt_title = findViewById(R.id.txt_title);
        txt_selected_count = findViewById(R.id.txt_selected_count);
        cir_group_image = findViewById(R.id.cir_group_image);
        edt_img = findViewById(R.id.edt_img);
        et_group_title = findViewById(R.id.et_group_title);
        edt_description = findViewById(R.id.edt_description);
        txt_participant = findViewById(R.id.txt_participant);
        recy_selected_list = findViewById(R.id.recy_selected_list);
        iv_done = findViewById(R.id.iv_done);

        selectedArrayList = getIntent().getParcelableArrayListExtra("selected_list");

        txt_participant.setText("Participants : " + selectedArrayList.size());

        recy_selected_list.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
//        int spanCount = 3; // 3 columns
//        int spacing = 50; // 50px
//        boolean includeEdge = false;
        recy_selected_list.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.GRID));

        userSelectionAdapter = new UserSelectionfinalAdapter(getApplicationContext(), selectedArrayList);
        recy_selected_list.setAdapter(userSelectionAdapter);

        userSelectionAdapter.setOnItemClickListener(new UserSelectionfinalAdapter.RemoveClickListener() {
            @Override
            public void onItemClick(CreateGroupModel createGroupModel, int position, View v) {
                selectedArrayList.remove(position);

                userSelectionAdapter = new UserSelectionfinalAdapter(getApplicationContext(), selectedArrayList);
                recy_selected_list.setAdapter(userSelectionAdapter);

                userSelectionAdapter.notifyDataSetChanged();
                txt_participant.setText("Participants : " + selectedArrayList.size());

            }
        });

        iv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cir_group_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(CreateGroupTitleActivity.this)
                        .crop()
                        .maxResultSize(1080, 1080)
                        .start(PICK_IMAGE);
            }
        });

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {

            try {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    cir_group_image.setImageBitmap(bitmap);
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}