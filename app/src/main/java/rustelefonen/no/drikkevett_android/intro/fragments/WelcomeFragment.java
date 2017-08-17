package rustelefonen.no.drikkevett_android.intro.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.extra.guidance.Guidance;

/**
 * Created by simenfonnes on 17.08.2017.
 */

public class WelcomeFragment extends Fragment implements Button.OnClickListener{

    public Button guidanceButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_welcome_layout, container, false);
        guidanceButton = (Button) view.findViewById(R.id.intro_welcome_guide);
        guidanceButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.intro_welcome_guide) {
            startActivity(new Intent(getContext(), Guidance.class));
        }
    }
}
