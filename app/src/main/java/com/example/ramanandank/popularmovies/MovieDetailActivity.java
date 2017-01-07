package com.example.ramanandank.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ramanandank.popularmovies.data.MovieDbContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ramanandank on 25/09/16.
 */

/**
 * Detail activity for the selected movie from grid view.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mBackdropImg;
    private ImageView mPosterImg;
    private TextView mSynopsisDes;
    private RatingBar mMovieRatingBar;
    private TextView mRatingTextView;
    private TextView mMovieReleaseDate;
    private ImageView mFavoriteImageView;

    private static final String LOG_TAG = "MovieDetailActivity";
    private static final String ARRAY_OF_RESULTS = "results";

    private MovieData mMovieData = new MovieData(0/* id */, "title", "url", "url",
            "synopsis", "date", 5/* Avg vote */, 1.0 /* popularity */, 100/*vote count*/, null, null);

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
                mRatingTextView.setText(getString(R.string.rating_text_view_string_int, (int) rating / 2));
            } else {
                mRatingTextView.setText(getString(R.string.rating_text_view_string, rating / 2));
            }
            setFavorite();
            mMovieRatingBar.setRating((float) mMovieData.getMovieVoteAvg() / 2);
            mMovieReleaseDate.setText(dateFormatter(mMovieData.getMovieReleaseDate()));
        }
        mFavoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean inFavorites = checkFavorites();
                if (inFavorites) {
                    deleteFromFavorites();
                } else {
                    addToFavorites();
                }
                setFavorite();
            }
        });
        renderTrailers();
        renderReviews();
    }

    private void setFavorite() {
        boolean inFavorites = checkFavorites();
        if (inFavorites) {
            mFavoriteImageView.setImageResource(R.drawable.favorite_added);
        } else {
            mFavoriteImageView.setImageResource(R.drawable.favorite_removed);
        }
    }

    private boolean checkFavorites() {
        Uri uri = MovieDbContract.MovieEntry.buildMovieUri(mMovieData.getMovieId());
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = null;
        try {

            cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst())
                return true;

        } finally {

            if (cursor != null)
                cursor.close();

        }
        return false;
    }

    private void addToFavorites() {

        Uri uri = MovieDbContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = this.getContentResolver();
        ContentValues values = new ContentValues();
        values.clear();
        values.put(MovieDbContract.MovieEntry.MOVIE_ID, mMovieData.getMovieId());
        values.put(MovieDbContract.MovieEntry.MOVIE_BACKDROP_URI, mMovieData.getMovieBackdropImgUrl());
        values.put(MovieDbContract.MovieEntry.MOVIE_TITLE, mMovieData.getMovieTitle());
        values.put(MovieDbContract.MovieEntry.MOVIE_POSTER, mMovieData.getMoviePosterImgUrl());
        values.put(MovieDbContract.MovieEntry.MOVIE_SYNOPSIS, mMovieData.getMovieSynopsis());
        values.put(MovieDbContract.MovieEntry.MOVIE_VOTE_AVERAGE, mMovieData.getMovieVoteAvg());
        values.put(MovieDbContract.MovieEntry.MOVIE_RELEASE_DATE, mMovieData.getMovieReleaseDate());
        values.put(MovieDbContract.MovieEntry.MOVIE_REVIEWS, mMovieData.getMovieReviews());
        values.put(MovieDbContract.MovieEntry.MOVIE_TRAILERS, mMovieData.getMovieTrailers());
        resolver.insert(uri, values);
    }


    /*
    * delete movie from the favorites in the DB
    * */
    private void deleteFromFavorites() {
        Uri uri = MovieDbContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = this.getContentResolver();
        resolver.delete(uri, MovieDbContract.MovieEntry.MOVIE_ID + " = ? ",
                new String[]{mMovieData.getMovieId() + ""});
    }


    private void renderReviews() {
        JSONObject jsonObject;
        final String AUTHOR = "author";
        final String CONTENT = "content";
        try {
            jsonObject = new JSONObject(mMovieData.getMovieReviews());
            JSONArray reviewArray = jsonObject.getJSONArray(ARRAY_OF_RESULTS);
            if (reviewArray.length() > 0) {
                LinearLayout innerScrollLayout = (LinearLayout)
                        this.findViewById(R.id.outer_liner_layout);
                LayoutInflater layoutInflater = this.getLayoutInflater();
                View reviewListView = layoutInflater.inflate(R.layout.review_header,
                        innerScrollLayout, false);
                innerScrollLayout.addView(reviewListView);
                LinearLayout reviewList = (LinearLayout) reviewListView.findViewById(R.id.review_list);
                for (int i = 0; i < reviewArray.length(); ++i) {
                    View reviewListItem = layoutInflater.inflate(R.layout.review_item_layout, reviewList, false);
                    JSONObject review = reviewArray.getJSONObject(i);
                    final String reviewAuthor = review.getString(AUTHOR);
                    String reviewContent = review.getString(CONTENT);
                    TextView reviewAuthorTextView = (TextView) reviewListItem.findViewById(R.id.review_author);
                    TextView reviewContentTextView = (TextView) reviewListItem.findViewById(R.id.review_content);
                    reviewAuthorTextView.setText(reviewAuthor);
                    reviewContentTextView.setText(reviewContent);
                    reviewList.addView(reviewListItem);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void renderTrailers() {
        JSONObject jsonObject;
        final String TRAILER_ID = "key";
        final String TRAILER_TITLE = "name";
        try {
            jsonObject = new JSONObject(mMovieData.getMovieTrailers());
            JSONArray trailerArray = jsonObject.getJSONArray(ARRAY_OF_RESULTS);
            if (trailerArray.length() > 0) {
                LinearLayout innerScrollLayout = (LinearLayout)
                        this.findViewById(R.id.outer_liner_layout);
                LayoutInflater layoutInflater = this.getLayoutInflater();
                View trailersListView = layoutInflater.inflate(R.layout.trailer_header,
                        innerScrollLayout, false);
                innerScrollLayout.addView(trailersListView);
                LinearLayout trailerList = (LinearLayout) trailersListView.findViewById(R.id.trailers_list);
                for (int i = 0; i < trailerArray.length(); ++i) {
                    View trailerListItem = layoutInflater.inflate(R.layout.trailer_item_layout, trailerList, false);
                    JSONObject trailer = trailerArray.getJSONObject(i);
                    final String trailerId = trailer.getString(TRAILER_ID);
                    String trailerTitle = trailer.getString(TRAILER_TITLE);
                    TextView trailerName = (TextView) trailerListItem.findViewById(R.id.trailer_name);
                    trailerName.setText(trailerTitle);
                    trailerList.addView(trailerListItem);
                    trailerListItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerId));
                            intent.putExtra("VIDEO_ID", trailerId);
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException ex) {
                                Log.i(LOG_TAG, "youtube app not installed");
                            }

                        }
                    });

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mBackdropImg = (ImageView) findViewById(R.id.movie_detail_backdrop_image_view);
        mPosterImg = (ImageView) findViewById(R.id.movie_detail_poster_image_view);
        mSynopsisDes = (TextView) findViewById(R.id.movie_detail_synopsis_text_view);
        mMovieRatingBar = (RatingBar) findViewById(R.id.movie_detail_rating_bar);
        mMovieReleaseDate = (TextView) findViewById(R.id.movie_detail_release_date_text_view);
        mRatingTextView = (TextView) findViewById(R.id.movie_detail_rating_text_view);
        mFavoriteImageView = (ImageView) findViewById(R.id.movie_fav);
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

