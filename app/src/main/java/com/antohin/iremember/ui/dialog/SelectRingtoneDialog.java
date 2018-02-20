package com.antohin.iremember.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.antohin.iremember.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public final class SelectRingtoneDialog {
    private static String mSelectName;
    public static Dialog newInstance(final Context context, IOnSelectItem selectItem, IOnAddUserSong addUserSong, String currentRingtone) {
        Map<String, Uri> ringtones = getRingtonesList(context);
        final String[] names = new String[ringtones.size()];
        final List<Uri> uris = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, Uri> entry : ringtones.entrySet()) {
            names[count++] = entry.getKey();
            uris.add(entry.getValue());
        }

        final Uri[] selectedUri = new Uri[1];
        final Ringtone[] selectedRingtone = new Ringtone[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.settings_ringtone_title)
                .setSingleChoiceItems(names, uris.indexOf(Uri.parse(currentRingtone)), (d, pos) -> {
                    mSelectName = names[pos];
                    Ringtone currentSelectedRing = selectedRingtone[0];
                    if (currentSelectedRing != null && currentSelectedRing.isPlaying()) {
                        currentSelectedRing.stop();
                    }
                    Uri uri = uris.get(pos);
                    selectedUri[0] = uri;

                    if (uri != null) {
                        currentSelectedRing = RingtoneManager.getRingtone(context, uri);
                    } else {
                        currentSelectedRing = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                    }
                    if (currentSelectedRing != null) {
                        currentSelectedRing.play();
                    }
                    selectedRingtone[0] = currentSelectedRing;
                })
                .setOnDismissListener(dialog -> {
                    if (selectedRingtone[0] != null) selectedRingtone[0].stop();
                })
                .setNeutralButton(R.string.add,(dialog, which) -> addUserSong.onClick())
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton(R.string.ok, (d, btn) -> {
                    Ringtone currentSelectedRing = selectedRingtone[0];
                    if (currentSelectedRing != null) {
                        if (currentSelectedRing.isPlaying()) {
                            currentSelectedRing.stop();
                        }
                        Uri uri = selectedUri[0];
                        selectItem.onSelectItem(uri.toString(), mSelectName);
                    }
                    d.dismiss();
                });

        return builder.create();
    }

    private SelectRingtoneDialog() {
    }

    private static LinkedHashMap<String, Uri> getRingtonesList(Context context) {
        LinkedHashMap<String, Uri> ringtones = new LinkedHashMap<>();
        RingtoneManager ringtoneManager = new RingtoneManager(context);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        Cursor alarmsCursor = ringtoneManager.getCursor();
        if (alarmsCursor != null && alarmsCursor.getCount() != 0) {
            alarmsCursor.moveToFirst();
            while (!alarmsCursor.isAfterLast()) {
                ringtones.put(alarmsCursor.getString(RingtoneManager.TITLE_COLUMN_INDEX), ringtoneManager.getRingtoneUri(alarmsCursor.getPosition()));
                alarmsCursor.moveToNext();
            }
        }
        if (alarmsCursor != null) {
            alarmsCursor.close();
        }
        return ringtones;
    }

    public interface IOnAddUserSong{
        void onClick();
    }
    public interface IOnSelectItem{
        void onSelectItem(String path, String name);
    }
}

