package rustelefonen.no.drikkevett_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BacCalcFragment extends android.support.v4.app.Fragment {

    int age = 22;
    int weight = 80;
    String gender = "Mann";

    // ALKOHOL I GRAM:
    static double beerGrams = 12.6;
    static double wineGrams = 14.0;
    static double drinkGrams = 15.0;
    static double shotGrams = 14.2;

    // ENHETER
    static int beer = 0;
    static int wine = 0;
    static int drink = 0;
    static int shot = 0;

    // HOURS
    static int hours = 0;

    // BUTTON
    private static Button addButton;
    private static Button removeButton;

    // TEXTVIEWS
    private static TextView labelPromille;
    private static TextView labelHours;
    private static TextView labelBeerNrUnits;
    private static TextView labelQuotes;

    // SEEKBAR
    private static SeekBar seekBar;

    // VIEWS
    private static View v;

    private UnitsPagerAdapter mUnitsPageAdapter;
    private ViewPager mUnitsViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_calc_frag, container, false);

        initVariabels();

        /*
        mUnitsPageAdapter = new UnitsPagerAdapter(getActivity().getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mUnitsViewPager = (ViewPager) v.findViewById(R.id.viewPagerForUnits);
        mUnitsViewPager.setAdapter(mUnitsPageAdapter);*/

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(beer >= 20){
                    beer = 20;
                } else {
                    beer++;
                }
                labelBeerNrUnits.setText("" + beer);

                String bac = calculateBAC("Mann", 80, countingGrams(beer, 0, 0, 0), hours);
                labelPromille.setText("" + bac);

                totalPromille();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(beer <= 0){
                    beer = 0;
                } else {
                    beer--;
                }
                labelBeerNrUnits.setText("" + beer);
                String bac = calculateBAC("Mann", 80, countingGrams(beer, 0, 0, 0), hours);
                labelPromille.setText("" + bac);

                totalPromille();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i <= 1){
                    i = 1;
                    hours = i;
                }
                hours = i;
                String bac = calculateBAC("Mann", 80, countingGrams(beer, 0, 0, 0), hours);
                labelPromille.setText("" + bac);

                if(hours == 1){
                    labelHours.setText("Promillen om " + hours + " time");
                } else {
                    labelHours.setText("Promillen om " + hours + " timer");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return v;
    }

    public static void totalPromille(){
        String bac = calculateBAC("Mann", 80, countingGrams(beer, 0, 0, 0), hours);
        labelPromille.setText("" + bac);
        labelQuotes.setText(textInQuote(Double.valueOf(bac)));
    }

    public static String textInQuote(double bac){
        String output = "";

        if(bac > 0 && bac < 0.4){
            output = "Kos deg";
        }
        if(bac >= 0.4 && bac < 0.8){
            output = "Lykkepromille";
        }
        if(bac >= 0.8 && bac < 1.0){
            output = "Du blir mer kritikkløs og risikovillig";
        }
        if(bac >= 1.0 && bac < 1.2){
            output = "Balansen blir dårligere";
        }
        if(bac >= 1.2 && bac < 1.4){
            output = "Talen snøvlete og \nkontroll på bevegelser forverres";
        }
        if(bac >= 1.4 && bac < 1.8){
            output = "Man blir trøtt, sløv og \nkan bli kvalm";
        }
        if(bac >= 1.8 && bac > 3.0){
            output = "Hukommelsen sliter";
        }
        if(bac >= 3.0 && bac > 5.0){
            output = "Svært høy promille! \nMan kan bli bevisstløs";
        }
        if(bac >= 5.0){
            output = "Du kan dø ved en så høy promille!";
        }

        return output;
    }

    public static String calculateBAC(String gender, int weight, double grams, double hours) {
        double genderScore = 0.0;
        double oppdatertPromille = 0.0;

        if(grams == 0.0){
            oppdatertPromille = 0.0;
        } else {
            if(gender == "Mann"){
                genderScore = 0.70;
            } else if (gender == "Kvinne"){
                genderScore = 0.60;
            }
            oppdatertPromille = grams/(weight * genderScore) - (0.15 * hours);
            if(oppdatertPromille < 0.0){
                oppdatertPromille = 0.0;
            }
        }
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        String newPromille = numberFormat.format(oppdatertPromille);

        return newPromille;
    }

    public static double countingGrams(double beerUnits, double wineUnits, double drinkUnits, double shotUnits){
        double totalGrams = (beerUnits * beerGrams) + (wineUnits * wineGrams) + (drinkUnits * drinkGrams) + (shotUnits * shotGrams);
        return totalGrams;
    }

    public static void initVariabels(){
        // BUTTONS
        addButton = (Button) v.findViewById(R.id.addBtn);
        removeButton = (Button) v.findViewById(R.id.btnRemove);

        // TEXTVIEWS
        labelPromille = (TextView) v.findViewById(R.id.promilleLbl);
        labelHours = (TextView) v.findViewById(R.id.textViewHours);
        labelBeerNrUnits = (TextView) v.findViewById(R.id.textViewBeerUnits);
        labelQuotes = (TextView) v.findViewById(R.id.textViewQuotes);

        // SEEKBAR
        seekBar = (SeekBar) v.findViewById(R.id.seekBarBacCalc);
    }

    public class UnitsPagerAdapter extends FragmentPagerAdapter {

        public UnitsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 0: return new BacHomeFragment();
                case 1: return new BacCalcFragment();
                case 2: return new BacPlanPartyFragment();
                case 3: return new BacDayAfterFragment();
                default: return new BacDayAfterFragment();
            }
        }

        @Override
        public int getCount() { return 5; }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}