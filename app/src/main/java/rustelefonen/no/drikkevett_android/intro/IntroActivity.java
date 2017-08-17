package rustelefonen.no.drikkevett_android.intro;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 17.08.2017.
 */

public class IntroActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    public ViewPager viewPager;
    public RadioGroup radioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        viewPager = (ViewPager) findViewById(R.id.intro_viewpager);
        radioGroup = (RadioGroup) findViewById(R.id.intro_radio_group);


        viewPager.setAdapter(new IntroViewPagerAdapter(getSupportFragmentManager()));

        viewPager.addOnPageChangeListener(this);
        radioGroup.setOnCheckedChangeListener(this);

        radioGroup.check(radioGroup.getChildAt(0).getId());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) radioGroup.check(R.id.intro_radio_one);
        else if (position == 1) radioGroup.check(R.id.intro_radio_two);
        else if (position == 2) radioGroup.check(R.id.intro_radio_three);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        int id = group.getCheckedRadioButtonId();
        if (id == R.id.intro_radio_one) viewPager.setCurrentItem(0);
        else if (id == R.id.intro_radio_two) viewPager.setCurrentItem(1);
        else if (id == R.id.intro_radio_three) viewPager.setCurrentItem(2);
    }
}
