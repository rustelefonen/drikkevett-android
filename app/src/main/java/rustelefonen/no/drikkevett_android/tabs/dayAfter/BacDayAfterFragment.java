package rustelefonen.no.drikkevett_android.tabs.dayAfter;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.GraphHistory;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.util.NavigationUtil;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;

import static rustelefonen.no.drikkevett_android.util.DateUtil.setNewUnitDate;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.addMinsToDate;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.calculateBAC;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.countingGrams;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.setGenderScore;

public class BacDayAfterFragment extends Fragment {
    private double weight = 0;
    private String gender = "";

    private int consumBeers = 0, consumWines = 0, consumDrink = 0, consumShots = 0, planBeers = 0, planWines = 0, planDrink = 0, planShots = 0, totalConsumed = 0;

    private int afterRegBeer = 0;
    private int afterRegWine = 0;
    private int afterRegDrink = 0;
    private int afterRegShot = 0;

    private int costs = 0;
    private String currentBAC = "";
    private double highestBAC = 0.0;

    public Status status;

    public Date startStamp = new Date(), endStamp = new Date();

    /*
    * WIDGETS
    * */
    private TextView beerLbl, wineLbl, drinkLbl, shotLbl, costsLbl, highBACLbl, currBACLbl;

    private TextView afterRegBeerLbl, afterRegWineLbl, afterRegDrinkLbl, afterRegShotLbl;

    private Button btnEndDA, beerBtnAfterReg_DA, wineBtnAfterReg_DA, drinkBtnAfterReg_DA, shotBtnAfterReg_DA;

    private LinearLayout planPaRunning_LinLay, dayAfterRunning_LinLay;

    private TextView txtView;
    private SeekBar seekBar;
    private int hours = 0, tempMins = 0;

    public View v;

    public PieChart pieChart;
    private static final String PER_MILLE = "\u2030";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_day_after_frag, container, false);
        initWidgets();
        setUserData();

        status = getStatus();
        System.out.println("Status DA: " + status);
        statusHandler(status);

        btnEndDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showAlert();
            }
        });

        // REGISTRATION

        // beer
        beerBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterPopUp("Beer");
            }
        });

        // wine
        wineBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterPopUp("Wine");
            }
        });

        // drink
        drinkBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterPopUp("Drink");
            }
        });

        // shot
        shotBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterPopUp("Shot");
            }
        });

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
        // hente status fra db
        status = getStatus();
        statusHandler(status);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!this.isVisible()) return;
        if (!isVisibleToUser) return;

        setUserData();
        status = getStatus();
        statusHandler(status);
    }

    /*
    * STATUS
    * */

    private void statusHandler(Status state){
        if(state == status.RUNNING || state == status.NOT_RUNNING || state == status.DEFAULT){
            planPartyRunning();
        }
        if(state == status.DA_RUNNING){
            dayAfterRunning();
        }
    }

    private enum Status {
        RUNNING, NOT_RUNNING, DA_RUNNING, DEFAULT
    }

    private Status getStatus(){
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
        // For å sjekke om listen er tom
        if(partyList.size() == 0){
            System.out.println("Party listen er tom! ");
        }
        return tempStatus;
    }

    private void planPartyRunning(){
        setVisualsPP();
    }

    private void dayAfterRunning(){
        getUnits();
        setStats();

        // pie chart
        pieChart = (PieChart) v.findViewById(R.id.pie_chart_bac_day_after);
        fillPieChart();
        stylePieChart();

        setVisualsDA();
    }

    private void clearUnitVariabels(){
        planBeers = 0;
        planWines = 0;
        planDrink = 0;
        planShots = 0;
        consumBeers = 0;
        consumWines = 0;
        consumDrink = 0;
        consumShots = 0;
        costs = 0;
    }

    /*
    * DATABASE COMMUNICATION METHODS
    * */

    private DaoSession setDaoSessionDB(){
        String DB_NAME = "my-db";
        SQLiteDatabase db;
        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }

    private void setUserData(){
        User user = ((MainActivity)getActivity()).getUser();
        weight = user.getWeight();
        gender = user.getGender();
    }

    private Date getFirstUnAddedStamp(){
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        List<PlanPartyElements> planPList = partyDao.queryBuilder().list();
        PlanPartyElements lastElement = planPList.get(planPList.size() - 1);
        return lastElement.getFirstUnitAddedDate();
    }

    private void getUnits(){
        clearUnitVariabels();

        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = setDaoSessionDB().getDayAfterBACDao();

        // GET elements to temporary store them in variables then re-saving them
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
        for (PlanPartyElements party : partyList) {
            planBeers = party.getPlannedBeer();
            planWines = party.getPlannedWine();
            planDrink = party.getPlannedDrink();
            planShots = party.getPlannedShot();
            if(party.getAftRegBeer() == null){
                afterRegBeer = 0;
            } else {
                afterRegBeer = party.getAftRegBeer();
            }
            if(party.getAftRegWine() == null){
                afterRegWine = 0;
            } else {
                afterRegWine = party.getAftRegWine();
            }
            if(party.getAftRegDrink() == null){
                afterRegDrink = 0;
            } else {
                afterRegDrink = party.getAftRegDrink();
            }
            if(party.getAftRegShot() == null){
                afterRegShot = 0;
            } else {
                afterRegShot = party.getAftRegShot();
            }
            System.out.println("After Reg beer? ffs" + party.getAftRegBeer());
        }

        List<DayAfterBAC> dayAfterList = dayAfterDao.queryBuilder().list();
        for(DayAfterBAC dayAfter : dayAfterList){
            String unit = dayAfter.getUnit();
            if(unit.equals("Beer")){
                consumBeers++;
            }
            if(unit.equals("Wine")){
                consumWines++;
            }
            if(unit.equals("Drink")){
                consumDrink++;
            }
            if(unit.equals("Shot")){
                consumShots++;
            }
        }
        totalConsumed = consumBeers + consumWines + consumDrink + consumShots;
    }

    private double getHighestBAC(){
        double tempVal = 0.0;
        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();
        if(historyDao == null){
            System.out.println("HISTORIE TOM");
        } else {
            List<History> histList = historyDao.queryBuilder().list();
            History lastElement = histList.get(histList.size() -1);
            tempVal = lastElement.getHighestBAC();
        }
        return tempVal;
    }

    private double getCurrentBAC(double weight, String gender, Date firstUnitAddedTimeStamp){
        double sum = 0.0;
        consumBeers = 0;
        consumWines = 0;
        consumDrink = 0;
        consumShots = 0;

        DayAfterBACDao dayAfterDao = setDaoSessionDB().getDayAfterBACDao();
        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();
        for (DayAfterBAC dayAfter : dayAfterBACList) {
            if(dayAfter.getUnit().equals("Beer")){
                consumBeers++;
            }
            if(dayAfter.getUnit().equals("Wine")){
                consumWines++;
            }
            if(dayAfter.getUnit().equals("Drink")){
                consumDrink++;
            }
            if(dayAfter.getUnit().equals("Shot")){
                consumShots++;
            }
            double totalGrams = countingGrams(consumBeers, consumWines, consumDrink, consumShots);
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
                try {
                    sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(minToHours) * minToHours);
                } catch (NumberFormatException e){
                    sum = 0;
                }
            }
            if(minToHours > 0.25){
                try{
                    sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, minToHours));
                } catch (NumberFormatException e){
                    sum = 0;
                }
            }
        }
        return sum;
    }

    private void clearDBTables(){
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = setDaoSessionDB().getDayAfterBACDao();

        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();
        for (DayAfterBAC dayAfter : dayAfterBACList) {
            System.out.println("(clearTables) units = " + dayAfter.getUnit());
        }

        // clearing the tables (PlanPartyDao and DayAfterBacDao)
        partyDao.deleteAll();
        dayAfterDao.deleteAll();

        // Clear variabels planned and consumed
        clearUnitVariabels();

        // Set state
        status = Status.NOT_RUNNING;

        // Inserting new planned elements
        PlanPartyElements newParty = new PlanPartyElements();
        newParty.setPlannedBeer(0);
        newParty.setPlannedWine(0);
        newParty.setPlannedDrink(0);
        newParty.setPlannedShot(0);
        newParty.setAftRegBeer(0);
        newParty.setAftRegWine(0);
        newParty.setAftRegDrink(0);
        newParty.setAftRegShot(0);
        newParty.setStatus(status.toString());
        partyDao.insert(newParty);
    }

    public int calculateCosts(int b, int w, int d, int s){
        User user = ((MainActivity)getActivity()).getUser();
        return (b * user.getBeerPrice()) + (w * user.getWinePrice()) + (d * user.getDrinkPrice()) + (s * user.getShotPrice());
    }

    private void setStats(){
        costs = calculateCosts(consumBeers, consumWines, consumDrink, consumShots);
        if(getFirstUnAddedStamp() != null){
            DecimalFormat numberFormat = new DecimalFormat("#.##");
            currentBAC = numberFormat.format(getCurrentBAC(weight, gender, getFirstUnAddedStamp()));
        }
        highestBAC = getHighestBAC();
    }

    /*
    * REGISTRATION ( OF FORGOTTEN UNITS )
    * */

    private int setMaxSeekBarVal(){
        int intervallHours = 0;

        // Get length of session ( interval )

        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();

        PlanPartyElements lastElement = partyList.get(partyList.size() -1);
        Date endS = lastElement.getEndTimeStamp();
        Date startS = lastElement.getStartTimeStamp();

        long timeDiff = getDateDiff(startS, endS, TimeUnit.MINUTES);
        System.out.println("End Stamp: " + endS + " Start Stamp: " + startS);
        System.out.println("Difference = " + timeDiff);

        double timeDiffToDouble = (double) timeDiff;

        // max være 24
        double convertToHours = timeDiffToDouble / 60;
        System.out.println("Timer: " + convertToHours);

        if(convertToHours < 0.5){
            intervallHours = 0;
        }
        if(convertToHours > 0.5 && convertToHours < 1){
            intervallHours = 1;
        }
        if(convertToHours > 1 && convertToHours < 1.5){
            intervallHours = 2;
        }
        if(convertToHours > 1.5 && convertToHours < 2){
            intervallHours = 3;
        }
        if(convertToHours > 2 && convertToHours < 2.5){
            intervallHours = 4;
        }
        if(convertToHours > 2.5 && convertToHours < 3){
            intervallHours = 5;
        }
        if(convertToHours > 3 && convertToHours < 3.5){
            intervallHours = 6;
        }
        if(convertToHours > 3.5 && convertToHours < 4){
            intervallHours = 7;
        }
        if(convertToHours > 4 && convertToHours < 4.5){
            intervallHours = 8;
        }
        if(convertToHours > 4.5 && convertToHours < 5){
            intervallHours = 9;
        }
        if(convertToHours > 5 && convertToHours < 5.5){
            intervallHours = 10;
        }
        if(convertToHours > 5.5 && convertToHours < 6){
            intervallHours = 11;
        }
        if(convertToHours > 5.5 && convertToHours < 6){
            intervallHours = 12;
        }
        if(convertToHours > 6 && convertToHours < 6.5){
            intervallHours = 13;
        }
        if(convertToHours > 6.5 && convertToHours < 7){
            intervallHours = 14;
        }
        if(convertToHours > 7 && convertToHours < 7.5){
            intervallHours = 15;
        }
        if(convertToHours > 7.5 && convertToHours < 8){
            intervallHours = 16;
        }
        if(convertToHours > 8 && convertToHours < 8.5){
            intervallHours = 17;
        }
        if(convertToHours > 8.5 && convertToHours < 9){
            intervallHours = 18;
        }
        if(convertToHours > 9 && convertToHours < 9.5){
            intervallHours = 19;
        }
        if(convertToHours > 9.5 && convertToHours < 10){
            intervallHours = 20;
        }
        if(convertToHours > 10 && convertToHours < 10.5){
            intervallHours = 21;
        }
        if(convertToHours > 10.5 && convertToHours < 11){
            intervallHours = 22;
        }
        if(convertToHours > 11 && convertToHours < 11.5){
            intervallHours = 23;
        }
        if(convertToHours > 11.5 && convertToHours < 12){
            intervallHours = 24;
        }
        return intervallHours;
    }

    private int configSeekBar(int hours){
        int minutes = 0;
        if(hours == 0){ txtView.setText("0"); minutes = 0; }
        if(hours == 1){ txtView.setText(30 + " m"); minutes = 30; }
        if(hours == 2){ txtView.setText(1 + " t"); minutes = 60; }
        if(hours == 3){ txtView.setText(1 + " t " + 30 + " m"); minutes = 90; }
        if(hours == 4){ txtView.setText(2 + " t "); minutes = 120;}
        if(hours == 5){ txtView.setText(2 + " t " + 30 + " m"); minutes = 150; }
        if(hours == 6){ txtView.setText(3 + " t "); minutes = 180; }
        if(hours == 7){ txtView.setText(3 + " t " + 30 + " m"); minutes = 210; }
        if(hours == 8){ txtView.setText(4 + " t "); minutes = 240; }
        if(hours == 9){ txtView.setText(4 + " t " + 30 + " m"); minutes = 270; }
        if(hours == 10){ txtView.setText(5 + " t "); minutes = 300; }
        if(hours == 11){ txtView.setText(5 + " t " + 30 + " m"); minutes = 330; }
        if(hours == 12){ txtView.setText(6 + " t"); minutes = 360; }
        if(hours == 13){ txtView.setText(6 + " t " + 30 + " m"); minutes = 390; }
        if(hours == 14){ txtView.setText(7 + " t"); minutes = 420; }
        if(hours == 15){ txtView.setText(7 + " t " + 30 + " m"); minutes = 450; }
        if(hours == 16){ txtView.setText(8 + " t"); minutes = 480; }
        if(hours == 17){ txtView.setText(8 + " t " + 30 + " m"); minutes = 510; }
        if(hours == 18){ txtView.setText(9 + " t"); minutes = 540; }
        if(hours == 19){ txtView.setText(9 + " t " + 30 + " m"); minutes = 570; }
        if(hours == 20){ txtView.setText(10 + " t"); minutes = 600; }
        if(hours == 21){ txtView.setText(10 + " t " + 30 + " m"); minutes = 630; }
        if(hours == 22){ txtView.setText(11 + " t"); minutes = 660; }
        if(hours == 23){ txtView.setText(11 + " t " + 30 + " m"); minutes = 690; }
        if(hours == 24){ txtView.setText(12 + " t"); minutes = 720; }
        return  minutes;
    }

    private void addForgottenUnit(String unit, int time){
        // add time to startTimeStamp to find out when this unit was added
        Date newUnitStamp = setNewUnitDate(time);
        System.out.println("New Unit (UnitType: " + unit + "), and timeStamp: " + newUnitStamp);

        // add one more unit to history table ( beer/wine/drink/shot )
        updateUnitInHistory(unit);

        // add unit to day after
        addDayAfter(newUnitStamp, unit);

        // refresh graphHistory, by removing all the last values and adding them again with the new unit added
        refreshGraphHist();

        // add number of after registered units
        PlanPartyElementsDao partyDao = setDaoSessionDB().getPlanPartyElementsDao();
        List<PlanPartyElements> plaList = partyDao.queryBuilder().list();
        PlanPartyElements getLastElement = plaList.get(plaList.size() - 1);

        afterRegBeer = 0;
        afterRegWine = 0;
        afterRegDrink = 0;
        afterRegShot = 0;

        // add unitToVariable
        if(unit.equals("Beer")){
            consumBeers++;
            if(getLastElement.getAftRegBeer() == null){
                afterRegBeer = 0;
            } else {
                afterRegBeer = getLastElement.getAftRegBeer();
            }
            afterRegBeer += 1;
            getLastElement.setAftRegBeer(afterRegBeer);
        }
        if(unit.equals("Wine")){
            consumWines++;
            if(getLastElement.getAftRegWine() == null){
                afterRegWine = 0;
            } else {
                afterRegWine = getLastElement.getAftRegWine();
            }
            afterRegWine++;
            getLastElement.setAftRegWine(afterRegWine);
        }
        if(unit.equals("Drink")){
            consumDrink++;
            if(getLastElement.getAftRegDrink() == null){
                afterRegDrink = 0;
            } else {
                afterRegDrink = getLastElement.getAftRegDrink();
            }
            afterRegDrink++;
            getLastElement.setAftRegDrink(afterRegDrink);
        }
        if(unit.equals("Shot")){
            consumShots++;
            if(getLastElement.getAftRegShot() == null){
                afterRegShot = 0;
            } else {
                afterRegShot = getLastElement.getAftRegShot();
            }
            afterRegShot++;
            getLastElement.setAftRegShot(afterRegShot);
        }

        partyDao.insertOrReplace(getLastElement);

        // refresh all visuals
        dayAfterRunning();
    }

    private void refreshGraphHist(){
        // REMOVE ALL LATEST GRAPH HIST WITH SAME ID AS HISTORY ID AND TEMP STORE THE TIMESTAMPS
        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);
        System.out.println("Last Element ID: " + lastElement.getId());

        GraphHistoryDao graphHistoryDao = setDaoSessionDB().getGraphHistoryDao();
        List<GraphHistory> graphHistList = graphHistoryDao.queryBuilder().where(GraphHistoryDao.Properties.HistoryId.eq(lastElement.getId())).list();

        System.out.println("refreshGraphHist: ------: ");
        for(GraphHistory graphHist : graphHistList){
            graphHistoryDao.deleteByKey(graphHist.getId());
        }

        // ADD NEW VALUES IN GRAPH HIST WITH THE AFTER REGISTRATED TIME STAMP
        long id = lastElement.getId();
        calcPr((int) id);
    }

    private void addDayAfter(Date newUnitStamp, String unitType){
        DayAfterBACDao dayAfterBACDao = setDaoSessionDB().getDayAfterBACDao();
        DayAfterBAC newDayAfter = new DayAfterBAC();
        newDayAfter.setTimestamp(newUnitStamp);
        newDayAfter.setUnit(unitType);
        dayAfterBACDao.insert(newDayAfter);
    }

    private void addGraphValues(double currentBAC, Date timeStamp, int id){
        GraphHistoryDao graphHistoryDao = setDaoSessionDB().getGraphHistoryDao();
        GraphHistory newGraphVal = new GraphHistory();
        newGraphVal.setHistoryId(id);
        newGraphVal.setCurrentBAC(currentBAC);
        newGraphVal.setTimestamp(timeStamp);
        graphHistoryDao.insert(newGraphVal);
    }

    private void setStartAndEndStamp(){
        PlanPartyElementsDao planDao = setDaoSessionDB().getPlanPartyElementsDao();
        List<PlanPartyElements> planPList = planDao.queryBuilder().list();
        PlanPartyElements element = planPList.get(planPList.size() - 1);
        startStamp = element.getStartTimeStamp();
        endStamp = element.getEndTimeStamp();
    }

    private void calcPr(int id){
        // Loop gjennom DayAfterBAC. Simuler hva promillen var hvert kvarter/halvtime
        DayAfterBACDao dayAfterBACDao = setDaoSessionDB().getDayAfterBACDao();
        List<DayAfterBAC> dayAfterBacList = dayAfterBACDao.queryBuilder().list();

        double highestBAC = 0.0;
        double promille = 0.0;
        Date tempTimeStamp = new Date();

        // GET START AND END STAMP
        setStartAndEndStamp();

        double sessionInterval = getDateDiff(startStamp, endStamp, TimeUnit.MINUTES);
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

                double hoursToMins = tempInterval / 60;

                String tempPromille = calculateBAC(gender, weight, countingGrams(beer, wine, drink, shot), hoursToMins);
                promille = Double.parseDouble(tempPromille);

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
        updateHighestBACinHistory(highestBAC);
        startStamp = null;
        endStamp = null;
        printLastGraphValues();
    }

    private void printLastGraphValues(){
        // REMOVE ALL LATEST GRAPH HIST WITH SAME ID AS HISTORY ID AND TEMP STORE THE TIMESTAMPS
        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);

        GraphHistoryDao graphHistoryDao = setDaoSessionDB().getGraphHistoryDao();
        List<GraphHistory> graphHistList = graphHistoryDao.queryBuilder().where(GraphHistoryDao.Properties.HistoryId.eq(lastElement.getId())).list();

        System.out.println("Last hist id: (" + lastElement.getId() + ")");
        for(GraphHistory graphHist : graphHistList){
            System.out.println("graph value id: " + graphHist.getId());
            System.out.println("graph value history_id: " + graphHist.getHistoryId());
            System.out.println("graph value timeStamp: " + graphHist.getTimestamp());
            System.out.println("graph value currentBac: " + graphHist.getCurrentBAC());
        }
    }

    private void updateHighestBACinHistory(double highBac){
        // update highest BAC in History
        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);
        lastElement.setHighestBAC(highBac);
        historyDao.insertOrReplace(lastElement);
    }

    private void updateUnitInHistory(String unit){
        // update highest BAC in History
        HistoryDao historyDao = setDaoSessionDB().getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);
        System.out.println("Siste Element i DB History: " + lastElement.getId());

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
    }

    private void afterPopUp(final String unit){
        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_day_a_pop_up);
        dialog.setTitle("Title...");

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogCancel = (Button) dialog.findViewById(R.id.dialogCancel);

        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tempMins == 0){
                    Toast.makeText(getContext(), "Kan ikke legge til med verdi 0!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    addForgottenUnit(unit, tempMins); // tempMins
                    Toast.makeText(getContext(), unit + " lagt til", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);
        txtView = (TextView) dialog.findViewById(R.id.txtViewTesting);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < 1 && i > 24) hours = 1;
                hours = i;
                seekBar.setMax(setMaxSeekBarVal());
                tempMins = configSeekBar(hours);
                System.out.println("Temp Mins: " + tempMins);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        dialog.show();
    }

    private void showAlert(){
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(getContext());
        alert_builder.setMessage("Er du sikker på at du vil avslutte dagen derpå? ").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("Ok");
                clearDBTables();
                statusHandler(status);
            }
        }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("Avbryt");
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

    private void fillPieChart() {
        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry((float) consumBeers, 0));
        entries.add(new Entry((float) consumWines, 1));
        entries.add(new Entry((float) consumDrink, 2));
        entries.add(new Entry((float) consumShots, 3));

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        dataset.setDrawValues(false);
        dataset.setColors(getColors());

        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < 4; i++) labels.add("");

        PieData data = new PieData(labels, dataset); // initialize Piedata
        pieChart.setData(data);
    }

    private void stylePieChart() {
        pieChart.setCenterText(totalConsumed + "\nTOTAL");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(70f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterTextRadiusPercent(100f);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setDescription("");
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.setCenterTextSize(20.0f);
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

    private void setVisualsPP(){
        clearUnitVariabels();
        btnEndDA.setVisibility(View.GONE);
        planPaRunning_LinLay.setVisibility(View.VISIBLE);
        dayAfterRunning_LinLay.setVisibility(View.GONE);
    }

    private void setVisualsDA(){
        beerLbl.setText(consumBeers + "");
        wineLbl.setText(consumWines + "");
        drinkLbl.setText(consumDrink + "");
        shotLbl.setText(consumShots + "");
        costsLbl.setText(costs + ",-");
        highBACLbl.setText(highestBAC + "");
        currBACLbl.setText(currentBAC + "");
        afterRegBeerLbl.setText(afterRegBeer + "");
        afterRegWineLbl.setText(afterRegWine + "");
        afterRegDrinkLbl.setText(afterRegDrink + "");
        afterRegShotLbl.setText(afterRegShot + "");

        btnEndDA.setVisibility(View.VISIBLE);
        planPaRunning_LinLay.setVisibility(View.GONE);
        dayAfterRunning_LinLay.setVisibility(View.VISIBLE);
    }

    private void initWidgets(){
        beerLbl = (TextView) v.findViewById(R.id.txtViewBeerDA);
        wineLbl = (TextView) v.findViewById(R.id.txtViewWineDA);
        drinkLbl = (TextView) v.findViewById(R.id.txtViewDrinkDA);
        shotLbl = (TextView) v.findViewById(R.id.txtViewShotDA);

        costsLbl = (TextView) v.findViewById(R.id.txtViewCosts_DA);
        highBACLbl = (TextView) v.findViewById(R.id.txtViewHighestBAC_DA);
        currBACLbl = (TextView) v.findViewById(R.id.txtViewCurrBAC_DA);

        btnEndDA = (Button) v.findViewById(R.id.btnEndDayAfter);
        beerBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegBeer_DA);
        wineBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegWine_DA);
        drinkBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegDrink_DA);
        shotBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegShot_DA);

        afterRegBeerLbl = (TextView) v.findViewById(R.id.txtViewAfterRegBeerUnit_DA);
        afterRegWineLbl = (TextView) v.findViewById(R.id.txtViewAfterRegWineUnit_DA);
        afterRegDrinkLbl = (TextView) v.findViewById(R.id.txtViewAfterRegDrinkUnit_DA);
        afterRegShotLbl = (TextView) v.findViewById(R.id.txtViewAfterRegShotUnit_DA);

        planPaRunning_LinLay = (LinearLayout) v.findViewById(R.id.planP_running_layout_DA);
        dayAfterRunning_LinLay = (LinearLayout) v.findViewById(R.id.dayAfterRunning_layout_ID);
    }
}