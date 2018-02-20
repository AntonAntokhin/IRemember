package com.antohin.iremember.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.antohin.iremember.R;
import com.antohin.iremember.utils.Utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AudioMediaPlayer extends ConstraintLayout {

    private MediaPlayer mMediaPlayer;
    private TaskForTimer mTaskForTimer;
    private Timer mTimer = new Timer();
    @BindView(R.id.play_pause) ImageView mPlayPause;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.time_count) TextView mTimeCount;


    public AudioMediaPlayer(Context context) {
        super(context);
    }

    public AudioMediaPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioMediaPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.audio_media_player, this, true);
        ButterKnife.bind(this, this);
    }

    public void init(String urlFile) {
        Uri myUri = Uri.parse(urlFile);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getContext(), myUri);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Snackbar.make(this,R.string.file_not_found,Snackbar.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        mTimeCount.setText(Utils.milSekToMInSek(mMediaPlayer.getDuration()));
        mMediaPlayer.setOnCompletionListener(mp -> {
            mPlayPause.setImageDrawable(getContext().getDrawable(R.drawable.ic_play));
            mProgressBar.setProgress(mMediaPlayer.getDuration());
            mTaskForTimer.cancel();
        });
        mPlayPause.setOnClickListener(v -> {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mPlayPause.setImageDrawable(getContext().getDrawable(R.drawable.ic_play));
                mTaskForTimer.cancel();
            } else {
                mMediaPlayer.start();
                mPlayPause.setImageDrawable(getContext().getDrawable(R.drawable.ic_pause));
                mTaskForTimer = new TaskForTimer();
                mTimer.schedule(mTaskForTimer, 0,300);
            }
        });

        this.setVisibility(VISIBLE);
    }


    class TaskForTimer extends TimerTask {
        @Override
        public void run() {
            mProgressBar.setMax(mMediaPlayer.getDuration());
            mProgressBar.setProgress(mMediaPlayer.getCurrentPosition());
        }
    }
}
