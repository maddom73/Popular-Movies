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

import com.tuxmind.popularmovies.data.MoviesContract;
import com.tuxmind.popularmovies.sync.PopularMoviesSyncAdapter;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MoviesAdapter mMoviesAdapter;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final int MOVIES_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";
    CursorLoader cursorFav;
    CursorLoader cursorMov;

    public static boolean onSort = false;
    static final String[] MOVIES_COLUMNS = {

            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_DATE,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MoviesContract.MovieEntry.COLUMN_SYNOSIS,
            MoviesContract.MovieEntry.COLUMN_USER_RATING

    };

    public static final String[] FAVORITE_COLUMNS = {

            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry._ID,
            MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID,
            MoviesContract.FavoriteEntry.COLUMN_DATE,
            MoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE,
            MoviesContract.FavoriteEntry.COLUMN_MOVIE_POSTER,
            MoviesContract.FavoriteEntry.COLUMN_ORIGINAL_TITLE,
            MoviesContract.FavoriteEntry.COLUMN_SYNOSIS,
            MoviesContract.FavoriteEntry.COLUMN_USER_RATING
    };

    public static final int COLUMN__ID = 0;

    public static final int COLUMN_MOVIE_ID = 1;
    public static final int COLUMN_DATE = 2;
    public static final int COLUMN_RELEASE_DATE = 3;
    public static final int COLUMN_MOVIE_POSTER = 4;
    public static final int COLUMN_ORIGINAL_TITLE = 5;
    public static final int COLUMN_SYNOSIS = 6;
    public static final int COLUMN_USER_RATING = 7;


    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

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


                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {

                    ((Callback) getActivity())
                            .onItemSelected(MoviesContract.MovieEntry.buildMovieUri(
                                            cursor.getLong(COLUMN_MOVIE_ID)),
                                    cursor.getInt(COLUMN__ID),
                                    cursor.getString(COLUMN_MOVIE_ID),
                                    cursor.getString(COLUMN_RELEASE_DATE), cursor.getString(COLUMN_SYNOSIS),
                                    cursor.getString(COLUMN_ORIGINAL_TITLE), cursor.getString(COLUMN_MOVIE_POSTER),
                                    cursor.getString(COLUMN_USER_RATING));
                }

                System.out.println("itemP: " + cursor.getLong(COLUMN_MOVIE_ID));

                mPosition = position;
                System.out.println("COLUMN_RELEASE_DATE: " + cursor.getString(COLUMN_RELEASE_DATE));


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



    void onSortChanged() {
        onSort = true;
        System.out.println("onSortChanged: true");
        updateMovies();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        System.out.println("LoaderManager: " + getLoaderManager().restartLoader(MOVIES_LOADER, null, this));
    }


    void onFavorite() {

        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        mMoviesAdapter.setSelectedIndex(mPosition);
        mMoviesAdapter.notifyDataSetChanged();
    }


    private void updateMovies() {
        PopularMoviesSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (MainActivity.mFavorite == true) {
            String sortOrder = MoviesContract.FavoriteEntry._ID;
            String sortSetting = Utility.getPreferredSort(getActivity());
            Uri movieFavoriteUri = MoviesContract.FavoriteEntry.buildFavoriteMovieUri();
            System.out.println("movieFavoriteUri: " + movieFavoriteUri);
            cursorFav = new CursorLoader(getActivity(),
                    movieFavoriteUri,
                    FAVORITE_COLUMNS,
                    null,
                    null,
                    sortOrder);
            return cursorFav;

        } else {
            String sortOrder = MoviesContract.MovieEntry.COLUMN_DATE;
            String sortSetting = Utility.getPreferredSort(getActivity());
            System.out.println("sortSetting: " + sortSetting);
            Uri movieForSortUri = MoviesContract.MovieEntry.buildMovieSort(sortSetting);

            System.out.println("movieForSortUri: " + movieForSortUri);
            cursorMov = new CursorLoader(getActivity(),
                    movieForSortUri,
                    MOVIES_COLUMNS,
                    null,
                    null,
                    sortOrder);
            return cursorMov;

        }
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
        public void onItemSelected(Uri dateUri, int mId, String movieId, String date, String synopsis,
                                   String oTitle, String mPoster, String uRating);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SELECTED_KEY,
                mGridView.getCheckedItemPosition());
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}
