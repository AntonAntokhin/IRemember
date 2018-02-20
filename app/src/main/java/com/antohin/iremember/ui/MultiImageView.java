package com.antohin.iremember.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.antohin.iremember.R;
import com.antohin.iremember.model.ImageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiImageView extends LinearLayout {

    private List<ImageView> mViewList = new ArrayList<>();

    public MultiImageView(Context context) {
        super(context);
    }

    public MultiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(),R.layout.item_multi_view, this);
        mViewList.add((ImageView) findViewById(R.id.img1));
        mViewList.add((ImageView) findViewById(R.id.img2));
        mViewList.add((ImageView) findViewById(R.id.img3));
    }

    public void init(ImageInfo imageInfo) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDisplay = displayMetrics.widthPixels;

        if (imageInfo.getPaths().size() > 3 || imageInfo.getPaths().isEmpty())
            throw new IllegalArgumentException("max count item = 3, min = 1. size = " + imageInfo.getPaths().size());


        float h = Collections.min(imageInfo.getHeights(), (o1, o2) -> {
            if (o1 > o2) return 1;
            else if (o1 < o2) return -1;
            else return 0;
        });
        float joinMult = 0;
        for (int i = 0; i < imageInfo.getPaths().size(); i++) {
            float q = imageInfo.getHeights().get(i) / h;
            imageInfo.getMultipliers().add(q);
            joinMult += imageInfo.getWidths().get(i) / q;
        }

        for (int i = 0; i < imageInfo.getPaths().size(); i++) {
            imageInfo.getResultWidths().add((int) ((widthDisplay / joinMult)
                    * (imageInfo.getWidths().get(i) / imageInfo.getMultipliers().get(i))));

            imageInfo.getResultHeights().add((int) (((float) imageInfo.getResultWidths().get(i)
                    / imageInfo.getWidths().get(i)) * imageInfo.getHeights().get(i)));
        }
        for (int i = 0; i < mViewList.size(); i++) {
            mViewList.get(i).setVisibility(GONE);
        }
        for (int i = 0; i < imageInfo.getPaths().size(); i++) {
            RequestOptions myOptions = new RequestOptions()
                    .error(R.drawable.ic_img_not_found)
                    .dontAnimate()
                    .override(imageInfo.getResultWidths().get(i), imageInfo.getResultHeights().get(i));

            Glide.with(getContext())
                    .load(imageInfo.getPaths().get(i))
                    .apply(myOptions)
                    .into(mViewList.get(i));

            ViewGroup.LayoutParams layoutParams = mViewList.get(i).getLayoutParams();
            layoutParams.height = imageInfo.getResultHeights().get(i);
            layoutParams.width = imageInfo.getResultWidths().get(i);
            mViewList.get(i).setLayoutParams(layoutParams);
            mViewList.get(i).setVisibility(VISIBLE);
        }
        this.setVisibility(VISIBLE);
    }
}
