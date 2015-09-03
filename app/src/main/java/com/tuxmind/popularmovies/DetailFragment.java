package com.tuxmind.popularmovies;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tuxmind.popularmovies.data.MoviesContract;
import com.tuxmind.popularmovies.data.MoviesContract.MovieEntry;

/**
 * Created by maddom73 on 31/08/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String MOVIES_SHARE_HASHTAG = " #PopularMovies";

    private ShareActionProvider mShareActionProvider;
    private String mMovie, rDate, pPath, description, oTitle, rate;
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_DATE,
            MovieEntry.COLUMN_SYNOSIS,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_MOVIE_POSTER,
            MovieEntry.COLUMN_USER_RATING,
            MovieEntry.COLUMN_MOVIE_ID,
    };

  private ImageView mPosterView;
    private TextView mDateView;
    private TextView mSynosisView;
    private TextView mOriginalTitleView;
    private TextView mUserRatingView;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DetailFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            rDate = arguments.getString("release_date");
            pPath = arguments.getString("poster_path");
            description = arguments.getString("overview");
            oTitle = arguments.getString("original_title");
            rate = arguments.getString("vote_average");
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mPosterView = (ImageView) rootView.findViewById(R.id.detail_poster);
        mDateView = (TextView) rootView.findViewById(R.id.detail_release_date_textview);
        mSynosisView = (TextView) rootView.findViewById(R.id.detail_synopsis_textview);
        mOriginalTitleView = (TextView) rootView.findViewById(R.id.detail_title_textview);
        mUserRatingView = (TextView) rootView.findViewById(R.id.detail_rating_textview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mMovie != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie + MOVIES_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onSortChanged( String newSort ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {

            Uri updatedUri = MoviesContract.MovieEntry.buildMovieSort(newSort);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            String posterUrl = "http://image.tmdb.org/t/p/w185/" + pPath;
            System.out.println("posterUrl: " + posterUrl);

            Picasso.with(getActivity())
                    .load(posterUrl)
                    .placeholder(R.drawable.posterno)
                    .error(R.drawable.error)
                    .tag(getActivity())
                    .into(mPosterView);


            mDateView.setText(rDate);
            mSynosisView.setText(description);

            mOriginalTitleView.setText(oTitle);
            mUserRatingView.setText(rate);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
