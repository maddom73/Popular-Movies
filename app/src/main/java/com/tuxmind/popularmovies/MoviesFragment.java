package com.tuxmind.popularmovies;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.tuxmind.popularmovies.data.MoviesContract;
import com.tuxmind.popularmovies.sync.PopularMoviesSyncAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MoviesAdapter mMoviesAdapter;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final int MOVIES_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";

    private static final String[] MOVIES_COLUMNS = {

            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_DATE,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MoviesContract.MovieEntry.COLUMN_SYNOSIS,
            MoviesContract.MovieEntry.COLUMN_USER_RATING,


    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COLUMN_MOVIE_ID = 1;
    public static final int COLUMN_DATE = 2;
    public static final int COLUMN_RELEASE_DATE = 3;
    public static final int COLUMN_MOVIE_POSTER = 4;
    public static final int COLUMN_ORIGINAL_TITLE = 5;
    public static final int COLUMN_SYNOSIS = 6;
    public static final int COLUMN_USER_RATING = 7;
    ;


    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      //  inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_refresh) {
//            updateWeather();
//            return true;
//        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.gridView_movies);
        mGridView.setAdapter(mMoviesAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    ((Callback) getActivity())
                            .onItemSelected(MoviesContract.MovieEntry.buildMovieUri(
                                    cursor.getLong(COLUMN_MOVIE_ID)),
                                    cursor.getString(COLUMN_RELEASE_DATE), cursor.getString(COLUMN_SYNOSIS),
                                    cursor.getString(COLUMN_ORIGINAL_TITLE), cursor.getString(COLUMN_MOVIE_POSTER),
                                    cursor.getString(COLUMN_USER_RATING));
                }

                System.out.println("itemP: " + cursor.getLong(COLUMN_MOVIE_ID));

                mPosition = position;
                System.out.println("COLUMN_RELEASE_DATE: " +  cursor.getString(COLUMN_RELEASE_DATE));
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {

            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onSortChanged( ) {
        System.out.println("onSortChanged: true");
        updateMovies();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        System.out.println("LoaderManager: " + getLoaderManager().restartLoader(MOVIES_LOADER, null, this));
    }

    private void updateMovies() {
        PopularMoviesSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MoviesContract.MovieEntry.COLUMN_DATE + " ASC";

        String sortSetting = Utility.getPreferredSort(getActivity());
        System.out.println("sortSetting: " + sortSetting);
        Uri movieForSortUri = MoviesContract.MovieEntry.buildMovieSort(
                sortSetting);
        System.out.println("movieForSortUri: " + movieForSortUri);
        return new CursorLoader(getActivity(),
                movieForSortUri,
                MOVIES_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);

    }



    public interface Callback {
        public void onItemSelected(Uri dateUri, String date, String synopsis,
                                   String oTitle, String mPoster, String uRating);
    }
}
