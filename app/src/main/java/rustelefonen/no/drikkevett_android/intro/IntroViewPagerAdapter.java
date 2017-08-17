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

    public IntroViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            default: return new WelcomeFragment();
            case 1: return new CostFragment();
            case 2: return new InfoFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
