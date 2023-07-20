package com.festum.festumfield.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.festum.festumfield.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditGroupDetalisActivity extends AppCompatActivity {

    ImageView ic_back;
    CircleImageView user_profile_image;
    RelativeLayout edit_img;
    AppCompatButton btn_save;
    EditText edt_name, edt_description;
    private static final int PICK_IMAGE = 100;
    Uri selectedImageUri;
    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_detalis);

        ic_back = findViewById(R.id.ic_back);
        edt_name = findViewById(R.id.edt_name);
        edt_description = findViewById(R.id.edt_description);
        user_profile_image = findViewById(R.id.user_profile_image);
        edit_img = findViewById(R.id.edit_img);
        btn_save = findViewById(R.id.btn_save);

        ic_back.setOnClickListener(new View.OnClickListener() {
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

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_profile_image.getDrawable() == null) {
                    Toast.makeText(EditGroupDetalisActivity.this, "Add Profile Image", Toast.LENGTH_LONG).show();
                } else if (edt_name.getText().toString().trim().isEmpty()) {
                    edt_name.setError(getResources().getString(R.string.enter_name));
                } else if (edt_description.getText().toString().trim().isEmpty()) {
                    edt_description.setError(getResources().getString(R.string.enter_description));
                } else {
                    if (selectedImageUri != null) {
                        Intent intent = new Intent(EditGroupDetalisActivity.this, GroupProfileDetalisActivity.class);
                        intent.putExtra("imgUri", uri);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EditGroupDetalisActivity.this, "Add Profile Image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try {
                selectedImageUri = data.getData();
                uri = String.valueOf(selectedImageUri);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    user_profile_image.setImageBitmap(bitmap);
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
        startActivity(new Intent(EditGroupDetalisActivity.this, GroupProfileDetalisActivity.class));
        finish();
    }
}