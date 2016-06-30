package rustelefonen.no.drikkevett_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BacCalcFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bac_calc_frag, container, false);

        // DO WHAT YOU WANNA DO
        TextView tv = (TextView) v.findViewById(R.id.textViewTesting);
        tv.setText("Promillekalkulator");

        return v;
    }
}
