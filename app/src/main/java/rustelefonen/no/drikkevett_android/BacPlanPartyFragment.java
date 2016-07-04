package rustelefonen.no.drikkevett_android;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;

import static rustelefonen.no.drikkevett_android.BacCalcFragment.beer;
import static rustelefonen.no.drikkevett_android.BacCalcFragment.calculateBAC;
import static rustelefonen.no.drikkevett_android.BacCalcFragment.countingGrams;
import static rustelefonen.no.drikkevett_android.BacCalcFragment.setGenderScore;
import static rustelefonen.no.drikkevett_android.BacCalcFragment.wine;

public class BacPlanPartyFragment extends Fragment {

    int age = 22;
    int weight = 80;
    String gender = "Mann";

    // UNITS
    public int plannedBeers = 0;
    public int plannedWines = 0;
    public int plannedDrinks = 0;
    public int plannedShots = 0;

    public int beersConsumed = 0;
    public int winesConsumed = 0;
    public int drinksConsumed = 0;
    public int shotsConsumed = 0;

    // PROMILLE
    public double promilleBAC = 0;

    // TEXTVIEWS
    public TextView promilleLbl;
    public TextView beerLbl;
    public TextView wineLbl;
    public TextView drinkLbl;
    public TextView shotLbl;
    public TextView textQuoteLbl;

    // BUTTONS
    public Button addBtn;
    public Button removeBtn;
    public Button statusBtn;

    // VIEWS
    public View v;

    // Status (running, not running, day after is running)
    public Status status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_plan_party_frag, container, false);

        initWidgets();
        stateHandler(status.RUNNING);

        return v;
    }

    private void initWidgets(){
       // PROMILLE / BAC
       promilleLbl =  (TextView) v.findViewById(R.id.textViewPromilleLabelPlanPrty);

        // UNITS
        beerLbl = (TextView) v.findViewById(R.id.textViewBeerPP);
        wineLbl = (TextView) v.findViewById(R.id.textViewWinePP);
        drinkLbl = (TextView) v.findViewById(R.id.textViewDrinkPP);
        shotLbl = (TextView) v.findViewById(R.id.textViewShotPP);

        // TEXT QUOTES
        textQuoteLbl = (TextView) v.findViewById(R.id.textViewQuotesPP);

        // BUTTONS
        addBtn = (Button) v.findViewById(R.id.buttonAddPP);
        removeBtn = (Button) v.findViewById(R.id.buttonRemovePP);
        statusBtn = (Button) v.findViewById(R.id.buttonStatusPP);
    }

    private enum Status {
        RUNNING, NOT_RUNNING, DA_RUNNING
    }

    private void stateHandler(Status state){

        // IF PLAN PARTY IS RUNNING:
        if(state == status.RUNNING){
            partyRunning();
        }

        // IF PLAN PARTY IS NOT RUNNING:
        if(state == status.NOT_RUNNING){
            partyNotRunning();
        }

        // IF DAY AFTER IS RUNNING
        if(state == status.DA_RUNNING){
            dayAfterRunning();
        }
    }

    private void partyRunning(){
        // LAYOUT / UTSEENDE
        beerLbl.setText(beersConsumed + "/" + plannedBeers + "\nØL");
        wineLbl.setText(winesConsumed + "/" + plannedWines + "\nVin");
        drinkLbl.setText(drinksConsumed + "/" + plannedDrinks + "\nDrink");
        shotLbl.setText(shotsConsumed + "/" + plannedShots + "\nShot");

        liveUpdatePromille(weight, gender, new Date());


        //promilleLbl.setText(promilleBAC + "");
    }

    private void partyNotRunning(){
        // LAYOUT / UTSEENDE
        beerLbl.setText(plannedBeers + "\nØL");
        wineLbl.setText(plannedWines + "\nVin");
        drinkLbl.setText(plannedDrinks + "\nDrink");
        shotLbl.setText(plannedShots + "\nShot");

        promilleLbl.setText(promilleBAC + "");
    }

    private void dayAfterRunning(){
        // LAYOUT / UTSEENDE
        beerLbl.setText("-\nØL");
        wineLbl.setText("-\nVin");
        drinkLbl.setText("-\nDrink");
        shotLbl.setText("-\nShot");

        promilleLbl.setText("-");
    }

    private double liveUpdatePromille(double weight, String gender, Date firstUnitAddedTimeStamp){
        double sum = 0.0;

        double beerCount = 0.0;
        double wineCount = 0.0;
        double drinkCount = 0.0;
        double shotCount = 0.0;

        // LOOP TABLE TIMESTAMPS
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        DayAfterBACDao dayAfterDao = daoSession.getDayAfterBACDao();
        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();

        for (DayAfterBAC dayAfter : dayAfterBACList) {
            if(dayAfter.getUnit().equals("Beer")){
                beerCount++;
            }
            if(dayAfter.getUnit().equals("Wine")){
                wineCount++;
            }
            if(dayAfter.getUnit().equals("Drink")){
                drinkCount++;
            }
            if(dayAfter.getUnit().equals("Shot")){
                shotCount++;
            }

            double totalGrams = countingGrams(beerCount, wineCount, drinkCount, shotCount);

            Date currentDate = new Date();
            long timeDifference =  getDateDiff(currentDate, firstUnitAddedTimeStamp, TimeUnit.HOURS);

            // FROM 0 - 4 mins
            if(timeDifference > 0.085){
                System.out.println("Fra 0-4 minutter...");
                sum = 0;
            }
            // FROM 5 - 15 MIN
            if(timeDifference > 0.085 && timeDifference <= 0.25){
                // KALKULER PROMILLE
                sum = totalGrams/(weight * setGenderScore(gender)) - (intervalCalc(timeDifference) * timeDifference);
            }
            if(timeDifference > 0.25){
                // KALKULER PROMILLE
                sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, timeDifference));
            }
        }
        return sum;
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    private void insertDummy(){
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();


        DayAfterBACDao dayAfterDao = daoSession.getDayAfterBACDao();
        DayAfterBAC newBAC = new DayAfterBAC();

        newBAC.setTimestamp(new Date());
        newBAC.setUnit("Beer");

        dayAfterDao.insert(newBAC);

        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();

        for (DayAfterBAC dayAfter : dayAfterBACList) {
            System.out.println("TIMESTAMP --> :) --> " + dayAfter.getTimestamp());
            System.out.println("UNIT: ->> " + dayAfter.getUnit());
        }
    }

    public double intervalCalc(long timeDifference){
        double BACDownPerHour = 0.0;
        double minute = 1 / 60;

        // 1 MIN
        if(timeDifference >= 0.0 && timeDifference <= minute){
            BACDownPerHour = 50.0;
        }
        // 2 MIN
        if(timeDifference > (minute) && timeDifference <= (minute * 2)){
            BACDownPerHour = 23.5;
        }
        // 3 MIN
        if(timeDifference > (minute * 2) && timeDifference <= (minute * 3)){
            BACDownPerHour = 11.5;
        }
        // 4 MIN
        if(timeDifference > (minute * 3) && timeDifference <= (minute * 4)){
            BACDownPerHour = 6.8;
        }
        // 5 MIN
        if(timeDifference > (minute * 4) && timeDifference <= (minute * 5)){
            BACDownPerHour = 4.8;
        }
        // 6 MIN
        if(timeDifference > (minute * 5) && timeDifference <= (minute * 6)){
            BACDownPerHour = 3.5;
        }
        // 7 MIN
        if(timeDifference > (minute * 6) && timeDifference <= (minute * 7)){
            BACDownPerHour = 2.55;
        }
        // 8 MIN
        if(timeDifference > (minute * 7) && timeDifference <= (minute * 8)){
            BACDownPerHour = 2.0;
        }
        // 9 MIN
        if(timeDifference > (minute * 8) && timeDifference <= (minute * 9)){
            BACDownPerHour = 1.5;
        }
        // 10 MIN
        if(timeDifference > (minute * 9) && timeDifference <= (minute * 10)){
            BACDownPerHour = 1.15;
        }
        // 11 MIN
        if(timeDifference > (minute * 10) && timeDifference <= (minute * 11)){
            BACDownPerHour = 0.85;
        }
        // 12 MIN
        if(timeDifference > (minute * 11) && timeDifference <= (minute * 12)){
            BACDownPerHour = 0.53;
        }
        // 13 MIN
        if(timeDifference > (minute * 12) && timeDifference <= (minute * 13)){
            BACDownPerHour = 0.33;
        }
        // 14 MIN
        if(timeDifference > (minute * 13) && timeDifference <= (minute * 14)){
            BACDownPerHour = 0.28;
        }
        // 15 MIN
        if(timeDifference > (minute * 14) && timeDifference <= (minute * 15)){
            BACDownPerHour = 0.20;
        }
        return BACDownPerHour;
    }
}