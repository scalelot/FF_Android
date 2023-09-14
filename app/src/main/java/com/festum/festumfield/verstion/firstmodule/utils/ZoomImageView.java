package com.festum.festumfield.verstion.firstmodule.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.festum.festumfield.R;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.io.File;
import java.util.ArrayList;

public class ZoomImageView extends AppCompatImageView {

    private StfalconImageViewer<Bitmap> mImageViewer;
    private File mLargeImageFile = null;
    private String mPath = null;

    public ZoomImageView(Context context) {
        super(context);
        init();
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(v -> launchPinchZoomImageViewer());
    }

    public void setLargeImageFile(File image) {
        mLargeImageFile = image;
    }

    public void setLargeImagePath(String path) {
        mPath = path;
    }

    private void launchPinchZoomImageViewer() {

        try {

            if (mImageViewer == null) {
                Bitmap bitmap;
                if (mLargeImageFile != null) {
                    bitmap = BitmapFactory.decodeFile(mLargeImageFile.getAbsolutePath());
                    launch(bitmap);
                } else if (mPath != null) {
                    Glide.with(this).asBitmap().load(mPath).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource,
                                                    @Nullable Transition<? super Bitmap> transition) {
                            launch(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launch(Bitmap bitmap) {
        if (bitmap != null) {
            Context context = getContext();

            // Create an overlay that contains a close button
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View overlay = inflater
                    .inflate(R.layout.stfalcon_close_button_overlay, new FrameLayout(context), false);

            overlay.findViewById(R.id.stfalcon_close_button).setOnClickListener(v -> {
                if (mImageViewer != null) {
                    mImageViewer.dismiss();
                }
            });

            ArrayList<Bitmap> mLargeImages = new ArrayList<>();
            mLargeImages.add(bitmap);

            // Build the pinch zoom image viewer
            StfalconImageViewer.Builder<Bitmap> builder = new StfalconImageViewer.Builder<>(context,
                    mLargeImages, ImageView::setImageBitmap);

            builder.withBackgroundColorResource(R.color.stfalcon_background_overlay);
            builder.withOverlayView(overlay);
            builder.withDismissListener(() -> mImageViewer = null);

            mImageViewer = builder.build();
            mImageViewer.show();
        }
    }

    public StfalconImageViewer<Bitmap> getImageViewer() {
        return mImageViewer;
    }
}