package rustelefonen.no.drikkevett_android.tabs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import java.text.DecimalFormat;
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

import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;

public class BacDayAfterFragment extends Fragment {
    /*
    * ATTRIBUTES
    * */
    int planBeers = 0;
    int planWines = 0;
    int planDrink = 0;
    int planShots = 0;

    int consumBeers = 0;
    int consumWines = 0;
    int consumDrink = 0;
    int consumShots = 0;

    int costs = 0;
    double currentBAC = 0.0;
    double highestBAC = 0.0;

    // dummy:
    int beerCost = 100;
    int wineCost = 200;
    int drinkCost = 300;
    int shotCost = 400;

    // dummy beergrams
    double beerGrams = 12.6;
    double wineGrams = 14.0;
    double drinkGrams = 15.0;
    double shotGrams = 16.0;

    public Status status;

    /*
    * WIDGETS
    * */
    public TextView titleLbl;

    public TextView beerLbl;
    public TextView wineLbl;
    public TextView drinkLbl;
    public TextView shotLbl;

    public TextView costsLbl;
    public TextView highBACLbl;
    public TextView currBAC;

    // BUTTONS
    public Button btnEndDA;
    public Button beerBtnAfterReg_DA;
    public Button wineBtnAfterReg_DA;
    public Button drinkBtnAfterReg_DA;
    public Button shotBtnAfterReg_DA;

    // VIEWS
    public View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bac_day_after_frag, container, false);
        initWidgets();

        status = getStatus();
        statusHandler(status);

        btnEndDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                clearDBTables();
                statusHandler(status);
            }
        });

        // AFTER REGISTRATION

        // beer
        beerBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Time Picker");
                builder.setIcon(R.mipmap.ic_launcher);
                final Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Toast.makeText(getContext(), i + ":" + i1, Toast.LENGTH_SHORT).show();
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        // wine
        wineBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // drink
        drinkBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // shot
        shotBtnAfterReg_DA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        // hente status fra db
        status = getStatus();
        statusHandler(status);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!this.isVisible()) return;
        if (!isVisibleToUser) return;
        // hente status fra db
        status = getStatus();
        statusHandler(status);
    }

    /*
    * STATUS
    * */

    private void statusHandler(Status state){
        if(state == status.RUNNING || state == status.NOT_RUNNING){
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
    * */

    private void planPartyRunning(){
        setVisualsPP();
    }

    private void dayAfterRunning(){
        getUnits();
        setVisualsDA();
        setBACValues();

        // Calculating total costs
        costs = calculateCosts(consumBeers, consumWines, consumDrink, consumShots);

        // Calculating current BAC
        currentBAC = getCurrentBAC(80.0, "Mann", new Date());

        // Calculating/Fetching current highest BAC
        // highestBAC = method2();

        System.out.println("Planlagte ØL: " + planBeers + " Planlagte VIN: " + planWines + " Planlagte DRINK: " + planDrink + " Planlagte SHOT: " + planShots);
        System.out.println("Drukkete ØL: " + consumBeers + " Drukkete VIN: " + consumWines + " Drukkede DRINK: " + consumDrink + " Drukkede SHOT: " + consumShots);
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

    private void getUnits(){
        clearUnitVariabels();

        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        PlanPartyElementsDao partyDao = daoSession.getPlanPartyElementsDao();
        DayAfterBACDao dayAfterDao = daoSession.getDayAfterBACDao();

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
        return 0.0;
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
                sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(minToHours) * minToHours);
                System.out.println("Fra 5-15 minutter... (" + sum + ")");
            }
            if(minToHours > 0.25){
                // KALKULER PROMILLE
                sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, minToHours));
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

    private void clearDBTables(){
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
        // get costs from DB:
        UserDao userDao = setDaoSessionDB().getUserDao();
        int beerC = 0;
        int wineC = 0;
        int drinkC = 0;
        int shotC = 0;

        /* DETTE FYLLER FRA DATABASEN, MEN FORELØPIG INGEN BRUKERINFO
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

    private void setBACValues(){
        System.out.println("REGN UT PROMILLE OG HØYESTE PROMILLE!");
    }

    /*
    * WIDGETS
    * */
    private void changeEndBtnText(Status status){
        if(status.equals(Status.DA_RUNNING)){

        }
    }

    private void setVisualsPP(){
        clearUnitVariabels();
        btnEndDA.setVisibility(View.GONE);

        titleLbl.setText("Planlegg Kvelden pågår");

        beerLbl.setText(consumBeers + "\nØL");
        wineLbl.setText(consumWines + "\nVin");
        drinkLbl.setText(consumDrink + "\nDrink");
        shotLbl.setText(consumShots + "\nShot");

        costsLbl.setText(costs + ",-\nForbruk");
    }

    private void setVisualsDA(){
        btnEndDA.setVisibility(View.VISIBLE);

        titleLbl.setText("Dagen Derpå pågår");

        beerLbl.setText(consumBeers + "\nØL");
        wineLbl.setText(consumWines + "\nVin");
        drinkLbl.setText(consumDrink + "\nDrink");
        shotLbl.setText(consumShots + "\nShot");

        costsLbl.setText(costs + ",-\nForbruk");
    }

    private void initWidgets(){
        titleLbl = (TextView) v.findViewById(R.id.txtViewTitleDA);

        beerLbl = (TextView) v.findViewById(R.id.txtViewBeerDA);
        wineLbl = (TextView) v.findViewById(R.id.txtViewWineDA);
        drinkLbl = (TextView) v.findViewById(R.id.txtViewDrinkDA);
        shotLbl = (TextView) v.findViewById(R.id.txtViewShotDA);

        costsLbl = (TextView) v.findViewById(R.id.txtViewCosts_DA);
        highBACLbl = (TextView) v.findViewById(R.id.txtViewHighestBAC_DA);
        currBAC = (TextView) v.findViewById(R.id.txtViewCurrBAC_DA);

        // BUTTONS
        btnEndDA = (Button) v.findViewById(R.id.btnEndDayAfter);
        beerBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegBeer_DA);
        wineBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegWine_DA);
        drinkBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegDrink_DA);
        shotBtnAfterReg_DA = (Button) v.findViewById(R.id.btnAfterRegShot_DA);
    }
}