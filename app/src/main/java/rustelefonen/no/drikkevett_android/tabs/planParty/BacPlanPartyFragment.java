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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import rustelefonen.no.drikkevett_android.util.NavigationUtil;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import static rustelefonen.no.drikkevett_android.util.DateUtil.setEndOfSesStamp;
import static rustelefonen.no.drikkevett_android.util.DateUtil.setNewUnitDate;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.calculateBAC;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.countingGrams;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.setGenderScore;

public class BacPlanPartyFragment extends Fragment {

    private double weight = 0;
    private String gender = "";
    private Date firstUnitAdded = new Date();

    private int plannedBeers = 0, plannedWines = 0, plannedDrinks = 0, plannedShots = 0, beersConsumed = 0, winesConsumed = 0, drinksConsumed = 0, shotsConsumed = 0, totalUnits = 0;

    private double promilleBAC = 0;

    private Status status;

    private Date startTimeStamp = new Date(), endTimeStamp = new Date();

    private PieChart pieChart;
    private static final String PER_MILLE = "\u2030";

    private TextView beerLbl, wineLbl, drinkLbl, shotLbl, textQuoteLbl;

    private Button addBtn, removeBtn, statusBtn;

    public View v;

    private LinearLayout dayAfterRunning_LinLay, planPartyRunning_LinLay;

    private ViewPager beerScroll;

    private PlanPartyDB planPartyDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_plan_party_frag, container, false);

        planPartyDB = new PlanPartyDB(getContext());
        initWidgets();
        setUserData();
        status = isSessionOver();
        stateHandler(status);



        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status.equals(Status.RUNNING)){
                    addConsumedUnits(getUnitId());
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

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePlannedUnits(getUnitId());
                stateHandler(status);
            }
        });

        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusBtnHandler();
            }
        });

        beerScroll = (ViewPager) v.findViewById(R.id.beer_scroll_plan_party);
        beerScroll.setAdapter(new BeerScrollAdapter(this.getFragmentManager()));

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.simple_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                NavigationUtil.navigateToContactInformation(getContext());
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        setUserData();
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
        if(state == Status.RUNNING){
            partyRunning();
        }
        if(state == Status.NOT_RUNNING){
            partyNotRunning();
        }
        if(state == Status.DA_RUNNING){
            dayAfterRunning();
        }
    }

    private void partyRunning(){
        statusBtn.setText("Avslutt Kvelden");
        removeBtn.setVisibility(View.GONE);
        addBtn.setText("Drikk");

        firstUnitAdded = planPartyDB.getFirstUnitAddedStamp();

        // calculate BAC
        if(firstUnitAdded != null){
            try{
                promilleBAC = Double.parseDouble(liveUpdatePromille(weight, gender, firstUnitAdded));
                System.out.println("PromilleBAC: " + promilleBAC);
            } catch(NumberFormatException e){
                promilleBAC = 0;
            }
        }

        getUnitsPlanned();

        // Layout
        beerLbl.setText(beersConsumed + "/" + plannedBeers);
        wineLbl.setText(winesConsumed + "/" + plannedWines);
        drinkLbl.setText(drinksConsumed + "/" + plannedDrinks);
        shotLbl.setText(shotsConsumed + "/" + plannedShots);

        // Visibility
        dayAfterRunning_LinLay.setVisibility(View.GONE);
        planPartyRunning_LinLay.setVisibility(View.VISIBLE);

        // pie chart
        pieChart = (PieChart) v.findViewById(R.id.pie_chart_bac_plan_party);
        fillPieChart(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed);
        stylePieChart();
        textQuoteLbl.setText(textInQuote(promilleBAC));
    }

    private void partyNotRunning(){
        removeBtn.setVisibility(View.VISIBLE);
        addBtn.setText("Legg til");

        textQuoteLbl.setText("Planlegg kvelden!");

        statusBtn.setText("Start Kvelden");

        // LAYOUT
        beerLbl.setText(plannedBeers + "");
        wineLbl.setText(plannedWines + "");
        drinkLbl.setText(plannedDrinks + "");
        shotLbl.setText(plannedShots + "");

        // Visibility
        dayAfterRunning_LinLay.setVisibility(View.GONE);
        planPartyRunning_LinLay.setVisibility(View.VISIBLE);

        // pie chart
        pieChart = (PieChart) v.findViewById(R.id.pie_chart_bac_plan_party);
        fillPieChart(plannedBeers, plannedWines, plannedDrinks, plannedShots);
        stylePieChart();
    }

    private void dayAfterRunning(){
        statusBtn.setText("Avslutt Dagen Derpå");
        dayAfterRunning_LinLay.setVisibility(View.VISIBLE);
        planPartyRunning_LinLay.setVisibility(View.GONE);
    }

    /*
    * ADDING UNITS ( CONSUMED AND PLANNED )
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
        int limit = 30;

        if(unit.equals("Beer")){
            if(totalUnits >= limit){
                totalUnits = limit;
            } else {
                plannedBeers++;
                totalUnits++;
            }
        }
        if(unit.equals("Wine")){
            if(totalUnits >= limit){
                totalUnits = limit;
            } else {
                plannedWines++;
                totalUnits++;
            }
        }
        if(unit.equals("Drink")){
            if(totalUnits >= limit){
                totalUnits = limit;
            } else {
                plannedDrinks++;
                totalUnits++;
            }
        }
        if(unit.equals("Shot")){
            if(totalUnits >= limit){
                totalUnits = limit;
            } else {
                totalUnits++;
                plannedShots++;
            }
        }
    }

    private void removePlannedUnits(String unit){
        if(unit.equals("Beer")){
            if(plannedBeers == 0){
                plannedBeers = 0;
            } else {
                plannedBeers--;
                totalUnits--;
            }
        }
        if(unit.equals("Wine")){
            if(plannedWines == 0){
                plannedWines = 0;
            } else {
                plannedWines--;
                totalUnits--;
            }
        }
        if(unit.equals("Drink")){
            if (plannedDrinks == 0){
                plannedDrinks = 0;
            } else {
                plannedDrinks--;
                totalUnits--;
            }
        }
        if(unit.equals("Shot")){
            if(plannedShots == 0){
                plannedShots = 0;
            } else {
                plannedShots--;
                totalUnits--;
            }
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
        setPlannedPartyElementsDB(startTimeStamp, endTimeStamp, firstUnitAdded, plannedBeers, plannedWines, plannedDrinks, plannedShots, 0, 0, 0, 0, status);
    }

    private void setPlannedPartyElementsDB(Date sDate, Date eDate, Date fDate, int pB, int pW, int pD, int pS, int aB, int aW, int aD, int aS, String status){
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
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
        PlanPartyElements lastElement = PlanPartyList.get(PlanPartyList.size() - 1);

        plannedBeers = lastElement.getPlannedBeer();
        plannedWines = lastElement.getPlannedWine();
        plannedDrinks = lastElement.getPlannedDrink();
        plannedShots = lastElement.getPlannedShot();
        startTimeStamp = lastElement.getStartTimeStamp();
        endTimeStamp = new Date();
        firstUnitAdded = lastElement.getFirstUnitAddedDate();

        setHistory();
        handleGraphHistory();
    }

    private void statusNotRunning(){
        // Reset all values
        plannedBeers = 0;
        plannedWines = 0;
        plannedDrinks = 0;
        plannedShots = 0;
        beersConsumed = 0;
        winesConsumed = 0;
        drinksConsumed = 0;
        shotsConsumed = 0;
        startTimeStamp = null;
        endTimeStamp = null;
        firstUnitAdded = null;
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
    private DaoSession setDaoSessionDB(){
        String DB_NAME = "my-db";
        SQLiteDatabase db;
        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    private String liveUpdatePromille(double weight, String gender, Date firstUnitAddedTimeStamp){
        double sum = 0.0;
        System.out.println("s");
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
                sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(minToHours) * minToHours);
            }
            if(minToHours > 0.25){
                sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, minToHours));
            }
        }
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        return numberFormat.format(sum);
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

    private void clearPartyTables(){
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
        setPlannedPartyElementsDB(startTimeStamp, endTimeStamp, firstUnitAdded, plannedBeers, plannedWines, plannedDrinks, plannedShots, 0, 0, 0, 0, state);
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

        int tempAfterB = 0;
        int tempAfterW = 0;
        int tempAfterD = 0;
        int tempAfterS = 0;


        for(PlanPartyElements parEl : PlanPartyList){
            plannedBeers = parEl.getPlannedBeer();
            plannedWines = parEl.getPlannedWine();
            plannedDrinks = parEl.getPlannedDrink();
            plannedShots = parEl.getPlannedShot();
            startTimeStamp = parEl.getStartTimeStamp();
            endTimeStamp = parEl.getEndTimeStamp();
            firstUnitAdded = parEl.getFirstUnitAddedDate();
            if(parEl.getAftRegBeer() == null){
                tempAfterB = 0;
            } else {
                tempAfterB = parEl.getAftRegBeer();
            }
            if(parEl.getAftRegWine() == null){
                tempAfterW = 0;
            } else {
                tempAfterW = parEl.getAftRegWine();
            }
            if(parEl.getAftRegDrink() == null){
                tempAfterD = 0;
            } else {
                tempAfterD = parEl.getAftRegDrink();
            }
            if(parEl.getAftRegShot() == null){
                tempAfterS = 0;
            } else {
                tempAfterS = parEl.getAftRegShot();
            }
        }
        partyDao.deleteAll();
        setPlannedPartyElementsDB(startTimeStamp, endTimeStamp, firstUnitAdded, plannedBeers, plannedWines, plannedDrinks, plannedShots, tempAfterB, tempAfterW, tempAfterD, tempAfterS, status);
        Date tempStartDate = null;

        HistoryDao histDao = setDaoSessionDB().getHistoryDao();
        List<History> histList = histDao.queryBuilder().list();
        for(History histItems : histList){
            tempStartDate = histItems.getStartDate();
        }
        if(!startTimeStamp.equals(tempStartDate)){
            setHistory();
            handleGraphHistory();
        }
        beersConsumed = 0;
        winesConsumed = 0;
        drinksConsumed = 0;
        shotsConsumed = 0;
    }

    private int calculateCosts(int b, int w, int d, int s){
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
        int totalCosts = calculateCosts(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed);
        insertHistoryDB(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed, startTimeStamp, endTimeStamp, totalCosts, 0.0);
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

    private void simulateBAC(int id){
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
                // set minutes to hours
                double hoursToMins = tempInterval / 60;
                String tempPromille = calculateBAC(gender, weight, countingGrams(beer, wine, drink, shot), hoursToMins);
                try{
                    promille = Double.parseDouble(tempPromille);
                } catch(NumberFormatException e) {
                    promille = 0;
                }
                // Check highest BAC
                if(highestBAC < promille){
                    highestBAC = promille;
                }

                // Add 15 minutes to simulator
                tempTimeStamp = setNewUnitDate((int) tempInterval);
            }
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

    private void handleGraphHistory(){
        int historyID = fetchHistoryID_DB();
        System.out.println("Last ID added: " + historyID);
        simulateBAC(historyID);
    }

    public String textInQuote(double bac){
        String output = "";
        if(bac >= 0 && bac < 0.4){
            output = "Kos deg";
            pieChart.setCenterTextColor((Color.rgb(255, 255, 255)));
        }
        if(bac >= 0.4 && bac < 0.8){
            output = "Lykkepromille";
            pieChart.setCenterTextColor((Color.rgb(26, 193, 73)));
        }
        if(bac >= 0.8 && bac < 1.0){
            output = "Du blir mer kritikkløs og risikovillig";
            pieChart.setCenterTextColor((Color.rgb(255, 180, 10)));
        }
        if(bac >= 1.0 && bac < 1.2){
            output = "Balansen blir dårligere";
            pieChart.setCenterTextColor((Color.rgb(255, 180, 10)));
        }
        if(bac >= 1.2 && bac < 1.4){
            output = "Talen snøvlete og \nkontroll på bevegelser forverres";
            pieChart.setCenterTextColor((Color.rgb(255, 160, 0)));
        }
        if(bac >= 1.4 && bac < 1.8){
            output = "Man blir trøtt, sløv og \nkan bli kvalm";
            pieChart.setCenterTextColor((Color.rgb(255, 160, 0)));
        }
        if(bac >= 1.8 && bac < 3.0){
            output = "Hukommelsen sliter";
            pieChart.setCenterTextColor((Color.rgb(255, 55, 55)));
        }
        if(bac >= 3.0 && bac < 5.0){
            output = "Svært høy promille! \nMan kan bli bevisstløs";
            pieChart.setCenterTextColor((Color.rgb(255, 55, 55)));
        }
        if(bac >= 5.0){
            output = "Du kan dø ved en så høy promille!";
            pieChart.setCenterTextColor((Color.rgb(255, 0, 0)));
        }
        return output;
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
    * PIE CHART
    * */
    private void fillPieChart(int b, int w, int d, int s) {
        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry((float) b, 0));
        entries.add(new Entry((float) w, 1));
        entries.add(new Entry((float) d, 2));
        entries.add(new Entry((float) s, 3));

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        dataset.setDrawValues(false);
        dataset.setColors(getColors());

        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < 4; i++) labels.add("");

        PieData data = new PieData(labels, dataset); // initialize Piedata
        pieChart.setData(data);
    }

    private void stylePieChart() {
        pieChart.setCenterText(promilleBAC + PER_MILLE + "");
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

    /*
    * WIDGETS
    * */
    private void initWidgets(){
        beerLbl = (TextView) v.findViewById(R.id.textViewBeerPP);
        wineLbl = (TextView) v.findViewById(R.id.textViewWinePP);
        drinkLbl = (TextView) v.findViewById(R.id.textViewDrinkPP);
        shotLbl = (TextView) v.findViewById(R.id.textViewShotPP);

        textQuoteLbl = (TextView) v.findViewById(R.id.textViewQuotesPP);

        addBtn = (Button) v.findViewById(R.id.buttonAddPP);
        removeBtn = (Button) v.findViewById(R.id.buttonRemovePP);
        statusBtn = (Button) v.findViewById(R.id.buttonStatusPP);

        planPartyRunning_LinLay = (LinearLayout) v.findViewById(R.id.layout_planPartyRunning_ID_PP);
        dayAfterRunning_LinLay = (LinearLayout) v.findViewById(R.id.layout_dayAfterRunning_ID_PP);
    }
}