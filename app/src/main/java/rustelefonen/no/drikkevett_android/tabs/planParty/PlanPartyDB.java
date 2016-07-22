package rustelefonen.no.drikkevett_android.tabs.planParty;

import android.content.Context;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import static rustelefonen.no.drikkevett_android.util.PartyUtil.calculateBAC;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.countingGrams;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.setGenderScore;

/**
 * Created by LarsPetterKristiansen on 22.07.2016.
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
        superDao.close();
        PlanPartyElements lastElement = planPartyList.get(planPartyList.size() -1);
        Date firstAdded;
        if(lastElement.getFirstUnitAddedDate() == null){
            firstAdded = null;
        } else {
            firstAdded = lastElement.getFirstUnitAddedDate();
        }
        return firstAdded;
    }

    private String liveUpdatePromille(double weight, String gender, Date firstUnitAddedTimeStamp){
        SuperDao superDao = new SuperDao(context);
        double sum = 0.0;

        int b = 0;
        int w = 0;
        int d = 0;
        int s = 0;

        DayAfterBACDao dayAfterDao = superDao.getDayAfterBACDao();

        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();

        for (DayAfterBAC dayAfter : dayAfterBACList) {
            if(dayAfter.getUnit().equals("Beer")){
                b++;
            }
            if(dayAfter.getUnit().equals("Wine")){
                w++;
            }
            if(dayAfter.getUnit().equals("Drink")){
                d++;
            }
            if(dayAfter.getUnit().equals("Shot")){
                s++;
            }
            double totalGrams = countingGrams(b, w, d, s);

            Date currentDate = new Date();
            long timeDifference = getDateDiff(firstUnitAddedTimeStamp, currentDate, TimeUnit.MINUTES);
            double newValueDouble = (double)timeDifference;
            double minToHours = newValueDouble / 60;

            // FROM 0 - 4 mins
            if(minToHours < 0.085){
                sum = 0;
            }
            // FROM 5 - 15 MIN
            if(minToHours > 0.085 && minToHours <= 0.25){
                sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(minToHours) * minToHours);
            }
            if(minToHours > 0.25){
                sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, minToHours));
            }
        }
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        return numberFormat.format(sum);
    }
}
