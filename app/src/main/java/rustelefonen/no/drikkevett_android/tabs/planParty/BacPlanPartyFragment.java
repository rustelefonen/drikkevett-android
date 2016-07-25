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
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
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
    private Status_DB status_DB;
    private PartyUtil partyUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_plan_party_frag, container, false);

        planPartyDB = new PlanPartyDB(getContext());
        status_DB = new Status_DB(getContext());
        partyUtil = new PartyUtil(getContext());

        initWidgets();
        setUserData();
        status = isSessionOver();
        stateHandler(status);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status.equals(Status.RUNNING)){
                    planPartyDB.addConsumedUnits(getUnitId());
                    if(!planPartyDB.isFirstUnitAdded()){
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
                removeBtnHandler();
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
        getActivity().getMenuInflater().inflate(R.menu.plan_party_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact_PP:
                NavigationUtil.navigateToContactInformation(getContext());
                return false;
            case R.id.action_clear_PP:
                if(status == Status.NOT_RUNNING){
                    emptyAllPlannedUnits();
                }
                if(status == Status.RUNNING){
                    emptyAllConsumedUnits();
                }
                stateHandler(status);
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
        addBtn.setText("Drikk");
        firstUnitAdded = planPartyDB.getFirstUnitAddedStamp();

        // calculate BAC
        if(firstUnitAdded != null){
            try{
                promilleBAC = Double.parseDouble(planPartyDB.liveUpdatePromille(weight, gender, firstUnitAdded));
                populateConsumtion();
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

        textQuoteLbl.setText(partyUtil.textQuote(promilleBAC));
        textQuoteLbl.setTextColor(partyUtil.colorQuote(promilleBAC));
        pieChart.setCenterTextColor(partyUtil.colorQuote(promilleBAC));
        colorsUnitLabels();
    }

    private void colorsUnitLabels(){
        if(plannedBeers < beersConsumed){
            beerLbl.setTextColor(Color.rgb(255, 0, 0));
        } else {
            beerLbl.setTextColor(Color.rgb(255, 255, 255));
        }
        if(plannedWines < winesConsumed){
            wineLbl.setTextColor(Color.rgb(255, 0, 0));
        } else {
            wineLbl.setTextColor(Color.rgb(255, 255, 255));
        }
        if(plannedDrinks < drinksConsumed){
            drinkLbl.setTextColor(Color.rgb(255, 0, 0));
        } else {
            drinkLbl.setTextColor(Color.rgb(255, 255, 255));
        }
        if(plannedShots < shotsConsumed){
            shotLbl.setTextColor(Color.rgb(255, 0, 0));
        } else {
            shotLbl.setTextColor(Color.rgb(255, 255, 255));
        }
    }

    private void partyNotRunning(){
        addBtn.setText("Legg til");

        textQuoteLbl.setText("Planlegg kvelden!");

        statusBtn.setText("Start Kvelden");

        // LAYOUT
        beerLbl.setText(plannedBeers + "");
        wineLbl.setText(plannedWines + "");
        drinkLbl.setText(plannedDrinks + "");
        shotLbl.setText(plannedShots + "");
        colorsUnitLabels();

        // Visibility
        dayAfterRunning_LinLay.setVisibility(View.GONE);
        planPartyRunning_LinLay.setVisibility(View.VISIBLE);

        // pie chart
        pieChart = (PieChart) v.findViewById(R.id.pie_chart_bac_plan_party);
        fillPieChart(plannedBeers, plannedWines, plannedDrinks, plannedShots);
        stylePieChart();

        textQuoteLbl.setText(partyUtil.textQuote(promilleBAC));
        textQuoteLbl.setTextColor(partyUtil.colorQuote(promilleBAC));
        pieChart.setCenterTextColor(partyUtil.colorQuote(promilleBAC));
    }

    private void dayAfterRunning(){
        statusBtn.setText("Avslutt Dagen Derpå");
        dayAfterRunning_LinLay.setVisibility(View.VISIBLE);
        planPartyRunning_LinLay.setVisibility(View.GONE);
    }

    /*
    * ADDING UNITS ( CONSUMED AND PLANNED )
    * */

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

    private void removeConsumedUnits(String unit){
        if(unit.equals("Beer")){
            if(beersConsumed == 0){
                beersConsumed = 0;
            } else {
                beersConsumed--;
                removeUnitConsumedDB(unit);
            }
        }
        if(unit.equals("Wine")){
            if(winesConsumed == 0){
                winesConsumed = 0;
            } else {
                winesConsumed--;
                removeUnitConsumedDB(unit);
            }
        }
        if(unit.equals("Drink")){
            if (drinksConsumed == 0){
                drinksConsumed = 0;
            } else {
                drinksConsumed--;
                removeUnitConsumedDB(unit);
            }
        }
        if(unit.equals("Shot")){
            if(shotsConsumed == 0){
                shotsConsumed = 0;
            } else {
                shotsConsumed--;
                removeUnitConsumedDB(unit);
            }
        }
    }

    private void removeUnitConsumedDB(String unit){
        DayAfterBACDao dayAfterBACDao = setDaoSessionDB().getDayAfterBACDao();
        List<DayAfterBAC> dayAfterBACList = dayAfterBACDao.queryBuilder().where(DayAfterBACDao.Properties.Unit.eq(unit)).list();
        DayAfterBAC lastElement = dayAfterBACList.get(dayAfterBACList.size() - 1);
        dayAfterBACDao.deleteByKey(lastElement.getId());
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
        planPartyDB.setPlannedPartyElementsDB(startTimeStamp, endTimeStamp, firstUnitAdded, plannedBeers, plannedWines, plannedDrinks, plannedShots, 0, 0, 0, 0, status);
    }

    private void statusRunning(PlanPartyElementsDao partyDao){
        // start og end timestamp blir satt
        startTimeStamp = new Date();

        // set sessionLength in the parameter
        endTimeStamp = setEndOfSesStamp(15);

        List<PlanPartyElements> PlanPartyList = partyDao.queryBuilder().list();

        for (PlanPartyElements party : PlanPartyList) {
            if(party.getFirstUnitAddedDate() != null){
                firstUnitAdded = party.getFirstUnitAddedDate();
            }
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

    private void emptyAllPlannedUnits(){
        plannedBeers = 0;
        plannedWines = 0;
        plannedDrinks = 0;
        plannedShots = 0;
    }

    private void emptyAllConsumedUnits(){
        beersConsumed = 0;
        winesConsumed = 0;
        drinksConsumed = 0;
        shotsConsumed = 0;

        SuperDao superDao = new SuperDao(getContext());
        DayAfterBACDao dayAfterBACDao = superDao.getDayAfterBACDao();
        dayAfterBACDao.deleteAll();
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

    private void removeBtnHandler(){
        String textOnBtn = (String) statusBtn.getText();
        if(textOnBtn.equals("Avslutt Kvelden")){
            removeConsumedUnits(getUnitId());
        }
        if(textOnBtn.equals("Start Kvelden")){
            removePlannedUnits(getUnitId());
        }
        stateHandler(status);
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

    private void populateConsumtion(){
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
        }
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
        firstUnitAdded = null;
        startTimeStamp = null;
        endTimeStamp = null;
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
        planPartyDB.setPlannedPartyElementsDB(startTimeStamp, endTimeStamp, firstUnitAdded, plannedBeers, plannedWines, plannedDrinks, plannedShots, 0, 0, 0, 0, state);
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
        SuperDao superDao = new SuperDao(getContext());

        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();
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
        if(firstUnitAdded != null){
            planPartyDB.setPlannedPartyElementsDB(startTimeStamp, endTimeStamp, firstUnitAdded, plannedBeers, plannedWines, plannedDrinks, plannedShots, tempAfterB, tempAfterW, tempAfterD, tempAfterS, status);
        }
        Date tempStartDate = null;

        superDao.close();

        SuperDao superDao2 = new SuperDao(getContext());
        HistoryDao histDao = superDao2.getHistoryDao();
        List<History> histList = histDao.queryBuilder().list();
        History lastElementHist = histList.get(histList.size() - 1);
        tempStartDate = lastElementHist.getStartDate();

        if(!startTimeStamp.equals(tempStartDate)){
            setHistory();
            handleGraphHistory();
        }
        superDao2.close();
    }

    private int calculateCosts(int b, int w, int d, int s){
        User user = ((MainActivity)getActivity()).getUser();
        return (b * user.getBeerPrice()) + (w * user.getWinePrice()) + (d * user.getDrinkPrice()) + (s * user.getShotPrice());
    }

    private void setHistory(){
        populateConsumtion();
        int totalCosts = calculateCosts(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed);
        planPartyDB.insertHistoryDB(beersConsumed, winesConsumed, drinksConsumed, shotsConsumed, startTimeStamp, endTimeStamp, totalCosts, 0.0);
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
            planPartyDB.addGraphValues(promille, tempTimeStamp, id);
        }
        planPartyDB.updateHighestBac(highestBAC);
    }

    private void handleGraphHistory(){
        simulateBAC(planPartyDB.fetchHistoryID_DB());
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