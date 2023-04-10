package com.example.friendfield.Activity;

import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.friendfield.BaseActivity;
import com.example.friendfield.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PublishDateTimeActivity extends BaseActivity {

    ImageView ic_back, img_select_date, img_select_time;
    private int CalendarHour, CalendarMinute;
    String format, chk;
    Calendar calendar;
    EditText txt_select_date, txt_select_time;
    TimePickerDialog timepickerdialog;
    AppCompatButton button_continue;
    private int lastSelectedYear;
    private int lastSelectedMonth;
    private int lastSelectedDayOfMonth;
    CheckBox chk_notification, chk_email, chk_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_date_time);

        ic_back = findViewById(R.id.ic_back);
        img_select_date = findViewById(R.id.img_select_date);
        txt_select_date = findViewById(R.id.txt_select_date);
        img_select_time = findViewById(R.id.img_select_time);
        txt_select_time = findViewById(R.id.edt_select_time);
        button_continue = findViewById(R.id.button_continue);
        chk_notification = findViewById(R.id.chk_notification);
        chk_email = findViewById(R.id.chk_email);
        chk_sms = findViewById(R.id.chk_sms);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Calendar c = Calendar.getInstance();
        this.lastSelectedYear = c.get(Calendar.YEAR);
        this.lastSelectedMonth = c.get(Calendar.MONTH);
        this.lastSelectedDayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        img_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelectDate();
            }
        });

        img_select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelectTime();
            }
        });

        if (chk_notification.isChecked()) {
            chk = chk_notification.getText().toString();
        }
        if (chk_email.isChecked()) {
            chk = chk_email.getText().toString();
        }
        if (chk_sms.isChecked()) {
            chk = chk_sms.getText().toString();
        }


        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_select_date.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getBaseContext(),"Enter Date",Toast.LENGTH_LONG).show();
                } else if (txt_select_time.getText().toString().trim().isEmpty()) {
                    Toast.makeText(PublishDateTimeActivity.this,"Enter Time", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("chk--->>" + txt_select_date);
                    SharedPreferences sharedPreferences = getSharedPreferences("Datetime", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("date_time", txt_select_date.getText().toString());
                    editor.apply();
                    Intent intent = new Intent(PublishDateTimeActivity.this, PromotionBillActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void buttonSelectTime() {
        calendar = Calendar.getInstance();
        CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
        CalendarMinute = calendar.get(Calendar.MINUTE);

        timepickerdialog = new TimePickerDialog(PublishDateTimeActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        if (hourOfDay == 0) {

                            hourOfDay += 12;

                            format = "AM";
                        } else if (hourOfDay == 12) {

                            format = "PM";

                        } else if (hourOfDay > 12) {

                            hourOfDay -= 12;

                            format = "PM";

                        } else {

                            format = "AM";
                        }


                        txt_select_time.setText(hourOfDay + ":" + minute + "\n" + format);
                    }
                }, CalendarHour, CalendarMinute, false);
        timepickerdialog.show();


    }

    private void buttonSelectDate() {

        // Date Select Listener.
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                int year1 = view.getYear();
                int month1 = view.getMonth();
                int day1 = view.getDayOfMonth();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year1, month1, day1);

                SimpleDateFormat format = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    format = new SimpleDateFormat("dd MMMM YYYY - EEEE");
                }
                String strDate = format.format(calendar.getTime());

                txt_select_date.setText(strDate);
            }
        };

        DatePickerDialog datePickerDialog = null;
        datePickerDialog = new DatePickerDialog(this,
                dateSetListener, lastSelectedYear, lastSelectedMonth, lastSelectedDayOfMonth);

        // Show
        datePickerDialog.show();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(PublishDateTimeActivity.this, ChooseUserPromotionActivity.class));
        finish();
    }
}