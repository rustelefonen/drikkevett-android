package rustelefonen.no.drikkevett_android.intro;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 14.07.2016.
 */

public class IntroDatepickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        //datePickerDialog.getDatePicker().setMinDate(new Date().getTime()-(new Date().getTime()%(24*60*60*1000)));
        //datePickerDialog.getDatePicker().setMinDate(new Date().getTime() + (new Date().getTime()%(24*60*60*1000)));

        datePickerDialog.getDatePicker().setMinDate(getTomorrow());



        return datePickerDialog;
    }

    private long getTomorrow() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        return c.getTime().getTime();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        String formattedDate = sdf.format(c.getTime());
        EditText dateEditText = (EditText) getActivity().findViewById(R.id.goal_reg_date_edit_text);
        dateEditText.setText(formattedDate);
        System.out.println(formattedDate);
    }


}
