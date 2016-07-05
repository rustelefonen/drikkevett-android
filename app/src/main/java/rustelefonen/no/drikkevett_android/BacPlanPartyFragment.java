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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import static rustelefonen.no.drikkevett_android.BacCalcFragment.calculateBAC;
import static rustelefonen.no.drikkevett_android.BacCalcFragment.countingGrams;
import static rustelefonen.no.drikkevett_android.BacCalcFragment.setGenderScore;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;

public class BacPlanPartyFragment extends Fragment {

    int weight = 80;
    String gender = "Mann";
    Date firstUnitAdded = new Date();

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

    // Status (running, not running, day after is running)
    public Status status;


    /* WIDGETS */

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_plan_party_frag, container, false);

        initWidgets();

        // hente status fra db
        status = getStatus();

        stateHandler(status);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlannedUnits("Beer");
                stateHandler(status);
            }
        });



        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textOnBtn = (String) statusBtn.getText();
                if(textOnBtn.equals("Start Kvelden")){
                    statusBtn.setText("Avslutt Kvelden");

                    // UPDATE BOTH HERE AND IN DB
                    status = status.RUNNING;
                    updateStatus(status.toString());
                }
                if(textOnBtn.equals("Avslutt Kvelden")){
                    statusBtn.setText("Avslutt Dagen Derpå");

                    // CLEAR DB - PLAN PARTY
                    status = status.DA_RUNNING;
                    updateStatus(status.toString());
                }
                if(textOnBtn.equals("Avslutt Dagen Derpå")){
                    statusBtn.setText("Start Kvelden");
                    status = status.NOT_RUNNING;
                    updateStatus(status.toString());
                }
                stateHandler(status);
            }
        });
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        // hente status fra db
        status = getStatus();
        stateHandler(status);
    }

    /*
    *
    * STATUS
    *
    * */

    private enum Status {
        RUNNING, NOT_RUNNING, DA_RUNNING, DEFAULT
    }

    private void stateHandler(Status state){
        System.out.println("Status: " + state);

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
        // Consuming alcohol


        // UPDATE PROMILLE/BAC
        // get firstUnitAddedStamp


        addConsumedUnits("Beer");

        promilleLbl.setText("" + liveUpdatePromille(weight, gender, firstUnitAdded));

        // GET UNITS (PLANNED AND CONSUMED)
        getUnitsPlanned();

        // Layout
        beerLbl.setText(beersConsumed + "/" + plannedBeers + "\nØL");
        wineLbl.setText(winesConsumed + "/" + plannedWines + "\nVin");
        drinkLbl.setText(drinksConsumed + "/" + plannedDrinks + "\nDrink");
        shotLbl.setText(shotsConsumed + "/" + plannedShots + "\nShot");
    }

    private void partyNotRunning(){
        // Plan party by adding units:


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

    /*
    *
    * ADDING UNITS ( CONSUMED AND PLANNED
    *
    * */

    private void addConsumedUnits(String unit){
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        DayAfterBACDao dayAfterDao = daoSession.getDayAfterBACDao();
        DayAfterBAC newTS = new DayAfterBAC();

        // EN IF SOM SJEKKER OM EN ENHET ALLEREDE ER LAGT TIL, OG HVIS DEN IKKE ER DET ER DETTE FIRST UNIT TIME STAMP

        newTS.setTimestamp(new Date());
        newTS.setUnit(unit);

        dayAfterDao.insert(newTS);
    }

    private void addPlannedUnits(String unit){
        if(unit.equals("Beer")){
            plannedBeers++;
        }
        if(unit.equals("Wine")){
            plannedWines++;
        }
        if(unit.equals("Drink")){
            plannedDrinks++;
        }
        if(unit.equals("Shot")){
            plannedShots++;
        }
    }

    private void updateStatus(String status){
        // CONNECT TO DB
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        PlanPartyElementsDao partyDao = daoSession.getPlanPartyElementsDao();

        if(status.equals("NOT_RUNNING")){
            // Inserting new planned elements
            PlanPartyElements newParty = new PlanPartyElements();

            // RESET PLAN UNITS
            plannedBeers = 0;
            plannedWines = 0;
            plannedDrinks = 0;
            plannedShots = 0;
        }
        if(status.equals("RUNNING")){
            // INGENTING SKJER
        }

        if(status.equals("DA_RUNNING")){
            List<PlanPartyElements> PlanPartyList = partyDao.queryBuilder().list();

            for (PlanPartyElements party : PlanPartyList) {
                plannedBeers = party.getPlannedBeer();
                plannedWines = party.getPlannedWine();
                plannedDrinks = party.getPlannedDrink();
                plannedShots = party.getPlannedShot();
            }
        }
        partyDao.deleteAll();

        // Inserting new planned elements
        PlanPartyElements newParty = new PlanPartyElements();

        newParty.setPlannedBeer(plannedBeers);
        newParty.setPlannedWine(plannedWines);
        newParty.setPlannedDrink(plannedDrinks);
        newParty.setPlannedShot(plannedShots);
        newParty.setStatus(status);

        partyDao.insert(newParty);
    }

    /*
    *
    * METHODS WHICH COMMUNICATES WITH DATABASE
    *
    * */

    private double liveUpdatePromille(double weight, String gender, Date firstUnitAddedTimeStamp){
        double sum = 0.0;

        beersConsumed = 0;
        winesConsumed = 0;
        drinksConsumed = 0;
        shotsConsumed = 0;

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
                beersConsumed++;
            }
            if(dayAfter.getUnit().equals("Wine")){
                winesConsumed++;
            }
            if(dayAfter.getUnit().equals("Drink")){
                drinksConsumed++;
            }
            if(dayAfter.getUnit().equals("Shot")){
                shotsConsumed++;
            }

            double totalGrams = countingGrams(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed);

            Date currentDate = new Date();
            long timeDifference = getDateDiff(currentDate, firstUnitAddedTimeStamp, TimeUnit.HOURS);

            // FROM 0 - 4 mins
            if(timeDifference > 0.085){
                System.out.println("Fra 0-4 minutter...");
                sum = 0;
            }
            // FROM 5 - 15 MIN
            if(timeDifference > 0.085 && timeDifference <= 0.25){
                // KALKULER PROMILLE
                sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(timeDifference) * timeDifference);
            }
            if(timeDifference > 0.25){
                // KALKULER PROMILLE
                sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, timeDifference));
            }
        }

        return sum;
    }

    private void getUnitsPlanned() {
        // TEMP VARIABLES
        int tempPlBeer = 0;
        int tempPlWine = 0;
        int tempPlDrink = 0;
        int tempPlShot = 0;

        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        PlanPartyElementsDao partyDao = daoSession.getPlanPartyElementsDao();

        // GET elements to temporary store them in variables then re-saving them
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
        for (PlanPartyElements party : partyList) {
            tempPlBeer = party.getPlannedBeer();
            tempPlWine = party.getPlannedWine();
            tempPlDrink = party.getPlannedDrink();
            tempPlShot = party.getPlannedShot();
        }

        plannedBeers = tempPlBeer;
        plannedWines = tempPlWine;
        plannedDrinks = tempPlDrink;
        plannedShots = tempPlShot;
    }

    public void clearTablePlanParty(){
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        PlanPartyElementsDao partyDao = daoSession.getPlanPartyElementsDao();

        // clearing the TABLE
        partyDao.deleteAll();

        plannedBeers = 0;
        plannedWines = 0;
        plannedDrinks = 0;
        plannedShots = 0;
    }

    public Status getStatus(){
        Status tempStatus = Status.DEFAULT;

        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        PlanPartyElementsDao partyDao = daoSession.getPlanPartyElementsDao();

        // GET elements to temporary store them in variables then re-saving them
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
        for (PlanPartyElements party : partyList) {
            if(party.getStatus().equals("NOT_RUNNING")){
                tempStatus = Status.NOT_RUNNING;
            }
            if(party.getStatus().equals("RUNNING")){
                tempStatus = Status.RUNNING;
            }
            if(party.getStatus().equals("DA_RUNNING")){
                tempStatus = Status.DA_RUNNING;
            }
        }
        return tempStatus;
    }

    /*
    *
    * WIDGETS
    *
    * */

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
}