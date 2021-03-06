package rustelefonen.no.drikkevett_android.intro;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import rustelefonen.no.drikkevett_android.InputFilterMinMax;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;


/**
 * Created by simenfonnes on 13.07.2016.
 */

public class UserRegistrationActivity extends AppCompatActivity {

    public EditText nicknameEditText;
    public Spinner genderSpinner;
    public EditText weightEditText;
    public EditText ageEditText;
    public Button nextButton;

    private static final String[] GENDERS = new String[]{"Velg kjønn", "Mann", "Kvinne"};
    private static final int DELAY_TIME = 2500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_layout);
        initWidgets();
        setupWidgets();
    }

    public void next(View view) {
        delaySpam();
        User user = new User();

        String nicknameText = nicknameEditText.getText().toString();

        if (nicknameText.length() > 25) {
            Toast.makeText(this, "Ugyldig kallenavn.", Toast.LENGTH_SHORT).show();
            return;
        } else if (nicknameText.length() < 1) {
            Toast.makeText(this, "Du må registrere kallenavn for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            user.setNickname(nicknameText);
        }

        String genderText = GENDERS[genderSpinner.getSelectedItemPosition()];

        if (genderText.isEmpty()) {
            Toast.makeText(this, "Du må registrere kjønn for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (genderText.equals(GENDERS[1]) || genderText.equals(GENDERS[2])) {
                user.setGender(genderText);
            } else {
                Toast.makeText(this, "Du må registrere kjønn for å gå videre", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String weightText = weightEditText.getText().toString();
        if (!weightText.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightText);

                if (weight < 40.0 || weight > 250) {
                    Toast.makeText(this, "Vekt under 40kg og over 250kg er ikke gyldig.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    user.setWeight(weight);
                }
            } catch (NumberFormatException ignored) {
                Toast.makeText(this, "Ugyldig vekt", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "Du må registrere vekt for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        }

        String ageText = ageEditText.getText().toString();
        if (!ageText.isEmpty()) {
            try {
                int age = Integer.parseInt(ageText);
                if (age < 18 || age > 99) {
                    Toast.makeText(this, "Alder må være mellom 18 og 99.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    user.setAge(age);
                }
            } catch (NumberFormatException ignored) {
                Toast.makeText(this, "Du må registrere alder for å gå videre", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "Du må registrere alder for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, AlcoholPricingRegistrationActivity.class);
        intent.putExtra(AlcoholPricingRegistrationActivity.ID, user);
        startActivity(intent);
    }

    private void initWidgets() {
        nicknameEditText = (EditText) findViewById(R.id.user_reg_nickname_edit_text);
        genderSpinner = (Spinner) findViewById(R.id.user_reg_gender_spinner);
        weightEditText = (EditText) findViewById(R.id.user_reg_weight_edit_text);
        ageEditText = (EditText) findViewById(R.id.user_reg_age_edit_text);
        nextButton = (Button) findViewById(R.id.user_reg_next_button);
    }

    private void setupWidgets() {
        setupGenderSpinner();
        weightEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "250")});
        ageEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "99")});
    }

    private void setupGenderSpinner() {
        genderSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, GENDERS));
        Drawable spinnerDrawable = genderSpinner.getBackground().getConstantState().newDrawable();
        spinnerDrawable.setColorFilter(ContextCompat.getColor(this, R.color.backgroundColor), PorterDuff.Mode.SRC_ATOP);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) genderSpinner.setBackground(spinnerDrawable);
        else genderSpinner.setBackgroundDrawable(spinnerDrawable);
    }

    private void delaySpam() {
        nextButton.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException ignored) {}
                UserRegistrationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {nextButton.setEnabled(true);}
                });
            }
        }).start();
    }

}
