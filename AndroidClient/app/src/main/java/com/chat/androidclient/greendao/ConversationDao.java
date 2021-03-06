package com.chat.androidclient.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.chat.androidclient.mvvm.model.Conversation;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CONVERSATION".
*/
public class ConversationDao extends AbstractDao<Conversation, Long> {

    public static final String TABLENAME = "CONVERSATION";

    /**
     * Properties of entity Conversation.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FromId = new Property(1, Long.class, "fromId", false, "FROM_ID");
        public final static Property Lastcontent = new Property(2, String.class, "lastcontent", false, "LASTCONTENT");
        public final static Property Time = new Property(3, Long.class, "time", false, "TIME");
        public final static Property Msgcount = new Property(4, int.class, "msgcount", false, "MSGCOUNT");
    }


    public ConversationDao(DaoConfig config) {
        super(config);
    }
    
    public ConversationDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CONVERSATION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"FROM_ID\" INTEGER UNIQUE ," + // 1: fromId
                "\"LASTCONTENT\" TEXT," + // 2: lastcontent
                "\"TIME\" INTEGER," + // 3: time
                "\"MSGCOUNT\" INTEGER NOT NULL );"); // 4: msgcount
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CONVERSATION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Conversation entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long fromId = entity.getFromId();
        if (fromId != null) {
            stmt.bindLong(2, fromId);
        }
 
        String lastcontent = entity.getLastcontent();
        if (lastcontent != null) {
            stmt.bindString(3, lastcontent);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(4, time);
        }
        stmt.bindLong(5, entity.getMsgcount());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Conversation entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long fromId = entity.getFromId();
        if (fromId != null) {
            stmt.bindLong(2, fromId);
        }
 
        String lastcontent = entity.getLastcontent();
        if (lastcontent != null) {
            stmt.bindString(3, lastcontent);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(4, time);
        }
        stmt.bindLong(5, entity.getMsgcount());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Conversation readEntity(Cursor cursor, int offset) {
        Conversation entity = new Conversation( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // fromId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // lastcontent
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // time
            cursor.getInt(offset + 4) // msgcount
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Conversation entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFromId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setLastcontent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTime(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setMsgcount(cursor.getInt(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Conversation entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Conversation entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Conversation entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
