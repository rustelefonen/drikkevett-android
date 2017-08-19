package rustelefonen.no.drikkevett_android.tabs.drinkEpisode;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by simenfonnes on 18.08.2017.
 */

public class DrinkEpisodeAdapter extends FragmentPagerAdapter{

    Fragment[] fragments = new Fragment[] {new PlanPartyFragment()};

    public DrinkEpisodeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() { return fragments.length; }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}