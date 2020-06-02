package com.leonyr.smartipaddemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/7/16
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class DBUtil {

    private static DaoSession DBSession;

    public static void init(Context c){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(c, "smart.db");
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
        DBSession = daoMaster.newSession();
    }

    public static DaoSession getDBSession(){
        return DBSession;
    }

}
