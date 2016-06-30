package rustelefonen.no.drikkevett_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by simenfonnes on 30.06.2016.
 */

public class UserInfo extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(findViewById(R.id.user_info));
        setTitle("Brukerinformasjon");
    }
}
