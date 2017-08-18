package rustelefonen.no.drikkevett_android.settings;

import android.app.Dialog;
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
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.util.BacUtility;

/**
 * Created by simenfonnes on 14.07.2016.
 */

public class UserSettingsActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    public static final String ID = "UserSettings";
    private static final String[] GENDERS = new String[]{"Velg kjønn", "Mann", "Kvinne"};
    private static final int DELAY_TIME = 2500;

    public TextView goalQuoteTextView;
    public EditText goalEditText;
    public EditText nicknameEditText;
    public Spinner genderSpinner;
    public EditText weightEditText;
    public Button saveButton;

    private User user;

    private static final String[] bacs = new String[]{"0,1", "0,2", "0,3", "0,4", "0,5", "0,6", "0,7", "0,8", "0,9", "1,0", "1,1", "1,2", "1,3", "1,4"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_layout);
        insertUserIfExists();
        initWidgets();
        setWidgetFilters();
        fillWidgets();
        insertToolbar();

        goalEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
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
        goalEditText = (EditText) findViewById(R.id.goal_settings_bac_edit_text);
        nicknameEditText = (EditText) findViewById(R.id.user_settings_nickname_edit_text);
        genderSpinner = (Spinner) findViewById(R.id.user_settings_gender_spinner);
        setupGenderSpinner();
        weightEditText = (EditText) findViewById(R.id.user_settings_weight_edit_text);
        saveButton = (Button) findViewById(R.id.user_settings_save_button);

        goalQuoteTextView = (TextView) findViewById(R.id.settings_goal_quotes);
    }

    private void setWidgetFilters() {
        weightEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3,1)});
    }

    private void fillWidgets() {
        double bac = user.getGoalBAC();
        goalEditText.setText(bacs[((int) (bac * 10.0)) - 1]);

        goalQuoteTextView.setText(BacUtility.getQuoteRegisterTextBy(bac));
        goalQuoteTextView.setTextColor(BacUtility.getQuoteTextColorBy(bac));

        String nickname = user.getNickname();
        if (nickname != null) nicknameEditText.setText(nickname);

        String gender = user.getGender();
        if (gender != null) {
            int position = getPosition(gender);
            genderSpinner.setSelection(position);
        }

        weightEditText.setText(Double.toString(user.getWeight()));
    }

    private int getPosition(String gender) {
        if (gender.equals("Mann")) return 1;
        else if (gender.equals("Kvinne")) return 2;
        else return 0;
    }

    private double getBac() {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        Number number = null;
        try {
            number = format.parse(goalEditText.getText().toString());
        } catch (ParseException ignored) {}

        return number != null ? number.doubleValue() : 0.0;
    }

    public void save (View view) {
        delaySpam();

        double bac = getBac();
        if (bac < 0.1 || bac > 1.4) {
            Toast.makeText(this, "Promillen må være mellom 0,1 og 1,4.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nicknameText = nicknameEditText.getText().toString();
        if (nicknameText.length() > 25) {
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

        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();

        List<User> users = userDao.queryBuilder().list();
        if (users.size() <= 0) return;

        User userToEdit = users.get(0);
        userToEdit.setGoalBAC(bac);
        userToEdit.setNickname(nicknameText);
        userToEdit.setGender(genderText);
        userToEdit.setWeight(weight);

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
        double bac = getBac();

        String nicknameText = nicknameEditText.getText().toString();
        int position = genderSpinner.getSelectedItemPosition();
        String genderText = GENDERS[position];

        double weight;
        try {
            weight = Double.parseDouble(weightEditText.getText().toString());
        } catch (NumberFormatException e) {
            return;
        }

        if (!nicknameText.equals(user.getNickname()) || !genderText.equals(user.getGender()) ||
                weight != user.getWeight() || bac != user.getGoalBAC()) {
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

    private void delaySpam() {
        saveButton.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException ignored) {}
                UserSettingsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveButton.setEnabled(true);}
                });
            }
        }).start();
    }

    public void show() {
        final Dialog dialog = new Dialog(this);
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
                goalEditText.setText(bacs[np.getValue()]);

                double bac = ((double) np.getValue() + 1) / 10.0;

                goalQuoteTextView.setText(BacUtility.getQuoteRegisterTextBy(bac));
                goalQuoteTextView.setTextColor(BacUtility.getQuoteTextColorBy(bac));
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
}
