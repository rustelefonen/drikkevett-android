package rustelefonen.no.drikkevett_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.intro.UserRegistrationActivity;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 14.07.2016.
 */

public class LauncherActivity extends AppCompatActivity {

    private User user;

    private boolean hasUser() {
        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();

        List<User> users = userDao.queryBuilder().list();
        superDao.close();

        if (users.size() <= 0) return false;
        user = users.get(0);

        return user != null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (hasUser()) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.ID, user);
        } else {
            intent = new Intent(this, UserRegistrationActivity.class);
        }
        startActivity(intent);
        finish();
    }
}