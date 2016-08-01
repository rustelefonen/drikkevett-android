package rustelefonen.no.drikkevett_android.settings;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 14.07.2016.
 */

public class UserSettingsActivity extends AppCompatActivity {

    public static final String ID = "UserSettings";
    private static final String[] GENDERS = new String[]{"Velg kjønn", "Mann", "Kvinne"};

    public EditText nicknameEditText;
    public Spinner genderSpinner;
    public EditText weightEditText;
    public EditText ageEditText;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_layout);
        insertUserIfExists();
        initWidgets();
        fillWidgets();
        insertToolbar();
    }

    private void insertUserIfExists() {
        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) user = (User) tmpUser;
    }

    private void insertToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle("Brukerinnstillinger");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasChanged();
            }
        });
    }

    private void setupGenderSpinner() {
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, GENDERS);
        genderSpinner.setAdapter(arrayAdapter);

        Drawable spinnerDrawable = genderSpinner.getBackground().getConstantState().newDrawable();
        spinnerDrawable.setColorFilter(ContextCompat.getColor(this, R.color.backgroundColor), PorterDuff.Mode.SRC_ATOP);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) genderSpinner.setBackground(spinnerDrawable);
        else genderSpinner.setBackgroundDrawable(spinnerDrawable);
    }

    private void initWidgets() {
        nicknameEditText = (EditText) findViewById(R.id.user_settings_nickname_edit_text);
        genderSpinner = (Spinner) findViewById(R.id.user_settings_gender_spinner);
        setupGenderSpinner();
        weightEditText = (EditText) findViewById(R.id.user_settings_weight_edit_text);
        ageEditText = (EditText) findViewById(R.id.user_settings_age_edit_text);
    }

    private void fillWidgets() {
        String nickname = user.getNickname();
        if (nickname != null) nicknameEditText.setText(nickname);

        String gender = user.getGender();
        if (gender != null) {
            int position = getPosition(gender);
            genderSpinner.setSelection(position);
        }

        weightEditText.setText(Double.toString(user.getWeight()));
        ageEditText.setText(Integer.toString(user.getAge()));
    }

    private int getPosition(String gender) {
        if (gender.equals("Mann")) return 1;
        else if (gender.equals("Kvinne")) return 2;
        else return 0;
    }

    public void save (View view) {
        String nicknameText = nicknameEditText.getText().toString();
        if (nicknameText.length() > 15) {
            Toast.makeText(this, "Ugyldig kallenavn.", Toast.LENGTH_SHORT).show();
            return;
        } else if (nicknameText.length() < 1) {
            Toast.makeText(this, "Du må registrere kallenavn for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        }

        String genderText = GENDERS[genderSpinner.getSelectedItemPosition()];
        if (genderText.isEmpty()) {
            Toast.makeText(this, "Du må registrere kjønn for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!genderText.equals(GENDERS[1]) && !genderText.equals(GENDERS[2])) {
                Toast.makeText(this, "Ugyldig kjønn", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String weightText = weightEditText.getText().toString();
        double weight;
        if (!weightText.isEmpty()) {
            try {
                weight = Double.parseDouble(weightText);
                if (weight < 40.0 || weight > 250) {
                    Toast.makeText(this, "Vekt må være mellom 40 og 250kg.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException ignored) {
                Toast.makeText(this, "Ugyldig vekt", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "Du må registrere vekt for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        String ageText = ageEditText.getText().toString();
        if (!ageText.isEmpty()) {
            try {
                age = Integer.parseInt(ageText);
                if (age < 18 || age > 99) {
                    Toast.makeText(this, "Alder må være mellom 18 og 99.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException ignored) {
                Toast.makeText(this, "Du må registrere alder for å gå videre", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "Du må registrere alder for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        }

        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();

        List<User> users = userDao.queryBuilder().list();
        if (users.size() <= 0) return;

        User userToEdit = users.get(0);
        userToEdit.setNickname(nicknameText);
        userToEdit.setGender(genderText);
        userToEdit.setWeight(weight);
        userToEdit.setAge(age);

        userDao.update(userToEdit);

        superDao.close();

        Toast.makeText(this, "Endringene ble lagret", Toast.LENGTH_SHORT).show();
        finish();
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
        hasChanged();
    }

    private void hasChanged() {
        String nicknameText = nicknameEditText.getText().toString();
        int position = genderSpinner.getSelectedItemPosition();
        String genderText = GENDERS[position];

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
            new AlertDialog.Builder(this)
                    .setTitle("Endringer oppdaget")
                    .setMessage("Er du sikker på at du vil gå tilbake? Endringene vil ikke bli lagret.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            goBack();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void goBack() {
        super.onBackPressed();
    }
}
