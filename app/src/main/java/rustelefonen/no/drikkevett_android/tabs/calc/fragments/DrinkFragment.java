package rustelefonen.no.drikkevett_android.tabs.calc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 11.07.2016.
 */

public class DrinkFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.drink_fragment, container, false);
    }
}
