package com.antohin.iremember.model;


import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ImageInfo extends RealmObject {
    private RealmList<String> mPaths = new RealmList<>();
    private RealmList<Integer> mWidths =  new RealmList<>();
    private RealmList<Integer> mHeights =  new RealmList<>();
    private RealmList<Integer> mResultWidths =  new RealmList<>();
    private RealmList<Integer> mResultHeights =  new RealmList<>();
    private RealmList<Float> mMultipliers =  new RealmList<>();


    public ImageInfo() { }

    public ImageInfo(ImageInfo info, int begin,int end){
        for (int i = begin; i < end; i++) {
            this.mPaths.add(info.getPaths().get(i));
            this.mWidths.add(info.getWidths().get(i));
            this.mHeights.add(info.getHeights().get(i));
        }
    }

    public void addPath(String path) {
        mPaths.add(path);
        getDropBoxImgSize(path);
    }

    public List<String> getPaths() {
        return mPaths;
    }

    public void setPaths(List<String> paths) {
        mPaths.addAll(paths);
        for (int i = 0; i < paths.size(); i++) {
            getDropBoxImgSize(paths.get(i));
        }
    }

    public List<Integer> getWidths() {
        return mWidths;
    }

    public List<Integer> getHeights() {
        return mHeights;
    }

    public List<Integer> getResultWidths() {
        return mResultWidths;
    }

    public List<Integer> getResultHeights() {
        return mResultHeights;
    }

    public List<Float> getMultipliers() {
        return mMultipliers;
    }

    private void getDropBoxImgSize(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(Uri.parse(path).getPath()).getAbsolutePath(), options);
        int orientation = -1;
        try {
            ExifInterface exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90){
            mHeights.add(options.outWidth);
            mWidths.add(options.outHeight);
        }else {
            mHeights.add(options.outHeight);
            mWidths.add(options.outWidth);
        }
    }
}
