package thunderiven.criminalintent;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Vincent Ngo on 5/29/2015.
 */
public class CrimeListFragment extends ListFragment {
    private ArrayList<Crime> mCrimes;
    private static final String TAG ="CrimeListFragment";
    private boolean mSubtitleVisible;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.crimes_title);
        setRetainInstance(true);
        mSubtitleVisible=false;
        mCrimes=CrimeLab.get(getActivity()).getCrimes();
        CrimeAdapter adapter=new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
    }
    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=super.onCreateView(inflater,container,savedInstanceState);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
            if (mSubtitleVisible) {
                ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle);
            }
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View v=getActivity().getLayoutInflater().inflate(R.layout.list_empty_crime,null);
        ((ViewGroup)getListView().getParent()).addView(v);
        Button mNewCrimeButton=(Button)v.findViewById(R.id.add_new_crime);
        mNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent i=new Intent(getActivity(),CrimePagerActivity.class);
                i.putExtra(CrimeFragment.EXTRA_CRIME_ID,crime.getId());
                startActivityForResult(i,0);
            }
        });
        getListView().setEmptyView(v);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime c=((CrimeAdapter)getListAdapter()).getItem(position);

        Intent i=new Intent(getActivity(),CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID,c.getId());
        startActivity(i);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(),0,crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null) {
                convertView=getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_crime,null);
            }
            Crime c=getItem(position);

            TextView titleTextView=(TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());
            TextView dateTextView=(TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(c.getDate().toString());
            CheckBox solvedCheckBox=(CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem showSubtitle=menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible && showSubtitle!=null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent i=new Intent(getActivity(),CrimePagerActivity.class);
                i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
                startActivityForResult(i,0);
                return true;
            case R.id.menu_item_show_subtitle:
                if (((AppCompatActivity)getActivity()).getSupportActionBar().getSubtitle()==null) {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle);
                    mSubtitleVisible=true;
                    item.setTitle(R.string.hide_subtitle);
                } else {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);
                    item.setTitle(R.string.show_subtitle);
                    mSubtitleVisible=false;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
