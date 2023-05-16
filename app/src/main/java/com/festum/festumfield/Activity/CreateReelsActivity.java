package com.festum.festumfield.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.festum.festumfield.R;

public class CreateReelsActivity extends AppCompatActivity {

    CardView rl_upload;
    private int GALLERY = 1;
    AppCompatButton btn_share;
    MediaController mediaController;
    ImageView ic_back, img_thumnail, img_play, btn_close;
    LinearLayout ll_upload;
    RelativeLayout rl_thumbnail;
    Uri contentURI, uriString;
    SharedPreferences sharedPreferences;
    String URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reels);

        ic_back = findViewById(R.id.ic_back);
        rl_upload = findViewById(R.id.rl_upload);
        img_thumnail = findViewById(R.id.img_thumnail);
        img_play = findViewById(R.id.img_play);
        rl_thumbnail = findViewById(R.id.rl_thumbnail);
        ll_upload = findViewById(R.id.ll_upload);
        btn_close = findViewById(R.id.btn_close);
        btn_share = findViewById(R.id.btn_share);

        sharedPreferences = getSharedPreferences("Reels", MODE_PRIVATE);
        URI = sharedPreferences.getString("uri", null);
        if (URI != null) {
            uriString = Uri.parse(URI);
            btn_close.setVisibility(View.VISIBLE);
            rl_thumbnail.setVisibility(View.VISIBLE);
            ll_upload.setVisibility(View.GONE);
            RequestOptions requestOptions = new RequestOptions();
            Glide.with(CreateReelsActivity.this)
                    .load(uriString)
                    .apply(requestOptions)
                    .placeholder(R.drawable.ic_launcher_background)
                    .thumbnail(Glide.with(CreateReelsActivity.this).load(uriString))
                    .into(img_thumnail);
        }

        mediaController = new MediaController(this);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ll_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, GALLERY);
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("Reels", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                contentURI = null;
                rl_thumbnail.setVisibility(View.GONE);
                btn_close.setVisibility(View.GONE);
                ll_upload.setVisibility(View.VISIBLE);
                Toast.makeText(CreateReelsActivity.this, "Delete Suceesfully", Toast.LENGTH_SHORT).show();
                System.out.println("file Deleted :" + String.valueOf(contentURI));
            }
        });


        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (URI != null) {
                    Intent intent = new Intent(CreateReelsActivity.this, ReelsPerviewActivity.class);
                    intent.putExtra("uri", String.valueOf(contentURI));
                    startActivity(intent);
                    Toast.makeText(CreateReelsActivity.this, "Select Video", Toast.LENGTH_SHORT).show();
                } else if (contentURI != null) {
                    Intent intent = new Intent(CreateReelsActivity.this, ReelsPerviewActivity.class);
                    intent.putExtra("uri", String.valueOf(contentURI));
                    startActivity(intent);
                    Toast.makeText(CreateReelsActivity.this, "Select Video", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("file Select");
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    contentURI = data.getData();

                    String selectedVideoPath = getPath(contentURI);
                    Log.d("path", selectedVideoPath);

                    ll_upload.setVisibility(View.GONE);
                    rl_thumbnail.setVisibility(View.VISIBLE);
                    btn_close.setVisibility(View.VISIBLE);

                    if (contentURI != null) {

                        RequestOptions requestOptions = new RequestOptions();
                        Glide.with(CreateReelsActivity.this)
                                .load(contentURI)
                                .apply(requestOptions)
                                .placeholder(R.drawable.ic_launcher_background)
                                .thumbnail(Glide.with(CreateReelsActivity.this).load(contentURI))
                                .into(img_thumnail);

                        sharedPreferences = getSharedPreferences("Reels", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("uri", String.valueOf(contentURI));
                        editor.apply();
                    } else {
                        rl_thumbnail.setVisibility(View.GONE);
                        btn_close.setVisibility(View.GONE);
                        ll_upload.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    @Override
    public void onBackPressed() {
        sharedPreferences = getSharedPreferences("Reels", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(CreateReelsActivity.this, ReelsActivity.class));
        finish();
    }
}