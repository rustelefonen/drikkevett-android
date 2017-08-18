package rustelefonen.no.drikkevett_android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 18.08.2017.
 */

public class WhoWarningActivity extends AppCompatActivity {

    private static final String whoWarningKey = "who_warning";

    public Switch whoSwitch;
    public Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warning_layout);
        whoSwitch = (Switch) findViewById(R.id.who_switch);
        button = (Button) findViewById(R.id.who_button);

        whoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = WhoWarningActivity.this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(whoWarningKey, isChecked);
                editor.commit();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Går til info");
            }
        });

        insertToolbar();
        setSwitch();
    }

    private void insertToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle("Risikofylt alkoholbruk");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhoWarningActivity.super.onBackPressed();
            }
        });
    }

    private void setSwitch() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        whoSwitch.setChecked(sharedPref.getBoolean(whoWarningKey, true));
    }
}
