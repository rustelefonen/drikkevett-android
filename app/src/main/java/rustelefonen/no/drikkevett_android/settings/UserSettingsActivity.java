package rustelefonen.no.drikkevett_android.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 14.07.2016.
 */

public class UserSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_layout);
    }
}
