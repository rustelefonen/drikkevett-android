package rustelefonen.no.drikkevett_android.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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

    public EditText nicknameEditText;
    public AutoCompleteTextView genderAutocompleteTextView;
    public EditText weightEditText;
    public EditText ageEditText;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_layout);

        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) {
            user = (User) tmpUser;
        }
        initWidgets();
        fillWidgets();
    }

    private void initWidgets() {
        nicknameEditText = (EditText) findViewById(R.id.user_settings_nickname_edit_text);
        genderAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.user_settings_gender_auto_complete_text_view);
        weightEditText = (EditText) findViewById(R.id.user_settings_weight_edit_text);
        ageEditText = (EditText) findViewById(R.id.user_settings_age_edit_text);
    }

    private void fillWidgets() {
        String nickname = user.getNickname();
        if (nickname != null) nicknameEditText.setText(nickname);

        String gender = user.getGender();
        if (gender != null) genderAutocompleteTextView.setText(gender);

        weightEditText.setText(Double.toString(user.getWeight()));
        ageEditText.setText(Integer.toString(user.getAge()));
    }

    public void save (View view) {
        String nicknameText = nicknameEditText.getText().toString();
        if (nicknameText.isEmpty()) {
            Toast.makeText(this, "Kallenavn kan ikke være tomt.", Toast.LENGTH_SHORT).show();
            return;
        }
        String genderText = genderAutocompleteTextView.getText().toString();
        if (genderText.isEmpty()) {
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
        if (weight <= 0.0) {
            Toast.makeText(this, "For lav vekt", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "For lav vekt", Toast.LENGTH_SHORT).show();
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
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    public void backToMain(View view) {
        hasChanged();
    }

    private void goBack() {
        super.onBackPressed();
    }
}
