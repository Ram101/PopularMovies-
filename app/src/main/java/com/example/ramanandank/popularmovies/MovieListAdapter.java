package com.example.ramanandank.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ramanandank on 18/09/16.
 */

/**
 *  Movie Adapter for the grid view
 */
class MovieListAdapter extends BaseAdapter {

    private final Context mContext;

    private final ArrayList<MovieData> mMovieList;

    MovieListAdapter(Context context, ArrayList<MovieData> movieList) {
        mContext = context;
        mMovieList = movieList;
    }

    @Override
    public int getCount() {
        return mMovieList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMovieList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Holder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.grid_item_layout, viewGroup, false);
            viewHolder = new Holder();
            viewHolder.movieImage = (ImageView) convertView.findViewById(R.id.movie_grid_poster);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Holder) convertView.getTag();
        }
        Picasso.with(mContext)
                .load(mMovieList.get(position).getMoviePosterImgUrl())
                .placeholder(R.drawable.placeholder_poster)
                .into(viewHolder.movieImage);
        return convertView;
    }

    private static class Holder {
        ImageView movieImage;
    }
}
