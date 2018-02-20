package com.antohin.iremember.screen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.antohin.iremember.R;
import com.antohin.iremember.screen.activity.BaseActivity;
import com.antohin.iremember.screen.activity.MvpView;
import com.antohin.iremember.screen.fragment.main.MainFragment;
import com.tbruyelle.rxpermissions2.RxPermissions;


public abstract class BaseFragment extends Fragment implements MvpView {

    private RxPermissions mRxPermissions;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRxPermissions = new RxPermissions(getActivity());
    }

    protected void setNavigationAndToolbar(MainFragment.ViewState state) {
        if (getActivity() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getActivity();
            if (activity != null && activity instanceof BaseActivity
                    && activity.getSupportActionBar() != null) {
                NavigationView navigationView = activity.findViewById(R.id.nav_view);
                if (navigationView != null) navigationView.setCheckedItem(state.getIdResItem());
                activity.getSupportActionBar().setTitle(state.getIdName());
            }
        }
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    @Override
    public void showToast(int textResId) {
        Toast.makeText(getContext(), textResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void onRefresh(){ }

    @Override
    public void finish() { }

    @Override
    public String getStringById(int resId) {
        return getString(resId);
    }
}
