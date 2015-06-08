package thunderiven.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Vincent Ngo on 5/29/2015.
 */
public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME="thunderiven.criminalintent.time";
    public static final String TAG="Activity";
    private Date mDate;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate=(Date)getArguments().getSerializable(EXTRA_TIME);
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(mDate);
        int hour =calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        Log.d(TAG,"TimePickerFragment created");

        View v=getActivity().getLayoutInflater().inflate(R.layout.dialog_time,null);

        TimePicker mTimePicker=(TimePicker)v.findViewById(R.id.dialog_time_timePicker);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mDate);
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);

                // Translating hourOfDay & minute into a Date object using a calendar, date keeps the same
                mDate = calendar.getTime();
                getArguments().putSerializable(EXTRA_TIME, mDate);
                Log.d(TAG,"Time Changed" );
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }
    public static TimePickerFragment newInstance(Date date) {
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_TIME,date);
        TimePickerFragment fragment=new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    private void sendResult(int resultCode) {
        if (getTargetFragment()==null) {
            return;
        }
        Intent i=new Intent();
        i.putExtra(EXTRA_TIME,mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
