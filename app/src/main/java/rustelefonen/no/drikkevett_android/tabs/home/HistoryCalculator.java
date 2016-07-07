package rustelefonen.no.drikkevett_android.tabs.home;

import java.util.List;

import rustelefonen.no.drikkevett_android.db.History;

import static rustelefonen.no.drikkevett_android.util.DateUtil.dateIsWithin30Days;

/**
 * Created by simenfonnes on 07.07.2016.
 */

public class HistoryCalculator {

    public static int getTotalCost(List<History> historyList) {
        int totalCost = 0;
        for (History history : historyList) {
            totalCost += history.getSum();
        }
        return totalCost;
    }

    public static double getTotalHighestBac(List<History> historyList) {
        double highestBac = 0.0;
        for (History history : historyList) {
            if (history.getHighestBAC() > highestBac) {
                highestBac = history.getHighestBAC();
            }
        }
        return highestBac;
    }

    public static double getTotalAverageHighestBac(List<History> historyList) {
        double sum = 0.0;
        for (History history : historyList) {
            sum += history.getHighestBAC();
        }
        return sum / historyList.size();
    }

    public static int getLastMonthCost(List<History> historyList) {
        int sum = 0;
        for (History history : historyList) {
            if (dateIsWithin30Days(history.getStartDate())) {
                sum += history.getSum();
            }
        }
        return sum;
    }

    public static double getLastMonthHighestBac(List<History> historyList) {
        double highestBac = 0.0;
        for (History history : historyList) {
            if (dateIsWithin30Days(history.getStartDate())) {
                if (history.getHighestBAC() > highestBac) {
                    highestBac = history.getHighestBAC();
                }
            }
        }
        return highestBac;
    }

    public static double getLastMonthAverageBac(List<History> historyList) {
        double sum = 0.0;
        int highestBacCount = 0;
        for (History history : historyList) {
            if (dateIsWithin30Days(history.getStartDate())) {
                sum += history.getHighestBAC();
                highestBacCount++;
            }
        }
        return sum / highestBacCount;
    }
}
