package com.example.ramanandank.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ramanandank.popularmovies.data.MovieDbContract.MovieEntry;

/**
 * Created by ramanandank on 28/11/16.
 */

public class MovieDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 1;


    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MovieEntry.MOVIE_ID + " TEXT UNIQUE NOT NULL," +
                        MovieEntry.MOVIE_BACKDROP_URI + " TEXT NOT NULL," +
                        MovieEntry.MOVIE_TITLE + " TEXT NOT NULL," +
                        MovieEntry.MOVIE_POSTER + " TEXT NOT NULL," +
                        MovieEntry.MOVIE_SYNOPSIS + " TEXT NOT NULL," +
                        MovieEntry.MOVIE_VOTE_AVERAGE + " TEXT NOT NULL," +
                        MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                        MovieEntry.MOVIE_REVIEWS + " TEXT NOT NULL," +
                        MovieEntry.MOVIE_TRAILERS + " TEXT NOT NULL," +
                        "UNIQUE (" + MovieEntry.MOVIE_ID + ") ON CONFLICT IGNORE" +
                        " );";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
    }
}
