package rustelefonen.no.drikkevett_android.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 20.07.2016.
 */

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
    }

    public void start(View view) {
        startActivity(new Intent(this, UserRegistrationActivity.class));
    }

    public void guide(View view) {
        Toast.makeText(this, "Her kommer det veiledning!", Toast.LENGTH_SHORT).show();
    }
}
