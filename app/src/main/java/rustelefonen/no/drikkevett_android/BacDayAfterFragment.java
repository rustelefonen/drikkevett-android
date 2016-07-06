package rustelefonen.no.drikkevett_android;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;

import rustelefonen.no.drikkevett_android.util.PartyUtil;

import java.util.List;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;

import static rustelefonen.no.drikkevett_android.util.PartyUtil.calculateCosts;

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

    // dummy:
    int beerCost = 100;
    int wineCost = 200;
    int drinkCost = 300;
    int shotCost = 400;

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
        getUnits();

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

        // Calculating total costs
        costs = calculateCosts(consumBeers, consumWines, consumDrink, consumShots);

        System.out.println("Planlagte ØL: " + planBeers + " Planlagte VIN: " + planWines + " Planlagte DRINK: " + planDrink + " Planlagte SHOT: " + planShots);
        System.out.println("Drukkete ØL: " + consumBeers + " Drukkete VIN: " + consumWines + " Drukkede DRINK: " + consumDrink + " Drukkede SHOT: " + consumShots);
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

    /*
    * WIDGETS
    * */
    private void changeEndBtnText(Status status){
        if(status.equals(Status.DA_RUNNING)){

        }
    }

    private void setVisualsPP(){
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
    }
}