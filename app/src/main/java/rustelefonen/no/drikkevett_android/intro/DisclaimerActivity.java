package rustelefonen.no.drikkevett_android.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 18.08.2017.
 */

public class DisclaimerActivity extends AppCompatActivity implements Button.OnClickListener {

    public Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disclaimer_layout);

        button = (Button) findViewById(R.id.intro_disclaimer_button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.intro_disclaimer_button){
            startActivity(new Intent(this, IntroActivity.class));
        }
    }
}
