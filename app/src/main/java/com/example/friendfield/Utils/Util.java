package com.example.friendfield.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

    private AmazonS3Client sS3Client;
    private AWSCredentialsProvider sMobileClient;
    private TransferUtility sTransferUtility;

    private AWSCredentialsProvider getCredProvider(Context context) {
        if (sMobileClient == null) {
            final CountDownLatch latch = new CountDownLatch(1);
            AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails result) {
                    latch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "onError: ", e);
                    latch.countDown();
                }
            });
            try {
                latch.await();
                sMobileClient = AWSMobileClient.getInstance();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sMobileClient;
    }

    public AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            try {
                String regionString = new AWSConfiguration(context)
                        .optJsonObject("S3TransferUtility")
                        .getString("Region");
                Region region = Region.getRegion(regionString);
                sS3Client = new AmazonS3Client(getCredProvider(context), region);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sS3Client;
    }

    public TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = TransferUtility.builder()
                    .context(context)
                    .s3Client(getS3Client(context))
                    .awsConfiguration(new AWSConfiguration(context))
                    .build();
        }

        return sTransferUtility;
    }

    public String getBytesString(long bytes) {
        String[] quantifiers = new String[]{
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0; ; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format(Locale.US, "%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    public File copyContentUriToFile(Context context, Uri uri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        File copiedData = new File(context.getDir("SampleImagesDir", Context.MODE_PRIVATE),
                UUID.randomUUID().toString());
        copiedData.createNewFile();

        FileOutputStream fos = new FileOutputStream(copiedData);
        byte[] buf = new byte[2046];
        int read = -1;
        while ((read = is.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }

        fos.flush();
        fos.close();

        return copiedData;
    }

    public void fillMap(Map<String, Object> map, TransferObserver observer, boolean isChecked) {
        int progress = (int) ((double) observer.getBytesTransferred() * 100 / observer
                .getBytesTotal());
        map.put("id", observer.getId());
        map.put("checked", isChecked);
        map.put("fileName", observer.getAbsoluteFilePath());
        map.put("progress", progress);
        map.put("bytes",
                getBytesString(observer.getBytesTransferred()) + "/"
                        + getBytesString(observer.getBytesTotal()));
        map.put("state", observer.getState());
        map.put("percentage", progress + "%");
    }
}
