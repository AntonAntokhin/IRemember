package com.antohin.iremember.screen.activity.alarm.job;

import android.content.Intent;
import android.support.annotation.NonNull;


import com.antohin.iremember.screen.activity.alarm.AlarmActivity;
import com.evernote.android.job.Job;

import static com.antohin.iremember.screen.activity.alarm.AlarmActivity.KEY_ID_JOB;


public class AlarmJob extends Job {

    @NonNull
    @Override
    protected Job.Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getContext(), AlarmActivity.class);
        intent.putExtra(KEY_ID_JOB,params.getTag());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        return Result.SUCCESS;
    }
}
