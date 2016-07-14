package rustelefonen.no.drikkevett_android.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;

/**
 * Created by simenfonnes on 14.07.2016.
 */

public class UserSettingsActivity extends AppCompatActivity {

    public EditText nicknameEditText;
    public AutoCompleteTextView genderAutocompleteTextView;
    public EditText weightEditText;
    public EditText ageEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_layout);

        nicknameEditText = (EditText) findViewById(R.id.user_settings_nickname_edit_text);
        genderAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.user_settings_gender_auto_complete_text_view);
        weightEditText = (EditText) findViewById(R.id.user_settings_weight_edit_text);
        ageEditText = (EditText) findViewById(R.id.user_settings_age_edit_text);



    }

    public void save (View view) {
        if (nicknameEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Kallenavn kan ikke være tomt.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (genderAutocompleteTextView.getText().toString().isEmpty()) {
            Toast.makeText(this, "Kjønn kan ikke være tomt.", Toast.LENGTH_SHORT).show();
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ugyldig vekt", Toast.LENGTH_SHORT).show();
            return;
        }
        if (weight <= 0) {
            Toast.makeText(this, "For lav vekt", Toast.LENGTH_SHORT);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ugyldig alder", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age <= 0) {
            Toast.makeText(this, "For lav vekt", Toast.LENGTH_SHORT);
            return;
        }

        Toast.makeText(this, "Endringene ble lagret", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        User user = new User();

        String nicknameText = nicknameEditText.getText().toString();
        String genderText = genderAutocompleteTextView.getText().toString();

        double weight;
        try {
            weight = Double.parseDouble(weightEditText.getText().toString());
        } catch (NumberFormatException e) {
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageEditText.getText().toString());
        } catch (NumberFormatException e) {
            return;
        }

        if (!nicknameText.equals(user.getNickname()) || !genderText.equals(user.getGender()) ||
                weight != user.getWeight() || age != user.getAge()) {
            Toast.makeText(this, "Er du sikker?", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
