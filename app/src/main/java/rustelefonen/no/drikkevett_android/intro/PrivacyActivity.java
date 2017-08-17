package rustelefonen.no.drikkevett_android.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 17.08.2017.
 */

public class PrivacyActivity extends AppCompatActivity implements Button.OnClickListener{

    public Button acceptButton;
    public static final String PRIVACY_USER = "privacy_user";
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_layout);

        acceptButton = (Button) findViewById(R.id.intro_privacy_accept_button);
        acceptButton.setOnClickListener(this);

        user = (User) getIntent().getSerializableExtra(PRIVACY_USER);

        insertToolbar();
    }

    private void insertToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle("Personvern");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivacyActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.intro_privacy_accept_button) {
            SuperDao superDao = new SuperDao(this);
            UserDao userDao = superDao.getUserDao();
            userDao.insert(user);
            superDao.close();

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra(MainActivity.ID, user);
            startActivity(intent);

        }
    }
}
