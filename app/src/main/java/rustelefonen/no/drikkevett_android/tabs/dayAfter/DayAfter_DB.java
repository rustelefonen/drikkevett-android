package rustelefonen.no.drikkevett_android.tabs.dayAfter;

import android.content.Context;

import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.GraphHistory;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by RUStelefonen on 27.07.2016.
 */

public class DayAfter_DB {
    private Context context;

    public DayAfter_DB(Context context) {
        this.context = context;
    }

    public Date getFirstUnAddedStamp(){
        SuperDao superDao = new SuperDao(context);
        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();
        List<PlanPartyElements> planPList = partyDao.queryBuilder().list();
        PlanPartyElements lastElement = planPList.get(planPList.size() - 1);
        superDao.close();
        return lastElement.getFirstUnitAddedDate();
    }

    public void addDayAfter(Date newUnitStamp, String unitType){
        SuperDao superDao = new SuperDao(context);
        DayAfterBACDao dayAfterBACDao = superDao.getDayAfterBACDao();
        DayAfterBAC newDayAfter = new DayAfterBAC();
        newDayAfter.setTimestamp(newUnitStamp);
        newDayAfter.setUnit(unitType);
        dayAfterBACDao.insert(newDayAfter);
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

    public void updateHighestBACinHistory(double highBac){
        SuperDao superDao = new SuperDao(context);
        HistoryDao historyDao = superDao.getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);
        lastElement.setHighestBAC(highBac);
        historyDao.insertOrReplace(lastElement);
        superDao.close();
    }

    public void printLastGraphValues(){
        // REMOVE ALL LATEST GRAPH HIST WITH SAME ID AS HISTORY ID AND TEMP STORE THE TIMESTAMPS
        SuperDao superDao = new SuperDao(context);
        HistoryDao historyDao = superDao.getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);

        GraphHistoryDao graphHistoryDao = superDao.getGraphHistoryDao();
        List<GraphHistory> graphHistList = graphHistoryDao.queryBuilder().where(GraphHistoryDao.Properties.HistoryId.eq(lastElement.getId())).list();
        superDao.close();
        System.out.println("Last hist id: (" + lastElement.getId() + ")");
        for(GraphHistory graphHist : graphHistList){
            System.out.println("graph value id: " + graphHist.getId());
            System.out.println("graph value history_id: " + graphHist.getHistoryId());
            System.out.println("graph value timeStamp: " + graphHist.getTimestamp());
            System.out.println("graph value currentBac: " + graphHist.getCurrentBAC());
        }
    }

    public void updateUnitInHistory(String unit){
        SuperDao superDao = new SuperDao(context);
        HistoryDao historyDao = superDao.getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);

        if(unit.equals("Beer")){
            lastElement.setBeerCount(lastElement.getBeerCount() + 1);
        }
        if(unit.equals("Wine")){
            lastElement.setWineCount(lastElement.getWineCount() + 1);
        }
        if(unit.equals("Drink")){
            lastElement.setDrinkCount(lastElement.getDrinkCount() + 1);
        }
        if(unit.equals("Shot")){
            lastElement.setShotCount(lastElement.getShotCount() + 1);
        }
        historyDao.insertOrReplace(lastElement);
        superDao.close();
    }
}
