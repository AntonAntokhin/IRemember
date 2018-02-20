package com.antohin.iremember.model;


import com.antohin.iremember.App;
import com.antohin.iremember.R;
import com.antohin.iremember.utils.DurationAlarm;

import io.realm.RealmObject;

public class UserSettings extends RealmObject {
    private String mPathSong = "";
    private String mNameSong = "";
    private int mIdDurationAlarm = DurationAlarm.ThreeMin.getId();
    private boolean isVibration = true;
    private boolean isSoundless = true;
    private boolean is24Format = true;

    public UserSettings() {
    }

    public String getPathSong() {
        return mPathSong;
    }

    public void setPathSong(String pathSong) {
        mPathSong = pathSong;
    }

    public boolean isVibration() {
        return isVibration;
    }

    public void setVibration(boolean vibration) {
        isVibration = vibration;
    }

    public boolean isSoundless() {
        return isSoundless;
    }

    public void setSoundless(boolean soundless) {
        isSoundless = soundless;
    }

    public boolean is24Format() {
        return is24Format;
    }

    public void set24Format(boolean is24Format) {
        this.is24Format = is24Format;
    }

    public String getNameSong() {
        return App.self().getString(R.string.alarm_song) + " - " +
                (!mNameSong.isEmpty() ? mNameSong : App.self().getString(R.string.default_song));
    }

    public void setNameSong(String nameSong) {
        mNameSong = nameSong;
    }

    public DurationAlarm getAlarmDuration() {
        return DurationAlarm.getById(mIdDurationAlarm);
    }

    public void setAlarmDuration(int idDurationAlarm) {
        mIdDurationAlarm = idDurationAlarm;
    }

    public boolean isEmptyPath(){
        return mPathSong.isEmpty();
    }
}
