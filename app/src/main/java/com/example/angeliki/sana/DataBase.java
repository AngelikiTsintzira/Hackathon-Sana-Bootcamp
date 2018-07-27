package com.example.axilleas.sana;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by axilleas on 17/10/15.
 */
public class DataBase {

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    private static final String USER_HEIGHT = "height_table";
    private static final String HEIGHT = "height";
    private static final String WEIGHT = "weight";


    private static final String CREATE_HEIGHT_TABLE = "create table "
            + USER_HEIGHT
            + " (" + HEIGHT + " text, " + WEIGHT + " text );";

//    private static final String CREATE_HEIGHT_TABLE = "create table "
//            + USER_HEIGHT
//            + " (" + HEIGHT + " text  );";


    private static String SQL_INSERT =
            "INSERT or replace INTO height_table (height, weight) VALUES('0','0')" ;
            //"INSERT or replace INTO height_table (height) VALUES('0')" ;

    private final Context mCtx;


    private static class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_HEIGHT_TABLE);
            db.execSQL(SQL_INSERT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USER_HEIGHT);
            onCreate(db);
            }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public DataBase(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public DataBase open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertALL(String height,String weight) {
        ContentValues cv = new ContentValues();
        cv.put(HEIGHT, height);
        cv.put(WEIGHT,weight);
        mDb.update(USER_HEIGHT, cv, null, null);

    }

    public void insertWeight(String weight) {
        ContentValues cv = new ContentValues();
        cv.put(WEIGHT,weight);
        mDb.update(USER_HEIGHT,cv,null,null);
    }

    public String retriveHeight() throws SQLException {
        Cursor cur = mDb.query(true, USER_HEIGHT, new String[]{HEIGHT}, null, null, null, null, null, null);
        if (cur.moveToLast()) {


            String height = cur.getString(cur.getColumnIndex(HEIGHT));

            cur.close();
            return height;
        }
        cur.close();
        return null;
    }
///////
    public String retriveWeight() throws SQLException {
        Cursor cur = mDb.query(true, USER_HEIGHT, new String[]{WEIGHT}, null, null, null, null, null, null);
        if (cur.moveToLast()) {


            String weight = cur.getString(cur.getColumnIndex(WEIGHT));

            cur.close();
            return weight;
        }
        cur.close();
        return null;
    }



}
