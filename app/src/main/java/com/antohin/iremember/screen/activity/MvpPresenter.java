package com.antohin.iremember.screen.activity;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V view);
    void detachView();
}
