package rustelefonen.no.drikkevett_android.tabs.planParty;

import android.content.Context;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.GraphHistory;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import static rustelefonen.no.drikkevett_android.util.PartyUtil.calculateBAC;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.countingGrams;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.setGenderScore;

/**
 * Created by RUStelefonen on 22.07.2016.
 */

public class PlanPartyDB {

    private Context context;

    public PlanPartyDB(Context context) {
        this.context = context;
    }

    public Date getFirstUnitAddedStamp(){
        SuperDao superDao = new SuperDao(context);

        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();
        List<PlanPartyElements> planPartyList = partyDao.queryBuilder().list();

        PlanPartyElements lastElement = planPartyList.get(planPartyList.size() -1);
        Date firstAdded;

        if(lastElement.getFirstUnitAddedDate() == null){
            firstAdded = null;
        } else {
            firstAdded = lastElement.getFirstUnitAddedDate();
        }
        System.out.println("FIRST ADDED: " + firstAdded);
        superDao.close();
        return firstAdded;
    }

    public boolean isFirstUnitAdded(){
        SuperDao superDao = new SuperDao(context);
        boolean bool = false;
        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
        for (PlanPartyElements party : partyList) {
            if(party.getFirstUnitAddedDate() != null){
                bool = true;
            } else {
                bool = false;
            }
        }
        return bool;
    }

    public String liveUpdatePromille(double weight, String gender, Date firstUnitAddedTimeStamp){
        SuperDao superDao = new SuperDao(context);
        double sum = 0.0;

        int b = 0;
        int w = 0;
        int d = 0;
        int s = 0;
        int totalUnits = 0;

        DayAfterBACDao dayAfterDao = superDao.getDayAfterBACDao();

        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();
        superDao.close();

        for (DayAfterBAC dayAfter : dayAfterBACList) {
            if(dayAfter.getUnit().equals("Beer")){
                b++;
                totalUnits++;
            }
            if(dayAfter.getUnit().equals("Wine")){
                w++;
                totalUnits++;
            }
            if(dayAfter.getUnit().equals("Drink")){
                d++;
                totalUnits++;
            }
            if(dayAfter.getUnit().equals("Shot")){
                s++;
                totalUnits++;
            }
            double totalGrams = countingGrams(b, w, d, s);

            Date currentDate = new Date();
            long timeDifference = getDateDiff(firstUnitAddedTimeStamp, currentDate, TimeUnit.MINUTES);
            double newValueDouble = (double)timeDifference;
            double minToHours = newValueDouble / 60;

            // FROM 0 - 15 MIN
            if(minToHours <= 0.25){
                try{
                    sum = PartyUtil.intervalCalc2(minToHours, totalUnits);
                    //sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(minToHours) * minToHours);
                } catch (NumberFormatException n){
                    sum = 0;
                }
            }
            if(minToHours > 0.25){
                try{
                    sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, minToHours));
                } catch(NumberFormatException e){
                    sum = 0;
                }
            }
        }
        DecimalFormat numberFormat = new DecimalFormat("#.###");
        return numberFormat.format(sum);
    }

    public void addConsumedUnits(String unit){
        SuperDao superDao = new SuperDao(context);
        DayAfterBACDao dayAfterDao = superDao.getDayAfterBACDao();
        DayAfterBAC newTS = new DayAfterBAC();

        newTS.setTimestamp(new Date());
        newTS.setUnit(unit);

        dayAfterDao.insert(newTS);
        superDao.close();
    }

    public void setPlannedPartyElementsDB(Date sDate, Date eDate, Date fDate, int pB, int pW, int pD, int pS, int aB, int aW, int aD, int aS, String status){
        SuperDao superDao = new SuperDao(context);
        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();
        PlanPartyElements newParty = new PlanPartyElements();
        newParty.setPlannedBeer(pB);
        newParty.setPlannedWine(pW);
        newParty.setPlannedDrink(pD);
        newParty.setPlannedShot(pS);
        newParty.setAftRegBeer(aB);
        newParty.setAftRegWine(aW);
        newParty.setAftRegDrink(aD);
        newParty.setAftRegShot(aS);
        newParty.setStatus(status);
        newParty.setStartTimeStamp(sDate);
        newParty.setEndTimeStamp(eDate);
        newParty.setFirstUnitAddedDate(fDate);
        partyDao.insert(newParty);
        superDao.close();
    }

    public void insertHistoryDB(int b, int w, int d, int s, Date startD, Date endD, int totalCosts, double highBAC){
        SuperDao superDao = new SuperDao(context);
        HistoryDao historyDao = superDao.getHistoryDao();
        History hist = new History();

        // units (4)
        hist.setBeerCount(b);
        hist.setWineCount(w);
        hist.setDrinkCount(d);
        hist.setShotCount(s);

        // dates (2)
        hist.setStartDate(startD);
        hist.setEndDate(endD);

        // costs (1)
        hist.setSum(totalCosts);

        // highest BAC (1)
        hist.setHighestBAC(highBAC);

        // insert to DB
        historyDao.insert(hist);
        superDao.close();
    }

    public void updateHighestBac(double highestBAC){
        SuperDao superDao = new SuperDao(context);
        // update highest BAC in History
        HistoryDao historyDao = superDao.getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);
        lastElement.setHighestBAC(highestBAC);
        historyDao.insertOrReplace(lastElement);
        superDao.close();
    }

    public void addGraphValues(double currentBAC, Date timeStamp, int id){
        SuperDao superDao = new SuperDao(context);
        GraphHistoryDao graphHistoryDao = superDao.getGraphHistoryDao();
        GraphHistory newGraphVal = new GraphHistory();
        newGraphVal.setHistoryId(id);
        newGraphVal.setCurrentBAC(currentBAC);
        newGraphVal.setTimestamp(timeStamp);
        graphHistoryDao.insert(newGraphVal);
        superDao.close();
    }

    public int fetchHistoryID_DB(){
        SuperDao superDao = new SuperDao(context);
        HistoryDao historyDao = superDao.getHistoryDao();
        List<History> historyList = historyDao.queryBuilder().list();
        History lastElement = historyList.get(historyList.size() -1);
        double tempID = lastElement.getId();
        superDao.close();
        return (int) tempID;
    }
}
