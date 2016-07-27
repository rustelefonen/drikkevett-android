package rustelefonen.no.drikkevett_android.extra.guidance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import java.util.List;

import rustelefonen.no.drikkevett_android.extra.guidance.fragments.GuideScrollAdapter;
import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.information.DBSeeder;
import rustelefonen.no.drikkevett_android.intro.UserRegistrationActivity;
import rustelefonen.no.drikkevett_android.intro.WelcomeActivity;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by RUStelefonen on 26.07.2016.
 */

public class Guidance extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        RadioGroup.OnCheckedChangeListener {

    private User user;
    private Button finishBtn;
    private ViewPager guideScroll;
    private RadioGroup pageIndicatorGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidance);
        initWidgets();
        setListeners();
        fillWidgets();

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleIntents();
            }
        });
        pageIndicatorGroup.check(pageIndicatorGroup.getChildAt(0).getId());
    }

    private boolean hasUser() {
        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();

        List<User> users = userDao.queryBuilder().list();
        superDao.close();

        if (users.size() <= 0) return false;
        user = users.get(0);

        return user != null;
    }

    private void handleIntents(){
        DBSeeder.seed(this);
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

    private void setListeners() {
        pageIndicatorGroup.setOnCheckedChangeListener(this);
        guideScroll.addOnPageChangeListener(this);
    }

    private void fillWidgets(){

        guideScroll.setAdapter(new GuideScrollAdapter(getSupportFragmentManager()));
        guideScroll.setCurrentItem(0);
        if (hasUser()) {
            finishBtn.setVisibility(View.GONE);
        } else {
            finishBtn.setVisibility(View.VISIBLE);
            finishBtn.setText("Fortsett til registrering");
        }
    }

    private void initWidgets(){
        finishBtn = (Button) findViewById(R.id.finish_guidance_btn);
        guideScroll = (ViewPager) findViewById(R.id.guide_scroll_plan_party);
        pageIndicatorGroup = (RadioGroup) findViewById(R.id.page_indicator_radio_guidance);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (position == 0) pageIndicatorGroup.check(R.id.radio_one_G);
        else if (position == 1) pageIndicatorGroup.check(R.id.radio_two_G);
        else if (position == 2) pageIndicatorGroup.check(R.id.radio_three_G);
        else if (position == 3) pageIndicatorGroup.check(R.id.radio_four_G);
        else if (position == 4) pageIndicatorGroup.check(R.id.radio_five_G);
        else if (position == 5) pageIndicatorGroup.check(R.id.radio_six_G);
        else if (position == 6) pageIndicatorGroup.check(R.id.radio_seven_G);
        else if (position == 7) pageIndicatorGroup.check(R.id.radio_eight_G);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onCheckedChanged(RadioGroup group, int i) {
        int id = group.getCheckedRadioButtonId();
        if (id == R.id.radio_one_G) guideScroll.setCurrentItem(0);
        else if (id == R.id.radio_two_G) guideScroll.setCurrentItem(1);
        else if (id == R.id.radio_three_G) guideScroll.setCurrentItem(2);
        else if (id == R.id.radio_four_G) guideScroll.setCurrentItem(3);
        else if (id == R.id.radio_five_G) guideScroll.setCurrentItem(4);
        else if (id == R.id.radio_six_G) guideScroll.setCurrentItem(5);
        else if (id == R.id.radio_seven_G) guideScroll.setCurrentItem(6);
        else if (id == R.id.radio_eight_G) guideScroll.setCurrentItem(7);
    }
}
