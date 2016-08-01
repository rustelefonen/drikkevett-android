package rustelefonen.no.drikkevett_android.goalreached;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by LarsPetterKristiansen on 25.07.2016.
 */

public class SetNewGoalActivity extends Activity{

    public static final String ID = "SetNewGoal";

    private User user;

    private double promilleBAC;
    private Date goalDate;

    private TextView titleTxtView, bacTextView;
    private EditText dateEditText;
    private Button saveBtn;
    private SeekBar seekBar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_goal);

        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) {
            user = (User) tmpUser;
        }

        initWidgets();
        fillWidgets();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkForWrongInput()){
                    showWrongInputDialog();
                } else {
                    showAlertView();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float perMille = (float) progress / 10f;
                bacTextView.setText(Float.toString(perMille));
                promilleBAC = perMille;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        disableSoftInputFromAppearing(dateEditText);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new GoalReachedDatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });
    }

    public static void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }

    private void saveAndGoToHome(){
        Date goalDate = getDate(dateEditText.getText().toString());
        saveUser(promilleBAC, goalDate);
        Toast.makeText(this, "Ny makspromille lagret", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.ID, user);
        startActivity(intent);
    }

    private Date getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean checkForWrongInput(){
        if(promilleBAC == 0 || dateEditText.getText().toString().equals("")){
            System.out.println("Ahh...");
            return false;
        } else {
            return true;
        }
    }

    private void fillWidgets(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(user.getGoalDate());
        System.out.println("Editted: " + formattedDate);
    }

    private void initWidgets(){
        titleTxtView = (TextView) findViewById(R.id.txtViewHeader_newGoal);
        saveBtn = (Button) findViewById(R.id.btn_saveNewGoal);
        seekBar = (SeekBar) findViewById(R.id.seekBar_newGoal);
        bacTextView = (TextView) findViewById(R.id.txtView_newGoalBAC);
        dateEditText = (EditText) findViewById(R.id.goal_reached_date_editText);
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

    private void showAlertView() {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        alert_builder.setMessage("").setCancelable(false).setPositiveButton("Bekreft", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveAndGoToHome();
            }
        }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("Sett makspromille");
        alert.show();
    }

    private void showWrongInputDialog() {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        alert_builder.setMessage("Feil innskrivning. Vennligst velg gyldige verdier!").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("Feil!");
        alert.show();
    }
}
