package com.example.ecss.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int Database_Version = 1;
    private static final String Database_Name = "Movies";
    private static final String Table_Favorites = "Favorites";
    private static final String Movie_Name = "name";
    private static final String Movie_Overview = "overview";
    private static final String Movie_Rate = "rate";
    private static final String Movie_ID = "id";
    private static final String Movie_Poster_Path = "poster_path";
    private static final String Movie_Release_Date = "release_date";
    private static final String Create_Table_Favourites = "Create Table " + Table_Favorites + " (" + Movie_ID + " TEXT PRIMARY KEY,"
            + Movie_Name + " TEXT NOT NULL,"
            + Movie_Overview + " TEXT NOT NULL,"
            + Movie_Rate + " TEXT NOT NULL,"
            + Movie_Poster_Path + " TEXT NOT NULL,"
            + Movie_Release_Date + " TEXT NOT NULL )";

    public DatabaseHelper(Context context) {
        super(context, Database_Name, null, Database_Version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Create_Table_Favourites);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Create_Table_Favourites);
        onCreate(sqLiteDatabase);
    }

    public void InsertIntoTable(MovieDetail movie) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Movie_ID, movie.getMovie_id());
        values.put(Movie_Name, movie.getTitle());
        values.put(Movie_Overview, movie.getOverview());
        values.put(Movie_Rate, movie.getVote_average());
        values.put(Movie_Poster_Path, movie.getPoster_path());
        values.put(Movie_Release_Date, movie.getRelease_date());

        sqLiteDatabase.insert(Table_Favorites, null, values);
        sqLiteDatabase.close();
    }

    public ArrayList<MovieDetail> getFavorites() {

        ArrayList<MovieDetail> movies = new ArrayList<MovieDetail>();

        String selectQuery = "SELECT  * FROM " + Table_Favorites;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                MovieDetail md = new MovieDetail();
                md.setMovie_id(c.getString(c.getColumnIndex(Movie_ID)));
                md.setTitle(c.getString(c.getColumnIndex(Movie_Name)));
                md.setOverview(c.getString(c.getColumnIndex(Movie_Overview)));
                md.setVote_average(c.getString(c.getColumnIndex(Movie_Rate)));
                md.setPoster_path(c.getString(c.getColumnIndex(Movie_Poster_Path)));
                md.setRelease_date(c.getString(c.getColumnIndex(Movie_Release_Date)));
                movies.add(md);
            } while (c.moveToNext());
        }
        return movies;
    }
}
