package rustelefonen.no.drikkevett_android.tabs.drinkEpisode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import java.text.NumberFormat;
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
import rustelefonen.no.drikkevett_android.tabs.home.HistoryUtility;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.unit.UnitEditActivity;
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

    private Runnable timeUpdater;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.drink_episode_layout, container, false);

        handler = new Handler();

        initWidgets(view);
        setListeners();

        setView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getCurrentStatus() == Status.RUNNING) {
            timeUpdater = new Runnable() {
                @Override
                public void run() {
                    updateRunningBac();
                    handler.postDelayed(this, 1000);
                }
            };
            timeUpdater.run();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(timeUpdater);
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

    private void updateRunningBac() {
        double beerUnits = tryParseDouble(currentPartyBeerUnits.getText().toString().split("/")[0]);
        double wineUnits = tryParseDouble(currentPartyWineUnits.getText().toString().split("/")[0]);
        double drinkUnits = tryParseDouble(currentPartyDrinkUnits.getText().toString().split("/")[0]);
        double shotUnits = tryParseDouble(currentPartyShotUnits.getText().toString().split("/")[0]);

        User user = ((MainActivity)getActivity()).getUser();

        Date firstUnitAddedDate = getDateOfFirstUnitAdded();
        if (firstUnitAddedDate == null) firstUnitAddedDate = new Date();

        double hours = (new Date().getTime() - firstUnitAddedDate.getTime()) / 3600000.0;
        boolean gender = user.getGender().equals("Mann");
        double weight = user.getWeight();

        double bac = BacUtility.calculateBac(beerUnits, wineUnits, drinkUnits, shotUnits, getUnitGrams(0), getUnitGrams(1), getUnitGrams(2), getUnitGrams(3), hours, gender, weight);

        String bacText = new DecimalFormat("##.00").format(bac) + "\u2030";
        currentPartyBac.setText(bacText);
        currentPartyQuote.setText(BacUtility.getQuoteTextBy(bac));
        int color = BacUtility.getQuoteTextColorBy(bac);
        currentPartyQuote.setTextColor(color);
        currentPartyBac.setTextColor(color);
    }

    public double getUnitGrams(int unitType) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("drikkevettShared", Context.MODE_PRIVATE);

        float percent = sharedPref.getFloat(UnitEditActivity.percentKeys[unitType], UnitEditActivity.defaultPercent[unitType]);
        int amount = sharedPref.getInt(UnitEditActivity.amountKeys[unitType], UnitEditActivity.defaultAmount[unitType]);

        return amount * percent / 10.0f;
    }

    private Date getDateOfFirstUnitAdded() {
        NewHistory history = getCurrentHistory();
        List<Unit> units = HistoryUtility.getHistoryUnitsOrdered(history, getContext());

        if (units.size() > 0) return units.get(0).getTimestamp();
        return null;
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

            updateParty();
        }
    }

    private void updateParty() {
        NewHistory newHistory = getCurrentHistory();
        if (newHistory == null) return;

        List<Unit> units = HistoryUtility.getHistoryUnits(newHistory, getContext());

        int beerUnits = 0;
        int wineUnits = 0;
        int drinkUnits = 0;
        int shotUnits = 0;

        for (Unit unit : units) {
            if (unit.getUnitType().equals("Beer")) beerUnits++;
            else if (unit.getUnitType().equals("Wine")) wineUnits++;
            else if (unit.getUnitType().equals("Drink")) drinkUnits++;
            else if (unit.getUnitType().equals("Shot")) shotUnits++;
        }

        String beerText = beerUnits + "/" + newHistory.getBeerPlannedUnitCount();
        String wineText = wineUnits + "/" + newHistory.getWinePlannedUnitCount();
        String drinkText = drinkUnits + "/" + newHistory.getDrinkPlannedUnitCount();
        String shotText = shotUnits + "/" + newHistory.getShotPlannedUnitCount();

        currentPartyBeerUnits.setText(beerText);
        currentPartyWineUnits.setText(wineText);
        currentPartyDrinkUnits.setText(drinkText);
        currentPartyShotUnits.setText(shotText);
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

        double bac = BacUtility.calculateBac(beerUnitCount, wineUnitCount, drinkUnitCount, shotUnitCount,
                getUnitGrams(0), getUnitGrams(1), getUnitGrams(2), getUnitGrams(3), hours, gender, weight);

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

        SuperDao superDao = new SuperDao(getContext());
        UnitDao unitDao = superDao.getUnitDao();
        unitDao.insert(unit);

        superDao.close();

        updateParty();
    }

    private void undoUnitFromCurrentParty() {
        NewHistory history = getCurrentHistory();
        if (history == null) return;

        SuperDao superDao = new SuperDao(getContext());
        UnitDao unitDao = superDao.getUnitDao();

        List<Unit> units = unitDao.queryBuilder().where(UnitDao.Properties.HistoryId.eq(history.getId())).orderDesc(UnitDao.Properties.Timestamp).list();

        if (units.size() > 0) {
            unitDao.delete(units.get(0));
        }
        updateParty();
    }

    private void endEvening() {
        NewHistory newHistory = getCurrentHistory();
        if (newHistory == null) return;

        newHistory.setBeerGrams(getUnitGrams(0));
        newHistory.setWineGrams(getUnitGrams(1));
        newHistory.setDrinkGrams(getUnitGrams(2));
        newHistory.setShotGrams(getUnitGrams(3));

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

        handler.removeCallbacks(timeUpdater);

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

        /*newHistory.setBeerCost(user.getBeerPrice());
        newHistory.setWineCost(user.getWinePrice());
        newHistory.setDrinkCost(user.getDrinkPrice());
        newHistory.setShotCost(user.getShotPrice());

        newHistory.setBeerGrams(getUnitGrams(0));
        newHistory.setWineGrams(BacUtility.getUnitGrams(1));
        newHistory.setDrinkGrams(BacUtility.getUnitGrams(2));
        newHistory.setShotGrams(BacUtility.getUnitGrams(3));*/

        newHistory.setGender(user.getGender().equals("Mann"));
        newHistory.setWeight(user.getWeight());

        newHistoryDao.insert(newHistory);
        superDao.close();

        resetPlannedUnits();

        setView();

        timeUpdater = new Runnable() {
            @Override
            public void run() {
                updateRunningBac();
                handler.postDelayed(this, 1000);
            }
        };
        timeUpdater.run();
    }

    private void resetPlannedUnits() {
        planPartyBeerUnits.setText("0");
        planPartyWineUnits.setText("0");
        planPartyDrinkUnits.setText("0");
        planPartyShotUnits.setText("0");

        planPartyExpectedBac.setText("0,0");
        planPartyExpectedCost.setText("0,-");
    }

}
