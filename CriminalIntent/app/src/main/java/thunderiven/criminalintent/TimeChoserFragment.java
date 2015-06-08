package thunderiven.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Date;

/**
 * Created by Vincent Ngo on 5/29/2015.
 */
public class TimeChoserFragment extends DialogFragment {
    private Button mSetTime;
    private Button mSetDay;
    private Date mDate;
    public static final String TAG="Activity";
    public static final String DIALOG_DATE="date";
    public static final String NEW_DATE="DATE";
    private static final int REQUEST_TIME=0;
    private static final int REQUEST_DATE=1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v=getActivity().getLayoutInflater().inflate(R.layout.chosing_activity,null);

        mDate=(Date)getArguments().getSerializable(NEW_DATE);

        mSetTime=(Button)v.findViewById(R.id.set_time);
        mSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm=getActivity().getSupportFragmentManager();
                TimePickerFragment timeDialog=TimePickerFragment.newInstance(mDate);
                timeDialog.setTargetFragment(TimeChoserFragment.this,REQUEST_TIME);
                timeDialog.show(fm,null);

            }
        });

        mSetDay=(Button)v.findViewById(R.id.set_date);
        mSetDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm=getActivity().getSupportFragmentManager();
                DatePickerFragment dialog=DatePickerFragment.newInstance(mDate);
                dialog.setTargetFragment(TimeChoserFragment.this,REQUEST_DATE);
                dialog.show(fm,null);
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_change_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    public static TimeChoserFragment newInstance(Date date) {
        Bundle args=new Bundle();
        args.putSerializable(NEW_DATE, date);
        TimeChoserFragment fragment=new TimeChoserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode!=Activity.RESULT_OK) return;
        if (requestCode==REQUEST_DATE) {
            Log.d(TAG, "RequestCode=Request_Date");
            mDate=(Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
        }
        if (requestCode==REQUEST_TIME) {
            Log.d(TAG,"RequestCode=Request_time");
            mDate=(Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
        }

    }
    public void sendResult(int resultCode) {
        if (getTargetFragment() == null) return;
        Intent i=new Intent();
        Log.d(TAG,"Result sent to CrimeFragment");
        i.putExtra(NEW_DATE,mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);
    }

}
