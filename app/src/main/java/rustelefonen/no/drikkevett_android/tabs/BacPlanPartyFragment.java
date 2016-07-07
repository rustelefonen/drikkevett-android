package rustelefonen.no.drikkevett_android.tabs;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import static rustelefonen.no.drikkevett_android.tabs.BacCalcFragment.calculateBAC;
import static rustelefonen.no.drikkevett_android.tabs.BacCalcFragment.countingGrams;
import static rustelefonen.no.drikkevett_android.tabs.BacCalcFragment.setGenderScore;
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
        updateStatus(status.toString());
        stateHandler(status);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status.equals(Status.RUNNING)){
                    addConsumedUnits("Beer");
                }
                if(status.equals(Status.NOT_RUNNING)){
                    addPlannedUnits("Beer");
                }
                if(status.equals(Status.DA_RUNNING)){
                    // NOTHING SHOULD HAPPOND
                }
                stateHandler(status);
            }
        });

        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusBtnHandler();
            }
        });
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        // hente status fra db
        status = getStatus();
        updateStatus(status.toString());
        stateHandler(status);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!this.isVisible()) return;
        if (!isVisibleToUser) return;

        // hente status fra db
        status = getStatus();
        updateStatus(status.toString());
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
        if(state == Status.RUNNING){
            partyRunning();
        }
        // IF PLAN PARTY IS NOT RUNNING:
        if(state == Status.NOT_RUNNING){
            partyNotRunning();
        }
        // IF DAY AFTER IS RUNNING
        if(state == Status.DA_RUNNING){
            dayAfterRunning();
        }
    }

    private void partyRunning(){
        // SET TEXT ON BUTTON:
        statusBtn.setText("Avslutt Kvelden");
        addBtn.setVisibility(View.VISIBLE);
        removeBtn.setVisibility(View.VISIBLE);

        // Consuming alcohol

        // UPDATE PROMILLE/BAC

        // get firstUnitAddedStamp

        promilleBAC = liveUpdatePromille(weight, gender, firstUnitAdded);

        promilleLbl.setText("" + promilleBAC);
        textQuoteLbl.setText(textInQuote(promilleBAC));

        // GET UNITS (PLANNED AND CONSUMED)
        getUnitsPlanned();

        // Layout
        beerLbl.setText(beersConsumed + "/" + plannedBeers + "\nØL");
        wineLbl.setText(winesConsumed + "/" + plannedWines + "\nVin");
        drinkLbl.setText(drinksConsumed + "/" + plannedDrinks + "\nDrink");
        shotLbl.setText(shotsConsumed + "/" + plannedShots + "\nShot");
    }

    private void partyNotRunning(){
        addBtn.setVisibility(View.VISIBLE);
        removeBtn.setVisibility(View.VISIBLE);

        // Text quote
        textQuoteLbl.setText("Planlegg kvelden!");

        // Set text on statusBtn
        statusBtn.setText("Start Kvelden");

        // LAYOUT / UTSEENDE
        beerLbl.setText(plannedBeers + "\nØL");
        wineLbl.setText(plannedWines + "\nVin");
        drinkLbl.setText(plannedDrinks + "\nDrink");
        shotLbl.setText(plannedShots + "\nShot");

        promilleLbl.setText(promilleBAC + "");
    }

    private void dayAfterRunning(){
        // change text on textQuotes
        textQuoteLbl.setText("Klikk på knappen for å starte å planlegge en ny kveld");



        // Remove buttons
        addBtn.setVisibility(View.GONE);
        removeBtn.setVisibility(View.GONE);

        // Set text on statusbtn
        statusBtn.setText("Avslutt Dagen Derpå");

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
        /*
        * ADDING PLANNED UNITS HAPPONDS IN THIS METHOD
        * */

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

            // RESET CONSUMED UNITS

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

    private void statusBtnHandler(){
        String textOnBtn = (String) statusBtn.getText();
        if(textOnBtn.equals("Start Kvelden")){
            statusBtn.setText("Avslutt Kvelden");
            status = status.RUNNING;
        }
        if(textOnBtn.equals("Avslutt Kvelden")){
            statusBtn.setText("Avslutt Dagen Derpå");
            status = status.DA_RUNNING;
        }
        if(textOnBtn.equals("Avslutt Dagen Derpå")){
            statusBtn.setText("Start Kvelden");
            status = status.NOT_RUNNING;

            // Clear tabels: (PlanPartyElements and DayAfterBAC)
            clearPartyTables();
        }
        updateStatus(status.toString());
        stateHandler(status);
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
            if(timeDifference < 0.085){
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

            System.out.println("LiveP ( UNIT ) = " + dayAfter.getUnit());

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

    public void clearPartyTables(){
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        PlanPartyElementsDao partyDao = daoSession.getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = daoSession.getDayAfterBACDao();

        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();
        for (DayAfterBAC dayAfter : dayAfterBACList) {
            System.out.println("(clearTables) units = " + dayAfter.getUnit());
        }

        // clearing the tables (PlanPartyDao and DayAfterBacDao)
        partyDao.deleteAll();
        dayAfterDao.deleteAll();

        // Clear variabels planned and consumed
        plannedBeers = 0;
        plannedWines = 0;
        plannedDrinks = 0;
        plannedShots = 0;
        beersConsumed = 0;
        winesConsumed = 0;
        drinksConsumed = 0;
        shotsConsumed = 0;
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

    public String textInQuote(double bac){
        String output = "";
        if(bac >= 0 && bac < 0.4){
            output = "Kos deg";
            promilleLbl.setTextColor(Color.rgb(0, 0, 0));
        }
        if(bac >= 0.4 && bac < 0.8){
            output = "Lykkepromille";
            promilleLbl.setTextColor(Color.rgb(26, 193, 73));
        }
        if(bac >= 0.8 && bac < 1.0){
            output = "Du blir mer kritikkløs og risikovillig";
            promilleLbl.setTextColor(Color.rgb(255, 180, 10));
        }
        if(bac >= 1.0 && bac < 1.2){
            output = "Balansen blir dårligere";
            promilleLbl.setTextColor(Color.rgb(255, 180, 10));
        }
        if(bac >= 1.2 && bac < 1.4){
            output = "Talen snøvlete og \nkontroll på bevegelser forverres";
            promilleLbl.setTextColor(Color.rgb(255, 160, 0));
        }
        if(bac >= 1.4 && bac < 1.8){
            output = "Man blir trøtt, sløv og \nkan bli kvalm";
            promilleLbl.setTextColor(Color.rgb(255, 160, 0));
        }
        if(bac >= 1.8 && bac < 3.0){
            output = "Hukommelsen sliter";
            promilleLbl.setTextColor(Color.rgb(255, 55, 55));
        }
        if(bac >= 3.0 && bac < 5.0){
            output = "Svært høy promille! \nMan kan bli bevisstløs";
            promilleLbl.setTextColor(Color.rgb(255, 55, 55));
        }
        if(bac >= 5.0){
            output = "Du kan dø ved en så høy promille!";
            promilleLbl.setTextColor(Color.rgb(255, 0, 0));
        }
        return output;
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