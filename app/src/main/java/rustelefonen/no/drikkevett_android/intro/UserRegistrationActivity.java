package rustelefonen.no.drikkevett_android.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;


/**
 * Created by simenfonnes on 13.07.2016.
 */

public class UserRegistrationActivity extends AppCompatActivity {

    public EditText nicknameEditText;
    public AutoCompleteTextView genderAutoCompleteTextView;
    public EditText weightEditText;
    public EditText ageEditText;

    private static final String[] GENDERS = new String[]{"Mann", "Kvinne"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_layout);
        nicknameEditText = (EditText) findViewById(R.id.user_reg_nickname_edit_text);


        genderAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.user_reg_gender_auto_complete_text_view);
        genderAutoCompleteTextView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, GENDERS));

        genderAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) genderAutoCompleteTextView.showDropDown();
            }
        });

        genderAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                genderAutoCompleteTextView.showDropDown();
                return false;
            }
        });

        weightEditText = (EditText) findViewById(R.id.user_reg_weight_edit_text);
        ageEditText = (EditText) findViewById(R.id.user_reg_age_edit_text);
    }

    public void next(View view) {
        User user = new User();

        String nicknameText = nicknameEditText.getText().toString();
        if (nicknameText.isEmpty()) {
            Toast.makeText(this, "Du må registrere kallenavn for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            user.setNickname(nicknameText);
        }

        String genderText = genderAutoCompleteTextView.getText().toString();
        if (genderText.isEmpty()) {
            Toast.makeText(this, "Du må registrere kjønn for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            user.setGender(genderText);
        }

        String weightText = weightEditText.getText().toString();
        if (!weightText.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightText);
                user.setWeight(weight);
            } catch (NumberFormatException ignored) {
                Toast.makeText(this, "Du må registrere vekt for å gå videre", Toast.LENGTH_SHORT).show();
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
                user.setAge(age);
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
}
