package rustelefonen.no.drikkevett_android.tabs.drinkEpisode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.NewHistory;
import rustelefonen.no.drikkevett_android.db.NewHistoryDao;
import rustelefonen.no.drikkevett_android.db.Unit;
import rustelefonen.no.drikkevett_android.db.UnitDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.tabs.calc.fragments.BeerScrollAdapter;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.util.BacUtility;

/**
 * Created by simenfonnes on 18.08.2017.
 */

public class DrinkEpisode extends Fragment implements Button.OnClickListener, ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    public LinearLayout planPartyLinearLayout;
    public LinearLayout currentPartyLinearLayout;

    //Plan
    public ViewPager planPartyViewPager;
    public RadioGroup planPartyRadioGroup;
    public Button planPartyRemoveButton;
    public Button planPartyAddButton;

    public TextView planPartyBeerUnits;
    public TextView planPartyWineUnits;
    public TextView planPartyDrinkUnits;
    public TextView planPartyShotUnits;

    public TextView planPartyExpectedBac;
    public TextView planPartyExpectedCost;
    public Button planPartyStartEveningButton;

    //Current
    public TextView currentPartyBac;
    public TextView currentPartyQuote;
    public ViewPager currentPartyViewPager;
    public RadioGroup currentPartyRadioGroup;

    public Button currentPartyUndoButton;
    public Button currentPartyAddButton;

    public TextView currentPartyBeerUnits;
    public TextView currentPartyWineUnits;
    public TextView currentPartyDrinkUnits;
    public TextView currentPartyShotUnits;

    public Button currentPartyEndEveningButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.drink_episode_layout, container, false);

        initWidgets(view);
        setListeners();

        setView();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {
            if (isVisibleToUser) {
                setView();
            }
        }
    }

    private void setView() {
        Status status = getCurrentStatus();
        if (status == Status.NOT_RUNNING) {
            planPartyLinearLayout.setVisibility(View.VISIBLE);
            currentPartyLinearLayout.setVisibility(View.GONE);
        }
        else if (status == Status.RUNNING) {
            currentPartyLinearLayout.setVisibility(View.VISIBLE);
            planPartyLinearLayout.setVisibility(View.GONE);

            initCurrentParty();
        }
    }

    private void initCurrentParty() {
        NewHistory newHistory = getCurrentHistory();
        currentPartyBeerUnits.setText("0/" + newHistory.getBeerPlannedUnitCount());
        currentPartyWineUnits.setText("0/" + newHistory.getWinePlannedUnitCount());
        currentPartyDrinkUnits.setText("0/" + newHistory.getDrinkPlannedUnitCount());
        currentPartyShotUnits.setText("0/" + newHistory.getShotPlannedUnitCount());


    }

    public enum Status {
        RUNNING, NOT_RUNNING
    }

    private Status getCurrentStatus() {
        SuperDao superDao = new SuperDao(getContext());
        NewHistoryDao newHistoryDao = superDao.getNewHistoryDao();
        List<NewHistory> histories = newHistoryDao.queryBuilder().list();
        superDao.close();

        for (NewHistory history : histories) {
            if (history.getEndDate() == null) return Status.RUNNING;
        }
        return Status.NOT_RUNNING;
    }

    private NewHistory getCurrentHistory() {
        SuperDao superDao = new SuperDao(getContext());
        NewHistoryDao newHistoryDao = superDao.getNewHistoryDao();
        List<NewHistory> histories = newHistoryDao.queryBuilder().list();
        superDao.close();

        for (NewHistory history : histories) {
            if (history.getEndDate() == null) return history;
        }
        return null;
    }

    private void initWidgets(View view) {
        planPartyLinearLayout = (LinearLayout) view.findViewById(R.id.drink_episode_plan_party);
        currentPartyLinearLayout = (LinearLayout) view.findViewById(R.id.drink_episode_current_party);

        //Plan
        planPartyViewPager = (ViewPager) view.findViewById(R.id.plan_party_view_pager);
        planPartyRadioGroup = (RadioGroup) view.findViewById(R.id.plan_party_radio_group);
        planPartyRemoveButton = (Button) view.findViewById(R.id.plan_party_remove_button);
        planPartyAddButton = (Button) view.findViewById(R.id.plan_party_add_button);

        planPartyBeerUnits = (TextView) view.findViewById(R.id.plan_party_beer_units);
        planPartyWineUnits = (TextView) view.findViewById(R.id.plan_party_wine_units);
        planPartyDrinkUnits = (TextView) view.findViewById(R.id.plan_party_drink_units);
        planPartyShotUnits = (TextView) view.findViewById(R.id.plan_party_shot_units);

        planPartyExpectedBac = (TextView) view.findViewById(R.id.plan_party_expected_bac);
        planPartyExpectedCost = (TextView) view.findViewById(R.id.plan_party_expected_cost);
        planPartyStartEveningButton = (Button) view.findViewById(R.id.plan_party_start_evening);

        //Current
        currentPartyBac = (TextView) view.findViewById(R.id.current_party_bac);
        currentPartyQuote = (TextView) view.findViewById(R.id.current_party_quote);
        currentPartyViewPager = (ViewPager) view.findViewById(R.id.current_party_view_pager);
        currentPartyRadioGroup = (RadioGroup) view.findViewById(R.id.current_party_radio_group);

        currentPartyUndoButton = (Button) view.findViewById(R.id.current_party_undo_button);
        currentPartyAddButton = (Button) view.findViewById(R.id.current_party_add_button);

        currentPartyBeerUnits = (TextView) view.findViewById(R.id.current_party_beer_units);
        currentPartyWineUnits = (TextView) view.findViewById(R.id.current_party_wine_units);
        currentPartyDrinkUnits = (TextView) view.findViewById(R.id.current_party_drink_units);
        currentPartyShotUnits = (TextView) view.findViewById(R.id.current_party_shot_units);

        currentPartyEndEveningButton = (Button) view.findViewById(R.id.current_party_end_evening);
    }

    private void setListeners() {
        planPartyRemoveButton.setOnClickListener(this);
        planPartyAddButton.setOnClickListener(this);
        planPartyViewPager.addOnPageChangeListener(this);
        planPartyRadioGroup.setOnCheckedChangeListener(this);
        planPartyStartEveningButton.setOnClickListener(this);

        planPartyViewPager.setAdapter(new BeerScrollAdapter(this.getChildFragmentManager()));
        planPartyViewPager.setCurrentItem(0);
        planPartyRadioGroup.check(planPartyRadioGroup.getChildAt(0).getId());

        currentPartyViewPager.addOnPageChangeListener(this);
        currentPartyRadioGroup.setOnCheckedChangeListener(this);
        currentPartyUndoButton.setOnClickListener(this);
        currentPartyAddButton.setOnClickListener(this);
        currentPartyEndEveningButton.setOnClickListener(this);

        currentPartyViewPager.setAdapter(new BeerScrollAdapter(this.getChildFragmentManager()));
        currentPartyViewPager.setCurrentItem(0);
        currentPartyRadioGroup.check(currentPartyRadioGroup.getChildAt(0).getId());
    }

    private void updateBac() {
        double beerUnitCount = tryParseDouble(planPartyBeerUnits.getText().toString());
        double wineUnitCount = tryParseDouble(planPartyWineUnits.getText().toString());
        double drinkUnitCount = tryParseDouble(planPartyDrinkUnits.getText().toString());
        double shotUnitCount = tryParseDouble(planPartyShotUnits.getText().toString());

        double hours = 0.0;

        User user = ((MainActivity)getActivity()).getUser();

        boolean gender = user.getGender().equals("Mann");
        double weight = user.getWeight();

        double bac = BacUtility.calculateBac(beerUnitCount, wineUnitCount, drinkUnitCount, shotUnitCount, hours, gender, weight);

        planPartyExpectedBac.setText(new DecimalFormat("##.00").format(bac));
    }

    private void updateCost() {
        double beerUnitCount = tryParseDouble(planPartyBeerUnits.getText().toString());
        double wineUnitCount = tryParseDouble(planPartyWineUnits.getText().toString());
        double drinkUnitCount = tryParseDouble(planPartyDrinkUnits.getText().toString());
        double shotUnitCount = tryParseDouble(planPartyShotUnits.getText().toString());

        User user = ((MainActivity)getActivity()).getUser();

        double totalCost = (beerUnitCount * user.getBeerPrice()) + (wineUnitCount * user.getWinePrice()) +
                (drinkUnitCount * user.getDrinkPrice()) + (shotUnitCount * user.getShotPrice());
        planPartyExpectedCost.setText("" + totalCost);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.plan_party_remove_button) removeUnitFromPlanParty(planPartyViewPager.getCurrentItem());
        else if (v.getId() == R.id.plan_party_add_button) addUnitToPlanParty(planPartyViewPager.getCurrentItem());
        else if (v.getId() == R.id.plan_party_start_evening) startEvening();
        else if (v.getId() == R.id.current_party_undo_button) undoUnitFromCurrentParty();
        else if (v.getId() == R.id.current_party_add_button) {
            addUnitToCurrentParty(currentPartyViewPager.getCurrentItem());
            NewHistory newHistory = getCurrentHistory();
            for (Unit unit : newHistory.getUnits()) {
                System.out.println(unit.getUnitType());
            }
        }
        else if (v.getId() == R.id.current_party_end_evening) endEvening();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (planPartyLinearLayout.getVisibility() == View.VISIBLE) {
            if (position == 0) planPartyRadioGroup.check(R.id.plan_party_button_one);
            else if (position == 1) planPartyRadioGroup.check(R.id.plan_party_button_two);
            else if (position == 2) planPartyRadioGroup.check(R.id.plan_party_button_three);
            else if (position == 3) planPartyRadioGroup.check(R.id.plan_party_button_four);
        }
        else {
            if (position == 0) currentPartyRadioGroup.check(R.id.current_party_radio_one);
            else if (position == 1) currentPartyRadioGroup.check(R.id.current_party_radio_two);
            else if (position == 2) currentPartyRadioGroup.check(R.id.current_party_radio_three);
            else if (position == 3) currentPartyRadioGroup.check(R.id.current_party_radio_four);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getCheckedRadioButtonId();

        //Plan
        if (id == R.id.plan_party_button_one) planPartyViewPager.setCurrentItem(0);
        else if (id == R.id.plan_party_button_two) planPartyViewPager.setCurrentItem(1);
        else if (id == R.id.plan_party_button_three) planPartyViewPager.setCurrentItem(2);
        else if (id == R.id.plan_party_button_four) planPartyViewPager.setCurrentItem(3);

        //Current
        else if (id == R.id.current_party_radio_one) currentPartyViewPager.setCurrentItem(0);
        else if (id == R.id.current_party_radio_two) currentPartyViewPager.setCurrentItem(1);
        else if (id == R.id.current_party_radio_three) currentPartyViewPager.setCurrentItem(2);
        else if (id == R.id.current_party_radio_four) currentPartyViewPager.setCurrentItem(3);
    }

    private int tryParse(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private double tryParseDouble(String number) {
        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException ignored) {
            return -1.0;
        }
    }

    private void addUnitToPlanParty(int unitType) {
        if (unitType == 0) {
            int beerUnitCount = tryParse(planPartyBeerUnits.getText().toString());
            if (beerUnitCount < 20) {
                planPartyBeerUnits.setText("" + (beerUnitCount + 1));
            }
        }
        else if (unitType == 1) {
            int wineUnitCount = tryParse(planPartyWineUnits.getText().toString());
            if (wineUnitCount < 20) {
                planPartyWineUnits.setText("" + (wineUnitCount + 1));
            }
        }
        else if (unitType == 2) {
            int drinkUnitCount = tryParse(planPartyDrinkUnits.getText().toString());
            if (drinkUnitCount < 20) {
                planPartyDrinkUnits.setText("" + (drinkUnitCount + 1));
            }
        }
        else if (unitType == 3) {
            int shotUnitCount = tryParse(planPartyShotUnits.getText().toString());
            if (shotUnitCount < 20) {
                planPartyShotUnits.setText("" + (shotUnitCount + 1));
            }
        }
        updateBac();
        updateCost();
    }

    private void removeUnitFromPlanParty(int unitType) {
        if (unitType == 0) {
            int beerUnitCount = tryParse(planPartyBeerUnits.getText().toString());
            if (beerUnitCount > 0) {
                planPartyBeerUnits.setText("" + (beerUnitCount - 1));
            }
        }
        else if (unitType == 1) {
            int wineUnitCount = tryParse(planPartyWineUnits.getText().toString());
            if (wineUnitCount > 0) {
                planPartyWineUnits.setText("" + (wineUnitCount - 1));
            }
        }
        else if (unitType == 2) {
            int drinkUnitCount = tryParse(planPartyDrinkUnits.getText().toString());
            if (drinkUnitCount > 0) {
                planPartyDrinkUnits.setText("" + (drinkUnitCount - 1));
            }
        }
        else if (unitType == 3) {
            int shotUnitCount = tryParse(planPartyShotUnits.getText().toString());
            if (shotUnitCount > 0) {
                planPartyShotUnits.setText("" + (shotUnitCount - 1));
            }
        }
        updateBac();
        updateCost();
    }

    private void addUnitToCurrentParty(int unitType) {
        NewHistory newHistory = getCurrentHistory();
        if (newHistory == null) return;

        String[] unitTypes= new String[]{"Beer", "Wine", "Drink", "Shot"};

        Unit unit = new Unit();
        unit.setUnitType(unitTypes[unitType]);
        unit.setTimestamp(new Date());
        unit.setHistoryId(newHistory.getId());

        newHistory.getUnits().add(unit);
        newHistory.resetUnits();

        SuperDao superDao = new SuperDao(getContext());
        UnitDao unitDao = superDao.getUnitDao();
        unitDao.insert(unit);
        //superDao.close();
    }

    private void undoUnitFromCurrentParty() {

    }

    private void endEvening() {
        NewHistory newHistory = getCurrentHistory();
        if (newHistory == null) return;

        newHistory.setBeerGrams(BacUtility.getUnitGrams(0));
        newHistory.setWineGrams(BacUtility.getUnitGrams(1));
        newHistory.setDrinkGrams(BacUtility.getUnitGrams(2));
        newHistory.setShotGrams(BacUtility.getUnitGrams(3));

        User user = ((MainActivity)getActivity()).getUser();

        newHistory.setBeerCost(user.getBeerPrice());
        newHistory.setWineCost(user.getWinePrice());
        newHistory.setDrinkCost(user.getDrinkPrice());
        newHistory.setShotCost(user.getShotPrice());

        newHistory.setGender(user.getGender().equals("Mann"));
        newHistory.setWeight(user.getWeight());
        newHistory.setEndDate(new Date());

        SuperDao superDao = new SuperDao(getContext());
        NewHistoryDao newHistoryDao = superDao.getNewHistoryDao();
        newHistoryDao.update(newHistory);
        superDao.close();

        setView();
    }

    private void startEvening() {
        int beerUnitCount = tryParse(planPartyBeerUnits.getText().toString());
        int wineUnitCount = tryParse(planPartyWineUnits.getText().toString());
        int drinkUnitCount = tryParse(planPartyDrinkUnits.getText().toString());
        int shotUnitCount = tryParse(planPartyShotUnits.getText().toString());

        SuperDao superDao = new SuperDao(getContext());
        NewHistoryDao newHistoryDao = superDao.getNewHistoryDao();
        NewHistory newHistory = new NewHistory();

        newHistory.setBeginDate(new Date());
        newHistory.setBeerPlannedUnitCount(beerUnitCount);
        newHistory.setWinePlannedUnitCount(wineUnitCount);
        newHistory.setDrinkPlannedUnitCount(drinkUnitCount);
        newHistory.setShotPlannedUnitCount(shotUnitCount);

        User user = ((MainActivity)getActivity()).getUser();

        newHistory.setBeerCost(user.getBeerPrice());
        newHistory.setWineCost(user.getWinePrice());
        newHistory.setDrinkCost(user.getDrinkPrice());
        newHistory.setShotCost(user.getShotPrice());

        newHistory.setBeerGrams(BacUtility.getUnitGrams(0));
        newHistory.setWineGrams(BacUtility.getUnitGrams(1));
        newHistory.setDrinkGrams(BacUtility.getUnitGrams(2));
        newHistory.setShotGrams(BacUtility.getUnitGrams(3));

        newHistory.setGender(user.getGender().equals("Mann"));
        newHistory.setWeight(user.getWeight());

        newHistoryDao.insert(newHistory);
        superDao.close();

        setView();
    }

}
