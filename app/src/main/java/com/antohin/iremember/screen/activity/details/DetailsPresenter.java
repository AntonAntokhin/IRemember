package com.antohin.iremember.screen.activity.details;

import android.content.Intent;


import com.antohin.iremember.R;
import com.antohin.iremember.model.NoteModel;
import com.antohin.iremember.screen.activity.PresenterForContent;

import java.util.UUID;

import io.realm.Realm;

import static com.antohin.iremember.model.NoteModel.DEFAULT_COLOR;
import static com.antohin.iremember.screen.activity.main.MainActivity.DATA_ID_FROM_REALM;


public class DetailsPresenter extends PresenterForContent<DetailsView> {

    private NoteModel mData;
    private Realm mRealm;

    private boolean isEdit = false;
    private int mColorBackground = DEFAULT_COLOR;

    DetailsPresenter() {
        mRealm = Realm.getDefaultInstance();
    }


    boolean initIntent(Intent intent) {
        if (intent.getExtras() != null && intent.getExtras().getString(DATA_ID_FROM_REALM) != null) {
            mData = getDataNoteById(intent.getExtras().getString((DATA_ID_FROM_REALM)));
            return initFromRealmData();
        }
        return false;
    }

    private boolean initFromRealmData() {
        if (mData == null) {
            mView.finish();
            return false;
        }
        mView.setTitle(mData.getTitle());
        mView.setNote(mData.getNote());
        if (!mData.getVoicePath().isEmpty()) {
            mView.setPlayer(mData.getVoicePath());
        }
        if (!mData.isEmptyImageInfo()) {
            mView.setInitImageView(mRealm.copyFromRealm(mData.getImageInfo()));
        } else {
            mView.setMultiImageViewGone();
        }
        if (!mData.isEmptyDateTimeAlarm()) {
            if (mUserSettings.is24Format()) mView.initAlarm(mData.getAlarmDateTimeText());
            else mView.initAlarm(mData.getAlarmDateTime12HourText());
        }
        if (!mData.isDefaultColor()) mView.setBackgroundColor(mData.getColor());
        return isEdit = true;
    }

    private NoteModel getDataNoteById(String id) {
        return mRealm.where(NoteModel.class).equalTo("mId", id).findFirst();
    }

    void onItemSelected(int idItem) {
        switch (idItem) {
            case R.id.navigation_add:
                mView.onClickAddContent();
                break;
            case R.id.navigation_delete:
                deleteNote();
                break;
            case R.id.navigation_color:
                mView.onClickChoiceColor();
                break;
            case android.R.id.home:
            case R.id.action_save:
                editOrCreate();
                break;
            case R.id.action_archive:
                moveUnToArchive();
                break;
        }
    }

    void editOrCreate() {
        if (isEdit) {
            editData();
        } else if (!mView.getTitleData().isEmpty() || !mView.getNote().isEmpty()) {
           createNewData();
        }
        mView.finish();
    }

    private void editData(){
        mRealm.executeTransaction(realm1 -> {
            if (mData == null) return;
            try {
                mData.setTitle(mView.getTitleData());
                mData.setNote(mView.getNote());
            }catch (IllegalStateException e){
                return;
            }
            if (mColorBackground != DEFAULT_COLOR){
                mData.setColor(mColorBackground);
            }
            if (isEmptyData(mData)) {
                mData.deleteFromRealm();
            }
        });
    }

    private void createNewData(){
        mRealm.executeTransaction(realm1 -> {
            mData = realm1.createObject(NoteModel.class, UUID.randomUUID().toString());
            mData.setTitle(mView.getTitleData());
            mData.setNote(mView.getNote());
            if (mColorBackground != DEFAULT_COLOR){
                mData.setColor(mColorBackground);
            }
            if (isEmptyData(mData)){
                mData.deleteFromRealm();
            }
        });
    }

    private void moveUnToArchive() {
        mRealm.executeTransaction(realm -> {
            if (mData == null) {
                mData = realm.createObject(NoteModel.class, UUID.randomUUID().toString());
            }
            mData.setTitle(mView.getTitleData());
            mData.setNote(mView.getNote());
            if (mData.isArchive()) {
                mData.setArchive(false);
            }else {
                mData.setArchive(true);
            }
            if (isEmptyData(mData)) {
                mData.deleteFromRealm();
            }
        });
        mView.finish();
    }

    private void deleteNote(){
        if (mData == null) {
            mView.finish();
            return;
        }
        mRealm.executeTransaction(realm -> {
            if (isEmptyData(mData)) {
                mData.deleteFromRealm();
                mView.finish();
            } else {
                mView.createAlertForDelete();
            }
        });

    }

    String getDataId(){
        return mData.getId();
    }

    public static boolean isEmptyData(NoteModel data) {
        return data.getImageInfo() == null
                && data.getDateTimeAlarm() == null
                && data.getTitle().isEmpty()
                && data.getNote().isEmpty()
                && data.getVoicePath().isEmpty();
    }

    void initColor(int color) {
        mColorBackground = color;
    }

    void deleteFromRealm() {
        mRealm.executeTransaction(realm -> {
            if (mData != null) {
                mData.deleteFromRealm();
                mView.finish();
            }
        });
    }


    @Override
    protected void whatDoItResult(String noteId) {
        if (mData == null) {
            mData = mRealm.where(NoteModel.class).equalTo("mId", noteId).findFirst();
        }
        initFromRealmData();
    }

    @Override
    protected void updateState() {
        initFromRealmData();
    }

    @Override
    protected NoteModel getCurrentNote() {
        if (mData == null) {
            mRealm.executeTransaction(realm -> mData = realm.createObject(NoteModel.class, UUID.randomUUID().toString()));
        }
        return mData;
    }

    int getColorBackground() {
        return mColorBackground;
    }

    boolean isNoteArchive(){
        return mData != null && mData.isArchive();
    }
}
