package com.antohin.iremember;

import android.app.Application;

import com.antohin.iremember.screen.activity.alarm.job.AdeptAndroidJobCreator;
import com.evernote.android.job.JobManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class App extends Application {

    private static App sSelf;

    public static App self() {
        return sSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sSelf = this;
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        JobManager.create(this).addJobCreator(new AdeptAndroidJobCreator());
    }
}
