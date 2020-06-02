package com.leonyr.smartipaddemo.db;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.leonyr.smartipaddemo.finger.FingerEntity;

import com.leonyr.smartipaddemo.nfc.UserEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_ENTITY".
*/
public class UserEntityDao extends AbstractDao<UserEntity, Long> {

    public static final String TABLENAME = "USER_ENTITY";

    /**
     * Properties of entity UserEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, String.class, "userId", false, "USER_ID");
        public final static Property UserName = new Property(2, String.class, "userName", false, "USER_NAME");
        public final static Property UserSex = new Property(3, String.class, "userSex", false, "USER_SEX");
        public final static Property Thumb = new Property(4, String.class, "thumb", false, "THUMB");
        public final static Property UserAge = new Property(5, String.class, "userAge", false, "USER_AGE");
        public final static Property UserGrade = new Property(6, String.class, "userGrade", false, "USER_GRADE");
    }

    private DaoSession daoSession;


    public UserEntityDao(DaoConfig config) {
        super(config);
    }
    
    public UserEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"USER_ID\" TEXT UNIQUE ," + // 1: userId
                "\"USER_NAME\" TEXT," + // 2: userName
                "\"USER_SEX\" TEXT," + // 3: userSex
                "\"THUMB\" TEXT," + // 4: thumb
                "\"USER_AGE\" TEXT," + // 5: userAge
                "\"USER_GRADE\" TEXT);"); // 6: userGrade
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(3, userName);
        }
 
        String userSex = entity.getUserSex();
        if (userSex != null) {
            stmt.bindString(4, userSex);
        }
 
        String thumb = entity.getThumb();
        if (thumb != null) {
            stmt.bindString(5, thumb);
        }
 
        String userAge = entity.getUserAge();
        if (userAge != null) {
            stmt.bindString(6, userAge);
        }
 
        String userGrade = entity.getUserGrade();
        if (userGrade != null) {
            stmt.bindString(7, userGrade);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(3, userName);
        }
 
        String userSex = entity.getUserSex();
        if (userSex != null) {
            stmt.bindString(4, userSex);
        }
 
        String thumb = entity.getThumb();
        if (thumb != null) {
            stmt.bindString(5, thumb);
        }
 
        String userAge = entity.getUserAge();
        if (userAge != null) {
            stmt.bindString(6, userAge);
        }
 
        String userGrade = entity.getUserGrade();
        if (userGrade != null) {
            stmt.bindString(7, userGrade);
        }
    }

    @Override
    protected final void attachEntity(UserEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserEntity readEntity(Cursor cursor, int offset) {
        UserEntity entity = new UserEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // userName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // userSex
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // thumb
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // userAge
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // userGrade
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUserSex(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setThumb(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUserAge(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setUserGrade(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getFingerEntityDao().getAllColumns());
            builder.append(" FROM USER_ENTITY T");
            builder.append(" LEFT JOIN FINGER_ENTITY T0 ON T.\"USER_ID\"=T0.\"USER_ID\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected UserEntity loadCurrentDeep(Cursor cursor, boolean lock) {
        UserEntity entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        FingerEntity feature = loadCurrentOther(daoSession.getFingerEntityDao(), cursor, offset);
        entity.setFeature(feature);

        return entity;    
    }

    public UserEntity loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<UserEntity> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<UserEntity> list = new ArrayList<UserEntity>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<UserEntity> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<UserEntity> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}