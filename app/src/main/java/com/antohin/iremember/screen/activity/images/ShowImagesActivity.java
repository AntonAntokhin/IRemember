package com.antohin.iremember.screen.activity.images;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.bumptech.glide.Glide;
import com.antohin.iremember.R;
import com.antohin.iremember.model.NoteModel;
import com.antohin.iremember.screen.activity.BaseActivity;
import com.antohin.iremember.utils.HackViewPager;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.antohin.iremember.screen.activity.main.MainActivity.DATA_ID_FROM_REALM;

public class ShowImagesActivity extends BaseActivity {

    public static Intent createIntent(Context context, String id){
        return new Intent(context,ShowImagesActivity.class).putExtra(DATA_ID_FROM_REALM,id);
    }
    private NoteModel mData;
    private SamplePagerAdapter mSamplePagerAdapter;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);
        ButterKnife.bind(this);
        registerActionBar(mToolbar,R.string.images);
        if (getIntent().getExtras() != null && getIntent().getExtras().getString(DATA_ID_FROM_REALM) != null) {
            mData = getDataNote(getIntent().getExtras().getString((DATA_ID_FROM_REALM)));
            if (mData.isEmptyImageInfo()) {
                finish();
                return;
            }
            mToolbar.setTitle("1 " + getString(R.string.of) + " " + mData.getImageInfo().getPaths().size());

            HackViewPager viewPager = findViewById(R.id.photo_view);

            mSamplePagerAdapter = new SamplePagerAdapter();

            mSamplePagerAdapter.setListPath(mData.getImageInfo().getPaths());
            viewPager.setAdapter(mSamplePagerAdapter);
        }else {
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mSamplePagerAdapter.removeCurrentItem();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    class SamplePagerAdapter extends PagerAdapter {
        private List<String> mListPath;
        private int mCurrentPosition;

        @Override
        public int getCount() {
            return mListPath != null ? mListPath.size() : 0;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
            mToolbar.setTitle((position + 1) + " " + getString(R.string.of) + " " + mData.getImageInfo().getPaths().size());
            mCurrentPosition = position;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            Glide.with(getApplicationContext()).load(mListPath.get(position)).into(photoView);
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        void setListPath(List<String> listPath) {
            mListPath = listPath;
        }

        void removeCurrentItem() {
            setResult(RESULT_OK);
            Realm.getDefaultInstance().executeTransaction(realm -> {
                mData.getImageInfo().getWidths()
                        .remove(mCurrentPosition);
                mData.getImageInfo().getHeights()
                        .remove(mCurrentPosition);
                mData.getImageInfo().getPaths()
                        .remove(mCurrentPosition);
                if (mListPath.size() == 0) {
                    finish();
                    mListPath = null;
                    mData.getImageInfo().deleteFromRealm();
                }else {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private NoteModel getDataNote(String id) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(NoteModel.class).equalTo("mId", id).findFirst();
    }
}
