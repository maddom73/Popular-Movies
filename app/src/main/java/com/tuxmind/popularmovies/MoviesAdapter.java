package com.tuxmind.popularmovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * Created by maddom73 on 29/08/15.
 */
public class MoviesAdapter extends CursorAdapter {

    private boolean mUseTodayLayout = true;



    public static class ViewHolder {
        public final ImageView posterView;
        public final TextView titleView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.list_item_poster);
            titleView = (TextView) view.findViewById(R.id.text);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movies, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String url = "http://image.tmdb.org/t/p/w185/" + cursor.getString(MoviesFragment.COLUMN_MOVIE_POSTER);

               Picasso.with(context)
                        .load(url)
                        .placeholder(R.drawable.posterno)
                        .error(R.drawable.error)
                        .tag(context)
                        .into(viewHolder.posterView);

      //  viewHolder.posterView.setImageResource(R.drawable.posterno);
        viewHolder.titleView.setText(cursor.getString((MoviesFragment.COLUMN_ORIGINAL_TITLE)));
        System.out.println("Picasso: " + url);
    }
    public void setUseTodayLayout(boolean useTodayLayout) {

        mUseTodayLayout = useTodayLayout;
    }
}
