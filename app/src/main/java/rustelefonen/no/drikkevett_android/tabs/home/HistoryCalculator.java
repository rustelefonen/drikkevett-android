package rustelefonen.no.drikkevett_android.tabs.home;

import android.content.Context;

import java.util.List;

import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.NewHistory;
import rustelefonen.no.drikkevett_android.db.Unit;
import rustelefonen.no.drikkevett_android.util.BacUtility;

import static rustelefonen.no.drikkevett_android.util.DateUtil.dateIsWithin30Days;

/**
 * Created by simenfonnes on 07.07.2016.
 */

public class HistoryCalculator {

    public static int getTotalCost(List<NewHistory> histories, Context context) {
        int totalCost = 0;
        for (NewHistory history : histories) {
            List<Unit> units = HistoryUtility.getHistoryUnits(history, context);
            for (Unit unit : units) {
                if (unit.getUnitType().equals("Beer")) totalCost += history.getBeerCost();
                else if (unit.getUnitType().equals("Wine")) totalCost += history.getWineCost();
                else if (unit.getUnitType().equals("Drink")) totalCost += history.getDrinkCost();
                else if (unit.getUnitType().equals("Shot")) totalCost += history.getShotCost();
            }
        }
        return totalCost;
    }

    public static double getTotalHighestBac(List<NewHistory> histories, Context context) {
        double highestBac = 0.0;
        for (NewHistory history : histories) {
            double beerUnits = 0.0;
            double wineUnits = 0.0;
            double drinkUnits = 0.0;
            double shotUnits = 0.0;

            List<Unit> units = HistoryUtility.getHistoryUnits(history, context);

            for (Unit unit : units) {
                if (unit.getUnitType().equals("Beer")) beerUnits += 1.0;
                else if (unit.getUnitType().equals("Wine")) wineUnits += 1.0;
                else if (unit.getUnitType().equals("Drink")) drinkUnits += 1.0;
                else if (unit.getUnitType().equals("Shot")) shotUnits += 1.0;
            }

            double bac = BacUtility.calculateBac(beerUnits, wineUnits, drinkUnits, shotUnits,
                    history.getBeerGrams(), history.getWineGrams(), history.getDrinkGrams(),
                    history.getShotCost(), 0.0, history.getGender(), history.getWeight());

            if (bac > highestBac) highestBac = bac;
        }
        return highestBac;
    }

    public static double getTotalAverageHighestBac(List<NewHistory> histories, Context context) {
        double bacSum = 0.0;
        for (NewHistory history : histories) {
            double beerUnits = 0.0;
            double wineUnits = 0.0;
            double drinkUnits = 0.0;
            double shotUnits = 0.0;

            List<Unit> units = HistoryUtility.getHistoryUnits(history, context);

            for (Unit unit : units) {
                if (unit.getUnitType().equals("Beer")) beerUnits += 1.0;
                else if (unit.getUnitType().equals("Wine")) wineUnits += 1.0;
                else if (unit.getUnitType().equals("Drink")) drinkUnits += 1.0;
                else if (unit.getUnitType().equals("Shot")) shotUnits += 1.0;
            }

            bacSum += BacUtility.calculateBac(beerUnits, wineUnits, drinkUnits, shotUnits,
                    history.getBeerGrams(), history.getWineGrams(), history.getDrinkGrams(),
                    history.getShotCost(), 0.0, history.getGender(), history.getWeight());

        }
        return bacSum / (double) histories.size();
    }
}
