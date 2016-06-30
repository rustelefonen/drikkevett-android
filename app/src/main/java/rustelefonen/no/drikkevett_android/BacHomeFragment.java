package rustelefonen.no.drikkevett_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BacHomeFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bac_home_frag, container, false);

        TextView tv = (TextView) v.findViewById(R.id.textViewHome);
        tv.setText("Hjem Skjerm");

        return v;
    }

}


