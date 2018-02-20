package com.antohin.iremember.utils;

import com.antohin.iremember.App;
import com.antohin.iremember.R;


public enum DurationAlarm {
    OneMin(App.self().getString(R.string.one_min),0,60 * 1000),
    TwoMin(App.self().getString(R.string.two_min),1,60 * 1000 * 2),
    ThreeMin(App.self().getString(R.string.three_min),2,60 * 1000 * 3),
    FiveMin(App.self().getString(R.string.five_min),3,60 * 1000 * 5),
    TenMin(App.self().getString(R.string.ten_min),4,60 * 1000 * 10),
    Infinity(App.self().getString(R.string.thirty_min),5,60 * 1000 * 30);

    private String mText;
    private int mValues;
    private Integer mId;

    DurationAlarm(final String text, int id, int milliseconds) {
        mText = text;
        mId=id;
        mValues = milliseconds;
    }

    public String getText() {
        return mText;
    }

    public int getId() {
        return mId;
    }

    public int getValues() {
        return mValues;
    }

    public static DurationAlarm getById(int id) {
        for(DurationAlarm e : values()) {
            if(e.mId.equals(id)) return e;
        }
        return ThreeMin;
    }

    @Override
    public String toString() {
        return App.self().getString(R.string.alarm_duration) + " - " + mText;
    }
}
