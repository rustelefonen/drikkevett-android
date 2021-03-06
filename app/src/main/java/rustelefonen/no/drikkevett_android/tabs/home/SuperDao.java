package rustelefonen.no.drikkevett_android.tabs.home;

import android.content.Context;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.InformationCategoryDao;
import rustelefonen.no.drikkevett_android.db.InformationDao;
import rustelefonen.no.drikkevett_android.db.NewHistoryDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.db.UnitDao;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.information.DatabaseHelper;

/**
 * Created by simenfonnes on 07.07.2016.
 */

public class SuperDao {

    //private DaoMaster.DevOpenHelper helper;
    private DatabaseHelper helper;
    private static final String DB_NAME = "my-db";
    private DaoSession daoSession;

    public SuperDao(Context context) {
        helper = new DatabaseHelper(context);// new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    public HistoryDao getHistoryDao() {
        return daoSession.getHistoryDao();
    }

    public UserDao getUserDao() {
        return daoSession.getUserDao();
    }

    public GraphHistoryDao getGraphHistoryDao() {
        return daoSession.getGraphHistoryDao();
    }

    public InformationCategoryDao getInformationCategoryDao() {
        return daoSession.getInformationCategoryDao();
    }

    public InformationDao getInformationDao() {
        return daoSession.getInformationDao();
    }

    public PlanPartyElementsDao getPlanPartyElementsDao(){ return daoSession.getPlanPartyElementsDao(); }

    public DayAfterBACDao getDayAfterBACDao() { return daoSession.getDayAfterBACDao(); }

    public NewHistoryDao getNewHistoryDao() { return daoSession.getNewHistoryDao(); }

    public UnitDao getUnitDao() {return daoSession.getUnitDao(); }

    public void close() {
        helper.close();
    }
}
