package com.antohin.iremember.screen.activity.alarm;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.antohin.iremember.R;
import com.antohin.iremember.model.NoteModel;
import com.antohin.iremember.model.UserSettings;
import com.antohin.iremember.screen.activity.BaseActivity;
import com.ebanx.swipebtn.SwipeButton;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.antohin.iremember.screen.activity.details.DetailsPresenter.isEmptyData;


public class AlarmActivity extends BaseActivity {
    public static final String KEY_ID_JOB= "key id job";

    private MediaPlayer mMediaPlayer;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.img_alarm) ImageView mImageAlarm;
    @BindView(R.id.swipe_btn) SwipeButton mSwipeBtn;
    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.note) TextView mNote;
    private Vibrator mVibrator;
    private int counter = 0;
    private boolean flag = true;
    private Timer mTimer;
    private Timer mTimerAlarmDuration;
    private UserSettings mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        registerActionBar(mToolbar,R.string.alarm);

        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            mSettings = realm.where(UserSettings.class).findFirst();
            if (mSettings == null) mSettings = realm.createObject(UserSettings.class);
        });

        mSwipeBtn.setOnActiveListener(this::stopAndFinish);

        if (!getIntent().getStringExtra(KEY_ID_JOB).isEmpty()){
            String idJob = getIntent().getStringExtra(KEY_ID_JOB);
            realmDb.executeTransaction(realm -> {
                NoteModel data = realm.where(NoteModel.class).equalTo("mId", idJob)
                        .isNotNull("mDateTimeAlarm").findFirst();
                if (data != null) {
                    mTitle.setText(data.getTitle());
                    mNote.setText(data.getNote());
                    data.getDateTimeAlarm().deleteFromRealm();
                    if (isEmptyData(data)){
                        data.deleteFromRealm();
                    }
                }
            });
        }

        startVibration();
        startAnim();
        playMusic();
        showInBlockScreen();
        startAlarmDuration();
    }

    private void startVibration() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator != null && mSettings.isVibration()) {
            long[] pattern = {
                    150, 150,
                    150, 150,
                    150, 150,
                    300, 900,
                    150, 150,
                    150, 150,
                    150, 150,
                    300, 900,
                    150, 150,
                    150, 150,
                    150, 150,
                    900, 0};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                mVibrator.vibrate(VibrationEffect.createWaveform(pattern,0));
            }else {
                mVibrator.vibrate(pattern,0);
            }
        }
    }

    public void startAnim() {
        mTimer = new Timer();
        final int SPEED_ANIMATION = 30;
        final int TILT_ANGLE = 30;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (flag) {
                        counter += 2;
                        mImageAlarm.setRotation(counter);
                        if (counter > TILT_ANGLE) {
                            flag = false;
                        }
                    } else {
                        counter -= 2;
                        mImageAlarm.setRotation(counter);
                        if (counter < (TILT_ANGLE * -1)) {
                            flag = true;
                        }
                    }
                });
            }
        }, 0, SPEED_ANIMATION);
    }

    private void playMusic(){
        if (mSettings.isEmptyPath()){
            mMediaPlayer = MediaPlayer.create(this, R.raw.sway);
        }else {
            mMediaPlayer = MediaPlayer.create(this, Uri.parse(mSettings.getPathSong()));
        }
        if (mSettings.isSoundless()){
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
    }

    private void showInBlockScreen(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }

    private void startAlarmDuration(){
        mTimerAlarmDuration = new Timer();
        mTimerAlarmDuration.schedule(new TimerTask() {
            @Override
            public void run() {
                stopAndFinish();
            }
        }, mSettings.getAlarmDuration().getValues());
    }

    private void stopAndFinish() {
        mMediaPlayer.stop();
        mTimer.cancel();
        mVibrator.cancel();
        mTimerAlarmDuration.cancel();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAndFinish();
    }
}
