package rustelefonen.no.drikkevett_android.unit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.tabs.calc.fragments.BeerScrollAdapter;

/**
 * Created by simenfonnes on 17.08.2017.
 */

public class UnitEditActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, Button.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public ViewPager unitViewPager;
    public RadioGroup radioGroup;

    public TextView percentTextView;
    public SeekBar percentSeekBar;

    public TextView amountTextView;
    public SeekBar amountSeekBar;

    public Button standardButton;

    public static String[] percentKeys = new String[] {"beer_percent_saved", "wine_percent_saved", "drink_percent_saved", "shot_percent_saved"};
    public static String[] amountKeys = new String[] {"beer_amount_saved", "wine_amount_saved", "drink_amount_saved", "shot_amount_saved"};

    public static float[] defaultPercent = new float[] {4.5f, 12.0f, 40.0f, 40.0f};
    public static int[] defaultAmount = new int[] {50, 12, 4, 4};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_layout);

        unitViewPager = (ViewPager) findViewById(R.id.unit_viewpager);
        unitViewPager.addOnPageChangeListener(this);
        unitViewPager.setAdapter(new BeerScrollAdapter(getSupportFragmentManager()));   //OBS
        unitViewPager.setCurrentItem(0);

        radioGroup = (RadioGroup) findViewById(R.id.unit_radio_group);
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.check(radioGroup.getChildAt(0).getId());

        percentTextView = (TextView) findViewById(R.id.unit_percent_text_view);
        percentSeekBar = (SeekBar) findViewById(R.id.unit_percent_seek_bar);
        percentSeekBar.setOnSeekBarChangeListener(this);

        amountTextView = (TextView) findViewById(R.id.unit_amount_text_view);
        amountSeekBar = (SeekBar) findViewById(R.id.unit_amount_seek_bar);
        amountSeekBar.setOnSeekBarChangeListener(this);

        standardButton = (Button) findViewById(R.id.unit_standard_button);
        standardButton.setOnClickListener(this);

        updateUnitValues(0);
        insertToolbar();
    }

    private void insertToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle("Enheter");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnitEditActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) radioGroup.check(R.id.unit_radio_one);
        else if (position == 1) radioGroup.check(R.id.unit_radio_two);
        else if (position == 2) radioGroup.check(R.id.unit_radio_three);
        else if (position == 3) radioGroup.check(R.id.unit_radio_four);

        updateUnitValues(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        int id = radioGroup.getCheckedRadioButtonId();
        if (id == R.id.unit_radio_one) unitViewPager.setCurrentItem(0);
        else if (id == R.id.unit_radio_two) unitViewPager.setCurrentItem(1);
        else if (id == R.id.unit_radio_three) unitViewPager.setCurrentItem(2);
        else if (id == R.id.unit_radio_four) unitViewPager.setCurrentItem(3);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.unit_standard_button) {
            int unitType = unitViewPager.getCurrentItem();

            float percent = defaultPercent[unitType];
            int amount = defaultAmount[unitType];

            String formattedPercent = percent + " %";
            percentTextView.setText(formattedPercent);

            String formattedAmount = amount + " cl";
            amountTextView.setText(formattedAmount);

            int percentProgress = ((int) (percent * 10f)) - 1;
            int amountProgress = amount - 1;

            percentSeekBar.setProgress(percentProgress);
            amountSeekBar.setProgress(amountProgress);

            saveUnit();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.unit_percent_seek_bar) {
            double percent = (double) (progress + 1) / 10.0;
            String formattedPercent = percent + " %";
            percentTextView.setText(formattedPercent);
        }
        else if (seekBar.getId() == R.id.unit_amount_seek_bar) {
            String formattedAmount = (progress + 1) + " cl";
            amountTextView.setText(formattedAmount);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.unit_percent_seek_bar || seekBar.getId() == R.id.unit_amount_seek_bar) {
            saveUnit();
        }
    }

    private void updateUnitValues(int unitType) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        float percent = sharedPref.getFloat(percentKeys[unitType], defaultPercent[unitType]);
        int amount = sharedPref.getInt(amountKeys[unitType], defaultAmount[unitType]);

        int percentProgress = ((int) (percent * 10f)) - 1;
        int amountProgress = amount - 1;

        percentSeekBar.setProgress(percentProgress);
        amountSeekBar.setProgress(amountProgress);
    }

    private void saveUnit() {
        int unitType = unitViewPager.getCurrentItem();
        float percent = Float.parseFloat(percentTextView.getText().toString().split(" ")[0]);
        int amount = Integer.parseInt(amountTextView.getText().toString().split(" ")[0]);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(percentKeys[unitType], percent);
        editor.putInt(amountKeys[unitType], amount);
        editor.commit();
    }
}