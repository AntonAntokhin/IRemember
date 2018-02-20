package com.antohin.iremember.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.antohin.iremember.R;

public class SelectAlarmDurationDialog {
    private static int mPosition;
    public static Dialog newInstance(final Context context, IDurationAlarmSelect durationAlarmSelect, int selectDuraton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.settings_alarm_duration)
                .setSingleChoiceItems(R.array.alarm_duration_array, selectDuraton, (d, pos) -> mPosition = pos)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialog, which) -> durationAlarmSelect.onSelectPos(mPosition));
        return builder.create();
    }
    private SelectAlarmDurationDialog() { }

    public interface IDurationAlarmSelect{
        void onSelectPos(int pos);
    }
}
