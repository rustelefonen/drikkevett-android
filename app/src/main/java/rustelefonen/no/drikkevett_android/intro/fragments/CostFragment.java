package rustelefonen.no.drikkevett_android.intro.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import rustelefonen.no.drikkevett_android.InputFilterMinMax;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.intro.AlcoholPricingRegistrationActivity;
import rustelefonen.no.drikkevett_android.unit.UnitEditActivity;

/**
 * Created by simenfonnes on 17.08.2017.
 */

public class CostFragment extends Fragment implements Button.OnClickListener{

    public EditText beerEditText;
    public EditText wineEditText;
    public EditText drinkEditText;
    public EditText shotEditText;
    public Button standardButton;
    public Button editUnitsButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_cost_layout, container, false);

        InputFilter[] inputFilter = new InputFilter[]{ new InputFilterMinMax("1", "999")};

        beerEditText = (EditText) view.findViewById(R.id.intro_cost_edit_beer);
        beerEditText.setFilters(inputFilter);
        beerEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        beerEditText.setTypeface(Typeface.DEFAULT);

        wineEditText = (EditText) view.findViewById(R.id.intro_cost_edit_wine);
        wineEditText.setFilters(inputFilter);
        wineEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        wineEditText.setTypeface(Typeface.DEFAULT);

        drinkEditText = (EditText) view.findViewById(R.id.intro_cost_edit_drink);
        drinkEditText.setFilters(inputFilter);
        drinkEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        drinkEditText.setTypeface(Typeface.DEFAULT);

        shotEditText = (EditText) view.findViewById(R.id.intro_cost_edit_shot);
        shotEditText.setFilters(inputFilter);
        shotEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        shotEditText.setTypeface(Typeface.DEFAULT);

        standardButton = (Button) view.findViewById(R.id.intro_cost_standard_button);
        standardButton.setOnClickListener(this);

        editUnitsButton = (Button) view.findViewById(R.id.intro_cost_edit_units);
        editUnitsButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.intro_cost_standard_button) {
            beerEditText.setText(Integer.toString(70));
            wineEditText.setText(Integer.toString(65));
            drinkEditText.setText(Integer.toString(110));
            shotEditText.setText(Integer.toString(100));
        }
        else if (v.getId() == R.id.intro_cost_edit_units) {
            startActivity(new Intent(getContext(), UnitEditActivity.class));
        }
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}
