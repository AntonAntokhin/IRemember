package com.antohin.iremember.screen.fragment.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antohin.iremember.R;
import com.antohin.iremember.screen.activity.details.DetailsNoteActivity;
import com.antohin.iremember.screen.fragment.BaseFragment;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.antohin.iremember.screen.activity.main.MainActivity.REQUEST_ADD_NOTE;


public class MainFragment extends BaseFragment implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        FragmentMainView, RecyclerViewAdapter.OnClickItemRecycler {


    public static final String BUNDLE_STATE = "bundle state";
    public FragmentMainPresenter mPresenter;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.navigation) BottomNavigationView mBottomNavigationView;
    private ViewState mCurrentState;


    public static MainFragment newInstance(ViewState viewState) {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_STATE, viewState);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        mPresenter = new FragmentMainPresenter();
        mPresenter.attachView(this);
        mPresenter.initAlarm();
        if (getArguments()!= null && getArguments().getSerializable(BUNDLE_STATE)!= null){
            setupState((ViewState) getArguments().getSerializable(BUNDLE_STATE));
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        setNavigationAndToolbar(mCurrentState);
    }

    @Override
    public void setupState(ViewState viewState){
        mCurrentState = viewState;
        switch (viewState){
            case All:
                mPresenter.initRecyclerViewAll();
                break;
            case Reminders:
                mPresenter.initRecyclerViewReminders();
                break;
            case Archive:
                mPresenter.initRecyclerViewArchive();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mPresenter.onNavigationAccContent(item.getItemId());
        setEnabledNavView();
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setAdapter(RecyclerViewAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        adapter.setOnClickItemRecycler(this);
    }

    @Override
    public ViewState getCurrentState() {
        return mCurrentState;
    }

    @Override
    public void onClickRecycler(String id) {
        startActivityForResult(DetailsNoteActivity.createIntent(getContext(), id), REQUEST_ADD_NOTE);
    }

    @Override
    public void onDelete(String id) {
        mPresenter.deleteModel(id);
    }

    @Override
    public void onArchive(String id) {
        mPresenter.archiveModel(id);
    }

    public enum ViewState {
        All(R.id.nav_notes,R.string.notes),
        Reminders(R.id.nav_reminders,R.string.reminders),
        Archive(R.id.nav_archive,R.string.archive),
        Tools(R.id.nav_tools,R.string.tools);

        private int mIdResItem;
        private int mIdName;
        ViewState (@IdRes int idResItem, @StringRes int idName){
            mIdResItem = idResItem;
            mIdName = idName;
        }

        public int getIdResItem() {
            return mIdResItem;
        }

        public int getIdName() {
            return mIdName;
        }
    }

    @Override
    public void onRefresh(){
        setupState(mCurrentState);
    }

    public void setEnabledNavView(){
        Observable.just("")
                .subscribeOn(Schedulers.newThread())
                .buffer(observer -> mBottomNavigationView.setOnNavigationItemSelectedListener(item -> false))
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> mBottomNavigationView.setOnNavigationItemSelectedListener(this), err->{});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }
}
