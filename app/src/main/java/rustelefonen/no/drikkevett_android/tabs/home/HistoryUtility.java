package rustelefonen.no.drikkevett_android.tabs.home;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.NewHistory;
import rustelefonen.no.drikkevett_android.db.NewHistoryDao;
import rustelefonen.no.drikkevett_android.db.Unit;
import rustelefonen.no.drikkevett_android.db.UnitDao;
import rustelefonen.no.drikkevett_android.util.BacUtility;

/**
 * Created by simenfonnes on 20.08.2017.
 */

public class HistoryUtility {

    public static double getHighestBac(NewHistory history, List<Unit> units) {
        double beerCount = 0.0;
        double wineCount = 0.0;
        double drinkCount = 0.0;
        double shotCount = 0.0;

        for (Unit unit : units) {
            if (unit.getUnitType().equals("Beer")) beerCount += 1.0;
            else if (unit.getUnitType().equals("Wine")) wineCount += 1.0;
            else if (unit.getUnitType().equals("Drink")) drinkCount += 1.0;
            else if (unit.getUnitType().equals("Shot")) shotCount += 1.0;
        }

        return BacUtility.calculateBac(beerCount, wineCount, drinkCount, shotCount,
                history.getBeerGrams(), history.getWineGrams(), history.getDrinkGrams(),
                history.getShotGrams(), 0, history.getGender(), history.getWeight());
    }

    public static double getTotalCost(NewHistory history, List<Unit> units) {
        double beerCount = 0.0;
        double wineCount = 0.0;
        double drinkCount = 0.0;
        double shotCount = 0.0;

        for (Unit unit : units) {
            if (unit.getUnitType().equals("Beer")) beerCount += 1.0;
            else if (unit.getUnitType().equals("Wine")) wineCount += 1.0;
            else if (unit.getUnitType().equals("Drink")) drinkCount += 1.0;
            else if (unit.getUnitType().equals("Shot")) shotCount += 1.0;
        }

        return (beerCount * history.getBeerCost()) + (wineCount * history.getWineCost()) + (drinkCount * history.getDrinkCost()) + (shotCount * history.getShotCost());
    }

    public static List<Unit> getHistoryUnits(NewHistory history, Context context) {
        SuperDao superDao = new SuperDao(context);
        UnitDao unitDao = superDao.getUnitDao();
        List<Unit> units = unitDao.queryBuilder().where(UnitDao.Properties.HistoryId.eq(history.getId())).list();
        superDao.close();
        return units;
    }

    public static List<NewHistory> getAllCompletedHistories(Context context) {
        SuperDao superDao = new SuperDao(context);
        NewHistoryDao newHistoryDao = superDao.getNewHistoryDao();
        List<NewHistory> historyList = newHistoryDao.queryBuilder().orderDesc(NewHistoryDao.Properties.BeginDate).list();
        superDao.close();

        List<NewHistory> tempList = new ArrayList<>();

        for (NewHistory newHistory : historyList) {
            if (newHistory.getEndDate() != null) tempList.add(newHistory);
        }

        return tempList;
    }

    private static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.MONTH);
    }

    public static List<NewHistory> getHistoriesInCurrentMonth(Context context) {
        SuperDao superDao = new SuperDao(context);
        NewHistoryDao newHistoryDao = superDao.getNewHistoryDao();
        List<NewHistory> historyList = newHistoryDao.queryBuilder().orderDesc(NewHistoryDao.Properties.BeginDate).list();
        superDao.close();


        List<NewHistory> tempList = new ArrayList<>();

        for (NewHistory newHistory : historyList) {
            if (newHistory.getEndDate() != null) tempList.add(newHistory);
        }

        List<NewHistory> historiesInThisMonth = new ArrayList<>();
        int currentMonth = getCurrentMonth();

        for (NewHistory history : tempList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(history.getBeginDate());
            if (calendar.get(Calendar.MONTH) == currentMonth) historiesInThisMonth.add(history);
        }
        return historiesInThisMonth;
    }
}