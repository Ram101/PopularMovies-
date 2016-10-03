package com.example.ramanandank.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by ramanandank on 24/09/16.
 */

class MovieListPref {
    private final SharedPreferences mPreference;

    static final int SORT_MOVIE_BY_POPULARITY = 0;
    static final int SORT_MOVIE_BY_RATING = 1;
    private static final String MOVIE_SORT_KEY = "movie.list.sort.ORDER";

    MovieListPref(@NonNull Context context) {
        mPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    int getMovieSortOrder() {
        return mPreference.getInt(MOVIE_SORT_KEY, SORT_MOVIE_BY_POPULARITY);
    }

    void setMovieSortOrder(int movieSortOrder) {
        mPreference.edit().putInt(MOVIE_SORT_KEY, movieSortOrder).apply();
    }

}
