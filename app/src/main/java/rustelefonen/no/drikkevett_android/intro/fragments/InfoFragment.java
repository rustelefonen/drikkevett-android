package rustelefonen.no.drikkevett_android.intro.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.intro.IntroActivity;
import rustelefonen.no.drikkevett_android.intro.PrivacyActivity;
import rustelefonen.no.drikkevett_android.util.BacUtility;

/**
 * Created by simenfonnes on 17.08.2017.
 */

public class InfoFragment extends Fragment implements NumberPicker.OnValueChangeListener, Button.OnClickListener {

    public EditText bacEditText;
    public EditText nicknameEditText;
    public Spinner genderSpinner;
    public EditText weightEditText;
    public Button startButton;

    public TextView bacQuoteTextView;

    private static final String[] GENDERS = new String[]{"Velg kjønn", "Mann", "Kvinne"};
    private static final String[] bacs = new String[]{"0,1", "0,2", "0,3", "0,4", "0,5", "0,6", "0,7", "0,8", "0,9", "1,0", "1,1", "1,2", "1,3", "1,4"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_info_layout, container, false);

        bacEditText = (EditText) view.findViewById(R.id.intro_goal);
        bacEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        nicknameEditText = (EditText) view.findViewById(R.id.intro_nickname);
        genderSpinner = (Spinner) view.findViewById(R.id.intro_gender_spinner);
        weightEditText = (EditText) view.findViewById(R.id.intro_weight);
        setupGenderSpinner();

        startButton = (Button) view.findViewById(R.id.intro_start_button);
        startButton.setOnClickListener(this);

        bacQuoteTextView = (TextView) view.findViewById(R.id.intro_bac_quote);

        return view;
    }

    private void setupGenderSpinner() {
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, GENDERS);
        genderSpinner.setAdapter(arrayAdapter);

        Drawable spinnerDrawable = genderSpinner.getBackground().getConstantState().newDrawable();
        spinnerDrawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.backgroundColor), PorterDuff.Mode.SRC_ATOP);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) genderSpinner.setBackground(spinnerDrawable);
        else genderSpinner.setBackgroundDrawable(spinnerDrawable);
    }

    public void show() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("NumberPicker");

        dialog.setContentView(R.layout.bac_dialog_layout);
        Button b1 = (Button) dialog.findViewById(R.id.button1);
        Button b2 = (Button) dialog.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker1);
        np.setMaxValue(13);
        np.setMinValue(0);
        np.setDisplayedValues(bacs);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                bacEditText.setText(bacs[np.getValue()]);

                double bac = ((double) np.getValue() + 1) / 10.0;

                bacQuoteTextView.setText(BacUtility.getQuoteRegisterTextBy(bac));
                bacQuoteTextView.setTextColor(BacUtility.getQuoteTextColorBy(bac));
                dialog.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.intro_start_button) {

            User user = ((IntroActivity)getActivity()).getUserWithCostValues();

            System.out.println(user.getBeerPrice());

            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            Number number = null;
            try {
                number = format.parse(bacEditText.getText().toString());
            } catch (ParseException ignored) {}

            double bac = number != null ? number.doubleValue() : 0.0;
            if (bac < 0.1 || bac > 1.4) {
                Toast.makeText(getContext(), "Promillen må være mellom 0,1 og 1,4.", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setGoalBAC(bac);

            String nicknameText = nicknameEditText.getText().toString();

            if (nicknameText.length() > 25) {
                Toast.makeText(getContext(), "Ugyldig kallenavn.", Toast.LENGTH_SHORT).show();
                return;
            } else if (nicknameText.length() < 1) {
                Toast.makeText(getContext(), "Du må registrere kallenavn for å gå videre", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.setNickname(nicknameText);
            }

            String genderText = GENDERS[genderSpinner.getSelectedItemPosition()];

            if (genderText.isEmpty()) {
                Toast.makeText(getContext(), "Du må registrere kjønn for å gå videre", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (genderText.equals(GENDERS[1]) || genderText.equals(GENDERS[2])) {
                    user.setGender(genderText);
                } else {
                    Toast.makeText(getContext(), "Du må registrere kjønn for å gå videre", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String weightText = weightEditText.getText().toString();
            if (!weightText.isEmpty()) {
                try {
                    double weight = Double.parseDouble(weightText);

                    if (weight < 40.0 || weight > 250) {
                        Toast.makeText(getContext(), "Vekt under 40kg og over 250kg er ikke gyldig.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        user.setWeight(weight);
                    }
                } catch (NumberFormatException ignored) {
                    Toast.makeText(getContext(), "Ugyldig vekt", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(getContext(), "Du må registrere vekt for å gå videre", Toast.LENGTH_SHORT).show();
                return;
            }



            Intent intent = new Intent(getContext(), PrivacyActivity.class);
            intent.putExtra(PrivacyActivity.PRIVACY_USER, user);
            startActivity(intent);
        }
    }
}
