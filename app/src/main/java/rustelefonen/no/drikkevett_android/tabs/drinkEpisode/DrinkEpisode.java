package rustelefonen.no.drikkevett_android.tabs.drinkEpisode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rustelefonen.no.drikkevett_android.NonSwipeableViewPager;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.SectionsPagerAdapter;
import rustelefonen.no.drikkevett_android.SelectedPageEvent;
import rustelefonen.no.drikkevett_android.db.NewHistory;
import rustelefonen.no.drikkevett_android.db.NewHistoryDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 18.08.2017.
 */

public class DrinkEpisode extends Fragment implements NonSwipeableViewPager.OnPageChangeListener {

    public NonSwipeableViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.drink_episode_layout, container, false);
        viewPager = (NonSwipeableViewPager) view.findViewById(R.id.drink_episode_view_pager);
        viewPager.setAdapter(new DrinkEpisodeAdapter(getActivity().getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(this);
        onPageSelected(0);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {
            System.out.println("grr");
            if (!isVisibleToUser) {

                System.out.println("brr");
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        /*ActionBar actionBar = getContext().getSupportActionBar();
        if (actionBar == null) return;
        String title = position == 0 ? "Hjem" : position == 1 ? "Promillekalkulator"
                : position == 2 ? "Drikkeepisode" : position == 3 ? "Historikk" : "";
        actionBar.setTitle(title);*/
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public enum Status {
        RUNNING, NOT_RUNNING
    }

    private Status getCurrentStatus() {
        SuperDao superDao = new SuperDao(getContext());
        NewHistoryDao newHistoryDao = superDao.getNewHistoryDao();
        List<NewHistory> histories = newHistoryDao.queryBuilder().list();

        for (NewHistory history : histories) {
            if (history.getEndDate() == null) return Status.RUNNING;
        }
        return Status.NOT_RUNNING;
    }

    private NewHistory getCurrentHistory() {
        SuperDao superDao = new SuperDao(getContext());
        NewHistoryDao newHistoryDao = superDao.getNewHistoryDao();
        List<NewHistory> histories = newHistoryDao.queryBuilder().list();

        for (NewHistory history : histories) {
            if (history.getEndDate() == null) return history;
        }
        return null;
    }

    public void changeView() {
        System.out.println("GRRR");
    }
}
