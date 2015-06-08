package thunderiven.game2048;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Vincent Ngo on 6/5/2015.
 */
public class MainFragment extends Fragment {
    private Button mNewGame;
    private EditText mSeedEditText;
    private long mSeed=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_main,container,false);

        mNewGame=(Button)v.findViewById(R.id.new_game_button);
        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),GameActivity.class);
                i.putExtra(GameFragment.SEED_EXTRA,mSeed);
                startActivity(i);
            }
        });

        mSeedEditText=(EditText)v.findViewById(R.id.seed_edit_text);
        mSeedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s!=null) {mSeed=Long.parseLong(s.toString());}
            }
        });

        return v;

    }
}
