package rustelefonen.no.drikkevett_android.tabs.dayAfter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.GraphHistory;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.util.NavigationUtil;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import static rustelefonen.no.drikkevett_android.util.DateUtil.setNewUnitDate;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.addMinsToDate;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.calculateBAC;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.countingGrams;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;

public class BacDayAfterFragment extends Fragment {
    private double weight = 0;
    private String gender = "";

    private int consumBeers = 0, consumWines = 0, consumDrink = 0, consumShots = 0, planBeers = 0, planWines = 0, planDrink = 0, planShots = 0, totalConsumed = 0;
    private int afterRegBeer = 0, afterRegWine = 0, afterRegDrink = 0, afterRegShot = 0;

    private int costs = 0;
    private String currentBAC = "";
    private double highestBAC = 0.0;

    private Status status;

    private Date startStamp = new Date(), endStamp = new Date();

    private TextView beerLbl, wineLbl, drinkLbl, shotLbl, costsLbl, highBACLbl, currBACLbl;
    private TextView afterRegBeerLbl, afterRegWineLbl, afterRegDrinkLbl, afterRegShotLbl;
    private Button btnEndDA, beerBtnAfterReg_DA, wineBtnAfterReg_DA, drinkBtnAfterReg_DA, shotBtnAfterReg_DA;
    private LinearLayout planPaRunning_LinLay, dayAfterRunning_LinLay;

    private TextView txtView;
    private SeekBar seekBar;
    private int hours = 0, tempMins = 0;

    public View v;

    private PieChart pieChart;

    private DayAfter_DB dayAfter_db;

    private static final String PER_MILLE = "\u2030";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_day_after_frag, container, false);

        dayAfter_db = new DayAfter_DB(getContext());
        initWidgets();
        setUserData();

        status = getStatus();
        statusHandler(status);

        btnEndDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showAlert();
            }
        });

        // REGISTRATION
        beerBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterPopUp("Beer");
            }
        });
        wineBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterPopUp("Wine");
            }
        });
        drinkBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterPopUp("Drink");
            }
        });
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

    private void statusHandler(Status state){
        if(state == Status.RUNNING || state == Status.NOT_RUNNING || state == Status.DEFAULT){
            planPartyRunning();
        }
        if(state == Status.DA_RUNNING){
            dayAfterRunning();
        }
    }

    private enum Status {
        RUNNING, NOT_RUNNING, DA_RUNNING, DEFAULT
    }

    private Status getStatus(){
        Status tempStatus = Status.DEFAULT;
        SuperDao superDao = new SuperDao(getContext());
        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();

        // GET elements to temporary store them in variables then re-saving them
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
        superDao.close();
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
        if(partyList.size() == 0){}
        return tempStatus;
    }

    private void planPartyRunning(){
        setVisualsPP();
    }

    private void dayAfterRunning(){
        getUnits();
        setStats();
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

    private void colorsUnitLabels(){
        if(planBeers < consumBeers){
            beerLbl.setTextColor(Color.rgb(255, 0, 0));
        } else {
            beerLbl.setTextColor(Color.rgb(255, 255, 255));
        }
        if(planWines < consumWines){
            wineLbl.setTextColor(Color.rgb(255, 0, 0));
        } else {
            wineLbl.setTextColor(Color.rgb(255, 255, 255));
        }
        if(planDrink < consumDrink){
            drinkLbl.setTextColor(Color.rgb(255, 0, 0));
        } else {
            drinkLbl.setTextColor(Color.rgb(255, 255, 255));
        }
        if(planShots < consumShots){
            shotLbl.setTextColor(Color.rgb(255, 0, 0));
        } else {
            shotLbl.setTextColor(Color.rgb(255, 255, 255));
        }
    }

    /*
    * DATABASE COMMUNICATION METHODS
    * */

    private void setUserData(){
        User user = ((MainActivity)getActivity()).getUser();
        weight = user.getWeight();
        gender = user.getGender();
    }

    private void getUnits(){
        clearUnitVariabels();
        SuperDao superDao = new SuperDao(getContext());
        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = superDao.getDayAfterBACDao();

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
        superDao.close();
        totalConsumed = consumBeers + consumWines + consumDrink + consumShots;
    }

    private double getHighestBAC(){
        double tempVal = 0.0;
        SuperDao superDao = new SuperDao(getContext());
        HistoryDao historyDao = superDao.getHistoryDao();
        if(historyDao != null){
            List<History> histList = historyDao.queryBuilder().list();
            History lastElement = histList.get(histList.size() -1);
            tempVal = lastElement.getHighestBAC();
        }
        superDao.close();
        return tempVal;
    }

    private double getCurrentBAC(double weight, String gender, Date firstUnitAddedTimeStamp){
        double sum = 0.0;
        int totalUnits = 0;
        consumBeers = 0;
        consumWines = 0;
        consumDrink = 0;
        consumShots = 0;

        SuperDao superDao = new SuperDao(getContext());
        DayAfterBACDao dayAfterDao = superDao.getDayAfterBACDao();
        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();
        superDao.close();
        for (DayAfterBAC dayAfter : dayAfterBACList) {
            if(dayAfter.getUnit().equals("Beer")){
                consumBeers++;
                totalUnits++;
            }
            if(dayAfter.getUnit().equals("Wine")){
                consumWines++;
                totalUnits++;
            }
            if(dayAfter.getUnit().equals("Drink")){
                consumDrink++;
                totalUnits++;
            }
            if(dayAfter.getUnit().equals("Shot")){
                consumShots++;
                totalUnits++;
            }
            double totalGrams = countingGrams(consumBeers, consumWines, consumDrink, consumShots);
            Date currentDate = new Date();
            long timeDifference = getDateDiff(firstUnitAddedTimeStamp, currentDate, TimeUnit.MINUTES);
            double newValueDouble = (double)timeDifference;
            double minToHours = newValueDouble / 60;

            // FROM 0 - 15 MIN
            if(minToHours <= 0.25){
                try {
                    sum = PartyUtil.intervalCalc2(minToHours, totalUnits);
                    //sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(minToHours) * minToHours);
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
        SuperDao superDao = new SuperDao(getContext());
        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = superDao.getDayAfterBACDao();

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
        newParty.setFirstUnitAddedDate(null);
        newParty.setStartTimeStamp(null);
        newParty.setEndTimeStamp(null);
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
        superDao.close();
    }

    public int calculateCosts(int b, int w, int d, int s){
        User user = ((MainActivity)getActivity()).getUser();
        return (b * user.getBeerPrice()) + (w * user.getWinePrice()) + (d * user.getDrinkPrice()) + (s * user.getShotPrice());
    }

    private void setStats(){
        costs = calculateCosts(consumBeers, consumWines, consumDrink, consumShots);
        if(dayAfter_db.getFirstUnAddedStamp() != null){
            DecimalFormat numberFormat = new DecimalFormat("#.##");
            currentBAC = numberFormat.format(getCurrentBAC(weight, gender, dayAfter_db.getFirstUnAddedStamp()));
        }
        highestBAC = getHighestBAC();
    }

    /*
    * REGISTRATION ( OF FORGOTTEN UNITS )
    * */

    private int setMaxSeekBarVal(){
        int intervallHours = 0;

        // Get length of session ( interval )
        SuperDao superDao = new SuperDao(getContext());
        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
        superDao.close();
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
        dayAfter_db.updateUnitInHistory(unit);

        // add unit to day after
        dayAfter_db.addDayAfter(newUnitStamp, unit);

        // refresh graphHistory, by removing all the last values and adding them again with the new unit added
        refreshGraphHist();

        // add number of after registered units
        SuperDao superDao = new SuperDao(getContext());
        PlanPartyElementsDao partyDao = superDao.getPlanPartyElementsDao();
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
        superDao.close();
        // refresh all visuals
        dayAfterRunning();
    }

    private void refreshGraphHist(){
        SuperDao superDao = new SuperDao(getContext());
        // REMOVE ALL LATEST GRAPH HIST WITH SAME ID AS HISTORY ID AND TEMP STORE THE TIMESTAMPS
        HistoryDao historyDao = superDao.getHistoryDao();
        List<History> histories = historyDao.queryBuilder().list();
        History lastElement = histories.get(histories.size() -1);

        GraphHistoryDao graphHistoryDao = superDao.getGraphHistoryDao();
        List<GraphHistory> graphHistList = graphHistoryDao.queryBuilder().where(GraphHistoryDao.Properties.HistoryId.eq(lastElement.getId())).list();
        superDao.close();
        for(GraphHistory graphHist : graphHistList){
            graphHistoryDao.deleteByKey(graphHist.getId());
        }

        // ADD NEW VALUES IN GRAPH HIST WITH THE AFTER REGISTRATED TIME STAMP
        long id = lastElement.getId();
        calcPr((int) id);
    }

    private void setStartAndEndStamp(){
        SuperDao superDao = new SuperDao(getContext());
        PlanPartyElementsDao planDao = superDao.getPlanPartyElementsDao();
        List<PlanPartyElements> planPList = planDao.queryBuilder().list();
        superDao.close();
        PlanPartyElements element = planPList.get(planPList.size() - 1);
        startStamp = element.getStartTimeStamp();
        endStamp = element.getEndTimeStamp();
    }

    private void calcPr(int id){
        // Loop gjennom DayAfterBAC. Simuler hva promillen var hvert kvarter/halvtime
        SuperDao superDao = new SuperDao(getContext());
        DayAfterBACDao dayAfterBACDao = superDao.getDayAfterBACDao();
        List<DayAfterBAC> dayAfterBacList = dayAfterBACDao.queryBuilder().list();
        superDao.close();

        double highestBAC = 0.0;
        double promille = 0.0;
        Date tempTimeStamp = new Date();

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
            dayAfter_db.addGraphValues(promille, tempTimeStamp, id);
        }
        dayAfter_db.updateHighestBACinHistory(highestBAC);
        startStamp = null;
        endStamp = null;
        dayAfter_db.printLastGraphValues();
    }

    private void afterPopUp(final String unit){
        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_day_a_pop_up);
        dialog.setTitle("Etterregistrer enhet");

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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        dialog.show();
    }

    private void showAlert(){
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
        alert_builder.setMessage("Er du sikker på at du vil avslutte dagen derpå? ").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clearDBTables();
                statusHandler(status);
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

    private void setVisualsPP(){
        clearUnitVariabels();
        btnEndDA.setVisibility(View.GONE);
        planPaRunning_LinLay.setVisibility(View.VISIBLE);
        dayAfterRunning_LinLay.setVisibility(View.GONE);
        colorsUnitLabels();
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
        colorsUnitLabels();
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