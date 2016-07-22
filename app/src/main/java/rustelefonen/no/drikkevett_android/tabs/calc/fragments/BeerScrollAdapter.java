package rustelefonen.no.drikkevett_android.tabs.calc.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by simenfonnes on 11.07.2016.
 */

public class BeerScrollAdapter extends FragmentPagerAdapter {

    public BeerScrollAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) return new WineFragment();
        else if (position == 2) return new DrinkFragment();
        else if (position == 3) return new ShotFragment();
        else return new BeerFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }

}
