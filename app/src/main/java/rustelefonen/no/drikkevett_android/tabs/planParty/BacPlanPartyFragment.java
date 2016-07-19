package rustelefonen.no.drikkevett_android.tabs.planParty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rustelefonen.no.drikkevett_android.MainActivity;
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
import rustelefonen.no.drikkevett_android.tabs.calc.fragments.BeerScrollAdapter;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.setGenderScore;

public class BacPlanPartyFragment extends Fragment {

    double weight = 0;
    String gender = "";
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

    public PieChart pieChart;

    private static final String PER_MILLE = "\u2030";

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
        setUserData();

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

    @Override
    public void onResume(){
        super.onResume();

        setUserData();
        // Check session
        status = isSessionOver();
        stateHandler(status);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!this.isVisible()) return;
        if (!isVisibleToUser) return;
        setUserData();
        status = isSessionOver();
        stateHandler(status);
    }

    /*
    * STATUS
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
            try{
                promilleBAC = Double.parseDouble(liveUpdatePromille(weight, gender, firstUnitAdded));
                System.out.println("PromilleBAC: " + promilleBAC);
            } catch(NumberFormatException e){
                promilleBAC = 0;
            }
        }

        promilleLbl.setText("" + promilleBAC);
        textQuoteLbl.setText(textInQuote(promilleBAC));

        // GET UNITS (PLANNED AND CONSUMED)
        getUnitsPlanned();

        // Layout
        beerLbl.setText(beersConsumed + "/" + plannedBeers);
        wineLbl.setText(winesConsumed + "/" + plannedWines);
        drinkLbl.setText(drinksConsumed + "/" + plannedDrinks);
        shotLbl.setText(shotsConsumed + "/" + plannedShots);

        // Visibility
        visuals(View.VISIBLE);
    }

    private void partyNotRunning(){
        addBtn.setVisibility(View.VISIBLE);
        removeBtn.setVisibility(View.VISIBLE);

        // Text quote
        textQuoteLbl.setText("Planlegg kvelden!");

        // Set text on statusBtn
        statusBtn.setText("Start Kvelden");

        // LAYOUT / UTSEENDE
        beerLbl.setText(plannedBeers + "");
        wineLbl.setText(plannedWines + "");
        drinkLbl.setText(plannedDrinks + "");
        shotLbl.setText(plannedShots + "");

        promilleLbl.setText("0.00");

        // Visibility
        visuals(View.VISIBLE);
    }

    private void dayAfterRunning(){
        // change text on textQuotes
        textQuoteLbl.setText("Klikk på knappen for å starte å planlegge en ny kveld");

        // Set text on statusbtn
        statusBtn.setText("Avslutt Dagen Derpå");

        // Visibility
        visuals(View.GONE);
    }

    private void visuals(int visibility){
        beerLbl.setVisibility(visibility);
        wineLbl.setVisibility(visibility);
        drinkLbl.setVisibility(visibility);
        shotLbl.setVisibility(visibility);
        promilleLbl.setVisibility(visibility);

        // Remove buttons
        addBtn.setVisibility(visibility);
        removeBtn.setVisibility(visibility);
    }

    /*
    * ADDING UNITS ( CONSUMED AND PLANNED
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

    private void updateStatusBtn(String status){
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
        System.out.println("Status in updateStatus: " + status);
        newParty.setStatus(status);

        // set start and end timeStamp for session
        newParty.setStartTimeStamp(startTimeStamp);
        System.out.println("Endstamp before setting it: " + endTimeStamp);
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
        System.out.println("Den kommer inn i status_DA_Running");
        List<PlanPartyElements> PlanPartyList = partyDao.queryBuilder().list();
        PlanPartyElements lastElement = PlanPartyList.get(PlanPartyList.size() - 1);

        System.out.println("End Date BEFORE: " + lastElement.getEndTimeStamp());
        plannedBeers = lastElement.getPlannedBeer();
        plannedWines = lastElement.getPlannedWine();
        plannedDrinks = lastElement.getPlannedDrink();
        plannedShots = lastElement.getPlannedShot();
        startTimeStamp = lastElement.getStartTimeStamp();
        endTimeStamp = new Date();
        firstUnitAdded = lastElement.getFirstUnitAddedDate();

        System.out.println("Planlegg Kvelden: (Avslutt kvelden trykket: EndSes: " + endTimeStamp + ") + (StartSes: + " + startTimeStamp + ")");

        setHistory();
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
            showAlertRunning();
        }
        if(textOnBtn.equals("Avslutt Dagen Derpå")){
            showAlertDayAfterRunning();
        }
        if(textOnBtn.equals("Start Kvelden")){
            showAlertNotRunning();
        }
    }

    private void setUserData(){
        User user = ((MainActivity)getActivity()).getUser();
        weight = user.getWeight();
        gender = user.getGender();
    }

    /*
    * DATABASE METHODS
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
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
        PlanPartyElements elements = partyList.get(partyList.size() - 1);
        plannedBeers = elements.getPlannedBeer();
        plannedWines = elements.getPlannedWine();
        plannedDrinks = elements.getPlannedDrink();
        plannedShots = elements.getPlannedShot();
    }

    public void clearPartyTables(){
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = setDaoSessionDB().getDayAfterBACDao();

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
        Date endSes = null;

        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();

        // GET elements to temporary store them in variables then re-saving them
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();

        for (PlanPartyElements party : partyList) {
            endSes = party.getEndTimeStamp();
        }
        System.out.println("Current Dato: " + currentDate);
        System.out.println("End of session: " + endSes);

        if(endSes == null){
            return Status.NOT_RUNNING;
        } else {
            if(endSes.after(currentDate)){
                return Status.RUNNING;
            } else {
                updateStateWhenSessionIsOver(Status.DA_RUNNING.toString());
                return Status.DA_RUNNING;
            }
        }
    }

    private void updateStateWhenSessionIsOver(String status){
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        List<PlanPartyElements> PlanPartyList = partyDao.queryBuilder().list();

        for(PlanPartyElements parEl : PlanPartyList){
            plannedBeers = parEl.getPlannedBeer();
            plannedWines = parEl.getPlannedWine();
            plannedDrinks = parEl.getPlannedDrink();
            plannedShots = parEl.getPlannedShot();
            startTimeStamp = parEl.getStartTimeStamp();
            endTimeStamp = parEl.getEndTimeStamp();
            firstUnitAdded = parEl.getFirstUnitAddedDate();
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
        System.out.println("Status in updateStatus: " + status);
        newParty.setStatus(status);

        // set start and end timeStamp for session
        newParty.setStartTimeStamp(startTimeStamp);
        System.out.println("Endstamp before setting it: " + endTimeStamp);
        newParty.setEndTimeStamp(endTimeStamp);

        // First unit added
        newParty.setFirstUnitAddedDate(firstUnitAdded);

        partyDao.insert(newParty);

        Date tempStartDate = null;

        HistoryDao histDao = setDaoSessionDB().getHistoryDao();
        List<History> histList = histDao.queryBuilder().list();
        for(History histItems : histList){
            tempStartDate = histItems.getStartDate();

        }
        System.out.println("History Date: " + tempStartDate + " = " + startTimeStamp);
        if(startTimeStamp.equals(tempStartDate)){
            // IF they are equal the history is allready added, if not, add the new history.
            System.out.println("History already added...");
        } else {
            System.out.println("Hist not added...");
            setHistory();
            handleGraphHistory();
        }
        beersConsumed = 0;
        winesConsumed = 0;
        drinksConsumed = 0;
        shotsConsumed = 0;
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
        User user = ((MainActivity)getActivity()).getUser();
        return (b * user.getBeerPrice()) + (w * user.getWinePrice()) + (d * user.getDrinkPrice()) + (s * user.getShotPrice());
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
        // hente forskjellige enheter:
        DayAfterBACDao dayDao = setDaoSessionDB().getDayAfterBACDao();
        List<DayAfterBAC> dayAfterBACList = dayDao.queryBuilder().list();
        beersConsumed = 0;
        winesConsumed = 0;
        drinksConsumed = 0;
        shotsConsumed = 0;

        for (DayAfterBAC dayAfter : dayAfterBACList) {
            String unit = dayAfter.getUnit();
            if(unit.equals("Beer")){
                beersConsumed++;
            }
            if(unit.equals("Wine")){
                winesConsumed++;
            }
            if(unit.equals("Drink")){
                drinksConsumed++;
            }
            if(unit.equals("Shot")){
                shotsConsumed++;
            }
        }

        // regne ut kostnader (1)
        totalCosts = calculateCosts(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed);
        insertHistoryDB(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed, startTimeStamp, endTimeStamp, totalCosts, 0.0);
        System.out.println("History inserted to DB...");
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
        History lastElement = historyList.get(historyList.size() -1);
        tempID = lastElement.getId();
        System.out.println("Temp ID: " + tempID);
        return (int) tempID;
    }

    private void addGraphValues(double currentBAC, Date timeStamp, int id){
        GraphHistoryDao graphHistoryDao = setDaoSessionDB().getGraphHistoryDao();
        GraphHistory newGraphVal = new GraphHistory();
        System.out.println("Id som blir sendt inn til G HIST: " + id);
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
                // set minuts to hours
                double hoursToMins = tempInterval / 60;


                String tempPromille = calculateBAC(gender, weight, countingGrams(beer, wine, drink, shot), hoursToMins);
                try{
                    promille = Double.parseDouble(tempPromille);
                } catch(NumberFormatException e) {
                    promille = 0;
                }

                // Sjekke høyeste promille og temp lagre denne verdien før man oppdaterer høyeste promille i historikken. ( I LOOPEN )
                if(highestBAC < promille){
                    highestBAC = promille;
                }

                // Legg til 15 min på en date
                tempTimeStamp = addMinsToDate((int) tempInterval);
            }
            // Legg til elementet i databasen ( I LOOPEN )
            addGraphValues(promille, tempTimeStamp, id);
        }
        updateHighestBac(highestBAC);
    }

    private void updateHighestBac(double highestBAC){
        // update highest BAC in History
        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);
        lastElement.setHighestBAC(highestBAC);
        historyDao.insertOrReplace(lastElement);
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

    private void showAlertNotRunning() {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(getContext());
        alert_builder.setMessage("Har du husket alt? ").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                statusBtn.setText("Avslutt Kvelden");
                status = Status.RUNNING;
                updateStatusBtn(status.toString());
                stateHandler(status);
            }
        }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("Avbryt");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = alert_builder.create();
        alert.setTitle("Start Kvelden");
        alert.show();
    }

    private void showAlertRunning() {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(getContext());
        alert_builder.setMessage("Er du sikker på at du vil avslutte kvelden?").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                statusBtn.setText("Avslutt Dagen Derpå");
                status = Status.DA_RUNNING;
                updateStatusBtn(status.toString());
                stateHandler(status);
            }
        }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = alert_builder.create();
        alert.setTitle("Avslutt Kvelden");
        alert.show();
    }

    private void showAlertDayAfterRunning() {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(getContext());
        alert_builder.setMessage("Er du sikker på at du vil avslutte Dagen Derpå? ").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                statusBtn.setText("Start Kvelden");
                status = Status.NOT_RUNNING;

                // Clear tabels: (PlanPartyElements and DayAfterBAC)
                clearPartyTables();

                updateStatusBtn(status.toString());
                stateHandler(status);
            }
        }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = alert_builder.create();
        alert.setTitle("Avslutt Dagen Derpå");
        alert.show();
    }

    /*
    * WIDGETS
    * */

    private void fillPieChart() {
        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry((float) beersConsumed, 0));
        entries.add(new Entry((float) winesConsumed, 1));
        entries.add(new Entry((float) drinksConsumed, 2));
        entries.add(new Entry((float) shotsConsumed, 3));

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        dataset.setDrawValues(false);
        dataset.setColors(getColors());

        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < 4; i++) labels.add("");

        PieData data = new PieData(labels, dataset); // initialize Piedata
        pieChart.setData(data);
    }

    private void stylePieChart() {
        pieChart.setCenterText("0.0" + PER_MILLE);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(90f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterTextRadiusPercent(100f);
        pieChart.setTransparentCircleRadius(95f);
        pieChart.setDescription("");
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.setCenterTextSize(27.0f);
        pieChart.setCenterTextColor(Color.parseColor("#FFFFFF"));
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private int[] getColors() {
        return new int[]{
                ContextCompat.getColor(getContext(), R.color.beerColor),
                ContextCompat.getColor(getContext(), R.color.wineColor),
                ContextCompat.getColor(getContext(), R.color.drinkColor),
                ContextCompat.getColor(getContext(), R.color.shotColor)};
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
}