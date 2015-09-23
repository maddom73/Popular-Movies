package com.tuxmind.popularmovies;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tuxmind.popularmovies.data.MoviesContract;


/**
 * Created by maddom73 on 29/08/15.
 */
public class MoviesAdapter extends CursorAdapter {

    ContentResolver mContent;
    public MoviesAdapter(Context context, Cursor c) {
        super(context, c);
        mContent = context.getContentResolver();
    }
    public static class ViewHolder {
        public final ImageView posterView;
        public final TextView titleView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.list_item_poster);
            titleView = (TextView) view.findViewById(R.id.text);
        }
    }
    String sortSetting;
    private int selectedIndex;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        selectedIndex = -1;
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movies, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (MainActivity.mFavorite == true || Utility.getFavorite(context)) {
            if (MoviesFragment.FAVORITE_COLUMNS != null) {
                String url = "http://image.tmdb.org/t/p/w185/" + cursor.getString(MoviesFragment.COLUMN_MOVIE_POSTER);
                System.out.println("mUrl: " + url);

                Picasso.with(context)
                        .load(url)
                        .placeholder(R.drawable.posterno)
                        .error(R.drawable.error)
                        .tag(context)
                        .into(viewHolder.posterView);

                viewHolder.titleView.setText(cursor.getString((MoviesFragment.COLUMN_ORIGINAL_TITLE)));
            }else{
                Toast.makeText(context, "No favorite movie bookmarked yet",
                        Toast.LENGTH_SHORT).show();
            }


        }
        if (MainActivity.mFavorite == false){

            String url = "http://image.tmdb.org/t/p/w185/" + cursor.getString(MoviesFragment.COLUMN_MOVIE_POSTER);

            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.posterno)
                    .error(R.drawable.error)
                    .tag(context)
                    .into(viewHolder.posterView);

            viewHolder.titleView.setText(cursor.getString((MoviesFragment.COLUMN_ORIGINAL_TITLE)));
        }

        System.out.println("Utility Adapter: " + Utility.getFavorite(context));
      //  viewHolder.posterView.setImageResource(R.drawable.posterno);


    }
    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return super.getCount();
    }



}
