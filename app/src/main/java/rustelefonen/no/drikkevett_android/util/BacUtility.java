package rustelefonen.no.drikkevett_android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;

import rustelefonen.no.drikkevett_android.unit.UnitEditActivity;

/**
 * Created by simenfonnes on 16.08.2017.
 */

public class BacUtility {

    public static final double beerGrams = 23.0;
    public static final double wineGrams = 16.0;
    public static final double drinkGrams = 16.0;
    public static final double shotGrams = 16.0;

    public static String getQuoteTextBy(double bac) {
        if (bac < 0.2) return "Kos deg!";
        else if (isBetween(bac, 0.2, 0.3)) return "Du begynner så vidt å merke at du har drukket.";
        else if (isBetween(bac, 0.3, 0.4)) return "Du føler deg lett påvirket.";
        else if (isBetween(bac, 0.4, 0.6)) return "De fleste kjenner seg avslappet.";
        else if (isBetween(bac, 0.6, 0.8)) return "Lykkepromille: Hevet stemningsleie og en følelse av velbehag, men man blir også mer impulsiv, kritikkløs og risikovillig.";
        else if (isBetween(bac, 0.8, 1.0)) return "Koordinasjon og balanse påvirkes. Drikker du mer nå vil de uønskede virkningene av alkoholen bli mer fremtredende enn de ønskede.";
        else if (isBetween(bac, 1.0, 1.3)) return "Balansen blir dårligere, man snakker snøvlete, og kontroll med bevegelser forverres.";
        else if (isBetween(bac, 1.3, 1.5)) return "Uønskede virkninger som kvalme, brekninger, tretthet og sløvhet øker. Mange blir også mer aggressive.";
        else if (isBetween(bac, 1.5, 2.0)) return "Hukommelsen sliter, faren for blackout øker.";
        else if (isBetween(bac, 2.0, 2.5)) return "Bevissthetsgraden senkes og man blir vanskelig å få kontakt med.";
        else if (isBetween(bac, 2.5, 3.0)) return "Bevisstløshet og pustehemning kan inntreffe.";
        else if (isBetween(bac, 3.0, 4.0)) return "Pustestans og død kan inntre. Risikoen for dette øker betydelig ved promille over 3.";
        else return "De aller fleste vil være døde ved promille over 4.";
    }

    public static String getQuoteRegisterTextBy(double bac) {
        if (bac < 0.2) return "Legg inn en langsiktig makspromille du ønsker å holde deg under. Makspromillen tilsvarer et promillenivå du ikke ønsker å gå over i løpet av én kveld/fest/drikkeepisode.";
        else if (isBetween(bac, 0.2, 0.3)) return "Du merker så vidt at du har drukket.";
        else if (isBetween(bac, 0.3, 0.4)) return "Du føler deg lett påvirket.";
        else if (isBetween(bac, 0.4, 0.6)) return "De fleste kjenner seg avslappet og man blir mer pratsom.";
        else if (isBetween(bac, 0.6, 0.8)) return "Lykkepromille: Hevet stemningsleie og en følelse av velbehag, men man blir også mer impulsiv, kritikkløs og risikovillig.";
        else if (isBetween(bac, 0.8, 1.0)) return "Koordinasjon og balanse påvirkes. Drikker du mer enn dette vil de uønskede virkningene av alkoholen bli mer fremtredende enn de ønskede.";
        else if (isBetween(bac, 1.0, 1.3)) return "Balansen blir dårligere, man snakker snøvlete, og kontroll med bevegelser forverres.";
        else if (isBetween(bac, 1.3, 1.5)) return "Uønskede virkninger som kvalme, brekninger, tretthet og sløvhet øker. Mange blir også mer aggressive.";
        else if (isBetween(bac, 1.5, 2.0)) return "Hukommelsen sliter, faren for blackout øker.";
        else if (isBetween(bac, 2.0, 2.5)) return "Bevissthetsgraden senkes og man blir vanskelig å få kontakt med.";
        else return "Bevisstløshet og pustehemning kan inntreffe.";
    }

    public static int getQuoteTextColorBy(double bac) {
        if (bac < 0.8) return Color.rgb(26, 193, 73);
        else if (isBetween(bac, 0.8, 1.3)) return Color.rgb(255, 180, 10);
        else if (isBetween(bac, 1.3, 2.0)) return Color.YELLOW; //Skal være orange
        else if (isBetween(bac, 2.0, 3.0)) return Color.rgb(235, 55, 55);
        else return Color.RED;
    }

    public static boolean isBetween(double x, double lower, double upper) {
        return lower <= x && x < upper;
    }
/*
    public static double calculateBac(double beerUnits, double wineUnits, double drinkUnits, double shotUnits, double hours, boolean gender, double weight) {
        double totalGrams = ((beerUnits * getUnitGrams(0)) + (wineUnits * getUnitGrams(1)) + (drinkUnits * getUnitGrams(2)) + (shotUnits * getUnitGrams(3))) * 0.79;

        double genderScore = gender ? 0.7 : 0.6;

        double currentBac = (totalGrams/(weight * genderScore) - (0.15 * hours));
        if (currentBac < 0.0) return 0.0;
        return currentBac;
    }
*/
    public static double calculateBac(double beerUnits, double wineUnits, double drinkUnits, double shotUnits, double beerGrams, double wineGrams, double drinkGrams, double shotGrams, double hours, boolean gender, double weight) {
        double totalGrams = ((beerUnits * beerGrams) + (wineUnits * wineGrams) + (drinkUnits * drinkGrams) + (shotUnits * shotGrams)) * 0.79;

        double genderScore = gender ? 0.7 : 0.6;

        double currentBac = (totalGrams/(weight * genderScore) - (0.15 * hours));
        if (currentBac < 0.0) return 0.0;
        return currentBac;
    }
/*
    public static double getUnitGrams(int unitType, AppCompatActivity appCompatActivity) {
        SharedPreferences sharedPref = appCompatActivity.getPreferences(Context.MODE_PRIVATE);

        float percent = sharedPref.getFloat(UnitEditActivity.percentKeys[unitType], UnitEditActivity.defaultPercent[unitType]);
        int amount = sharedPref.getInt(UnitEditActivity.amountKeys[unitType], UnitEditActivity.defaultAmount[unitType]);

        return amount * percent / 10.0f;

















        if (unitType == 0) return beerGrams;
        else if (unitType == 1) return wineGrams;
        else if (unitType == 2) return drinkGrams;
        else return shotGrams;
    }*/
}
