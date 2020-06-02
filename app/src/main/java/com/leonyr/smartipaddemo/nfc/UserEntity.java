package com.leonyr.smartipaddemo.nfc;

import android.support.annotation.Keep;

import com.leonyr.smartipaddemo.finger.FingerEntity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.leonyr.smartipaddemo.db.DaoSession;
import com.leonyr.smartipaddemo.db.FingerEntityDao;
import com.leonyr.smartipaddemo.db.UserEntityDao;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/7/3
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
@Keep
@Entity
public class UserEntity {

    @Id(autoincrement = true)
    Long id;

    @Unique
    String userId;
    String userName;
    String userSex;
    String thumb;
    String userAge;
    String userGrade;

    @ToOne(joinProperty = "userId")
    FingerEntity feature;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1814575071)
    private transient UserEntityDao myDao;

    @Generated(hash = 1291639929)
    public UserEntity(Long id, String userId, String userName, String userSex,
            String thumb, String userAge, String userGrade) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userSex = userSex;
        this.thumb = thumb;
        this.userAge = userAge;
        this.userGrade = userGrade;
    }

    @Generated(hash = 1433178141)
    public UserEntity() {
    }

    @Generated(hash = 471921184)
    private transient String feature__resolvedKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1641748015)
    public FingerEntity getFeature() {
        String __key = this.userId;
        if (feature__resolvedKey == null || feature__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FingerEntityDao targetDao = daoSession.getFingerEntityDao();
            FingerEntity featureNew = targetDao.load(__key);
            synchronized (this) {
                feature = featureNew;
                feature__resolvedKey = __key;
            }
        }
        return feature;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 568998066)
    public void setFeature(FingerEntity feature) {
        synchronized (this) {
            this.feature = feature;
            userId = feature == null ? null : feature.getUserId();
            feature__resolvedKey = userId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 287999134)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserEntityDao() : null;
    }
}
