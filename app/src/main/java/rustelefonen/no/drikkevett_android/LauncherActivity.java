package rustelefonen.no.drikkevett_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import rustelefonen.no.drikkevett_android.db.InformationCategory;
import rustelefonen.no.drikkevett_android.db.InformationCategoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.information.DBSeeder;
import rustelefonen.no.drikkevett_android.intro.DisclaimerActivity;
import rustelefonen.no.drikkevett_android.intro.IntroActivity;
import rustelefonen.no.drikkevett_android.intro.UserRegistrationActivity;
import rustelefonen.no.drikkevett_android.intro.WelcomeActivity;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 14.07.2016.
 */

public class LauncherActivity extends AppCompatActivity {

    private boolean hasUser() {
        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();
        List<User> users = userDao.queryBuilder().list();
        superDao.close();
        if (users.size() <= 0) return false;
        User user = users.get(0);
        return user != null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (hasUser()) intent = new Intent(this, MainActivity.class);
        else intent = new Intent(this, DisclaimerActivity.class);
        startActivity(intent);
        finish();
    }
}
