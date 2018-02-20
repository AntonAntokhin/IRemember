package com.antohin.iremember.screen.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.antohin.iremember.R;
import com.antohin.iremember.model.UserSettings;
import com.antohin.iremember.screen.fragment.main.MainFragment;
import com.antohin.iremember.ui.dialog.SelectAlarmDurationDialog;
import com.antohin.iremember.ui.dialog.SelectRingtoneDialog;
import com.antohin.iremember.utils.FileUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;

import static android.app.Activity.RESULT_OK;

public class ToolsFragment extends BaseFragment {
    public static final String TAG = ToolsFragment.class.getSimpleName();
    private static final int REQUEST_SONG = 3076;
    private Dialog mDialogRingtone;
    private Realm mRealm;
    private UserSettings mSettings;
    private Dialog mDialogAlarmDuration;
    public static ToolsFragment newInstance() {
        return new ToolsFragment();
    }

    @BindView(R.id.ringtone) TextView mRingtone;
    @BindView(R.id.alarm_duration) TextView mAlarmDuration;
    @BindView(R.id.vibration) Switch mVibration;
    @BindView(R.id.soundless) Switch mSoundless;
    @BindView(R.id.use_24_hour_format) Switch mUse24Format;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(realm -> {
            mSettings = mRealm.where(UserSettings.class).findFirst();
            if (mSettings == null) {
                mSettings = realm.createObject(UserSettings.class);
            }
        });
        setNavigationAndToolbar(MainFragment.ViewState.Tools);
        initField();
        initDialog();
    }

    private void initField(){
        mRingtone.setText(mSettings.getNameSong());
        mAlarmDuration.setText(mSettings.getAlarmDuration().toString());
        mVibration.setChecked(mSettings.isVibration());
        mSoundless.setChecked(mSettings.isSoundless());
        mUse24Format.setChecked(mSettings.is24Format());
    }

    private void initDialog() {
        if (mDialogRingtone == null) {
            mDialogRingtone = SelectRingtoneDialog.newInstance(getContext(), (path, name) -> {
                mRealm.executeTransaction(realm -> {
                    mSettings.setPathSong(path);
                    mSettings.setNameSong(name);
                });
                mRingtone.setText(mSettings.getNameSong());
            }, this::choiceUserSongForAlarm,mSettings.getPathSong());
        }
        if (mDialogAlarmDuration == null) {
            mDialogAlarmDuration = SelectAlarmDurationDialog.newInstance(getContext(),pos -> {
                mRealm.executeTransaction(realm -> mSettings.setAlarmDuration(pos));
                mAlarmDuration.setText(mSettings.getAlarmDuration().toString());
            },mSettings.getAlarmDuration().getId());
        }
    }



    @OnClick(R.id.ringtone)
    public void choiceRingtone() {
        if (!mDialogRingtone.isShowing()) {
            mDialogRingtone.show();
        }
    }

    @OnClick(R.id.alarm_duration)
    public void choiceAlarmDuration() {
        if (!mDialogAlarmDuration.isShowing()) {
            mDialogAlarmDuration.show();
        }
    }

    @OnCheckedChanged(R.id.use_24_hour_format)
    public void onChangedHourFormat(Switch s) {
        if (s.isChecked()){
            s.setText(new StringBuilder(getString(R.string.use_24_hour_format))
                    .append(" - ")
                    .append("13:00")
            );
            mRealm.executeTransaction(realm -> mSettings.set24Format(true));
        }else {
            s.setText(new StringBuilder(getString(R.string.use_24_hour_format))
                    .append(" - ")
                    .append("1:00 PM")
            );
            mRealm.executeTransaction(realm -> mSettings.set24Format(false));
        }
    }

    @OnCheckedChanged(R.id.vibration)
    public void onChangedVibration(Switch s) {
        if (s.isChecked()) {
            mRealm.executeTransaction(realm -> mSettings.setVibration(true));
        } else {
            mRealm.executeTransaction(realm -> mSettings.setVibration(false));
        }
    }

    @OnCheckedChanged(R.id.soundless)
    public void onChangedSoundless(Switch s) {
        if (s.isChecked()) {
            mRealm.executeTransaction(realm -> mSettings.setSoundless(true));
        } else {
            mRealm.executeTransaction(realm -> mSettings.setSoundless(false));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_SONG){
            String path = FileUtils.getPath(getContext(), data.getData());
            if (path == null) return;
            String name  = FileUtils.getNameFileByPath(path);
            mRealm.executeTransaction(realm -> {
                mSettings.setNameSong(name);
                mSettings.setPathSong(path);
            });
            mRingtone.setText(mSettings.getNameSong());
            mDialogRingtone.dismiss();
        }
    }

    private void choiceUserSongForAlarm() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                REQUEST_SONG);
    }
}
