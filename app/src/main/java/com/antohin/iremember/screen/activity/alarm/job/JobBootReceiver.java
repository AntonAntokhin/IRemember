package com.antohin.iremember.screen.activity.alarm.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobManagerCreateException;


public class JobBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            JobManager.create(context);
        } catch (JobManagerCreateException ignored) {
        }
    }
}