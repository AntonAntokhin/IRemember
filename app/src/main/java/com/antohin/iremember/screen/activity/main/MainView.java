package com.antohin.iremember.screen.activity.main;


import com.antohin.iremember.screen.activity.MvpView;
import com.antohin.iremember.screen.fragment.main.MainFragment;


interface MainView extends MvpView {

    void setFragment(MainFragment.ViewState viewState);

    void setToolsFragment();
}
