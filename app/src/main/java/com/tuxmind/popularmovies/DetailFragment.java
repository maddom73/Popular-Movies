package com.tuxmind.popularmovies;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tuxmind.popularmovies.data.MoviesContract;
import com.tuxmind.popularmovies.data.MoviesContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by maddom73 on 31/08/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.OnScrollListener {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String MOVIES_SHARE_HASHTAG = " #PopularMovies";

    private ShareActionProvider mShareActionProvider;
    private String rDate, pPath, description, oTitle, rate;
    private Uri mUri;
    TrailerAdapter adapter;
    boolean onClicked = false;
    String videoId;
    String mMovieId;
    public static List<Review> REVIEW_LIST;
    public static List<Review> list = new ArrayList<Review>();
    ReviewParse reAsyncTask;
    int ids;
    long myMovieId;
    boolean del;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_DATE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_SYNOSIS,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_MOVIE_POSTER,
            MovieEntry.COLUMN_USER_RATING,


    };

    public static final int COL_ID = 0;
   /*  public static final int COL_MOVIE_DATE = 1;
    public static final int COL_MOVIE_SYNOSIS = 2;
    public static final int COL_ORIGINAL_TITLE = 3;
    public static final int COL_MOVIE_POSTER = 4;
    public static final int COL_USER_RATING = 5;
    public static final int COL_MOVIE_ID = 6;*/


    private ImageView mPosterView, mTrailer, mTrailer1, mTrailer2;
    private TextView mDateView;
    private TextView mSynosisView;
    private TextView mOriginalTitleView;
    private TextView mUserRatingView;
    private TextView mReview;
    private ListView tList;
    private View header;
    private AlertDialog dialog;
    Bundle arguments;
    ImageButton star;
    SharedPreferences prefs;
    String favoriteStar;
    View rootView;
    List<Trailer> videoList;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            ids = arguments.getInt("mid");
            mMovieId = arguments.getString("id");
            rDate = arguments.getString("release_date");
            pPath = arguments.getString("poster_path");
            description = arguments.getString("overview");
            oTitle = arguments.getString("original_title");
            rate = arguments.getString("vote_average");
        }

        rootView = inflater.inflate(
                R.layout.trailer_list_layout, null, false);
        tList = (ListView) rootView.findViewById(R.id.listView_trailer);


        header = (View) inflater.inflate(R.layout.fragment_detail, tList,
                false);
        mPosterView = (ImageView) header.findViewById(R.id.detail_poster);
        mDateView = (TextView) header.findViewById(R.id.detail_release_date_textview);
        mSynosisView = (TextView) header.findViewById(R.id.detail_synopsis_textview);
        mOriginalTitleView = (TextView) header.findViewById(R.id.detail_title_textview);
        mUserRatingView = (TextView) header.findViewById(R.id.detail_rating_textview);
        mReview = (TextView) header.findViewById(R.id.review_textview);
        star = (ImageButton) header.findViewById(R.id.favorite);

        if (adapter == null) {
            if (MainActivity.mTwoPane == true) {

                videoList = MainActivity.VIDEO_LIST;

            } else {
                videoList = DetailActivity.VIDEO_LIST;

            }
            adapter = new TrailerAdapter(getActivity(),
                    videoList, R.layout.list_trailer);
            System.out.println("VIDEO_LIST: " + videoList);
        }

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (oTitle != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }
    }

    private Intent createShareMovieIntent() {
        Trailer currentEntry = adapter.getItem(0);
        System.out.println("Posizione: " + currentEntry);
        videoId = currentEntry.trailerId;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + videoId);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);


        if (header != null) {
            tList.addHeaderView(header, null, false);

        }

        tList.setAdapter(adapter);

        star.setOnClickListener(new View.OnClickListener() {

            public void onClick(View star) {
                star.setSelected(!star.isSelected());
                if (star.isSelected()) {

                    storeFavorite();

                } else {
                    deleteFavorite();

                }

            }
        });


        tList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                position -= tList.getHeaderViewsCount();
                System.out.println("getHeaderViewsCount: " + position);
                onListItemClick((ListView) parent, view, position, id);
            }

            private void onListItemClick(ListView parent, View view, int position, long id) {
                Activity activity = getActivity();

                if (activity != null) {
                    onClicked = true;
                    Trailer currentEntry = adapter.getItem(position);
                    System.out.println("Posizione: " + currentEntry);
                    videoId = currentEntry.trailerId;
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + videoId));
                        startActivity(intent);

                    }
                } else {
                    Toast.makeText(getActivity(),
                            getString(R.string.videonot),
                            Toast.LENGTH_SHORT).show();
                }

            }


        });
        adapter.notifyDataSetChanged();
        tList.setOnScrollListener(this);
        mReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reAsyncTask = new ReviewParse();
                reAsyncTask.execute(mMovieId);

                list.clear();

                try {
                    if (reAsyncTask.get() == true) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Reviews");
                        ListView listReview = new ListView(getActivity());
                        listReview.setAdapter(new ReviewAdapter(getActivity(), REVIEW_LIST, R.layout.list_review));

                        builder.setView(listReview);
                        dialog = builder.create();
                        dialog.show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    void onSortChanged(String newSort) {
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
        if (null != mUri) {
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
            if (mPosterView != null)
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


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public class ReviewParse extends AsyncTask<String, Void, Boolean> {

        public final String LOG_TAG = ReviewParse.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            boolean status = false;
            Log.d(LOG_TAG, "Starting asincytask");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJsonStr = null;
            String apiKey = DeveloperKey.DEVELOPER_KEY;

            try {

                final String REVIEWS_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + mMovieId + "/reviews?";

                final String QUERY_KEY = "api_key";


                Uri builtUri = Uri.parse(REVIEWS_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_KEY, apiKey)
                        .build();
                URL url = new URL(builtUri.toString());

                System.out.println("URL: " + url);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return false;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return false;
                }
                reviewJsonStr = buffer.toString();
                System.out.println("reviewJsonStr: " + reviewJsonStr);
                getReviewFromJson(reviewJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            REVIEW_LIST = Collections.unmodifiableList(list);

            status = true;

            return status;
        }

        private void getReviewFromJson(String reviewJsonStr)
                throws JSONException {
            final String REVIEW_RESULTS = "results";
            final String REVIEW_AUTHOR = "author";
            final String REVIEW_CONTENT = "content";
            try {
                JSONObject movieJson = new JSONObject(reviewJsonStr);
                JSONArray movieArray = movieJson.getJSONArray(REVIEW_RESULTS);


                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject mMovie = movieArray.getJSONObject(i);
                    String reviewAuthor = mMovie.getString(REVIEW_AUTHOR);
                    String reviewContent = mMovie.getString(REVIEW_CONTENT);
                    list.add(new Review(reviewAuthor, reviewContent));

                }


            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);


        }
    }

    private void storeFavorite() {

        addFavorite(mMovieId);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putString("favorite" + mMovieId, mMovieId).apply();
        System.out.println("favoriteStore: " + "favorite" + mMovieId);
    }

    private void deleteFavorite() {

        if (favoriteStar.equals(mMovieId)) {
            getActivity().getContentResolver().delete(MoviesContract.FavoriteEntry.CONTENT_URI,
                    MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID + " = " + mMovieId,
                    null);
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.edit().putBoolean("deleteId" + mMovieId, true).apply();

        }

    }

    long addFavorite(String mMovieID) {


        Cursor movieCursor = getActivity().getContentResolver().query(
                MoviesContract.FavoriteEntry.CONTENT_URI,
                new String[]{MoviesContract.FavoriteEntry._ID},
                MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{mMovieId},
                null);

        if (movieCursor.moveToFirst()) {
            int locationIdIndex = movieCursor.getColumnIndex(MoviesContract.FavoriteEntry._ID);
            myMovieId = movieCursor.getLong(locationIdIndex);
        } else {

            ContentValues movieValues = new ContentValues();
            movieValues.put(MoviesContract.FavoriteEntry.COLUMN_DATE, System.currentTimeMillis());
            movieValues.put(MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovieId);
            movieValues.put(MoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE, rDate);
            movieValues.put(MoviesContract.FavoriteEntry.COLUMN_MOVIE_POSTER, pPath);
            movieValues.put(MoviesContract.FavoriteEntry.COLUMN_ORIGINAL_TITLE, oTitle);
            movieValues.put(MoviesContract.FavoriteEntry.COLUMN_SYNOSIS, description);
            movieValues.put(MoviesContract.FavoriteEntry.COLUMN_USER_RATING, rate);


            Uri insertedUri = getActivity().getContentResolver().insert(
                    MoviesContract.FavoriteEntry.CONTENT_URI,
                    movieValues
            );

            myMovieId = ContentUris.parseId(insertedUri);

        }
        System.out.println("myMovieId: " + myMovieId);
        System.out.println("ids: " + ids);
        movieCursor.close();
        return myMovieId;
    }


    @Override
    public void onResume() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        favoriteStar = prefs.getString("favorite" + mMovieId, "");
        del = prefs.getBoolean("deleteId" + mMovieId, false);
        System.out.println("deleteId: " + del);
        System.out.println("favoriteResume: " + "favorite" + mMovieId);
        if (del == true) {
            favoriteStar = "";
        }
        if (favoriteStar.equals(mMovieId)) {
            star.setSelected(true);
        } else {
            star.setSelected(false);
        }

        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
