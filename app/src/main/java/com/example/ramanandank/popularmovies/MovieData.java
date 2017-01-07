package com.example.ramanandank.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ramanandank on 19/09/16.
 */

class MovieData implements Parcelable {

    private int movieId;
    private String movieTitle;
    private String moviePosterImgUrl;
    private String movieBackdropImgUrl;
    private String movieSynopsis;
    private String movieReleaseDate;
    private double movieVoteAvg;
    private double moviePopularity;
    private int movieVoteCount;
    private String movieTrailers;
    private String movieReviews;


    MovieData(int movieId, String movieTitle, String moviePosterImgUrl,
              String movieBackdropImgUrl, String movieSynopsis,
              String movieReleaseDate, double movieVoteAvg, double moviePopularity,
              int movieVoteCount, String movieTrailers, String movieReviews) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePosterImgUrl = moviePosterImgUrl;
        this.movieBackdropImgUrl = movieBackdropImgUrl;
        this.movieSynopsis = movieSynopsis;
        this.movieReleaseDate = movieReleaseDate;
        this.movieVoteAvg = movieVoteAvg;
        this.moviePopularity = moviePopularity;
        this.movieVoteCount = movieVoteCount;
        this.movieTrailers = movieTrailers;
        this.movieReviews = movieReviews;

    }

    private MovieData(Parcel in) {
        int[] movieInt = new int[2];
        in.readIntArray(movieInt);
        movieId = movieInt[0];
        movieVoteCount = movieInt[1];

        String[] movieStringData = new String[7];
        in.readStringArray(movieStringData);
        movieTitle = movieStringData[0];
        moviePosterImgUrl = movieStringData[1];
        movieBackdropImgUrl = movieStringData[2];
        movieSynopsis = movieStringData[3];
        movieReleaseDate = movieStringData[4];
        movieTrailers = movieStringData[5];
        movieReviews = movieStringData[6];

        double[] movieDouble = new double[2];
        in.readDoubleArray(movieDouble);
        movieVoteAvg = movieDouble[0];
        moviePopularity = movieDouble[1];
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    String getMovieBackdropImgUrl() {
        return movieBackdropImgUrl;
    }

    String getMoviePosterImgUrl() {
        return moviePosterImgUrl;
    }

    String getMovieTitle() {
        return movieTitle;
    }

    int getMovieId() {
        return movieId;
    }

    String getMovieSynopsis() {
        return movieSynopsis;
    }

    String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    double getMovieVoteAvg() {
        return movieVoteAvg;
    }

    String getMovieTrailers() {
        return movieTrailers;
    }

    String getMovieReviews() {
        return movieReviews;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(new int[]{movieId, movieVoteCount});
        parcel.writeStringArray(new String[]{
                movieTitle,
                moviePosterImgUrl,
                movieBackdropImgUrl,
                movieSynopsis,
                movieReleaseDate, movieTrailers, movieReviews
        });
        parcel.writeDoubleArray(new double[]{movieVoteAvg, moviePopularity});
    }
}
