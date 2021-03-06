package rustelefonen.no.drikkevett_android.db;

import java.util.List;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "INFORMATION_CATEGORY".
 */
public class InformationCategory implements java.io.Serializable {

    private Long id;
    private String name;
    private byte[] image;
    private Integer orderNumber;
    private Integer versionNumber;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient InformationCategoryDao myDao;

    private List<Information> informationList;

    public InformationCategory() {
    }

    public InformationCategory(Long id) {
        this.id = id;
    }

    public InformationCategory(Long id, String name, byte[] image, Integer orderNumber, Integer versionNumber) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.orderNumber = orderNumber;
        this.versionNumber = versionNumber;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getInformationCategoryDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Information> getInformationList() {
        if (informationList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InformationDao targetDao = daoSession.getInformationDao();
            List<Information> informationListNew = targetDao._queryInformationCategory_InformationList(id);
            synchronized (this) {
                if(informationList == null) {
                    informationList = informationListNew;
                }
            }
        }
        return informationList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetInformationList() {
        informationList = null;
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
