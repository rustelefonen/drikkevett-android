package rustelefonen.no.drikkevett_android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import rustelefonen.no.drikkevett_android.tabs.calc.BacCalcFragment;
import rustelefonen.no.drikkevett_android.tabs.dayAfter.BacDayAfterFragment;
import rustelefonen.no.drikkevett_android.tabs.history.BacHistoryFragment;
import rustelefonen.no.drikkevett_android.tabs.home.BacHomeFragment;
import rustelefonen.no.drikkevett_android.tabs.planParty.BacPlanPartyFragment;

/**
 * Created by simenfonnes on 20.07.2016.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            default: return new BacHomeFragment();
            case 1: return new BacCalcFragment();
            case 2: return new BacPlanPartyFragment();
            case 3: return new BacHistoryFragment();
        }
    }

    @Override
    public int getCount() { return 4; }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
