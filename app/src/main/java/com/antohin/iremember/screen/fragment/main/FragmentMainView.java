package com.antohin.iremember.screen.fragment.main;


import com.antohin.iremember.screen.activity.MvpView;


public interface FragmentMainView extends MvpView {
    void setAdapter(RecyclerViewAdapter adapter);
    MainFragment.ViewState getCurrentState();
    void setupState(MainFragment.ViewState viewState);
}
