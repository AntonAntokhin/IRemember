package com.antohin.iremember.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.DatePicker;

import com.antohin.iremember.R;

import org.joda.time.DateTime;

import java.util.Calendar;


public class ChoiceDateTime {
    private Context mContext;
    private TimePickerDialog mTimePickerDialog;
    private DatePicker mDatePicker;
    private OnSelectTimeDate mOnSelectTimeDate;
    private int mMinute;
    private int mHour;
    private int mDay;
    private int mMonth;
    private int mYear;
    private boolean mIs24HourFormat;

    private AlertDialog mAlertDialog;
    private DatePickerDialog mDatePickerDialog;

    public ChoiceDateTime(Context context, boolean is24HourFormat) {
        mContext = context;
        mIs24HourFormat = is24HourFormat;
        getChoiceDateAndTime();
        initDataPiker();
    }

    public boolean isShowing(){
        return  (mTimePickerDialog != null && mTimePickerDialog.isShowing())
                || (mDatePickerDialog != null && mDatePickerDialog.isShowing())
                || (mAlertDialog !=null && mAlertDialog.isShowing());
    }

    public void show() {
        mTimePickerDialog.show();
    }

    private void getChoiceDateAndTime() {
        Calendar calendar = Calendar.getInstance();
        DateTime dataTime = new DateTime(calendar);

        mTimePickerDialog = new TimePickerDialog(mContext, R.style.MyDatePickerStyle,(timePicker, i, i1) -> {
            mHour = i;
            mMinute = i1;
            if (mOnSelectTimeDate != null)
                mOnSelectTimeDate.onSelect(mYear, mMonth, mDay, mHour, mMinute);
        }, dataTime.getHourOfDay(), dataTime.getMinuteOfHour(), mIs24HourFormat);
        mTimePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, dataTime.toLocalDate().toString(), mNeutrall);
    }

    private void showDataPiker(){
        if (mDatePickerDialog!= null){
            mDatePickerDialog.show();
        }else if (mAlertDialog!= null){
            mAlertDialog.show();
        }
    }

    private void initDataPiker() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mDatePickerDialog = new DatePickerDialog(mContext,R.style.MyDatePickerStyle);
            mDatePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> datePickerSelect(year, month, dayOfMonth));
            mDatePickerDialog.setOnCancelListener(dialog -> mTimePickerDialog.show());
            mDatePicker = mDatePickerDialog.getDatePicker();
        } else {
            AlertDialog.Builder ab = new AlertDialog.Builder(mContext,R.style.MyDatePickerStyle);
            mDatePicker = new DatePicker(mContext);
            ab.setPositiveButton(R.string.ok, (dialogInterface, i) -> datePickerSelect(mDatePicker.getYear()
                    , mDatePicker.getMonth()
                    , mDatePicker.getDayOfMonth()))
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                            mTimePickerDialog.show());
            mAlertDialog = ab.setView(mDatePicker).create();
        }
    }

    private void datePickerSelect(int year, int mouth, int day) {
        mYear = year;
        mMonth = mouth +1;
        mDay = day;
        mTimePickerDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setText(getSelectDate());
        mTimePickerDialog.show();
    }

    private DialogInterface.OnClickListener mNeutrall = (dialogInterface, i) -> showDataPiker();


    private String getSelectDate() {
        StringBuilder sb = new StringBuilder();
        sb.append(mDatePicker.getYear())
                .append("-")
                .append(mMonth + 1 < 10 ? "0" + (mMonth) : mMonth)
                .append("-")
                .append(mDay < 10 ? "0" + mDay : mDay);
        return new String(sb);
    }

    public interface OnSelectTimeDate {
        void onSelect(int year, int month, int day, int hour, int minute);
    }

    public void setOnSelectTimeDate(OnSelectTimeDate onSelectTimeDate) {
        mOnSelectTimeDate = onSelectTimeDate;
    }

    public int getMinute() {
        return mMinute;
    }

    public int getHour() {
        return mHour;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }
}

