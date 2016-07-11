package rustelefonen.no.drikkevett_android.tabs.calc;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import rustelefonen.no.drikkevett_android.tabs.BacHistoryFragment;

/**
 * Created by simenfonnes on 11.07.2016.
 */

public class BeveragePagerAdapter extends FragmentStatePagerAdapter {

    public BeveragePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new BacHistoryFragment();
    }

    @Override
    public int getCount() {
        return 0;
    }
}
