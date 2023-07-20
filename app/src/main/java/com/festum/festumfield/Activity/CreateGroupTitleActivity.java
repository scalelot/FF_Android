package com.festum.festumfield.Activity;

import androidx.annotation.Nullable;
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

import com.festum.festumfield.Adapter.UserSelectionfinalAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.Model.CreateGroupModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.EqualSpacingItemDecoration;
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

        edt_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
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
        try {
            if (requestCode == PICK_IMAGE) {

                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                cir_group_image.setImageBitmap(bitmap);

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}