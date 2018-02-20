package com.antohin.iremember.screen.activity.main;

import android.content.Intent;
import android.view.MenuItem;

import com.antohin.iremember.R;
import com.antohin.iremember.screen.activity.BasePresenter;
import com.antohin.iremember.screen.fragment.main.MainFragment;


class MainPresenter extends BasePresenter<MainView> {

    MainPresenter() {
    }

    void onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_notes:
                mView.setFragment(MainFragment.ViewState.All);
                break;
            case R.id.nav_reminders:
                mView.setFragment(MainFragment.ViewState.Reminders);
                break;
            case R.id.nav_archive:
                mView.setFragment(MainFragment.ViewState.Archive);
                break;
            case R.id.nav_tools:
                mView.setToolsFragment();
                break;
            case R.id.nav_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here is the share content body";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                mView.getContext().startActivity(Intent.createChooser(sharingIntent, mView.getStringById(R.string.share_about_us)));
                break;
        }
    }


}