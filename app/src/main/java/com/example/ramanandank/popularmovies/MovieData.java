package com.example.ramanandank.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ramanandank on 19/09/16.
 */
public class MovieData implements Parcelable {

    private int movieId;
    private String movieTitle;
    private String moviePosterImgUrl;
    private String movieBackdropImgUrl;
    private String movieSynopsis;
    private String movieReleaseDate;
    private double movieVoteAvg;
    private double moviePopularity;
    private int movieVoteCount;

    MovieData(int movieId, String movieTitle, String moviePosterImgUrl,
              String movieBackdropImgUrl, String movieSynopsis,
              String movieReleaseDate, double movieVoteAvg, double moviePopularity,
              int movieVoteCount) {
        this.movieId              = movieId;
        this.movieTitle           = movieTitle;
        this.moviePosterImgUrl    = moviePosterImgUrl;
        this.movieBackdropImgUrl  = movieBackdropImgUrl;
        this.movieSynopsis        = movieSynopsis;
        this.movieReleaseDate     = movieReleaseDate;
        this.movieVoteAvg         = movieVoteAvg;
        this.moviePopularity      = moviePopularity;
        this.movieVoteCount       = movieVoteCount;


    }

    private MovieData(Parcel in) {
        int[] movieInt = new int[2];
        in.readIntArray(movieInt);
        movieId        = movieInt[0];
        movieVoteCount = movieInt[1];

        String[] movieStringData = new String[5];
        in.readStringArray(movieStringData);
        movieTitle               = movieStringData[0];
        moviePosterImgUrl        = movieStringData[1];
        movieBackdropImgUrl      = movieStringData[2];
        movieSynopsis            = movieStringData[3];
        movieReleaseDate         = movieStringData[4];

        double[] movieDouble = new double[2];
        in.readDoubleArray(movieDouble);
        movieVoteAvg         = movieDouble[0];
        moviePopularity      = movieDouble[1];
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

    public String getMovieBackdropImgUrl() {
        return movieBackdropImgUrl;
    }

    public void setMovieBackdropImgUrl(String movieBackdropImgUrl) {
        this.movieBackdropImgUrl = movieBackdropImgUrl;
    }

    public String getMoviePosterImgUrl() {
        return moviePosterImgUrl;
    }

    public void setMoviePosterImgUrl(String moviePosterImgUrl) {
        this.moviePosterImgUrl = moviePosterImgUrl;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieSynopsis() {
        return movieSynopsis;
    }

    public void setMovieSynopsis(String movieSynopsis) {
        this.movieSynopsis = movieSynopsis;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public double getMovieVoteAvg() {
        return movieVoteAvg;
    }

    public void setMovieVoteAvg(double movieVoteAvg) {
        this.movieVoteAvg = movieVoteAvg;
    }

    public double getMoviePopularity() {
        return moviePopularity;
    }

    public void setMoviePopularity(double moviePopularity) {
        this.moviePopularity = moviePopularity;
    }

    public int getMovieVoteCount() {
        return movieVoteCount;
    }

    public void setMovieVoteCount(int movieVoteCount) {
        this.movieVoteCount = movieVoteCount;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(new int[]{movieId,movieVoteCount});
        parcel.writeStringArray(new String[]{
                movieTitle,
                moviePosterImgUrl,
                movieBackdropImgUrl,
                movieSynopsis,
                movieReleaseDate,
        });
        parcel.writeDoubleArray(new double[]{movieVoteAvg,moviePopularity});
    }
}
