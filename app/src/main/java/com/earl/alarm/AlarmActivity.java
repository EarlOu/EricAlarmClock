package com.earl.alarm;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AlarmActivity extends Activity implements OnGestureListener {

    private MediaPlayer mMediaPlayer;
    private ImageView mBackground;
    private ProgressBar mProgress;

    private GestureDetector mDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        mDetector = new GestureDetector(this);
        mBackground = (ImageView)findViewById(R.id.background);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setMax(1000);

        Uri alert = getDefaultAlarm();
        mMediaPlayer = new MediaPlayer();
        setMaxVolumn();

        try {
            mMediaPlayer.setDataSource(this, alert);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mStopTask.execute();
    }

    private AsyncTask<Void, Void, Void> mStopTask = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {
            while(mProgress.getProgress()<1000) {
                publishProgress();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void...voids) {
            int progress = mProgress.getProgress();
            progress-=(int)Math.ceil(progress*0.017);
            if(progress < 0) progress = 0;
            mProgress.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Void v) {
            finish();
        }

    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDetector.onTouchEvent(ev);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mBackground.setBackgroundResource(R.drawable.alarm_back);
        AnimationDrawable anim = (AnimationDrawable) mBackground.getBackground();
        anim.start();
    }

    private void setMaxVolumn() {
        AudioManager audioManager = (AudioManager)getSystemService(
                Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM
                , audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                , AudioManager.FLAG_VIBRATE);
    }

    private Uri getDefaultAlarm() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alert == null){
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if(alert == null){
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getText(R.string.lalala), Toast.LENGTH_SHORT).show();
    }

    public void onDestroy() {
        mMediaPlayer.stop();
        super.onDestroy();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx,
            float dy) {
        int progress = mProgress.getProgress();
        DisplayMetrics m = getResources().getDisplayMetrics();
        progress += (int) (Math.sqrt(dx * dx + dy * dy) / m.density / 8.0);
        mProgress.setProgress(progress);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}
