package com.antohin.iremember.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.antohin.iremember.R;
import com.antohin.iremember.model.ImageInfo;

public class SixImageView extends LinearLayout {

    MultiImageView mFirstMultiImage;
    MultiImageView mTwoMultiImage;

    public SixImageView(Context context) {
        super(context);
    }

    public SixImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SixImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SixImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.item_six_image_view, this);
        mFirstMultiImage = findViewById(R.id.first);
        mTwoMultiImage = findViewById(R.id.two);
    }

    public void init(ImageInfo info){
        this.setVisibility(VISIBLE);
        if (info.getPaths().size()>3){
            mFirstMultiImage.init(new ImageInfo(info,0,3));
            mTwoMultiImage.init(new ImageInfo(info,3,info.getPaths().size()));
        }else {
            mFirstMultiImage.init(info);
            mTwoMultiImage.setVisibility(GONE);
        }
    }

}
