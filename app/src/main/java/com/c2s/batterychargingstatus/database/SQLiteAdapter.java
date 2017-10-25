package com.c2s.batterychargingstatus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.c2s.batterychargingstatus.model.Battery;
import com.c2s.batterychargingstatus.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.c2s.batterychargingstatus.database.SQLiteAdapter.SQLiteHelper.KEY_ID;
import static com.c2s.batterychargingstatus.database.SQLiteAdapter.SQLiteHelper.KEY_DATE;
import static com.c2s.batterychargingstatus.database.SQLiteAdapter.SQLiteHelper.KEY_JSON;
import static com.c2s.batterychargingstatus.database.SQLiteAdapter.SQLiteHelper.TABLE_BATTERY_STATUS;

/**
 * Created by satya on 27-Sep-17.
 */

public class SQLiteAdapter {
    private static final String TAG = "SQLiteAdapter";
    private SQLiteHelper mSqLiteHelper;

    public SQLiteAdapter(Context context) {
        mSqLiteHelper = new SQLiteHelper(context);
    }

    public void addBatteryStatusData(Battery battery) {
        try {
            SQLiteDatabase db = mSqLiteHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ID, battery.getId());
            values.put(KEY_DATE,battery.getDate());
            values.put(KEY_JSON, battery.getJson());
            db.insert(TABLE_BATTERY_STATUS, null, values);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception while adding user" + e.getMessage());
        }
    }

    // Getting All Users
    public List<Battery> getBatteryStatus() {
        List<Battery> batteries = null;
        SQLiteDatabase db = mSqLiteHelper.getWritableDatabase();
        try {
            batteries = new ArrayList<>();

           // WHERE "+KEY_ID+" <=1000
            String selectQuery = "SELECT  * FROM " + TABLE_BATTERY_STATUS + "  ORDER BY "+KEY_ID+" DESC LIMIT 1000";
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Battery battery = new Battery();
                    battery.setId(Integer.parseInt(cursor.getString(0)));
                    battery.setDate(cursor.getString(1));
                    battery.setJson(cursor.getString(2));
                    batteries.add(battery);
                } while (cursor.moveToNext());
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "NumberFormat Exception");
            return null;
        } finally {
            if (null != db) {
                db.close();
            }
        }

        return batteries;
    }

    class SQLiteHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "battery_status_db";
        static final String TABLE_BATTERY_STATUS = "battery_status";
        static final String KEY_ID = "id";
        static final String KEY_DATE = "date";
        static final String KEY_JSON = "time";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_BATTERY_STATUS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT," + KEY_JSON + " TEXT" + ")";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_BATTERY_STATUS;

        SQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(CREATE_TABLE);
            } catch (SQLException e) {
                Log.e(TAG, "Exception onCreate" + e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            try {
                sqLiteDatabase.execSQL(DROP_TABLE);
                onCreate(sqLiteDatabase);
            } catch (SQLException e) {
                Log.e(TAG, "Exception onUpgrade" + e.getMessage());
            }
        }
    }


}
