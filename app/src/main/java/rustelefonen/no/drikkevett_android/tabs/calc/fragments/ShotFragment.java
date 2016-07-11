package rustelefonen.no.drikkevett_android.tabs.calc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 11.07.2016.
 */

public class ShotFragment extends Fragment {

    public ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.shot_fragment, container, false);
        return view;

    }
}
