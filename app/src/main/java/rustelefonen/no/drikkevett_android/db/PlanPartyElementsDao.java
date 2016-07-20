package rustelefonen.no.drikkevett_android.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import rustelefonen.no.drikkevett_android.db.PlanPartyElements;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PLAN_PARTY_ELEMENTS".
*/
public class PlanPartyElementsDao extends AbstractDao<PlanPartyElements, Long> {

    public static final String TABLENAME = "PLAN_PARTY_ELEMENTS";

    /**
     * Properties of entity PlanPartyElements.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Status = new Property(1, String.class, "status", false, "STATUS");
        public final static Property PlannedBeer = new Property(2, Integer.class, "plannedBeer", false, "PLANNED_BEER");
        public final static Property PlannedWine = new Property(3, Integer.class, "plannedWine", false, "PLANNED_WINE");
        public final static Property PlannedDrink = new Property(4, Integer.class, "plannedDrink", false, "PLANNED_DRINK");
        public final static Property PlannedShot = new Property(5, Integer.class, "plannedShot", false, "PLANNED_SHOT");
        public final static Property AftRegBeer = new Property(6, Integer.class, "aftRegBeer", false, "AFT_REG_BEER");
        public final static Property AftRegWine = new Property(7, Integer.class, "aftRegWine", false, "AFT_REG_WINE");
        public final static Property AftRegDrink = new Property(8, Integer.class, "aftRegDrink", false, "AFT_REG_DRINK");
        public final static Property AftRegShot = new Property(9, Integer.class, "aftRegShot", false, "AFT_REG_SHOT");
        public final static Property FirstUnitAddedDate = new Property(10, java.util.Date.class, "firstUnitAddedDate", false, "FIRST_UNIT_ADDED_DATE");
        public final static Property StartTimeStamp = new Property(11, java.util.Date.class, "startTimeStamp", false, "START_TIME_STAMP");
        public final static Property EndTimeStamp = new Property(12, java.util.Date.class, "endTimeStamp", false, "END_TIME_STAMP");
    };


    public PlanPartyElementsDao(DaoConfig config) {
        super(config);
    }
    
    public PlanPartyElementsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PLAN_PARTY_ELEMENTS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"STATUS\" TEXT," + // 1: status
                "\"PLANNED_BEER\" INTEGER," + // 2: plannedBeer
                "\"PLANNED_WINE\" INTEGER," + // 3: plannedWine
                "\"PLANNED_DRINK\" INTEGER," + // 4: plannedDrink
                "\"PLANNED_SHOT\" INTEGER," + // 5: plannedShot
                "\"AFT_REG_BEER\" INTEGER," + // 6: aftRegBeer
                "\"AFT_REG_WINE\" INTEGER," + // 7: aftRegWine
                "\"AFT_REG_DRINK\" INTEGER," + // 8: aftRegDrink
                "\"AFT_REG_SHOT\" INTEGER," + // 9: aftRegShot
                "\"FIRST_UNIT_ADDED_DATE\" INTEGER," + // 10: firstUnitAddedDate
                "\"START_TIME_STAMP\" INTEGER," + // 11: startTimeStamp
                "\"END_TIME_STAMP\" INTEGER);"); // 12: endTimeStamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PLAN_PARTY_ELEMENTS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PlanPartyElements entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(2, status);
        }
 
        Integer plannedBeer = entity.getPlannedBeer();
        if (plannedBeer != null) {
            stmt.bindLong(3, plannedBeer);
        }
 
        Integer plannedWine = entity.getPlannedWine();
        if (plannedWine != null) {
            stmt.bindLong(4, plannedWine);
        }
 
        Integer plannedDrink = entity.getPlannedDrink();
        if (plannedDrink != null) {
            stmt.bindLong(5, plannedDrink);
        }
 
        Integer plannedShot = entity.getPlannedShot();
        if (plannedShot != null) {
            stmt.bindLong(6, plannedShot);
        }
 
        Integer aftRegBeer = entity.getAftRegBeer();
        if (aftRegBeer != null) {
            stmt.bindLong(7, aftRegBeer);
        }
 
        Integer aftRegWine = entity.getAftRegWine();
        if (aftRegWine != null) {
            stmt.bindLong(8, aftRegWine);
        }
 
        Integer aftRegDrink = entity.getAftRegDrink();
        if (aftRegDrink != null) {
            stmt.bindLong(9, aftRegDrink);
        }
 
        Integer aftRegShot = entity.getAftRegShot();
        if (aftRegShot != null) {
            stmt.bindLong(10, aftRegShot);
        }
 
        java.util.Date firstUnitAddedDate = entity.getFirstUnitAddedDate();
        if (firstUnitAddedDate != null) {
            stmt.bindLong(11, firstUnitAddedDate.getTime());
        }
 
        java.util.Date startTimeStamp = entity.getStartTimeStamp();
        if (startTimeStamp != null) {
            stmt.bindLong(12, startTimeStamp.getTime());
        }
 
        java.util.Date endTimeStamp = entity.getEndTimeStamp();
        if (endTimeStamp != null) {
            stmt.bindLong(13, endTimeStamp.getTime());
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PlanPartyElements readEntity(Cursor cursor, int offset) {
        PlanPartyElements entity = new PlanPartyElements( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // status
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // plannedBeer
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // plannedWine
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // plannedDrink
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // plannedShot
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // aftRegBeer
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // aftRegWine
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // aftRegDrink
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // aftRegShot
            cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)), // firstUnitAddedDate
            cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)), // startTimeStamp
            cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)) // endTimeStamp
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PlanPartyElements entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStatus(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPlannedBeer(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setPlannedWine(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setPlannedDrink(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setPlannedShot(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setAftRegBeer(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setAftRegWine(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setAftRegDrink(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setAftRegShot(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setFirstUnitAddedDate(cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)));
        entity.setStartTimeStamp(cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)));
        entity.setEndTimeStamp(cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PlanPartyElements entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PlanPartyElements entity) {
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
