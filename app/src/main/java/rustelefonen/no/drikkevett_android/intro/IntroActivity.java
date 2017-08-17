package rustelefonen.no.drikkevett_android.intro;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.intro.fragments.CostFragment;
import rustelefonen.no.drikkevett_android.unit.UnitEditActivity;

/**
 * Created by simenfonnes on 17.08.2017.
 */

public class IntroActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    public ViewPager viewPager;
    public RadioGroup radioGroup;

    public static final int[] defaultPrices = new int[] {70, 80, 110, 90};

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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

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

    private int tryParse(String number) {
        try {
            return Integer.parseInt(number);
        }
        catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public User getUserWithCostValues() {
        User user = new User();

        CostFragment costFragment = (CostFragment) ((IntroViewPagerAdapter)viewPager.getAdapter()).getItem(1);

        int beerPrice = tryParse(costFragment.beerEditText.getText().toString());
        user.setBeerPrice(beerPrice > 0 ? beerPrice : defaultPrices[0]);

        int winePrice = tryParse(costFragment.wineEditText.getText().toString());
        user.setWinePrice(winePrice > 0 ? winePrice : defaultPrices[1]);

        int drinkPrice = tryParse(costFragment.drinkEditText.getText().toString());
        user.setDrinkPrice(drinkPrice > 0 ? drinkPrice : defaultPrices[2]);

        int shotPrice = tryParse(costFragment.shotEditText.getText().toString());
        user.setShotPrice(shotPrice > 0 ? shotPrice : defaultPrices[3]);

        return user;
    }
}
