package com.leonyr.smartipaddemo.finger;

import android.support.annotation.Keep;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

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
public class FingerEntity {

    @Id
    private String userId;

    @Unique
    private byte[] feature;

    private Long creationDate = System.currentTimeMillis();


    @Generated(hash = 431328656)
    public FingerEntity(String userId, byte[] feature, Long creationDate) {
        this.userId = userId;
        this.feature = feature;
        this.creationDate = creationDate;
    }

    @Generated(hash = 679121743)
    public FingerEntity() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }
}
