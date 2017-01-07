package com.example.ramanandank.popularmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ramanandank on 26/12/16.
 */

public class MovieDbContract {
    public static final String CONTENT_AUTHORITY = "com.popular.movie.android";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";


    public static final class MovieEntry implements BaseColumns {

        public static final int PROJECTION_MOVIE_ID = 1;
        public static final int PROJECTION_MOVIE_BACKDROP_URI = 2;
        public static final int PROJECTION_MOVIE_TITLE = 3;
        public static final int PROJECTION_MOVIE_POSTER = 4;
        public static final int PROJECTION_MOVIE_SYNOPSIS = 5;
        public static final int PROJECTION_MOVIE_VOTE_AVERAGE = 6;
        public static final int PROJECTION_MOVIE_RELEASE_DATE = 7;
        public static final int PROJECTION_MOVIE_REVIEWS = 8;
        public static final int PROJECTION_MOVIE_TRAILERS = 9;


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_ID = "id";
        public static final String MOVIE_BACKDROP_URI = "backdrop_path";
        public static final String MOVIE_TITLE = "original_title";
        public static final String MOVIE_POSTER = "poster_path";
        public static final String MOVIE_SYNOPSIS = "synopsis";
        public static final String MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String MOVIE_RELEASE_DATE = "release_date";
        public static final String MOVIE_REVIEWS = "reviews";
        public static final String MOVIE_TRAILERS = "trailers";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
