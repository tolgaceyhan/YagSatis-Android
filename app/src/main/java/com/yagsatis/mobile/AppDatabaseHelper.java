package com.yagsatis.mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "yag_satis.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_STATE = "app_state";
    private static final String COL_KEY = "state_key";
    private static final String COL_VALUE = "state_value";
    private static final String COL_UPDATED_AT = "updated_at";

    public AppDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_STATE + " (" +
                        COL_KEY + " TEXT PRIMARY KEY NOT NULL, " +
                        COL_VALUE + " TEXT NOT NULL, " +
                        COL_UPDATED_AT + " INTEGER NOT NULL" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // İlk şema. Gelecek sürümlerde veri kaybetmeyen migration adımları buraya eklenecek.
    }

    public synchronized String getValue(String key) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                TABLE_STATE,
                new String[]{COL_VALUE},
                COL_KEY + " = ?",
                new String[]{key},
                null,
                null,
                null,
                "1"
        )) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return null;
        }
    }

    public synchronized void setValue(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(COL_KEY, key);
        values.put(COL_VALUE, value);
        values.put(COL_UPDATED_AT, System.currentTimeMillis());

        getWritableDatabase().insertWithOnConflict(
                TABLE_STATE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public synchronized void removeValue(String key) {
        getWritableDatabase().delete(
                TABLE_STATE,
                COL_KEY + " = ?",
                new String[]{key}
        );
    }
}
