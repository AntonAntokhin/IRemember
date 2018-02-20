package com.antohin.iremember.screen.activity.details;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.antohin.iremember.R;
import com.antohin.iremember.model.ImageInfo;
import com.antohin.iremember.screen.activity.BaseActivity;
import com.antohin.iremember.screen.activity.images.ShowImagesActivity;
import com.antohin.iremember.ui.AudioMediaPlayer;
import com.antohin.iremember.ui.SixImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.antohin.iremember.model.NoteModel.DEFAULT_COLOR;
import static com.antohin.iremember.screen.activity.main.MainActivity.DATA_ID_FROM_REALM;


public class DetailsNoteActivity extends BaseActivity implements DetailsView, CircleAdapter.OnClickItemColor {
    public static final int REQUEST_SHOW_OR_DELETE_IMAGE = 4035;

    public static Intent createIntent(Context context) {
        return new Intent(context, DetailsNoteActivity.class);
    }

    public static Intent createIntent(Context context, String id) {
        return new Intent(context, DetailsNoteActivity.class).putExtra(DATA_ID_FROM_REALM, id);
    }

    @BindView(R.id.root) ConstraintLayout mRoot;
    @BindView(R.id.title) EditText mTitle;
    @BindView(R.id.note) EditText mNote;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.audio_media) AudioMediaPlayer mPlayer;
    @BindView(R.id.multiImageView) SixImageView mMultiImage;
    @BindView(R.id.scroll) ScrollView mScrollView;
    @BindView(R.id.alarm) ImageView mAlarmImage;
    @BindView(R.id.alarm_date_time) TextView mAlarmText;
    @BindView(R.id.navigation) BottomNavigationView mNavigation;
    private BottomSheetDialog mSheetDialogContent;

    private AlertDialog mDialogDelete;
    private BottomSheetDialog mColorSheetDialog;
    private DetailsPresenter mPresenter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        mPresenter.onItemSelected(item.getItemId());
        return false;
    };

    private View.OnClickListener AddContentOnClick = v -> {
        mPresenter.onNavigationAccContent(v.getId());
        mSheetDialogContent.cancel();
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_note);
        ButterKnife.bind(this);
        mPresenter = new DetailsPresenter();
        mPresenter.attachView(this);
        mPresenter.initAlarm();
        initDialog();
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (mPresenter.initIntent(getIntent())){
            registerActionBar(mToolbar,R.string.edit);
        }else {
            registerActionBar(mToolbar,R.string.created);
        }
    }

    @OnClick(R.id.multiImageView)
    public void onClickImages() {
        startActivityForResult(ShowImagesActivity.createIntent(this, mPresenter.getDataId()), REQUEST_SHOW_OR_DELETE_IMAGE);
    }

    private void initDialog(){
        mSheetDialogContent = new BottomSheetDialog(this,R.style.BottomSheetDialog);
        View contentView = getLayoutInflater().inflate(R.layout.menu_bottom_add_content, null);
        contentView.findViewById(R.id.navigation_alarm).setOnClickListener(AddContentOnClick);
        contentView.findViewById(R.id.navigation_mic).setOnClickListener(AddContentOnClick);
        contentView.findViewById(R.id.navigation_gallery).setOnClickListener(AddContentOnClick);
        contentView.findViewById(R.id.navigation_camera).setOnClickListener(AddContentOnClick);
        mSheetDialogContent.setContentView(contentView );

        mColorSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialog);
        View colorView = getLayoutInflater().inflate(R.layout.menu_bottom_color, null);
        RecyclerView rv = colorView.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new GridLayoutManager(this,4));
        rv.setAdapter( new CircleAdapter().setOnClickItemColor(this));
        mColorSheetDialog.setContentView(colorView);


        mDialogDelete = new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_message_alert)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    mPresenter.deleteFromRealm();
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    @Override
    public void onClickAddContent(){
        if (!mSheetDialogContent.isShowing()){
            mSheetDialogContent.show();
        }
    }



    @Override
    public void onClickChoiceColor(){
        if (!mColorSheetDialog.isShowing()){
            mColorSheetDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        mPresenter.editOrCreate();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu_check_archive, menu);
        MenuItem item = menu.findItem(R.id.action_archive);
        if (mPresenter.isNoteArchive()){
            item.setIcon(getDrawable(R.drawable.ic_unarchive));
        }else {
            item.setIcon(getDrawable(R.drawable.ic_archive));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPresenter.onItemSelected(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public String getTitleData(){
        return mTitle.getText().toString();
    }

    @Override
    public void setNote(String note) {
        mNote.setText(note);
    }

    @Override
    public String getNote() {
        return mNote.getText().toString();
    }

    @Override
    public void setPlayer(String voicePath) {
        mPlayer.init(voicePath);
    }

    @Override
    public void initAlarm(String alarmDateTimeText) {
        mAlarmImage.setVisibility(View.VISIBLE);
        mAlarmText.setVisibility(View.VISIBLE);
        mAlarmText.setText(alarmDateTimeText);
    }

    @Override
    public void setInitImageView(ImageInfo imageInfo) {
        mMultiImage.init(imageInfo);
    }

    @Override
    public void setMultiImageViewGone() {
        mMultiImage.setVisibility(View.GONE);
    }


    public void setBackground(ColorDrawable colorDrawable){
        mPresenter.initColor(colorDrawable.getColor());
        setBackgroundColor(colorDrawable.getColor());
    }

    @Override
    public void createAlertForDelete(){
        if (!mDialogDelete.isShowing()){
            mDialogDelete.show();
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        mRoot.setBackgroundColor(color);
    }

    @Override
    public void onClickColor(ColorDrawable colorDrawable) {
        if (colorDrawable.getColor()!= DEFAULT_COLOR){
            setBackground(colorDrawable);
        }else if(mPresenter.getColorBackground() != DEFAULT_COLOR){
            setBackgroundColor(ContextCompat.getColor(getContext(),R.color.default_color_window));
            mPresenter.initColor(DEFAULT_COLOR);
        }
        mColorSheetDialog.dismiss();
    }

    @Override
    public void finish(){
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
