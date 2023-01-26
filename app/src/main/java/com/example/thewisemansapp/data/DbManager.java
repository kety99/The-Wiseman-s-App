package com.example.thewisemansapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.thewisemansapp.model.Advice;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DbManager(Context c) {
        context = c;
    }

    public DbManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(Advice advice) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ID, advice.getId());
        contentValue.put(DatabaseHelper.ADVICE, advice.getAdvice());
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Advice findById(Integer id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, new String[]{"id", "advice"}, "id = ?", new String[]{id.toString()}, null, null, null);
        if (cursor != null) {
            if (!cursor.moveToFirst()) {
                return null;
            }
        } else return null;
        Advice advice = new Advice();
        advice.setAdvice(cursor.getString(1));
        advice.setId(cursor.getInt(0));
        return advice;
    }

    public List<Advice> findAll() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,
                new String[]{"id", "advice"}, null, null, null, null, null);
        if (cursor != null) {
            if (!cursor.moveToFirst()) {
                return new ArrayList<>();
            }
        } else return new ArrayList<>();
        List<Advice> advices = new ArrayList<>();
        do {
            Advice advice = new Advice();
            advice.setAdvice(cursor.getString(1));
            advice.setId(cursor.getInt(0));
            advices.add(advice);
        }
        while (cursor.moveToNext());

        return advices;
    }

    public void delete(Integer id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
    }

}
