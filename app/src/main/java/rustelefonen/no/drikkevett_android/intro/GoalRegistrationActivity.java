package rustelefonen.no.drikkevett_android.intro;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 13.07.2016.
 */

public class GoalRegistrationActivity extends AppCompatActivity {

    public static final String ID = "GoalRegistration";
    private static final String PROGRESS_START = 0.5f + "";

    public EditText bacEditText;
    public EditText dateEditText;
    public SeekBar bacSeekBar;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_registration_layout);

        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) {
            user = (User) tmpUser;
        }
        initWidgets();
        fillWidgets();
    }

    private void initWidgets() {
        bacEditText = (EditText) findViewById(R.id.goal_reg_bac_edit_text);
        dateEditText = (EditText) findViewById(R.id.goal_reg_date_edit_text);
        bacSeekBar = (SeekBar) findViewById(R.id.goal_reg_seek_bar);
        bacSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float perMille = (float) progress / 10f;
                bacEditText.setText(Float.toString(perMille));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void fillWidgets() {
        bacEditText.setText(PROGRESS_START);
        bacSeekBar.setProgress(5);
    }

    public void showDialog(View view) {
        DialogFragment picker = new IntroDatepickerFragment();
        picker.show(getFragmentManager(), "datePicker");
    }

    public void finish(View view) {
        String goalBacText = bacEditText.getText().toString();
        if (!goalBacText.isEmpty()) {
            try {
                double goalBac = Double.parseDouble(goalBacText);
                user.setGoalBAC(goalBac);
            } catch (NumberFormatException ignored) {
                Toast.makeText(this, "Du må registrere mål for å gå videre", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "Du må registrere mål for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateText = dateEditText.getText().toString();
        if (!dateText.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date goalDate = sdf.parse(dateText);
                user.setGoalDate(goalDate);
            } catch (ParseException e) {
                Toast.makeText(this, "Du må registrere måldato for å gå videre", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "Du må registrere måldato for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        }

        insertUser();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.ID, user);
        startActivity(intent);
    }

    private void insertUser() {
        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();
        userDao.insert(user);
        superDao.close();
    }
}
