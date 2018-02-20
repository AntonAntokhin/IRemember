package com.antohin.iremember;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class TimeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_TIME_TICK))
            Toast.makeText(context, "TIME TICK", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "NOT !!! TIME TICK", Toast.LENGTH_SHORT).show();
    }
}