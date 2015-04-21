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

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private Button mSubmitBtn;
    private Button mCancelBtn;
    private TextView mTimeText;

    private AlarmManager mAlarmManager;

    private long mTime;
    private boolean mIsSet;

    public static final String PREF = "pref";
    public static final String PREF_TIME = "time";
    public static final String PREF_IS_SET = "set";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPrefDate();

        if(!mIsSet) {
            initSetTimeView();
        } else {
            resetTimer();
            initWaitingView();
        }

    }

    private void getPrefDate() {
        SharedPreferences pref = getSharedPreferences(PREF, 0);
        mIsSet = pref.getBoolean(PREF_IS_SET, false);
        mTime = pref.getLong(PREF_TIME, 0);
    }

    private void initSetTimeView() {
        setContentView(R.layout.main);

        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
        mSubmitBtn = (Button) findViewById(R.id.submit);

        mSubmitBtn.setOnClickListener(this);
    }

    private void initWaitingView() {
        setContentView(R.layout.waiting);
        mTimeText = (TextView) findViewById(R.id.time);
        mCancelBtn = (Button) findViewById(R.id.cancel);

        mCancelBtn.setOnClickListener(this);

        Date d = new Date(mTime);
        DateFormat timeFormat =
                android.text.format.DateFormat.getTimeFormat(this);
        DateFormat dateFormat =
                android.text.format.DateFormat.getDateFormat(this);

        mTimeText.setText(dateFormat.format(d)+" "+timeFormat.format(d));

    }

    @Override
    public void onClick(View view) {
        if(view == mSubmitBtn) {
            setTime();
        } else if (view == mCancelBtn){
            cancelTime();
            initSetTimeView();
        }
    }

    private void resetTimer() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this
                , 0, intent, 0);
        mAlarmManager.cancel(pendingIntent);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, mTime, pendingIntent);
    }

    private void cancelTime() {
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

    private void setTime() {
        Calendar c = Calendar.getInstance();
        c.set(mDatePicker.getYear()
                , mDatePicker.getMonth()
                , mDatePicker.getDayOfMonth()
                , mTimePicker.getCurrentHour()
                , mTimePicker.getCurrentMinute());

        if(c.getTimeInMillis() < System.currentTimeMillis()) {
            Toast.makeText(this, "請把鬧鐘設在未來吧！", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this
                , 0, intent, 0);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

        save(c.getTimeInMillis());

        initWaitingView();
    }

    private void save(long time) {
        SharedPreferences pref = getSharedPreferences(PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_IS_SET, true);
        editor.putLong(PREF_TIME, time);
        mIsSet = true;
        mTime = time;
        editor.commit();
    }
}