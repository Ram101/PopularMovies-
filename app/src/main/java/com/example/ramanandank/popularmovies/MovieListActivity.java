package com.example.ramanandank.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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

    private static final String LOG_TAG = "MovieListActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        mMovieListPref = new MovieListPref(this);
        mMovieGridView = (GridView) findViewById(R.id.movie_grid_view);
        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MovieListActivity.this,MovieDetailActivity.class);
                intent.putExtra("movie_data", (Parcelable) mMovieListAdapter.getItem(position));
                startActivity(intent);
            }
        });
        mMovieListAdapter = new MovieListAdapter(this, mMovieList);
        mMovieGridView.setAdapter(mMovieListAdapter);
        if (mMovieList.size() == 0) {
            new MovieFetchTask().execute(mMovieListPref.getMovieSortOrder() == 0 ? SORT_POPULAR : SORT_RATING);
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
        return super.onOptionsItemSelected(item);
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
                        buffer.append(line + "\n");
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
                return new ArrayList<MovieData>();
            } else {
                ArrayList<MovieData> movieList = new ArrayList<MovieData>();
                try {
                    String imgBaseUrl =  "http://image.tmdb.org/t/p/";
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

                        moviePosterImgUrl = imgBaseUrl + "w342" + moviePosterImgUrl;

                        if (!movieBackdropImgUr.equals("null")) {
                            movieBackdropImgUr = imgBaseUrl + "w500" + movieBackdropImgUr;
                        }

                        MovieData info = new MovieData(movieId, movieTitle, moviePosterImgUrl, movieBackdropImgUr,
                                movieSynopsis, movieReleaseDate, movieVoteAvg, moviePopularity, movieVoteCount);

                        movieList.add(info);
                    }

                    return movieList;
                } catch (JSONException e) {
                    Log.e(LOG_TAG,"Error Parsing JSON");
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


}

