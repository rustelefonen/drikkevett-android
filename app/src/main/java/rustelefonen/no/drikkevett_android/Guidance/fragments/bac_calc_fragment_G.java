package rustelefonen.no.drikkevett_android.Guidance.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by RUStelefonen on 26.07.2016.
 */

public class bac_calc_fragment_G extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.guidance_bac_calc_fragment, container, false);
    }

}
