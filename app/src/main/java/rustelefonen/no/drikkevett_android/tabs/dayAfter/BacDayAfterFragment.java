package rustelefonen.no.drikkevett_android.tabs.dayAfter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.GraphHistory;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;

import static rustelefonen.no.drikkevett_android.util.PartyUtil.addMinsToDate;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.setGenderScore;

public class BacDayAfterFragment extends Fragment {
    /*
    * ATTRIBUTES
    * */
    // user data:
    double weight = 0;
    String gender = "";

    private int planBeers = 0, planWines = 0, planDrink = 0, planShots = 0;
    private int consumBeers = 0, consumWines = 0, consumDrink = 0, consumShots = 0;

    private int costs = 0;
    private double currentBAC = 0.0, highestBAC = 0.0;

    // dummy beergrams
    private double beerGrams = 12.6;
    double wineGrams = 14.0;
    double drinkGrams = 15.0;
    double shotGrams = 16.0;

    public Status status;

    // start and end stamp session
    public Date startStamp = new Date(), endStamp = new Date();

    /*
    * WIDGETS
    * */
    private TextView titleLbl;
    private TextView beerLbl, wineLbl, drinkLbl, shotLbl;
    private TextView costsLbl, highBACLbl, currBACLbl;

    // after reg:
    private TextView afterRegBeerLbl, afterRegWineLbl, afterRegDrinkLbl, afterRegShotLbl;

    // BUTTONS
    private Button btnEndDA, beerBtnAfterReg_DA, wineBtnAfterReg_DA, drinkBtnAfterReg_DA, shotBtnAfterReg_DA;

    // SEEKBAR
    private TextView txtView;
    private SeekBar seekBar;
    private int hours = 0, tempMins = 0;

    // VIEWS
    public View v;

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

        return v;
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
        // hente status fra db
        status = getStatus();
        statusHandler(status);

        // TESTING:
        testingForLoop();
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

        PlanPartyElementsDao partyDao = getSesDB().getPlanPartyElementsDao();

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
        setVisualsDA();
    }

    private void clearUnitVariabels(){
        // Planned units
        planBeers = 0;
        planWines = 0;
        planDrink = 0;
        planShots = 0;

        // consumed units
        consumBeers = 0;
        consumWines = 0;
        consumDrink = 0;
        consumShots = 0;

        // costs
        costs = 0;
    }

    /*
    * DATABASE COMMUNICATION METHODS
    * */

    private DaoSession getSesDB(){
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
        PlanPartyElementsDao partyDao = getSesDB().getPlanPartyElementsDao();
        List<PlanPartyElements> planPList = partyDao.queryBuilder().list();
        PlanPartyElements lastElement = planPList.get(planPList.size() - 1);
        return lastElement.getFirstUnitAddedDate();
    }

    private void getUnits(){
        clearUnitVariabels();

        PlanPartyElementsDao partyDao = getSesDB().getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = getSesDB().getDayAfterBACDao();

        // GET elements to temporary store them in variables then re-saving them
        List<PlanPartyElements> partyList = partyDao.queryBuilder().list();
        for (PlanPartyElements party : partyList) {
            planBeers = party.getPlannedBeer();
            planWines = party.getPlannedWine();
            planDrink = party.getPlannedDrink();
            planShots = party.getPlannedShot();
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
    }

    private double getHighestBAC(){
        double tempVal = 0.0;
        HistoryDao historyDao = getSesDB().getHistoryDao();
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
            System.out.println("Nåværende dato: " + currentDate + "\nFirst Unit added ts: " + firstUnitAddedTimeStamp);

            long timeDifference = getDateDiff(firstUnitAddedTimeStamp, currentDate, TimeUnit.MINUTES);

            double newValueDouble = (double)timeDifference;
            double minToHours = newValueDouble / 60;

            System.out.println("Time Difference: " + timeDifference);
            System.out.println("Timer: " + minToHours);
            System.out.println("<---------------------------------------------------->");
            // FROM 0 - 4 mins
            if(minToHours < 0.085){
                System.out.println("Fra 0-4 minutter...(" + sum + ")");
                sum = 0;
            }
            // FROM 5 - 15 MIN
            if(minToHours > 0.085 && minToHours <= 0.25){
                // KALKULER PROMILLE
                try {
                    sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(minToHours) * minToHours);
                } catch (NumberFormatException e){
                    sum = 0;
                }
                System.out.println("Fra 5-15 minutter... (" + sum + ")");
            }
            if(minToHours > 0.25){
                // KALKULER PROMILLE
                try{
                    sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, minToHours));
                } catch (NumberFormatException e){
                    sum = 0;
                }

                System.out.println("Fra 15 og oppover... (" + sum + ")");
            }
        }
        System.out.println("LivePromille Promille: " + sum);
        System.out.println("<---------------------------------------------------->");


        return sum;
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

    private void clearDBTables(){
        PlanPartyElementsDao partyDao = getSesDB().getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = getSesDB().getDayAfterBACDao();

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
        newParty.setStatus(status.toString());
        partyDao.insert(newParty);
    }

    public int calculateCosts(int b, int w, int d, int s){
        User user = ((MainActivity)getActivity()).getUser();
        return (b * user.getBeerPrice()) + (w * user.getWinePrice()) + (d * user.getDrinkPrice()) + (s * user.getShotPrice());
    }

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

    private void setStats(){
        costs = calculateCosts(consumBeers, consumWines, consumDrink, consumShots);
        if(getFirstUnAddedStamp() != null){
            currentBAC = getCurrentBAC(weight, gender, getFirstUnAddedStamp());
        }
        highestBAC = getHighestBAC();
    }

    /*
    * REGISTRATION ( OF FORGOTTEN UNITS )
    * */

    private void testingForLoop(){
        int intervalHours = 0;
        for(double i = 0; i < 5; i+= 0.5){
            System.out.println(i);
            if(i > 0.5 && i < 1){
                intervalHours = 1;
            }
        }
    }

    private int setMaxSeekBarVal(){
        int intervallHours = 0;

        // Get length of session ( interval )

        PlanPartyElementsDao partyDao = getSesDB().getPlanPartyElementsDao();
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

        // refresh all visuals
        dayAfterRunning();

        // add number of after registered units
        int afterRegBeer = 0;
        int afterRegWine = 0;
        int afterRegDrink = 0;
        int afterRegShot = 0;

        // add unitToVariable
        if(unit == "Beer"){
            consumBeers++;
            afterRegBeer++;
            afterRegBeerLbl.setText(afterRegBeer + "");
        }
        if(unit == "Wine"){
            consumWines++;
        }
        if(unit == "Drink"){
            consumDrink++;
        }
        if(unit == "Shot"){
            consumShots++;
        }
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

    private Date setNewUnitDate(int minutes){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.MINUTE, minutes);

        date = calendar.getTime();
        return date;
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

        // SEEKBAR
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
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
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
    * WIDGETS
    * */

    private void setVisibility(int visibility){
        // Units
        beerLbl.setVisibility(visibility);
        wineLbl.setVisibility(visibility);
        drinkLbl.setVisibility(visibility);
        shotLbl.setVisibility(visibility);

        // Stats
        costsLbl.setVisibility(visibility);
        highBACLbl.setVisibility(visibility);
        currBACLbl.setVisibility(visibility);

        // Buttons
        btnEndDA.setVisibility(visibility);
        beerBtnAfterReg_DA.setVisibility(visibility);
        wineBtnAfterReg_DA.setVisibility(visibility);
        drinkBtnAfterReg_DA.setVisibility(visibility);
        shotBtnAfterReg_DA.setVisibility(visibility);

        // after Reg
        afterRegBeerLbl.setVisibility(visibility);
        afterRegWineLbl.setVisibility(visibility);
        afterRegDrinkLbl.setVisibility(visibility);
        afterRegShotLbl.setVisibility(visibility);
    }

    private void setVisualsPP(){
        clearUnitVariabels();
        btnEndDA.setVisibility(View.GONE);
        titleLbl.setVisibility(View.VISIBLE);
        titleLbl.setText("Planlegg Kvelden pågår");
        setVisibility(View.GONE);
    }

    private void setVisualsDA(){
        setVisibility(View.VISIBLE);
        titleLbl.setVisibility(View.GONE);

        beerLbl.setText(consumBeers + "");
        wineLbl.setText(consumWines + "");
        drinkLbl.setText(consumDrink + "");
        shotLbl.setText(consumShots + "");

        costsLbl.setText(costs + ",-");
        highBACLbl.setText(highestBAC + "");
        currBACLbl.setText(currentBAC + "");
    }

    private void initWidgets(){
        titleLbl = (TextView) v.findViewById(R.id.txtViewTitleDA);

        beerLbl = (TextView) v.findViewById(R.id.txtViewBeerDA);
        wineLbl = (TextView) v.findViewById(R.id.txtViewWineDA);
        drinkLbl = (TextView) v.findViewById(R.id.txtViewDrinkDA);
        shotLbl = (TextView) v.findViewById(R.id.txtViewShotDA);

        costsLbl = (TextView) v.findViewById(R.id.txtViewCosts_DA);
        highBACLbl = (TextView) v.findViewById(R.id.txtViewHighestBAC_DA);
        currBACLbl = (TextView) v.findViewById(R.id.txtViewCurrBAC_DA);

        // BUTTONS
        btnEndDA = (Button) v.findViewById(R.id.btnEndDayAfter);
        beerBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegBeer_DA);
        wineBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegWine_DA);
        drinkBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegDrink_DA);
        shotBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegShot_DA);

        // AFTER REG UNITS
        afterRegBeerLbl = (TextView) v.findViewById(R.id.txtViewAfterRegBeerUnit_DA);
        afterRegWineLbl = (TextView) v.findViewById(R.id.txtViewAfterRegWineUnit_DA);
        afterRegDrinkLbl = (TextView) v.findViewById(R.id.txtViewAfterRegDrinkUnit_DA);
        afterRegShotLbl = (TextView) v.findViewById(R.id.txtViewAfterRegShotUnit_DA);
    }
}