package rustelefonen.no.drikkevett_android.intro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import rustelefonen.no.drikkevett_android.intro.fragments.CostFragment;
import rustelefonen.no.drikkevett_android.intro.fragments.InfoFragment;
import rustelefonen.no.drikkevett_android.intro.fragments.WelcomeFragment;

/**
 * Created by simenfonnes on 17.08.2017.
 */

public class IntroViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments = new Fragment[3];

    public IntroViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments[0] = new WelcomeFragment();
        fragments[1] = new CostFragment();
        fragments[2] = new InfoFragment();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
