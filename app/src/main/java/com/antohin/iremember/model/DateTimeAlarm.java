package com.antohin.iremember.model;

import com.evernote.android.job.JobManager;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;


public class DateTimeAlarm extends RealmObject {

    private int mMinute;
    private int mHour;
    private int mDay;
    private int mMonth;
    private int mYear;
    private int mIdJob = -1;

    public DateTimeAlarm() {
    }

    public void setDate(int year, int month, int day, int hour, int minute) {
        mMinute = minute;
        mHour = hour;
        mDay = day;
        mMonth = month;
        mYear = year;
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();
        sb.append(mHour < 10 ? "0" + mHour : mHour)
                .append(":")
                .append(mMinute < 10 ? "0" + mMinute : mMinute);
        if (mYear != 0 && mMonth != 0 && mDay != 0) {
            sb.append(" ")
                    .append(mYear)
                    .append("-")
                    .append(mMonth < 10 ? "0" + mMonth : mMonth)
                    .append("-")
                    .append(mDay < 10 ? "0" + mDay : mDay);
        }
        return new String(sb);
    }

    public long getMillisToTask() {
        if (mYear != 0 && mMonth != 0 && mDay != 0) {
            return new DateTime(mYear, mMonth, mDay, mHour, mMinute).getMillis() - new Date().getTime();
        } else {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DateTime(year, month, day, mHour, mMinute).getMillis() - new Date().getTime();
        }
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public int getIdJob() {
        return mIdJob;
    }

    public void setIdJob(int idJob) {
        if (mIdJob != -1) {
            JobManager.instance().cancel(mIdJob);
        }
        mIdJob = idJob;
    }
}
