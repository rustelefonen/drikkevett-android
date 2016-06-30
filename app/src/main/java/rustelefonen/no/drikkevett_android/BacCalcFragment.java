package rustelefonen.no.drikkevett_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bac_calc_frag, container, false);

        // DO WHAT YOU WANNA DO
        final Button addButton = (Button) v.findViewById(R.id.addBtn);


        final TextView tvBeer = (TextView) v.findViewById(R.id.promilleLbl);
        tvBeer.setText("");

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //beer++;

            }
        });

        return v;
    }

    public static double calculateBAC(String gender, int weight, double grams, double hours) {
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
        return oppdatertPromille;
    }

    public static double countingGrams(double beerUnits, double wineUnits, double drinkUnits, double shotUnits){
        double totalGrams = (beerUnits * beerGrams) + (wineUnits * wineGrams) + (drinkUnits * drinkGrams) + (shotUnits * shotGrams);
        return totalGrams;
    }
}