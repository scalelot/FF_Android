package com.festum.festumfield.Activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kyleduo.switchbutton.SwitchButton;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SettingActivity extends BaseActivity {

    ImageView ic_back_arrow, img_close, ic_close,img_dark_mode;
    AppCompatButton btn_clear_data, btn_select_file;
    SwitchButton noti_switch,dark_mode_switch;
    RelativeLayout chat_backup, block_contact, change_number, contact_us, help, conversation, sign_out, upload_excel;
    File myFile;
    Uri uri;
    TextView txt_ph_no, txt_file_name, txt_email_id;
    LinearLayout ll_txt, ll_phone_email, ll_sample_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ic_back_arrow = findViewById(R.id.ic_back_arrow);
        btn_clear_data = findViewById(R.id.btn_clear_data);
        noti_switch = findViewById(R.id.noti_switch);
        block_contact = findViewById(R.id.block_contact);
        chat_backup = findViewById(R.id.chat_backup);
        change_number = findViewById(R.id.change_number);
        contact_us = findViewById(R.id.contact_us);
        help = findViewById(R.id.help);
        conversation = findViewById(R.id.conversation);
        sign_out = findViewById(R.id.sign_out);
        upload_excel = findViewById(R.id.upload_excel);
        ic_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        chat_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatBackupDialog();
            }
        });

        upload_excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadExcelDialog();
            }
        });

        block_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, BlockedContactActivity.class));
                finish();
            }
        });

        change_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, ChangeNumberActivity.class));
                finish();
            }
        });

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, Contact_Us_Activity.class));
                finish();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, HelpActivity.class));
                finish();
            }
        });

        conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ConversationActivity.class));
                finish();
            }
        });

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyApplication.setuserActive(getApplicationContext(), false);
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void uploadExcelDialog() {
        Dialog dialog = new Dialog(SettingActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.upload_excel_dialog, null);
        dialog.setContentView(view);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(back, 50);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);

        img_close = dialog.findViewById(R.id.img_close);
        ic_close = dialog.findViewById(R.id.ic_close);
        btn_select_file = dialog.findViewById(R.id.btn_select_file);
        txt_ph_no = dialog.findViewById(R.id.txt_ph_no);
        txt_file_name = dialog.findViewById(R.id.txt_file_name);
        ll_txt = dialog.findViewById(R.id.ll_txt);
        ll_phone_email = dialog.findViewById(R.id.ll_phone_email);
        ll_sample_txt = dialog.findViewById(R.id.ll_sample_txt);
        txt_email_id = dialog.findViewById(R.id.txt_email_id);
        AppCompatButton dialog_no = dialog.findViewById(R.id.dialog_no);

        btn_select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SettingActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    selectExcel();
                }
            }
        });

        ic_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               btn_select_file.setVisibility(View.VISIBLE);
                ll_sample_txt.setVisibility(View.VISIBLE);
                ll_phone_email.setVisibility(View.GONE);
                ll_txt.setVisibility(View.GONE);
                ic_close.setVisibility(View.GONE);

            }
        });

        ll_sample_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SettingActivity.this, R.style.CustomBottomSheetDialogTheme);
                View inflate = LayoutInflater.from(SettingActivity.this).inflate(R.layout.bottom_dialog, null);
                bottomSheetDialog.setContentView(inflate);

                ImageView bottom_img = inflate.findViewById(R.id.bottom_img);

                try {
                    InputStream ims = getAssets().open("sample_exe_file.png");
                    Drawable d = Drawable.createFromStream(ims, null);
                    bottom_img.setImageDrawable(d);
                } catch (IOException ex) {
                    return;
                }

                bottomSheetDialog.show();
            }
        });

        dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private String getPathFromExtSD(String[] pathData) {
        final String relativePath = "/" + pathData[1];

        return relativePath;
    }

    private void selectExcel() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(intent, 1);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    uri = data.getData();
                    String uriString = uri.toString();
                    myFile = new File(uri.getPath());
                    String displayName = null;
                    onReadClick(uri);

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                Log.e("ExcelSheetName>>>>", displayName);
                                txt_file_name.setText(displayName);
                                ll_txt.setVisibility(View.VISIBLE);
                                ll_phone_email.setVisibility(View.VISIBLE);
                                ll_sample_txt.setVisibility(View.GONE);
                                ic_close.setVisibility(View.VISIBLE);
                                btn_select_file.setVisibility(View.GONE);
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                        Log.d("ExcelSheetName>>>>  ", displayName);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void onReadClick(Uri uriString) {
        try {
            InputStream stream = getContentResolver().openInputStream(uriString);
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            ArrayList<String> email = new ArrayList<>();
            ArrayList<String> pass = new ArrayList<>();

            int column_index_2 = 0;
            int column_index_3 = 0;
            Row row = sheet.getRow(0);

            for (Cell cell : row) {
                if (cell.getStringCellValue().equals("Email")) {
                    column_index_2 = cell.getColumnIndex();
                    for (Row r : sheet) {
                        if (r.getRowNum() == 0) continue;
                        Cell c_2 = r.getCell(column_index_2);
                        if (c_2 != null) {
                            System.out.print("" + c_2);

                            email.add(String.valueOf(c_2));
                            txt_email_id.setText(String.valueOf(email.size()));
                        }
                    }
                } else if (cell.getStringCellValue().equals("Phone Number")) {
                    column_index_3 = cell.getColumnIndex();
                    for (Row r1 : sheet) {
                        if (r1.getRowNum() == 0) continue;
                        Cell c_3 = r1.getCell(column_index_3);
                        if (c_3 != null) {
                            System.out.print("" + c_3);

                            pass.add(String.valueOf(c_3));
                            txt_ph_no.setText(String.valueOf(pass.size()));
                        }
                    }
                }
            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectExcel();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void chatBackupDialog() {
        Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.chat_backup_dialog, null);
        dialog.setContentView(view);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        AppCompatButton button_cancle = view.findViewById(R.id.button_cancle);
        AppCompatButton button_backup = view.findViewById(R.id.button_backup);
        ImageView btn_close = view.findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        button_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        button_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "BackUp Done", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
    }
}