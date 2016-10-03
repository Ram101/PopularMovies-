package com.example.ramanandank.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by ramanandank on 25/09/16.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mBackdropImg;
    private ImageView mPosterImg;
    private TextView mSynopsisDes;
    private RatingBar mMovieRatingBar;
    private TextView mRatingTextView;
    private TextView mMovieReleaseDate;

    private MovieData mMovieData = new MovieData(0/* id */, "title", "url", "url",
            "synopsis", "date", 5/* Avg vote */, 1.0 /* popularity */, 100/*vote count*/);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_view);
        initToolbar();
        initView();
        setData();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    private void setData() {
        Bundle movieData = getIntent().getExtras();
        if (movieData != null) {
            mMovieData = movieData.getParcelable("movie_data");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mMovieData.getMovieTitle());
            }
            if (!mMovieData.getMovieBackdropImgUrl().equals("null")) {
                Picasso.with(this).load(mMovieData.getMovieBackdropImgUrl())
                        .placeholder(R.drawable.placeholder_backdrop).into(mBackdropImg);
            } else {
                mBackdropImg.setMaxHeight(750);
                Picasso.with(this).load(mMovieData.getMoviePosterImgUrl())
                        .placeholder(R.drawable.placeholder_backdrop).into(mBackdropImg);

            }
            Picasso.with(this).load(mMovieData.getMoviePosterImgUrl())
                    .placeholder(R.drawable.placeholder_poster).into(mPosterImg);
            mSynopsisDes.setText(mMovieData.getMovieSynopsis());
            double rating = mMovieData.getMovieVoteAvg();
            if (rating == (int) rating) {
                mRatingTextView.setText(getString(R.string.rating_text_view_string_int, (int) rating));
            } else {
                mRatingTextView.setText(getString(R.string.rating_text_view_string, rating));
            }
            mMovieRatingBar.setRating((float) mMovieData.getMovieVoteAvg());
            mMovieReleaseDate.setText(dateFormatter(mMovieData.getMovieReleaseDate()));
        }
    }

    private void initView() {
        mBackdropImg = (ImageView) findViewById(R.id.movie_detail_backdrop_image_view);
        mPosterImg = (ImageView) findViewById(R.id.movie_detail_poster_image_view);
        mSynopsisDes = (TextView) findViewById(R.id.movie_detail_synopsis_text_view);
        mMovieRatingBar = (RatingBar) findViewById(R.id.movie_detail_rating_bar);
        mMovieReleaseDate = (TextView) findViewById(R.id.movie_detail_release_date_text_view);
        mRatingTextView = (TextView) findViewById(R.id.movie_detail_rating_text_view);
    }

    private String dateFormatter(@NonNull String date) {
        if (date.equals("")) {
            return date;
        }
        String partOfDate[] = date.split("-");
        int year = Integer.valueOf(partOfDate[0]);
        int month = Integer.valueOf(partOfDate[1]);
        int day = Integer.valueOf(partOfDate[2]);
        String monthList[] = getResources().getStringArray(R.array.month_names);
        return day + " " + monthList[month - 1] + " " + year;
    }
}

