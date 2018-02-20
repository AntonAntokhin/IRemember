package com.antohin.iremember.screen.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.antohin.iremember.R;
import com.antohin.iremember.model.DateTimeAlarm;
import com.antohin.iremember.model.ImageInfo;
import com.antohin.iremember.model.NoteModel;
import com.antohin.iremember.model.UserSettings;
import com.antohin.iremember.screen.activity.details.DetailsNoteActivity;
import com.antohin.iremember.ui.ChoiceDateTime;
import com.antohin.iremember.utils.FileUtils;
import com.evernote.android.job.JobRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import io.realm.Realm;

import static android.app.Activity.RESULT_OK;
import static com.antohin.iremember.screen.activity.details.DetailsNoteActivity.REQUEST_SHOW_OR_DELETE_IMAGE;
import static com.antohin.iremember.screen.activity.main.MainActivity.REQUEST_ADD_NOTE;
import static com.antohin.iremember.screen.activity.main.MainActivity.REQUEST_CAMERA_CAPTURE;
import static com.antohin.iremember.screen.activity.main.MainActivity.REQUEST_IMAGE_GALLERY;
import static com.antohin.iremember.screen.activity.main.MainActivity.REQUEST_VOICE_WRITE;
import static com.antohin.iremember.utils.FileUtils.INDEX_PATH;
import static com.antohin.iremember.utils.FileUtils.INDEX_TEXT_RESULT;

public abstract class PresenterForContent<T extends MvpView> extends BasePresenter<T> {

    private static final int MAX_COUNT_PHOTO_IN_NOTE = 6;
    protected Realm mRealm;
    protected UserSettings mUserSettings;
    private NoteModel mCurrentModel;
    private ChoiceDateTime mChoiceDateTime;

    public PresenterForContent() {
        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(realm -> {
            mUserSettings = realm.where(UserSettings.class).findFirst();
            if (mUserSettings == null){
                mUserSettings = realm.createObject(UserSettings.class);
            }
        });
    }

    private void navigationMic() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mView.getStringById(R.string.tell_me_something));
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.GET_AUDIO", true);
        try {
            mView.startActivityForResult(intent, REQUEST_VOICE_WRITE);
        } catch (ActivityNotFoundException a) {
            Log.d(this.getClass().getSimpleName(), "navigationMic: " + a);
        }
    }

    private void navigationGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mView.startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                REQUEST_IMAGE_GALLERY);
    }

    private void navigationCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mView.getContext().getPackageManager()) != null) {
            mView.startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
        }
    }

    private void navigationAlarm() {
        if (!mChoiceDateTime.isShowing()) {
            mChoiceDateTime.show();
        }
    }

    public void initAlarm() {
        mChoiceDateTime = new ChoiceDateTime(mView.getContext(), mUserSettings.is24Format());
        mChoiceDateTime.setOnSelectTimeDate((year, month, day, hour, minute) -> {
            mCurrentModel = getCurrentNote();
            Realm.getDefaultInstance().executeTransaction(realm -> {
                if (mCurrentModel.getDateTimeAlarm() == null) {
                    mCurrentModel.setDateTimeAlarm(realm.createObject(DateTimeAlarm.class));
                }
                mCurrentModel.getDateTimeAlarm().setDate(year, month, day, hour, minute);
                if (mCurrentModel.getDateTimeAlarm().getMillisToTask() < 1) {
                    new android.support.v7.app.AlertDialog.Builder(mView.getContext())
                            .setTitle(R.string.error)
                            .setMessage(R.string.past_time)
                            .setPositiveButton(R.string.ok, null)
                            .create()
                            .show();
                    return;
                }
                mCurrentModel.getDateTimeAlarm().setIdJob(new JobRequest.Builder(mCurrentModel.getId())
                        .setExact(mCurrentModel.getDateTimeAlarm().getMillisToTask())
                        .setUpdateCurrent(true)
                        .build()
                        .schedule());
                whatDoItResult(mCurrentModel.getId());
            });
        });
    }


    public void onNavigationAccContent(int idItem) {
        switch (idItem) {
            case R.id.navigation_add:
                mView.startActivityForResult(DetailsNoteActivity.createIntent(mView.getContext()), REQUEST_ADD_NOTE);
                break;
            case R.id.navigation_mic:
                mView.getRxPermissions().request(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                navigationMic();
                            } else {
                                showAlertMicPermission();
                            }
                        });
                break;
            case R.id.navigation_camera:
                mView.getRxPermissions().request(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                navigationCamera();
                            } else {
                                showAlertCameraPermission();
                            }
                        });
                break;
            case R.id.navigation_gallery:
                mView.getRxPermissions().request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                navigationGallery();
                            } else {
                                showAlertGalleryPermission();
                            }
                        });
                break;
            case R.id.navigation_alarm:
                navigationAlarm();
                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_NOTE || requestCode == REQUEST_SHOW_OR_DELETE_IMAGE) {
                updateState();
                return;
            }
            if (data != null) {
                mCurrentModel = getCurrentNote();
                if (requestCode == REQUEST_VOICE_WRITE) {
                  voiceResult(data);
                }
                if (requestCode == REQUEST_CAMERA_CAPTURE && data.getExtras() != null) {
                 cameraResult(data);
                }
                if (requestCode == REQUEST_IMAGE_GALLERY) {
                    galleryResult(data);
                }
            }
        }
    }

    private void voiceResult(@Nonnull Intent data){
        final List<String> pathAndText = FileUtils.writeFileByMic(data, mView.getContext());
        if (pathAndText == null || pathAndText.isEmpty()) return;
        mRealm.executeTransaction(realm -> {
            mCurrentModel.setVoicePath(pathAndText.get(INDEX_PATH) != null ? pathAndText.get(0) : "");
            mCurrentModel.setNote(pathAndText.get(INDEX_TEXT_RESULT) != null ? pathAndText.get(1) : "");
            whatDoItResult(mCurrentModel.getId());
        });
    }

    private void cameraResult(@Nonnull Intent data){
        if (mCurrentModel.getImageInfo() != null
                && mCurrentModel.getImageInfo().getPaths().size() + 1 > MAX_COUNT_PHOTO_IN_NOTE) {
            showAlertMaxImageViewView();
            return;
        }
        String pathImage;
        if (data.getData()!=null){
            pathImage = FileUtils.getPath(mView.getContext(),data.getData());
        }else {
            pathImage = FileUtils.getPathImage(mView.getContext(), (Bitmap) data.getExtras().get("data"));
        }
        mRealm.executeTransaction(realm -> {
            if (mCurrentModel.getImageInfo() == null)
                mCurrentModel.setImageInfo(realm.createObject(ImageInfo.class));
            mCurrentModel.getImageInfo().addPath(pathImage);
            whatDoItResult(mCurrentModel.getId());
        });
    }

    private void galleryResult(@Nonnull Intent data) {
        List<String> list = new ArrayList<>();
        if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            if (mCurrentModel.getImageInfo() != null)
                count += mCurrentModel.getImageInfo().getPaths().size();
            if (count > MAX_COUNT_PHOTO_IN_NOTE) {
                showAlertMaxImageViewView();
                return;
            }
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                list.add(FileUtils.getPath(mView.getContext(), data.getClipData().getItemAt(i).getUri()));
            }

        } else if (data.getData() != null) {
            if (mCurrentModel.getImageInfo() != null
                    && mCurrentModel.getImageInfo().getPaths().size() + 1 > MAX_COUNT_PHOTO_IN_NOTE) {
                showAlertMaxImageViewView();
                return;
            }
            list.add(FileUtils.getPath(mView.getContext(), data.getData()));
        }

        mRealm.executeTransaction(realm -> {
            if (mCurrentModel.getImageInfo() == null)
                mCurrentModel.setImageInfo(realm.createObject(ImageInfo.class));
            mCurrentModel.getImageInfo().setPaths(list);
            whatDoItResult(mCurrentModel.getId());
        });
    }

    private void showAlertMaxImageViewView() {
        new AlertDialog.Builder(mView.getContext())
                .setTitle(R.string.max_image_title)
                .setMessage(R.string.max_image_message)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }

    private void showAlertMicPermission() {
        new AlertDialog.Builder(mView.getContext())
                .setTitle(R.string.permission)
                .setMessage(R.string.permission_microphone_rationale)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }

    private void showAlertGalleryPermission() {
        new AlertDialog.Builder(mView.getContext())
                .setTitle(R.string.permission)
                .setMessage(R.string.permission_gallery)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }

    private void showAlertCameraPermission() {
        new AlertDialog.Builder(mView.getContext())
                .setTitle(R.string.permission)
                .setMessage(R.string.permission_camera)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }

    protected abstract void whatDoItResult(String noteId);

    protected abstract void updateState();

    protected abstract NoteModel getCurrentNote();

}
