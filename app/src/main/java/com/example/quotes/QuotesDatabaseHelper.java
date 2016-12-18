package com.example.quotes;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class QuotesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Quotes";
    private static final int DB_VERSION = 1;

    public QuotesDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Authors (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                "name TEXT NOT NULL, "+
                "surname TEXT NOT NULL);");
        db.execSQL("CREATE TABLE Quotes (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "author_id INTEGER NOT NULL, " +
                "content TEXT NOT NULL, " +
                "favorite BOOLEAN NOT NULL," +
                "date DATE, " +
                "FOREIGN KEY(author_id) REFERENCES Authors(_id));");
        setUpSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void setUpSampleData(SQLiteDatabase db) {
        long id1 = insertAuthor(db, "Jose", "Mourinho");
        long id2 = insertAuthor(db, "John", "Adams");
        long id3 = insertAuthor(db, "Peter", "Richards");
        insertQuote(db, id1, "I think I'm a special one", true);
        insertQuote(db, id2, "Be happy", false);
        insertQuote(db, id3, "Have a hobby", false);
    }


    private long insertAuthor(SQLiteDatabase db, String name, String surname){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("surname", surname);
        long id = db.insert("Authors", null, values);
        return id;
    }

    private void insertQuote(SQLiteDatabase db, long authorID, String content, boolean isFavorite){
        ContentValues values = new ContentValues();
        values.put("author_id", authorID);
        values.put("content", content);
        values.put("favorite", isFavorite);
        values.put("date", String.valueOf(new Date()));
        db.insert("Quotes", null, values);
    }
}
