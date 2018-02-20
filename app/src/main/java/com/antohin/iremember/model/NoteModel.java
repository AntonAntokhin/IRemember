package com.antohin.iremember.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class NoteModel extends RealmObject implements Serializable {

    public static final int DEFAULT_COLOR = -12434878;
    @PrimaryKey
    private String mId;
    private String mTitle;
    private String mNote;
    private int mColor = DEFAULT_COLOR;
    private String mVoicePath;
    private ImageInfo mImageInfo;
    private DateTimeAlarm mDateTimeAlarm;
    private boolean isArchive = false;
    private long mLastEdit;

    public NoteModel(){

    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle!= null ? mTitle : "";
    }

    public void setTitle(String title) {
        if (getTitle().equals(title)) return;
        mTitle = title;
        mLastEdit = new Date().getTime();
    }

    public String getNote() {
        return mNote != null ? mNote : "";
    }

    public void setNote(String note) {
        if (getNote().equals(note)) return;
        mNote = note;
        mLastEdit = new Date().getTime();
    }

    public int getColor() {
        return mColor;
    }

    public boolean isDefaultColor(){
        return mColor == DEFAULT_COLOR;
    }

    public void setColor(int color) {
        mColor = color;
        mLastEdit = new Date().getTime();
    }

    public String getVoicePath() {
        return mVoicePath != null ? mVoicePath :"";
    }

    public void setVoicePath(String nUrlVoicePath) {
        this.mVoicePath = nUrlVoicePath;
        mLastEdit = new Date().getTime();
    }

    public DateTimeAlarm getDateTimeAlarm() {
        return mDateTimeAlarm;
    }

    public boolean isEmptyDateTimeAlarm(){
        return mDateTimeAlarm == null || getAlarmDateTimeText().isEmpty();
    }

    public String getAlarmDateTimeText(){
        return mDateTimeAlarm.getText();
    }

    public String getAlarmDateTime12HourText(){
        final Date dateObj;
        String ss1 = null;
        try {
            String dateTime = mDateTimeAlarm.getText();
            if (dateTime.length() > 5){
                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-MM-dd", Locale.getDefault());
                dateObj = sdf.parse(dateTime);
                ss1 = new SimpleDateFormat("K:mm aa yyyy-MM-dd", Locale.getDefault()).format(dateObj);
            }else {
                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.getDefault());
                dateObj = sdf.parse(dateTime);
                ss1 = new SimpleDateFormat("K:mm aa", Locale.getDefault()).format(dateObj);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ss1;
    }

    public void setDateTimeAlarm(DateTimeAlarm dateTimeAlarm) {
        mDateTimeAlarm = dateTimeAlarm;
        mLastEdit = new Date().getTime();
    }

    public boolean isArchive() {
        return isArchive;
    }

    public void setArchive(boolean archive) {
        isArchive = archive;
        mLastEdit = new Date().getTime();
    }

    public ImageInfo getImageInfo() {
        return mImageInfo ;
    }

    public boolean isEmptyImageInfo(){
        return mImageInfo == null || mImageInfo.getPaths().isEmpty();
    }

    public void setImageInfo(ImageInfo imageInfo) {
        mImageInfo = imageInfo;
        mLastEdit = new Date().getTime();
    }

    public long getLastEdit() {
        return mLastEdit;
    }
}
