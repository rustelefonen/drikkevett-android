package rustelefonen.no.drikkevett_android.tabs.planParty;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.GraphHistory;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.tabs.calc.fragments.BeerScrollAdapter;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

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

    // start and ending date of "session"
    public Date startTimeStamp = new Date();
    public Date endTimeStamp = new Date();
    public static final long HOUR = 3600*1000; // in milli-seconds.

    // dummy grams
    double beerGrams = 12.6;
    double wineGrams = 14.0;
    double drinkGrams = 15.0;
    double shotGrams = 16.0;

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

    // BEER SCROLL
    public ViewPager beerScroll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_plan_party_frag, container, false);

        initWidgets();

        // Check session
        status = isSessionOver();
        stateHandler(status);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status.equals(Status.RUNNING)){
                    addConsumedUnits(getUnitId());

                    // set firstUnit time stamp
                    if(!isFirstUnitAdded()){
                        setFirstUnitAdded();
                    }
                }
                if(status.equals(Status.NOT_RUNNING)){
                    addPlannedUnits(getUnitId());
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

        //beer_scroll_plan_party
        beerScroll = (ViewPager) v.findViewById(R.id.beer_scroll_plan_party);
        beerScroll.setAdapter(new BeerScrollAdapter(this.getFragmentManager()));


        return v;
    }

    public void delayedLivePromille(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firstUnitAdded != null){
                    promilleBAC = Double.parseDouble(liveUpdatePromille(weight, gender, firstUnitAdded));
                    promilleLbl.setText("" + promilleBAC);
                }
            }
        }, 5000);
    }

    @Override
    public void onResume(){
        super.onResume();

        // Check session
        status = isSessionOver();
        stateHandler(status);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!this.isVisible()) return;
        if (!isVisibleToUser) return;

        System.out.println("<-------------------------------------------->");
        System.out.println("1. Status when re-entering view: " + status);

        // Check session
        status = isSessionOver();
        System.out.println("2. Status when re-entering view: " + status);

        stateHandler(status);
        System.out.println("3. Status when re-entering view: " + status);
        System.out.println("<-------------------------------------------->");
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

        // get firstUnitAddedStamp
        firstUnitAdded = getFirstUnitAddedStamp();

        // calculate BAC
        if(firstUnitAdded != null){
            promilleBAC = Double.parseDouble(liveUpdatePromille(weight, gender, firstUnitAdded)) ;
        }
        delayedLivePromille();

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
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();

        if(status.equals("NOT_RUNNING")){
            statusNotRunning();
        }
        if(status.equals("RUNNING")){
            statusRunning(partyDao);
        }
        if(status.equals("DA_RUNNING")){
            statusDA_Running(partyDao);
        }
        partyDao.deleteAll();

        // Inserting new planned elements
        PlanPartyElements newParty = new PlanPartyElements();

        // set planned units
        newParty.setPlannedBeer(plannedBeers);
        newParty.setPlannedWine(plannedWines);
        newParty.setPlannedDrink(plannedDrinks);
        newParty.setPlannedShot(plannedShots);

        // set new status
        newParty.setStatus(status);

        // set start and end timeStamp for session
        newParty.setStartTimeStamp(startTimeStamp);
        newParty.setEndTimeStamp(endTimeStamp);

        // First unit added
        newParty.setFirstUnitAddedDate(firstUnitAdded);

        partyDao.insert(newParty);
    }

    private void statusRunning(PlanPartyElementsDao partyDao){
        // start og end timestamp blir satt
        startTimeStamp = new Date();

        // set sessionLength in the parameter
        endTimeStamp = setEndOfSesStamp(12);

        List<PlanPartyElements> PlanPartyList = partyDao.queryBuilder().list();

        for (PlanPartyElements party : PlanPartyList) {
            firstUnitAdded = party.getFirstUnitAddedDate();
        }
    }

    private void statusDA_Running(PlanPartyElementsDao partyDao){
        List<PlanPartyElements> PlanPartyList = partyDao.queryBuilder().list();

        for (PlanPartyElements party : PlanPartyList) {
            plannedBeers = party.getPlannedBeer();
            plannedWines = party.getPlannedWine();
            plannedDrinks = party.getPlannedDrink();
            plannedShots = party.getPlannedShot();
            startTimeStamp = party.getStartTimeStamp();
            endTimeStamp = new Date();
            firstUnitAdded = party.getFirstUnitAddedDate();
        }
        // send to graphHistory (values needed for the Graph)
        handleGraphHistory();
    }

    private void statusNotRunning(){
        // RESET PLAN UNITS
        plannedBeers = 0;
        plannedWines = 0;
        plannedDrinks = 0;
        plannedShots = 0;

        // consumed
        beersConsumed = 0;
        winesConsumed = 0;
        drinksConsumed = 0;
        shotsConsumed = 0;

        // times
        startTimeStamp = null;
        endTimeStamp = null;
        firstUnitAdded = null;

        // promille / BAC
        promilleBAC = 0;
    }

    private void statusBtnHandler(){
        String textOnBtn = (String) statusBtn.getText();

        if(textOnBtn.equals("Avslutt Kvelden")){
            statusBtn.setText("Avslutt Dagen Derpå");
            status = Status.DA_RUNNING;
            // Send til historikk
            setHistory();
        }
        if(textOnBtn.equals("Avslutt Dagen Derpå")){
            statusBtn.setText("Start Kvelden");
            status = Status.NOT_RUNNING;

            // Clear tabels: (PlanPartyElements and DayAfterBAC)
            clearPartyTables();
        }
        if(textOnBtn.equals("Start Kvelden")){
            statusBtn.setText("Avslutt Kvelden");
            status = Status.RUNNING;
        }
        updateStatus(status.toString());
        stateHandler(status);
    }

    private void updateEndStamp(){

    }

    /*
    *
    * DATABASE METHODS
    *
    * */

    public DaoSession setDaoSessionDB(){
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        return daoSession;
    }

    private String liveUpdatePromille(double weight, String gender, Date firstUnitAddedTimeStamp){
        double sum = 0.0;

        beersConsumed = 0;
        winesConsumed = 0;
        drinksConsumed = 0;
        shotsConsumed = 0;

        DayAfterBACDao dayAfterDao = setDaoSessionDB().getDayAfterBACDao();

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

            long timeDifference = getDateDiff(firstUnitAddedTimeStamp, currentDate, TimeUnit.MINUTES);

            double newValueDouble = (double)timeDifference;
            double minToHours = newValueDouble / 60;

            // FROM 0 - 4 mins
            if(minToHours < 0.085){
                sum = 0;
            }
            // FROM 5 - 15 MIN
            if(minToHours > 0.085 && minToHours <= 0.25){
                // KALKULER PROMILLE
                sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(minToHours) * minToHours);
            }
            if(minToHours > 0.25){
                // KALKULER PROMILLE
                sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, minToHours));
            }
        }
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        String newPromilleBac = numberFormat.format(sum);
        return newPromilleBac;
    }

    private void getUnitsPlanned() {
        // TEMP VARIABLES
        int tempPlBeer = 0;
        int tempPlWine = 0;
        int tempPlDrink = 0;
        int tempPlShot = 0;

        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();

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
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = setDaoSessionDB().getDayAfterBACDao();

        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();
        for (DayAfterBAC dayAfter : dayAfterBACList) {
            //System.out.println("(clearTables) units = " + dayAfter.getUnit());
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
        promilleBAC = 0;
    }

    public Status getStatus(){
        Status tempStatus = Status.DEFAULT;
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();

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

    private boolean isFirstUnitAdded(){
        boolean bool = false;

        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();

        // GET elements to temporary store them in variables then re-saving them
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

    private void setFirstUnitAdded(){
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        List<PlanPartyElements> PlanPartyList = partyDao.queryBuilder().list();

        String state = "";

        for (PlanPartyElements party : PlanPartyList) {
            plannedBeers = party.getPlannedBeer();
            plannedWines = party.getPlannedWine();
            plannedDrinks = party.getPlannedDrink();
            plannedShots = party.getPlannedShot();
            startTimeStamp = party.getStartTimeStamp();
            endTimeStamp = party.getEndTimeStamp();
            state = party.getStatus();
        }
        firstUnitAdded = new Date();
        insertPlanParty(plannedBeers, plannedWines, plannedDrinks, plannedShots, state, startTimeStamp, endTimeStamp, firstUnitAdded);
    }

    private Date getFirstUnitAddedStamp(){
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        List<PlanPartyElements> planPartyList = partyDao.queryBuilder().list();
        PlanPartyElements lastElement = planPartyList.get(planPartyList.size() -1);
        Date firstAdded;
        if(lastElement.getFirstUnitAddedDate() == null){
            firstAdded = null;
        } else {
            firstAdded = lastElement.getFirstUnitAddedDate();
        }
        return firstAdded;
    }

    private void insertPlanParty(int plB, int plW, int plD, int plS, String status, Date sStamp, Date eStamp, Date fStamp){
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        partyDao.deleteAll();
        // Inserting new planned elements
        PlanPartyElements newParty = new PlanPartyElements();

        // set planned units
        newParty.setPlannedBeer(plB);
        newParty.setPlannedWine(plW);
        newParty.setPlannedDrink(plD);
        newParty.setPlannedShot(plS);

        // set new status
        newParty.setStatus(status);

        // set start and end timeStamp for session
        newParty.setStartTimeStamp(sStamp);
        newParty.setEndTimeStamp(eStamp);

        // First unit added
        newParty.setFirstUnitAddedDate(fStamp);
        partyDao.insert(newParty);
    }

    private Status isSessionOver(){
        Date currentDate = new Date();
        Date endSes = new Date();

        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();

        // GET elements to temporary store them in variables then re-saving them
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
        for (PlanPartyElements party : partyList) {
            endSes = party.getEndTimeStamp();
        }

        if(endSes == null){
            return Status.NOT_RUNNING;
        } else {
            if(endSes.after(currentDate)){
                return Status.RUNNING;
            } else {
                return Status.DA_RUNNING;
            }
        }
    }

    private Date setEndOfSesStamp(int hours){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.HOUR, hours);

        date = calendar.getTime();
        return date;
    }

    public int calculateCosts(int b, int w, int d, int s){
        // get costs from DB:
        UserDao userDao = setDaoSessionDB().getUserDao();
        int beerC = 0;
        int wineC = 0;
        int drinkC = 0;
        int shotC = 0;

        /* DETTE ER IKKE IMPLEMENTERT ENDA:
        List<User> userList = userDao.queryBuilder().list();
        for (User user : userList) {
            beerC = user.getBeerPrice();
            wineC = user.getWinePrice();
            drinkC = user.getDrinkPrice();
            shotC = user.getShotPrice();
        }
        */

        // dummy costs:
        beerC = 100;
        wineC = 200;
        drinkC = 300;
        shotC = 400;

        return (b * beerC) + (w * wineC) + (d * drinkC) + (s * shotC);
    }

    private void insertHistoryDB(int b, int w, int d, int s, Date startD, Date endD, int totalCosts, double highBAC){
        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();
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
    }

    private void setHistory(){
        int totalCosts = 0;
        double highestBAC = 0.0;
        // highest BAC and units is filled within the method (5)
        if(firstUnitAdded != null){
            highestBAC = Double.parseDouble(liveUpdatePromille(weight, gender, firstUnitAdded));
        }

        // regne ut kostnader (1)
        totalCosts = calculateCosts(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed);

        // hente sesjonen ( altså start og når kvelden ble avsluttet ) (2)
        if(startTimeStamp == null || endTimeStamp == null){
            System.out.println("ERROR: Ingen verdier i STS eller ETS...");
        } else {
            PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
            List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
            for (PlanPartyElements party : partyList) {
                startTimeStamp = party.getStartTimeStamp();
                endTimeStamp = party.getEndTimeStamp();
            }
        }
        insertHistoryDB(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed, startTimeStamp, endTimeStamp, totalCosts, highestBAC);
        System.out.println("History inserted to DB...");
        //getHistory();
    }

    private void getHistory(){
        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();
        List<History> historyList = historyDao.queryBuilder().list();

        for(History hist : historyList) {
            System.out.println("<---------------------------------------------------->");
            System.out.println("Historikk verdi( " + hist.getId() + " )");
            System.out.println("ØL: " + hist.getBeerCount());
            System.out.println("Vin: " + hist.getWineCount());
            System.out.println("Drink: " + hist.getDrinkCount());
            System.out.println("Shot: " + hist.getShotCount());

            System.out.println("Kostnader/Forbruk: " + hist.getSum());
            System.out.println("Høyeste Promille: " + hist.getHighestBAC());

            System.out.println("Starttidspunkt: " + hist.getStartDate());
            System.out.println("End of Session: " + hist.getEndDate());
            System.out.println("<---------------------------------------------------->");
        }
    }

    private int fetchHistoryID_DB(){
        double tempID = 0;

        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();
        List<History> historyList = historyDao.queryBuilder().list();

        for(History hist : historyList) {
            tempID = (double) hist.getId();
        }

        return (int) tempID;
    }

    private void addGraphValues(double currentBAC, Date timeStamp, int id){
        GraphHistoryDao graphHistoryDao = setDaoSessionDB().getGraphHistoryDao();
        GraphHistory newGraphVal = new GraphHistory();

        newGraphVal.setHistoryId(id);
        newGraphVal.setCurrentBAC(currentBAC);
        newGraphVal.setTimestamp(timeStamp);

        graphHistoryDao.insert(newGraphVal);
    }

    private void calcPr(int id){
        // Loop gjennom DayAfterBAC. Simuler hva promillen var hvert kvarter/halvtime
        DayAfterBACDao dayAfterBACDao = setDaoSessionDB().getDayAfterBACDao();
        List<DayAfterBAC> dayAfterBacList = dayAfterBACDao.queryBuilder().list();

        double highestBAC = 0.0;
        double promille = 0.0;
        Date tempTimeStamp = new Date();

        double sessionInterval = getDateDiff(startTimeStamp, endTimeStamp, TimeUnit.MINUTES);
        System.out.println("Session Interval: " + sessionInterval);

        double tempInterval = 0;

        while(tempInterval < sessionInterval){
            int beer = 0;
            int wine = 0;
            int drink = 0;
            int shot = 0;

            tempInterval += 15;
            System.out.println("Temp Interval: " + tempInterval);

            for(DayAfterBAC dayAfter : dayAfterBacList){
                String unit = dayAfter.getUnit();

                if(unit.equals("Beer")){
                    beer++;
                }
                if(unit.equals("Wine")){
                    wine++;
                }
                if(unit.equals("Drink")){
                    drink++;
                }
                if(unit.equals("Shot")){
                    shot++;
                }

                System.out.println("-- (beer) --: " + beer);
                // set minuts to hours
                double hoursToMins = tempInterval / 60;
                System.out.println("Hours to mins: " + hoursToMins);

                // Legg til IDen til hvert av promillene og et tidspunkt ( I LOOPEN )
                String tempPromille = calculateBAC(gender, weight, countingGrams(beer, wine, drink, shot), hoursToMins);
                promille = Double.parseDouble(tempPromille);
                System.out.println("Promille: " + promille);

                // Sjekke høyeste promille og temp lagre denne verdien før man oppdaterer høyeste promille i historikken. ( I LOOPEN )
                if(highestBAC < promille){
                    highestBAC = promille;
                }

                // Legg til 15 min på en date
                tempTimeStamp = addMinsToDate((int) tempInterval);
                System.out.println("Temp Time Stamp: " + tempTimeStamp);

                // Legg til elementet i databasen ( I LOOPEN )
                addGraphValues(promille, tempTimeStamp, id);
            }
        }

        // update highest BAC in History
        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();

        List<History> histories = historyDao.queryBuilder().list();

        History lastElement = histories.get(histories.size() -1);
        System.out.println("Siste Element i DB History: " + lastElement.getId() + "Høyeste før: " + lastElement.getHighestBAC());

        lastElement.setHighestBAC(highestBAC);

        historyDao.insertOrReplace(lastElement);

        System.out.println("Høyeste etter: " + lastElement.getHighestBAC());
    }

    /*
    *
    * METHODS WITHOUT CONNECTION TO DB
    *
    * */

    private void handleGraphHistory(){
        // Add HistoryValue ID to graphHistoryID (History_ID)
        // Fetch last history_ID and set "History_ID" in graphHistory to that value
        int historyID = fetchHistoryID_DB();
        System.out.println("Last ID added: " + historyID);

        calcPr(historyID);
    }

    private Date addMinsToDate(int minutes){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.MINUTE, minutes);

        date = calendar.getTime();
        return date;
    }

    public String textInQuote(double bac){
        String output = "";
        if(bac >= 0 && bac < 0.4){
            output = "Kos deg";
            promilleLbl.setTextColor(Color.rgb(255, 255, 255));
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

    public String calculateBAC(String gender, double weight, double grams, double hours) {
        double oppdatertPromille = 0.0;
        double genderScore = setGenderScore(gender);

        if(grams == 0.0){
            oppdatertPromille = 0.0;
        } else {
            oppdatertPromille = grams/(weight * genderScore) - (0.15 * hours);
            if(oppdatertPromille < 0.0){
                oppdatertPromille = 0.0;
            }
        }
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        String newPromille = numberFormat.format(oppdatertPromille);

        return newPromille;
    }

    public double countingGrams(double beerUnits, double wineUnits, double drinkUnits, double shotUnits){
        return (beerUnits * beerGrams) + (wineUnits * wineGrams) + (drinkUnits * drinkGrams) + (shotUnits * shotGrams);
    }

    public double setGenderScore(String gender){
        double genderScore = 0.0;

        if(gender.equals("Mann")){
            genderScore = 0.70;
        }
        if(gender.equals("Kvinne")){
            genderScore = 0.60;
        }

        return genderScore;
    }

    private String getUnitId(){
        String unit = "";
        switch (beerScroll.getCurrentItem()) {
            case 0: {
                unit = "Beer";
                break;
            }
            case 1: {
                unit = "Wine";
                break;
            }
            case 2: {
                unit = "Drink";
                break;
            }
            case 3: {
                unit = "Shot";
                break;
            }
        }
        return unit;
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