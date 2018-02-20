package com.antohin.iremember.screen.activity;

import android.support.annotation.CallSuper;

public abstract class BasePresenter<T extends MvpView> implements MvpPresenter<T> {

    protected T mView;

    @Override
    @CallSuper
    public void attachView(final T view) {
        mView = view;
    }

    @Override
    @CallSuper
    public void detachView() {
        mView = null;
    }

    protected boolean isViewDetached() {
        return mView == null;
    }

}
