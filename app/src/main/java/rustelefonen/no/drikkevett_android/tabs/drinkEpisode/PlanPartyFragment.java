package rustelefonen.no.drikkevett_android.tabs.drinkEpisode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Date;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.NewHistory;
import rustelefonen.no.drikkevett_android.db.NewHistoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.tabs.calc.fragments.BeerScrollAdapter;
import rustelefonen.no.drikkevett_android.tabs.calc.fragments.DrinkFragment;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.util.BacUtility;

/**
 * Created by simenfonnes on 18.08.2017.
 */

public class PlanPartyFragment extends Fragment implements Button.OnClickListener, ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    public ViewPager viewPager;
    public RadioGroup radioGroup;
    public Button removeButton;
    public Button addButton;

    public TextView beerUnits;
    public TextView wineUnits;
    public TextView drinkUnits;
    public TextView shotUnits;

    public TextView expectedBac;
    public TextView expectedCost;
    public Button startEveningButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plan_party_layout, container, false);
        initWidgets(view);
        setListeners();

        viewPager.setAdapter(new BeerScrollAdapter(this.getChildFragmentManager()));
        viewPager.setCurrentItem(0);

        return view;
    }

    private void initWidgets(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.plan_party_view_pager);
        radioGroup = (RadioGroup) view.findViewById(R.id.plan_party_radio_group);
        removeButton = (Button) view.findViewById(R.id.plan_party_remove_button);
        addButton = (Button) view.findViewById(R.id.plan_party_add_button);

        beerUnits = (TextView) view.findViewById(R.id.plan_party_beer_units);
        wineUnits = (TextView) view.findViewById(R.id.plan_party_wine_units);
        drinkUnits = (TextView) view.findViewById(R.id.plan_party_drink_units);
        shotUnits = (TextView) view.findViewById(R.id.plan_party_shot_units);

        expectedBac = (TextView) view.findViewById(R.id.plan_party_expected_bac);
        expectedCost = (TextView) view.findViewById(R.id.plan_party_expected_cost);
        startEveningButton = (Button) view.findViewById(R.id.plan_party_start_evening);
    }

    private void setListeners() {
        removeButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
        startEveningButton.setOnClickListener(this);
    }

    private void updateBac() {
        double beerUnitCount = tryParseDouble(beerUnits.getText().toString());
        double wineUnitCount = tryParseDouble(wineUnits.getText().toString());
        double drinkUnitCount = tryParseDouble(drinkUnits.getText().toString());
        double shotUnitCount = tryParseDouble(shotUnits.getText().toString());

        double hours = 0.0;

        User user = ((MainActivity)getActivity()).getUser();

        boolean gender = user.getGender().equals("Mann");
        double weight = user.getWeight();

        //double bac = BacUtility.calculateBac(beerUnitCount, wineUnitCount, drinkUnitCount, shotUnitCount, hours, gender, weight);

        //expectedBac.setText(new DecimalFormat("##.00").format(bac));
    }

    private void updateCost() {
        double beerUnitCount = tryParseDouble(beerUnits.getText().toString());
        double wineUnitCount = tryParseDouble(wineUnits.getText().toString());
        double drinkUnitCount = tryParseDouble(drinkUnits.getText().toString());
        double shotUnitCount = tryParseDouble(shotUnits.getText().toString());

        User user = ((MainActivity)getActivity()).getUser();

        double totalCost = (beerUnitCount * user.getBeerPrice()) + (wineUnitCount * user.getWinePrice()) +
                (drinkUnitCount * user.getDrinkPrice()) + (shotUnitCount * user.getShotPrice());
        expectedCost.setText("" + totalCost);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.plan_party_remove_button) {
            removeUnit(viewPager.getCurrentItem());
        }
        else if (v.getId() == R.id.plan_party_add_button) {
            addUnit(viewPager.getCurrentItem());
        }
        else if (v.getId() == R.id.plan_party_start_evening) {
            startEvening();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getCheckedRadioButtonId();
        if (id == R.id.plan_party_button_one) viewPager.setCurrentItem(0);
        else if (id == R.id.plan_party_button_two) viewPager.setCurrentItem(1);
        else if (id == R.id.plan_party_button_three) viewPager.setCurrentItem(2);
        else if (id == R.id.plan_party_button_four) viewPager.setCurrentItem(3);
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

    private void addUnit(int unitType) {
        if (unitType == 0) {
            int beerUnitCount = tryParse(beerUnits.getText().toString());
            if (beerUnitCount < 20) {
                beerUnits.setText("" + (beerUnitCount + 1));
            }
        }
        else if (unitType == 1) {
            int wineUnitCount = tryParse(wineUnits.getText().toString());
            if (wineUnitCount < 20) {
                wineUnits.setText("" + (wineUnitCount + 1));
            }
        }
        else if (unitType == 2) {
            int drinkUnitCount = tryParse(drinkUnits.getText().toString());
            if (drinkUnitCount < 20) {
                drinkUnits.setText("" + (drinkUnitCount + 1));
            }
        }
        else if (unitType == 3) {
            int shotUnitCount = tryParse(shotUnits.getText().toString());
            if (shotUnitCount < 20) {
                shotUnits.setText("" + (shotUnitCount + 1));
            }
        }
        updateBac();
        updateCost();
    }

    private void removeUnit(int unitType) {
        if (unitType == 0) {
            int beerUnitCount = tryParse(beerUnits.getText().toString());
            if (beerUnitCount > 0) {
                beerUnits.setText("" + (beerUnitCount - 1));
            }
        }
        else if (unitType == 1) {
            int wineUnitCount = tryParse(wineUnits.getText().toString());
            if (wineUnitCount > 0) {
                wineUnits.setText("" + (wineUnitCount - 1));
            }
        }
        else if (unitType == 2) {
            int drinkUnitCount = tryParse(drinkUnits.getText().toString());
            if (drinkUnitCount > 0) {
                drinkUnits.setText("" + (drinkUnitCount - 1));
            }
        }
        else if (unitType == 3) {
            int shotUnitCount = tryParse(shotUnits.getText().toString());
            if (shotUnitCount > 0) {
                shotUnits.setText("" + (shotUnitCount - 1));
            }
        }
        updateBac();
        updateCost();
    }

    private void startEvening() {
        int beerUnitCount = tryParse(beerUnits.getText().toString());
        int wineUnitCount = tryParse(wineUnits.getText().toString());
        int drinkUnitCount = tryParse(drinkUnits.getText().toString());
        int shotUnitCount = tryParse(shotUnits.getText().toString());

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

        /*newHistory.setBeerGrams(BacUtility.getUnitGrams(0));
        newHistory.setWineGrams(BacUtility.getUnitGrams(1));
        newHistory.setDrinkGrams(BacUtility.getUnitGrams(2));
        newHistory.setShotGrams(BacUtility.getUnitGrams(3));*/

        newHistory.setGender(user.getGender().equals("Mann"));
        newHistory.setWeight(user.getWeight());

        newHistoryDao.insert(newHistory);
        superDao.close();

        //((DrinkEpisode)getParentFragment()).changeView();




    }

}