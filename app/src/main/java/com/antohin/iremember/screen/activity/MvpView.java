package com.antohin.iremember.screen.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;

import com.tbruyelle.rxpermissions2.RxPermissions;


public interface MvpView  {

    Context getContext();

    void showToast(@StringRes int textResId);

    void showToast(String text);

    void finish();

    String getStringById(@StringRes int resId);

    void startActivityForResult(Intent intent, int requestCode);

    RxPermissions getRxPermissions();
}