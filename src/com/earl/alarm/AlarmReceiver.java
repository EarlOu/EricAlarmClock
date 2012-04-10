package com.earl.alarm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = context.getSharedPreferences(
                EricAlarmClockActivity.PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(EricAlarmClockActivity.PREF_IS_SET, false);
        editor.putLong(EricAlarmClockActivity.PREF_TIME, 0);
        editor.commit();

        Intent i = new Intent(context, AlarmActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
