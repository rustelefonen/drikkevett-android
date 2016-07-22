package rustelefonen.no.drikkevett_android.tabs.calc.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by simenfonnes on 11.07.2016.
 */

public class BeerScrollAdapter extends FragmentPagerAdapter {

    public BeerScrollAdapter(FragmentManager fm) {
        super(fm);
    }

    private Fragment[] fragments = new Fragment[] {
            new BeerFragment(), new WineFragment(), new DrinkFragment(), new ShotFragment()
    };

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
