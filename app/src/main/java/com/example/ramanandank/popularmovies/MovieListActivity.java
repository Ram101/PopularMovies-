package com.example.ramanandank.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ramanandank.popularmovies.data.MovieDbContract;
import com.facebook.stetho.Stetho;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MovieListActivity extends AppCompatActivity {

    private MovieListPref mMovieListPref;

    private GridView mMovieGridView;
    private MovieListAdapter mMovieListAdapter;
    private final ArrayList<MovieData> mMovieList = new ArrayList<>();

    private static final String SORT_POPULAR = "popular";
    private static final String SORT_RATING = "top_rated";
    private static final String TRAILER_DATA = "videos";
    private static final String REVIEW_DATA = "reviews";

    private static final String LOG_TAG = "MovieListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        mMovieListPref = new MovieListPref(this);
        mMovieGridView = (GridView) findViewById(R.id.movie_grid_view);
        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("movie_data", (Parcelable) mMovieListAdapter.getItem(position));
                startActivity(intent);
            }
        });
        mMovieListAdapter = new MovieListAdapter(this, mMovieList);
        mMovieGridView.setAdapter(mMovieListAdapter);
        if (mMovieList.size() == 0) {
            switch (mMovieListPref.getMovieSortOrder()) {
                case 0:
                    new MovieFetchTask().execute(SORT_POPULAR);
                    break;
                case 1:
                    new MovieFetchTask().execute(SORT_POPULAR);
                    break;
                case 2:
                    new MovieFetchFromDb().execute();
                    break;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        if (mMovieListPref.getMovieSortOrder() == MovieListPref.SORT_MOVIE_BY_POPULARITY) {
            menu.findItem(R.id.sort_by_popularity).setChecked(true);
        } else {
            menu.findItem(R.id.sort_by_top_ratting).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_by_popularity) {
            new MovieFetchTask().execute(SORT_POPULAR);
            mMovieGridView.smoothScrollToPosition(0);
            item.setChecked(true);
            mMovieListPref.setMovieSortOrder(MovieListPref.SORT_MOVIE_BY_POPULARITY);
            return true;
        }
        if (id == R.id.sort_by_top_ratting) {
            new MovieFetchTask().execute(SORT_RATING);
            mMovieGridView.smoothScrollToPosition(0);
            item.setChecked(true);
            mMovieListPref.setMovieSortOrder(MovieListPref.SORT_MOVIE_BY_RATING);
            return true;
        }
        if (id == R.id.show_fav) {
            new MovieFetchFromDb().execute();
            mMovieGridView.smoothScrollToPosition(0);
            item.setChecked(true);
            mMovieListPref.setMovieSortOrder(MovieListPref.SHOW_FAV);
        }
        return super.onOptionsItemSelected(item);
    }

    private class MovieFetchFromDb extends AsyncTask<Void, Void, ArrayList<MovieData>> {
        @Override
        protected void onPostExecute(ArrayList<MovieData> result) {
            mMovieListAdapter = new MovieListAdapter(MovieListActivity.this, result);
            mMovieGridView.setAdapter(mMovieListAdapter);
        }

        @Override
        protected ArrayList<MovieData> doInBackground(Void... voids) {
            ArrayList<MovieData> movieList = new ArrayList<>();
            Cursor movieCursor = getApplicationContext().getContentResolver().query(MovieDbContract.MovieEntry.CONTENT_URI, null, null, null, null);
            if (movieCursor != null && movieCursor.moveToFirst()) {
                do {
                    int movieId = movieCursor.getInt(MovieDbContract.MovieEntry.PROJECTION_MOVIE_ID);
                    String moviePosterImgUrl = movieCursor.getString(MovieDbContract.MovieEntry.PROJECTION_MOVIE_POSTER);
                    String movieBackdropImgUr = movieCursor.getString(MovieDbContract.MovieEntry.PROJECTION_MOVIE_BACKDROP_URI);
                    String movieTitle = movieCursor.getString(MovieDbContract.MovieEntry.PROJECTION_MOVIE_TITLE);
                    String movieSynopsis = movieCursor.getString(MovieDbContract.MovieEntry.PROJECTION_MOVIE_SYNOPSIS);
                    String movieReleaseDate = movieCursor.getString(MovieDbContract.MovieEntry.PROJECTION_MOVIE_RELEASE_DATE);
                    double movieVoteAvg = movieCursor.getDouble(MovieDbContract.MovieEntry.PROJECTION_MOVIE_VOTE_AVERAGE);
                    String movieTrailers = movieCursor.getString(MovieDbContract.MovieEntry.PROJECTION_MOVIE_TRAILERS);
                    String movieReviews = movieCursor.getString(MovieDbContract.MovieEntry.PROJECTION_MOVIE_REVIEWS);

                    MovieData info = new MovieData(movieId, movieTitle, moviePosterImgUrl, movieBackdropImgUr,
                            movieSynopsis, movieReleaseDate, movieVoteAvg, 0,
                            0, movieTrailers, movieReviews);

                    movieList.add(info);
                } while (movieCursor.moveToNext());

            }
            if (movieCursor != null) {
                movieCursor.close();
            }
            return movieList;
        }
    }

    private class MovieFetchTask extends AsyncTask<String, Void, ArrayList<MovieData>> {
        @Override
        protected ArrayList<MovieData> doInBackground(String... movieSortOrder) {
            if (movieSortOrder.length == 0) {
                return null;
            }
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String movieJson = null;
            String apiKey = getString(R.string.movie_api_key);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").authority("api.themoviedb.org").appendPath("3").appendPath("movie").appendPath(movieSortOrder[0])
                    .appendQueryParameter("api_key", apiKey).appendQueryParameter("language", "en-US");
            try {
                URL url = new URL(builder.build().toString());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    movieJson = null;
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    if (buffer.length() == 0) {
                        movieJson = null;
                    }
                    movieJson = buffer.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                movieJson = null;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (movieJson == null) {
                return new ArrayList<>();
            } else {
                ArrayList<MovieData> movieList = new ArrayList<>();
                try {
                    String imgBaseUrl = "http://image.tmdb.org/t/p/";
                    JSONObject jsonObject = new JSONObject(movieJson);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        int movieId = object.getInt("id");
                        String moviePosterImgUrl = object.getString("poster_path");
                        String movieBackdropImgUr = object.getString("backdrop_path");
                        String movieTitle = object.getString("original_title");
                        String movieSynopsis = object.getString("overview");
                        String movieReleaseDate = object.getString("release_date");
                        double movieVoteAvg = object.getDouble("vote_average");
                        double moviePopularity = object.getDouble("popularity");
                        int movieVoteCount = object.getInt("vote_count");
                        String movieTrailers = getMovieTrailerOrReview(movieId, TRAILER_DATA);
                        String movieReviews = getMovieTrailerOrReview(movieId, REVIEW_DATA);
                        moviePosterImgUrl = imgBaseUrl + "w342" + moviePosterImgUrl;

                        if (!movieBackdropImgUr.equals("null")) {
                            movieBackdropImgUr = imgBaseUrl + "w500" + movieBackdropImgUr;
                        }

                        MovieData info = new MovieData(movieId, movieTitle, moviePosterImgUrl, movieBackdropImgUr,
                                movieSynopsis, movieReleaseDate, movieVoteAvg, moviePopularity,
                                movieVoteCount, movieTrailers, movieReviews);

                        movieList.add(info);
                    }

                    return movieList;
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error Parsing JSON");
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> result) {
            mMovieListAdapter = new MovieListAdapter(MovieListActivity.this, result);
            mMovieGridView.setAdapter(mMovieListAdapter);
        }
    }

    private String getMovieTrailerOrReview(int movieId, String type) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String movieJson = null;
        String apiKey = getString(R.string.movie_api_key);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.themoviedb.org").appendPath("3").appendPath("movie")
                .appendPath(String.valueOf(movieId)).appendPath(type)
                .appendQueryParameter("api_key", apiKey).appendQueryParameter("language", "en-US");
        try {
            URL url = new URL(builder.build().toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                movieJson = null;
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0) {
                    movieJson = null;
                }
                movieJson = buffer.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            movieJson = null;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return movieJson;
    }

}

