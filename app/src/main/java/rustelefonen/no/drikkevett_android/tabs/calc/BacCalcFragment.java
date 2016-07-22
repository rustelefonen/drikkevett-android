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
import android.widget.RadioButton;
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

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.tabs.calc.fragments.BeerScrollAdapter;
import rustelefonen.no.drikkevett_android.util.NavigationUtil;

public class BacCalcFragment extends android.support.v4.app.Fragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private int age = 22;
    private int weight = 80;
    private String gender = "Mann";

    // ALKOHOL I GRAM:
    private double beerGrams = 12.6;
    private double wineGrams = 14.0;
    private double drinkGrams = 15.0;
    private double shotGrams = 14.2;

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

    // SEEKBAR
    private SeekBar seekBar;

    // VIEWS
    private View view;

    public PieChart pieChart;
    public ViewPager beerScroll;

    private static final String PER_MILLE = "\u2030";

    public RadioGroup pageIndicatorGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bac_calc_frag, container, false);

        setHasOptionsMenu(true);

        initVariabels();

        addButton.setOnClickListener(new View.OnClickListener() {public void onClick(View v){addBeverage();}});
        removeButton.setOnClickListener(new View.OnClickListener() {public void onClick(View v){removeBeverage();}});

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("kek");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fillPieChart();
                pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            }
        });

        pieChart = (PieChart) view.findViewById(R.id.pie_chart_bac_calc);
        fillPieChart();
        stylePieChart();



        beerScroll = (ViewPager) view.findViewById(R.id.beer_scroll);
        beerScroll.addOnPageChangeListener(this);
        beerScroll.setAdapter(new BeerScrollAdapter(this.getFragmentManager()));

        beerScroll.setCurrentItem(0);
        pageIndicatorGroup.check(pageIndicatorGroup.getChildAt(0).getId());



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
        String bac = calculateBAC("Mann", 80, countingGrams(beer, wine, drink, shot), hours);
        //labelPromille.setText("" + bac);
        pieChart.setCenterText(Double.valueOf(bac) + PER_MILLE);
        pieChart.animateY(0, Easing.EasingOption.EaseInOutQuad);
        //labelQuotes.setText(textInQuote(Double.valueOf(bac)));
    }

    public String textInQuote(double bac){
        String output = "";
        if(bac >= 0 && bac < 0.4){
            output = "Kos deg";
            //labelPromille.setTextColor(Color.rgb(0, 0, 0));
        }
        if(bac >= 0.4 && bac < 0.8){
            output = "Lykkepromille";
            //labelPromille.setTextColor(Color.rgb(26, 193, 73));
        }
        if(bac >= 0.8 && bac < 1.0){
            output = "Du blir mer kritikkløs og risikovillig";
            //labelPromille.setTextColor(Color.rgb(255, 180, 10));
        }
        if(bac >= 1.0 && bac < 1.2){
            output = "Balansen blir dårligere";
            //labelPromille.setTextColor(Color.rgb(255, 180, 10));
        }
        if(bac >= 1.2 && bac < 1.4){
            output = "Talen snøvlete og \nkontroll på bevegelser forverres";
            //labelPromille.setTextColor(Color.rgb(255, 160, 0));
        }
        if(bac >= 1.4 && bac < 1.8){
            output = "Man blir trøtt, sløv og \nkan bli kvalm";
            //labelPromille.setTextColor(Color.rgb(255, 160, 0));
        }
        if(bac >= 1.8 && bac < 3.0){
            output = "Hukommelsen sliter";
            //labelPromille.setTextColor(Color.rgb(255, 55, 55));
        }
        if(bac >= 3.0 && bac < 5.0){
            output = "Svært høy promille! \nMan kan bli bevisstløs";
            //labelPromille.setTextColor(Color.rgb(255, 55, 55));
        }
        if(bac >= 5.0){
            output = "Du kan dø ved en så høy promille!";
            //labelPromille.setTextColor(Color.rgb(255, 0, 0));
        }
        return output;
    }

    public String calculateBAC(String gender, double weight, double grams, double hours) {
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
        return (beerUnits * beerGrams) + (wineUnits * wineGrams) + (drinkUnits * drinkGrams) + (shotUnits * shotGrams);
    }

    public double setGenderScore(String gender){
        double genderScore = 0.0;

        if(gender.equals("Mann")){
            genderScore = 0.70;
        }
        if(gender.equals("Kvinne")){
            genderScore = 0.60;
        }

        return genderScore;
    }

    public void initVariabels(){
        pageIndicatorGroup = (RadioGroup) view.findViewById(R.id.page_indicator_radio);
        pageIndicatorGroup.setOnCheckedChangeListener(this);





        // BUTTONS
        addButton = (Button) view.findViewById(R.id.addBtn);
        removeButton = (Button) view.findViewById(R.id.btnRemove);

        // TEXTVIEWS
        //labelPromille = (TextView) view.findViewById(R.id.promilleLbl);
        labelHours = (TextView) view.findViewById(R.id.textViewHours);
        //labelQuotes = (TextView) view.findViewById(R.id.text_view_quotes);

        // SEEKBAR
        seekBar = (SeekBar) view.findViewById(R.id.seekBarBacCalc);

        //
        labelBeerNrUnits = (TextView) view.findViewById(R.id.textViewBeerUnits);
        labelWineNrUnits = (TextView) view.findViewById(R.id.textViewWineUnits);
        labelDrinkNrUnits = (TextView) view.findViewById(R.id.textViewDrinkUnits);
        labelShotNrUnits = (TextView) view.findViewById(R.id.textViewShotUnits);
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

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
}