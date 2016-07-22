package rustelefonen.no.drikkevett_android.tabs.calc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.tabs.calc.fragments.BeerScrollAdapter;
import rustelefonen.no.drikkevett_android.util.Gender;
import rustelefonen.no.drikkevett_android.util.NavigationUtil;

public class BacCalcFragment extends android.support.v4.app.Fragment
        implements ViewPager.OnPageChangeListener,
        RadioGroup.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {

    // ENHETER
    private int beer = 0;
    private int wine = 0;
    private int drink = 0;
    private int shot = 0;

    // HOURS
    private int hours = 0;

    // BUTTON
    public Button addButton;
    public Button removeButton;

    // TEXTVIEWS
    public TextView labelHours;
    //public TextView labelQuotes;

    public TextView labelBeerNrUnits;
    public TextView labelWineNrUnits;
    public TextView labelDrinkNrUnits;
    public TextView labelShotNrUnits;

    private SeekBar seekBar;
    public PieChart pieChart;
    public ViewPager beerScroll;

    private static final String PER_MILLE = "\u2030";

    public RadioGroup pageIndicatorGroup;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bac_calc_frag, container, false);
        user = ((MainActivity)getActivity()).getUser();
        setHasOptionsMenu(true);
        initWidgets(view);
        setListeners();
        fillWidgets();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.bac_calc_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove:
                removeAddedBeverages();
                refreshFragment();
                return false;
            case R.id.action_contact:
                NavigationUtil.navigateToContactInformation(getContext());
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    public void totalPromille(){
        String tmpGender = user.getGender();
        Gender gender = null;
        if (tmpGender.equals("Mann")) gender = Gender.MALE;
        else if (tmpGender.equals("Kvinne")) gender = Gender.FEMALE;

        String bac = calculateBAC(gender, user.getWeight(), countingGrams(beer, wine, drink, shot), hours);
        //labelPromille.setText("" + bac);
        pieChart.setCenterText(Double.valueOf(bac) + PER_MILLE);
        pieChart.animateY(0, Easing.EasingOption.EaseInOutQuad);
        //labelQuotes.setText(textInQuote(Double.valueOf(bac)));
    }

    public String calculateBAC(Gender gender, double weight, double grams, double hours) {
        double oppdatertPromille = 0.0;
        double genderScore = setGenderScore(gender);

        if(grams == 0.0){
            oppdatertPromille = 0.0;
        } else {
            oppdatertPromille = grams/(weight * genderScore) - (0.15 * hours);
            if(oppdatertPromille < 0.0){
                oppdatertPromille = 0.0;
            }
        }
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        String newPromille = numberFormat.format(oppdatertPromille);

        return newPromille;
    }

    public double countingGrams(double beerUnits, double wineUnits, double drinkUnits, double shotUnits){
        return (beerUnits * 12.6) + (wineUnits * 14.0) + (drinkUnits * 15.0) + (shotUnits * 14.2);
    }

    public double setGenderScore(Gender gender){
        if(gender == Gender.MALE)return 0.70;
        else if(gender == Gender.FEMALE)return 0.60;
        return 0.0;
    }

    private void initWidgets(View view){
        pageIndicatorGroup = (RadioGroup) view.findViewById(R.id.page_indicator_radio);
        addButton = (Button) view.findViewById(R.id.addBtn);
        removeButton = (Button) view.findViewById(R.id.btnRemove);
        labelHours = (TextView) view.findViewById(R.id.textViewHours);
        seekBar = (SeekBar) view.findViewById(R.id.seekBarBacCalc);
        labelBeerNrUnits = (TextView) view.findViewById(R.id.textViewBeerUnits);
        labelWineNrUnits = (TextView) view.findViewById(R.id.textViewWineUnits);
        labelDrinkNrUnits = (TextView) view.findViewById(R.id.textViewDrinkUnits);
        labelShotNrUnits = (TextView) view.findViewById(R.id.textViewShotUnits);
        pieChart = (PieChart) view.findViewById(R.id.pie_chart_bac_calc);
        beerScroll = (ViewPager) view.findViewById(R.id.beer_scroll);
    }

    private void setListeners() {
        pageIndicatorGroup.setOnCheckedChangeListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        beerScroll.addOnPageChangeListener(this);
        addButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
    }

    private void fillWidgets() {
        beerScroll.setAdapter(new BeerScrollAdapter(this.getFragmentManager()));
        beerScroll.setCurrentItem(0);
        pageIndicatorGroup.check(pageIndicatorGroup.getChildAt(0).getId());
        fillPieChart();
        stylePieChart();
    }


    private void fillPieChart() {
        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry((float) beer, 0));
        entries.add(new Entry((float) wine, 1));
        entries.add(new Entry((float) drink, 2));
        entries.add(new Entry((float) shot, 3));

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        dataset.setDrawValues(false);
        dataset.setColors(getColors());

        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < 4; i++) labels.add("");

        PieData data = new PieData(labels, dataset); // initialize Piedata
        pieChart.setData(data);
    }

    private void stylePieChart() {
        pieChart.setCenterText("0.0" + PER_MILLE);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(90f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterTextRadiusPercent(100f);
        pieChart.setTransparentCircleRadius(95f);
        pieChart.setDescription("");
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.setCenterTextSize(27.0f);
        pieChart.setCenterTextColor(Color.parseColor("#FFFFFF"));
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private int[] getColors() {
        return new int[]{
                ContextCompat.getColor(getContext(), R.color.beerColor),
                ContextCompat.getColor(getContext(), R.color.wineColor),
                ContextCompat.getColor(getContext(), R.color.drinkColor),
                ContextCompat.getColor(getContext(), R.color.shotColor)};
    }

    private void addBeverage() {
        switch (beerScroll.getCurrentItem()) {
            case 0: {
                if (beer < 20) beer++;
                labelBeerNrUnits.setText(beer + "");
                break;
            }
            case 1: {
                if (wine < 20) wine++;
                labelWineNrUnits.setText(wine + "");
                break;
            }
            case 2: {
                if (drink < 20) drink++;
                labelDrinkNrUnits.setText(drink + "");
                break;
            }
            case 3: {
                if (shot < 20) shot++;
                labelShotNrUnits.setText(shot + "");
                break;
            }
        }
        totalPromille();
        fillPieChart();
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private void removeBeverage() {
        switch (beerScroll.getCurrentItem()) {
            case 0: {
                if (beer > 0) beer--;
                labelBeerNrUnits.setText(beer + "");
                break;
            }
            case 1: {
                if (wine > 0) wine--;
                labelWineNrUnits.setText(wine + "");
                break;
            }
            case 2: {
                if (drink > 0) drink--;
                labelDrinkNrUnits.setText(drink + "");
                break;
            }
            case 3: {
                if (shot > 0) shot--;
                labelShotNrUnits.setText(shot + "");
                break;
            }
        }
        totalPromille();
        fillPieChart();
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private void removeAddedBeverages() {
        labelBeerNrUnits.setText("0");
        labelWineNrUnits.setText("0");
        labelDrinkNrUnits.setText("0");
        labelShotNrUnits.setText("0");

        beer = 0;
        wine = 0;
        drink = 0;
        shot = 0;
    }

    private void refreshFragment() {
        totalPromille();
        fillPieChart();
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (position == 0) pageIndicatorGroup.check(R.id.radio_one);
        else if (position == 1) pageIndicatorGroup.check(R.id.radio_two);
        else if (position == 2) pageIndicatorGroup.check(R.id.radio_three);
        else if (position == 3) pageIndicatorGroup.check(R.id.radio_four);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getCheckedRadioButtonId();
        if (id == R.id.radio_one) beerScroll.setCurrentItem(0);
        else if (id == R.id.radio_two) beerScroll.setCurrentItem(1);
        else if (id == R.id.radio_three) beerScroll.setCurrentItem(2);
        else if (id == R.id.radio_four) beerScroll.setCurrentItem(3);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (i < 1 && i > 24) hours = 1;
        hours = i;

        if(hours == 1){
            labelHours.setText("Promillen om " + hours + " time");
        } else {
            labelHours.setText("Promillen om " + hours + " timer");
        }
        totalPromille();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        fillPieChart();
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.addBtn) addBeverage();
        else if (id == R.id.btnRemove) removeBeverage();
    }
}