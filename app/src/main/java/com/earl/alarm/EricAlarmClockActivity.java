package com.earl.alarm;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EricAlarmClockActivity extends Activity implements OnClickListener {

    private View mSettingView;
    private View mWaitingView;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private Button mSubmitBtn;
    private Button mCancelBtn;
    private TextView mTimeText;

    private AlarmManager mAlarmManager;

    private long mTime;
    private boolean mIsTimeSet;

    public static final String PREF = "pref";
    public static final String PREF_TIME = "time";
    public static final String PREF_IS_SET = "set";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        SharedPreferences pref = getSharedPreferences(PREF, 0);
        mIsTimeSet = pref.getBoolean(PREF_IS_SET, false);
        mTime = pref.getLong(PREF_TIME, 0);

        setContentView(R.layout.main);
        mSettingView = findViewById(R.id.setting_container);
        mWaitingView = findViewById(R.id.waiting_container);
        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
        mSubmitBtn = (Button) findViewById(R.id.submit);
        mTimeText = (TextView) findViewById(R.id.time);
        mCancelBtn = (Button) findViewById(R.id.cancel);

        mSubmitBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);

        if (mIsTimeSet) {
            mSettingView.setVisibility(View.GONE);
            mWaitingView.setVisibility(View.VISIBLE);
            setTimeText();
        } else {
            mWaitingView.setVisibility(View.GONE);
            mSettingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == mSubmitBtn) {
            long time = getTime();
            if (getTime() < System.currentTimeMillis()) {
                Toast.makeText(this, "時間錯誤", Toast.LENGTH_SHORT).show();
            } else {
                setAlarm(time);
                mSettingView.setVisibility(View.GONE);
                mWaitingView.setVisibility(View.VISIBLE);
                setTimeText();
            }
        } else if (view == mCancelBtn){
            cancelAlarm();
            mWaitingView.setVisibility(View.GONE);
            mSettingView.setVisibility(View.VISIBLE);
        }
    }

    private void setTimeText() {
        Date d = new Date(mTime);
        DateFormat timeFormat =
                android.text.format.DateFormat.getTimeFormat(this);
        DateFormat dateFormat =
                android.text.format.DateFormat.getDateFormat(this);
        mTimeText.setText(dateFormat.format(d)+" "+timeFormat.format(d));
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this
                , 0, intent, 0);
        mAlarmManager.cancel(pendingIntent);

        SharedPreferences pref = getSharedPreferences(PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_IS_SET, false);
        editor.putLong(PREF_TIME, 0);
        editor.commit();
    }

    private long getTime() {
        Calendar c = Calendar.getInstance();
        c.set(mDatePicker.getYear()
                , mDatePicker.getMonth()
                , mDatePicker.getDayOfMonth()
                , mTimePicker.getCurrentHour()
                , mTimePicker.getCurrentMinute());
        return c.getTimeInMillis();
    }

    private void setAlarm(long time) {
        mIsTimeSet = true;
        mTime = time;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this
                , 0, intent, 0);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        SharedPreferences pref = getSharedPreferences(PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_IS_SET, true);
        editor.putLong(PREF_TIME, time);
        editor.commit();
    }
}