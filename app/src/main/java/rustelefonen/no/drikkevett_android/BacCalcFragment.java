package rustelefonen.no.drikkevett_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_calc_frag, container, false);

        initVariabels();

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(beer >= 20){
                    beer = 20;
                } else {
                    beer++;
                }
                labelBeerNrUnits.setText(beer + "\nØL");

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
                labelBeerNrUnits.setText(beer + "\nØL");

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

                if(hours == 1){
                    labelHours.setText("Promillen om " + hours + " time");
                } else {
                    labelHours.setText("Promillen om " + hours + " timer");
                }

                totalPromille();
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
        if(bac >= 0 && bac < 0.4){
            output = "Kos deg";
            labelPromille.setTextColor(Color.rgb(0, 0, 0));
        }
        if(bac >= 0.4 && bac < 0.8){
            output = "Lykkepromille";
            labelPromille.setTextColor(Color.rgb(26, 193, 73));
        }
        if(bac >= 0.8 && bac < 1.0){
            output = "Du blir mer kritikkløs og risikovillig";
            labelPromille.setTextColor(Color.rgb(255, 180, 10));
        }
        if(bac >= 1.0 && bac < 1.2){
            output = "Balansen blir dårligere";
            labelPromille.setTextColor(Color.rgb(255, 180, 10));
        }
        if(bac >= 1.2 && bac < 1.4){
            output = "Talen snøvlete og \nkontroll på bevegelser forverres";
            labelPromille.setTextColor(Color.rgb(255, 160, 0));
        }
        if(bac >= 1.4 && bac < 1.8){
            output = "Man blir trøtt, sløv og \nkan bli kvalm";
            labelPromille.setTextColor(Color.rgb(255, 160, 0));
        }
        if(bac >= 1.8 && bac < 3.0){
            output = "Hukommelsen sliter";
            labelPromille.setTextColor(Color.rgb(255, 55, 55));
        }
        if(bac >= 3.0 && bac < 5.0){
            output = "Svært høy promille! \nMan kan bli bevisstløs";
            labelPromille.setTextColor(Color.rgb(255, 55, 55));
        }
        if(bac >= 5.0){
            output = "Du kan dø ved en så høy promille!";
            labelPromille.setTextColor(Color.rgb(255, 0, 0));
        }
        return output;
    }

    public static String calculateBAC(String gender, double weight, double grams, double hours) {
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

    public static double countingGrams(double beerUnits, double wineUnits, double drinkUnits, double shotUnits){
        double totalGrams = (beerUnits * beerGrams) + (wineUnits * wineGrams) + (drinkUnits * drinkGrams) + (shotUnits * shotGrams);
        return totalGrams;
    }

    public static double setGenderScore(String gender){
        double genderScore = 0.0;

        if(gender == "Mann"){
            genderScore = 0.70;
        }
        if(gender == "Kvinne"){
            genderScore = 0.60;
        }

        return genderScore;
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


}