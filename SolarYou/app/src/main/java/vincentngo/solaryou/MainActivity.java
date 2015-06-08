package vincentngo.solaryou;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ArrayList<Planet> mPlanets;
    private Spinner mFromSpinner;
    private Spinner mToSpinner;
    private EditText mFromEditText;
    private TextView mDesiredTextView;
    private TextView mFromDayTextView;
    private TextView mToDayTextView;
//    private ToggleButton mToggleButton;

    public static String TAG="Tracking";
    private Planet mReferencePlanet;
    private Planet mDesiredPlanet;
    private ToggleButton mToToggleButton;
    private ToggleButton mFromToggleButton;
    private String mFromAttribute;
    private String mToAttribute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlanets=PlanetList.get().getPlanetList();

//        mFromDayTextView=(TextView) findViewById(R.id.from_day_textview);
//        mToDayTextView=(TextView) findViewById(R.id.to_day_textview);
        mDesiredTextView=(TextView)findViewById(R.id.desired_textview);
        mDesiredTextView.setText("0");

        mFromAttribute="mEarthYear";
        mToAttribute="mEarthYear";


//        mToggleButton=(ToggleButton)findViewById(R.id.togglebutton);
//        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mType="Days";
//                    Log.d(TAG,"mType sets to Day");
//                    updateDate();
//                } else {
//                    mType="Years";
//                    Log.d(TAG,"mType sets to Year");
//                    updateDate();
//                }
//            }
//        });

//        mFromToggleButton=(ToggleButton) findViewById(R.id.from_togglebutton);
//        mFromToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mFromAttribute="mEarthDay";
//                    updateCalculation(mFromEditText.getText().toString());
//                } else {
//                    mFromAttribute="mEarthYear";
//                    updateCalculation(mFromEditText.getText().toString());
//                }
//            }
//        });
//
//        mToToggleButton=(ToggleButton) findViewById(R.id.to_togglebutton);
//        mToToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mToAttribute="mEarthDay";
//                    updateCalculation(mFromEditText.getText().toString());
//                } else {
//                    mToAttribute="mEarthYear";
//                    updateCalculation(mFromEditText.getText().toString());
//                }
//            }
//        });

        // Setup an adapter array for 2 spinners
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.planets_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mFromSpinner=(Spinner) findViewById(R.id.ref_planet_spinner);
        mFromSpinner.setAdapter(adapter);
        mFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mPlanet = parent.getItemAtPosition(position).toString();
                mReferencePlanet = PlanetList.get().getPlanet(mPlanet);
                updateCalculation(mFromEditText.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        mToSpinner=(Spinner) findViewById(R.id.des_planet_spinner);
        mToSpinner.setAdapter(adapter);
        mToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mPlanet = parent.getItemAtPosition(position).toString();
                mDesiredPlanet = PlanetList.get().getPlanet(mPlanet);
                updateCalculation(mFromEditText.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });




        mFromEditText=(EditText)findViewById(R.id.from_edittext);
        mFromEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string=s.toString();
                updateCalculation(string);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });


    }

//    private void updateDate() {
//        mToDayTextView.setText(mType);
//        mFromDayTextView.setText(mType);
//    }

    private void updateCalculation(String string) {
        if (string.isEmpty()) {
            mDesiredTextView.setText("0");
        } else {
            if (string.substring(0,1).contentEquals(".")) {
                string="0"+string;

            }
            double result=Converter.convert(Double.parseDouble(string),
                    mReferencePlanet, mDesiredPlanet);
            mDesiredTextView.setText(Double.toString(result));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
