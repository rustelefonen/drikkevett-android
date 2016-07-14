package rustelefonen.no.drikkevett_android.intro;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;

import static android.R.attr.id;

/**
 * Created by simenfonnes on 13.07.2016.
 */

public class GoalRegistrationActivity extends AppCompatActivity {

    public static final String ID = "GoalRegistration";
    private static final int DIALOG_ID = 0;

    public EditText bacEditText;
    public EditText dateEditText;


    private User user;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_registration_layout);

        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) {
            user = (User) tmpUser;
        }

        bacEditText = (EditText) findViewById(R.id.goal_reg_bac_edit_text);
        dateEditText = (EditText) findViewById(R.id.goal_reg_date_edit_text);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new IntroDatepickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });

    }




    public void showDialog() {

    }

    public void finish(View view) {

    }
}
