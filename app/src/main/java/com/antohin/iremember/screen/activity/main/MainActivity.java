package com.antohin.iremember.screen.activity.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.antohin.iremember.R;
import com.antohin.iremember.screen.activity.BaseActivity;
import com.antohin.iremember.screen.fragment.BaseFragment;
import com.antohin.iremember.screen.fragment.ToolsFragment;
import com.antohin.iremember.screen.fragment.main.MainFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.antohin.iremember.screen.fragment.main.MainFragment.ViewState.All;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainView {

    public static final int REQUEST_ADD_NOTE = 1095;
    public static final int REQUEST_VOICE_WRITE = 116;
    public static int REQUEST_CAMERA_CAPTURE = 1005;
    public static int REQUEST_IMAGE_GALLERY = 7050;
    public static final String DATA_ID_FROM_REALM = "data id from realm";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    private MainPresenter mPresenter;
    private BaseFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        if (getSupportFragmentManager().getFragments().isEmpty()){
            setFragment(All);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mPresenter.onNavigationItemSelected(item);
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void setFragment(MainFragment.ViewState viewState) {
        if (mCurrentFragment != null && Objects.equals(mCurrentFragment.getTag(), viewState.toString())) return;
        mCurrentFragment = MainFragment.newInstance(viewState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mCurrentFragment, viewState.toString())
                .commit();
    }

    @Override
    public void setToolsFragment() {
        if (mCurrentFragment != null && Objects.equals(mCurrentFragment.getTag(), ToolsFragment.TAG)) return;
        mCurrentFragment = ToolsFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, mCurrentFragment, ToolsFragment.TAG)
                .commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mCurrentFragment != null) mCurrentFragment.onRefresh();
    }
}
