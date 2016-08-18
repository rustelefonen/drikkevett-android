package rustelefonen.no.drikkevett_android.settings;

import android.app.DialogFragment;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 15.07.2016.
 */

public class GoalSettingsActivity extends AppCompatActivity {

    public static final String ID = "GoalSettings";

    public EditText goalBacEditText;
    public EditText dateEditText;
    public SeekBar goalBacSeekBar;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_settings_layout);
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
        toolbar.setTitle("Målsetning");

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

    private void initWidgets() {
        goalBacEditText = (EditText) findViewById(R.id.goal_settings_bac_edit_text);
        dateEditText = (EditText) findViewById(R.id.goal_settings_date_edit_text);
        goalBacSeekBar = (SeekBar) findViewById(R.id.goal_settings_seek_bar);
        goalBacSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float permille = (float) progress / 10f;
                goalBacEditText.setText(Float.toString(permille));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void fillWidgets() {
        goalBacEditText.setText(Double.toString(user.getGoalBAC()));
        int currentProgress = (int) (user.getGoalBAC() * 10.0);
        goalBacSeekBar.setProgress(currentProgress);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(user.getGoalDate());
        dateEditText.setText(formattedDate);
    }

    public void showDialog(View view) {
        DialogFragment picker = new SettingsDatepickerFragment();
        picker.show(getFragmentManager(), "datePicker");
    }

    private void hasChanged() {
        double goalBac = getDouble(goalBacEditText.getText().toString());
        Date goalDate = getDate(dateEditText.getText().toString());

        if (goalBac != user.getGoalBAC() || !removeTime(goalDate).equals(removeTime(user.getGoalDate()))) {
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

    private Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private double getDouble(String number) {
        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Date getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private void goBack() {
        super.onBackPressed();
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

    public void saveNewGoals(View view) {
        double goalBac = getDouble(goalBacEditText.getText().toString());
        if (goalBac <= 0.0 || goalBac > 2.0) {
            Toast.makeText(this, "Ugyldig mål", Toast.LENGTH_SHORT).show();
            return;
        }
        Date goalDate = getDate(dateEditText.getText().toString());
        if (goalDate == null) {
            Toast.makeText(this, "Ugyldig dato", Toast.LENGTH_SHORT).show();
            return;
        }

        saveUser(goalBac, goalDate);

        Toast.makeText(this, "Endringene ble lagret", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveUser(double goalBac, Date goalDate) {
        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();

        List<User> users = userDao.queryBuilder().list();
        if (users.size() <= 0) return;

        User userToEdit = users.get(0);
        userToEdit.setGoalBAC(goalBac);
        userToEdit.setGoalDate(goalDate);

        userDao.update(userToEdit);

        superDao.close();
    }
}
