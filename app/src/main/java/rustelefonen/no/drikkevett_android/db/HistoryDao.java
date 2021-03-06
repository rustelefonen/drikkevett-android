package rustelefonen.no.drikkevett_android.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import rustelefonen.no.drikkevett_android.db.History;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "HISTORY".
*/
public class HistoryDao extends AbstractDao<History, Long> {

    public static final String TABLENAME = "HISTORY";

    /**
     * Properties of entity History.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DrinkCount = new Property(1, Integer.class, "drinkCount", false, "DRINK_COUNT");
        public final static Property BeerCount = new Property(2, Integer.class, "beerCount", false, "BEER_COUNT");
        public final static Property ShotCount = new Property(3, Integer.class, "shotCount", false, "SHOT_COUNT");
        public final static Property WineCount = new Property(4, Integer.class, "wineCount", false, "WINE_COUNT");
        public final static Property StartDate = new Property(5, java.util.Date.class, "startDate", false, "START_DATE");
        public final static Property EndDate = new Property(6, java.util.Date.class, "endDate", false, "END_DATE");
        public final static Property Sum = new Property(7, Integer.class, "sum", false, "SUM");
        public final static Property HighestBAC = new Property(8, Double.class, "highestBAC", false, "HIGHEST_BAC");
        public final static Property PlannedUnitsCount = new Property(9, Integer.class, "plannedUnitsCount", false, "PLANNED_UNITS_COUNT");
    };

    private DaoSession daoSession;


    public HistoryDao(DaoConfig config) {
        super(config);
    }
    
    public HistoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"HISTORY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"DRINK_COUNT\" INTEGER," + // 1: drinkCount
                "\"BEER_COUNT\" INTEGER," + // 2: beerCount
                "\"SHOT_COUNT\" INTEGER," + // 3: shotCount
                "\"WINE_COUNT\" INTEGER," + // 4: wineCount
                "\"START_DATE\" INTEGER," + // 5: startDate
                "\"END_DATE\" INTEGER," + // 6: endDate
                "\"SUM\" INTEGER," + // 7: sum
                "\"HIGHEST_BAC\" REAL," + // 8: highestBAC
                "\"PLANNED_UNITS_COUNT\" INTEGER);"); // 9: plannedUnitsCount
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"HISTORY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, History entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer drinkCount = entity.getDrinkCount();
        if (drinkCount != null) {
            stmt.bindLong(2, drinkCount);
        }
 
        Integer beerCount = entity.getBeerCount();
        if (beerCount != null) {
            stmt.bindLong(3, beerCount);
        }
 
        Integer shotCount = entity.getShotCount();
        if (shotCount != null) {
            stmt.bindLong(4, shotCount);
        }
 
        Integer wineCount = entity.getWineCount();
        if (wineCount != null) {
            stmt.bindLong(5, wineCount);
        }
 
        java.util.Date startDate = entity.getStartDate();
        if (startDate != null) {
            stmt.bindLong(6, startDate.getTime());
        }
 
        java.util.Date endDate = entity.getEndDate();
        if (endDate != null) {
            stmt.bindLong(7, endDate.getTime());
        }
 
        Integer sum = entity.getSum();
        if (sum != null) {
            stmt.bindLong(8, sum);
        }
 
        Double highestBAC = entity.getHighestBAC();
        if (highestBAC != null) {
            stmt.bindDouble(9, highestBAC);
        }
 
        Integer plannedUnitsCount = entity.getPlannedUnitsCount();
        if (plannedUnitsCount != null) {
            stmt.bindLong(10, plannedUnitsCount);
        }
    }

    @Override
    protected void attachEntity(History entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public History readEntity(Cursor cursor, int offset) {
        History entity = new History( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // drinkCount
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // beerCount
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // shotCount
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // wineCount
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // startDate
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // endDate
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // sum
            cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8), // highestBAC
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9) // plannedUnitsCount
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, History entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDrinkCount(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setBeerCount(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setShotCount(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setWineCount(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setStartDate(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setEndDate(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setSum(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setHighestBAC(cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8));
        entity.setPlannedUnitsCount(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(History entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(History entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
