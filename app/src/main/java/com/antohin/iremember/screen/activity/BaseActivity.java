package com.antohin.iremember.screen.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;


public abstract class BaseActivity extends AppCompatActivity implements MvpView {
    private  RxPermissions mRxPermissions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRxPermissions = new RxPermissions(this);
    }

    protected void registerActionBar(Toolbar toolbar, @StringRes int idTextToolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(idTextToolbar);
        }
    }

    public String getStringById(int idRes) {
        return getString(idRes);
    }

    public Context getContext() {
        return this;
    }

    public void showToast(@StringRes int textResId) {
        showToast(getString(textResId));
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

}
