package rustelefonen.no.drikkevett_android.intro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 17.08.2017.
 */

public class InfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_info_layout, container, false);
        return view;
    }
}
