package rustelefonen.no.drikkevett_android.extra.guidance.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by RUStelefonen on 26.07.2016.
 */

public class GuideScrollAdapter  extends FragmentPagerAdapter {

    public GuideScrollAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) return new bac_calc_fragment_G();
        else if (position == 2) return new plan_Party_fragment_G();
        else if (position == 3) return new day_after_fragment_G();
        else if (position == 4) return new history_fragment_G();
        else if (position == 5) return new calculation_fragment_G();
        else if (position == 6) return new info_units_fragment_G();
        else if (position == 7) return new info_widgets_fragment_G();
        else return new HomeFragment_G();
    }

    @Override
    public int getCount() {
        return 8;
    }
}
