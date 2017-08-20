package rustelefonen.no.drikkevett_android.db;

import java.util.List;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "NEW_HISTORY".
 */
public class NewHistory implements java.io.Serializable {

    private Long id;
    private Integer beerCost;
    private Integer wineCost;
    private Integer drinkCost;
    private Integer shotCost;
    private Double beerGrams;
    private Double wineGrams;
    private Double drinkGrams;
    private Double shotGrams;
    private Integer beerPlannedUnitCount;
    private Integer winePlannedUnitCount;
    private Integer drinkPlannedUnitCount;
    private Integer shotPlannedUnitCount;
    private java.util.Date beginDate;
    private java.util.Date endDate;
    private Boolean gender;
    private Double weight;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient NewHistoryDao myDao;

    private List<Unit> units;

    public NewHistory() {
    }

    public NewHistory(Long id) {
        this.id = id;
    }

    public NewHistory(Long id, Integer beerCost, Integer wineCost, Integer drinkCost, Integer shotCost, Double beerGrams, Double wineGrams, Double drinkGrams, Double shotGrams, Integer beerPlannedUnitCount, Integer winePlannedUnitCount, Integer drinkPlannedUnitCount, Integer shotPlannedUnitCount, java.util.Date beginDate, java.util.Date endDate, Boolean gender, Double weight) {
        this.id = id;
        this.beerCost = beerCost;
        this.wineCost = wineCost;
        this.drinkCost = drinkCost;
        this.shotCost = shotCost;
        this.beerGrams = beerGrams;
        this.wineGrams = wineGrams;
        this.drinkGrams = drinkGrams;
        this.shotGrams = shotGrams;
        this.beerPlannedUnitCount = beerPlannedUnitCount;
        this.winePlannedUnitCount = winePlannedUnitCount;
        this.drinkPlannedUnitCount = drinkPlannedUnitCount;
        this.shotPlannedUnitCount = shotPlannedUnitCount;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.gender = gender;
        this.weight = weight;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNewHistoryDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBeerCost() {
        return beerCost;
    }

    public void setBeerCost(Integer beerCost) {
        this.beerCost = beerCost;
    }

    public Integer getWineCost() {
        return wineCost;
    }

    public void setWineCost(Integer wineCost) {
        this.wineCost = wineCost;
    }

    public Integer getDrinkCost() {
        return drinkCost;
    }

    public void setDrinkCost(Integer drinkCost) {
        this.drinkCost = drinkCost;
    }

    public Integer getShotCost() {
        return shotCost;
    }

    public void setShotCost(Integer shotCost) {
        this.shotCost = shotCost;
    }

    public Double getBeerGrams() {
        return beerGrams;
    }

    public void setBeerGrams(Double beerGrams) {
        this.beerGrams = beerGrams;
    }

    public Double getWineGrams() {
        return wineGrams;
    }

    public void setWineGrams(Double wineGrams) {
        this.wineGrams = wineGrams;
    }

    public Double getDrinkGrams() {
        return drinkGrams;
    }

    public void setDrinkGrams(Double drinkGrams) {
        this.drinkGrams = drinkGrams;
    }

    public Double getShotGrams() {
        return shotGrams;
    }

    public void setShotGrams(Double shotGrams) {
        this.shotGrams = shotGrams;
    }

    public Integer getBeerPlannedUnitCount() {
        return beerPlannedUnitCount;
    }

    public void setBeerPlannedUnitCount(Integer beerPlannedUnitCount) {
        this.beerPlannedUnitCount = beerPlannedUnitCount;
    }

    public Integer getWinePlannedUnitCount() {
        return winePlannedUnitCount;
    }

    public void setWinePlannedUnitCount(Integer winePlannedUnitCount) {
        this.winePlannedUnitCount = winePlannedUnitCount;
    }

    public Integer getDrinkPlannedUnitCount() {
        return drinkPlannedUnitCount;
    }

    public void setDrinkPlannedUnitCount(Integer drinkPlannedUnitCount) {
        this.drinkPlannedUnitCount = drinkPlannedUnitCount;
    }

    public Integer getShotPlannedUnitCount() {
        return shotPlannedUnitCount;
    }

    public void setShotPlannedUnitCount(Integer shotPlannedUnitCount) {
        this.shotPlannedUnitCount = shotPlannedUnitCount;
    }

    public java.util.Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(java.util.Date beginDate) {
        this.beginDate = beginDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Unit> getUnits() {
        if (units == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UnitDao targetDao = daoSession.getUnitDao();
            List<Unit> unitsNew = targetDao._queryNewHistory_Units(id);
            synchronized (this) {
                if(units == null) {
                    units = unitsNew;
                }
            }
        }
        return units;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetUnits() {
        units = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
