package com.antohin.iremember.screen.activity.details;


import com.antohin.iremember.model.ImageInfo;
import com.antohin.iremember.screen.activity.MvpView;

public interface DetailsView extends MvpView {
    void setTitle(String title);

    String getTitleData();

    void setNote(String note);

    String getNote();

    void setPlayer(String voicePath);

    void initAlarm(String alarmDateTimeText);

    void setInitImageView(ImageInfo imageInfo);

    void setMultiImageViewGone();

    void onClickAddContent();

    void onClickChoiceColor();

    void createAlertForDelete();

    void setBackgroundColor(int color);
}
