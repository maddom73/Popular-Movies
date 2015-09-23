package com.tuxmind.popularmovies;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tuxmind.popularmovies.DetailEmptyFragment;
import com.tuxmind.popularmovies.sync.PopularMoviesSyncAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends ActionBarActivity implements MoviesFragment.Callback, TaskFragment.TaskCallbacks{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String DETAILEMPTYFRAGMENT_TAG = "DFEMPTAG";

    static boolean mTwoPane;
    private String mSort;
    static boolean mFavorite;
    private TaskFragment mTaskFragment;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    static String mMovieId;
    public static List<Trailer> VIDEO_LIST;
    public static List<Trailer> list = new ArrayList<Trailer>();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSort = Utility.getPreferredSort(this);
        System.out.println("mSort: " + mSort);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;


            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailEmptyFragment(), DETAILEMPTYFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }


        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onItemSelected(Uri contentUri, int mId, String movieId, String date, String synopsis,
                               String oTitle, String mPoster, String uRating ) {
        if (mTwoPane) {


            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);
            args.putInt("mid", mId);
            args.putString("id", movieId);
            args.putString("release_date", date);
            args.putString("overview", synopsis);
            args.putString("original_title", oTitle);
            args.putString("poster_path", mPoster);
            args.putString("vote_average", uRating);
            System.out.println("DetailFragment.DETAIL_URI: " + DetailFragment.DETAIL_URI);

            if (args != null) {
                mMovieId = args.getString("id");
            }
            System.out.println("MainmMovieId: " + mMovieId);


            FragmentManager fm = getFragmentManager();
            mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
            if (mTaskFragment == null) {
                mTaskFragment = new TaskFragment();
                fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
            }
            if (mTaskFragment.isRunning()) {
                mTaskFragment.cancel();
                mTaskFragment = new TaskFragment();
                fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
                try {
                    list.clear();

                    mTaskFragment.start();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    list.clear();

                    mTaskFragment.start();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            intent.putExtra("mid", mId);
            intent.putExtra("id", movieId);
            intent.putExtra("release_date", date);
            intent.putExtra("overview", synopsis);
            intent.putExtra("original_title", oTitle);
            intent.putExtra("poster_path", mPoster);
            intent.putExtra("vote_average", uRating);
            startActivity(intent);
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        String sort = Utility.getPreferredSort(this);
        System.out.println("Sort: " + sort);
        if (sort != null && !sort.equals(mSort)) {
            MoviesFragment mf = (MoviesFragment) getFragmentManager().findFragmentById(R.id.fragment);
            if (null != mf) {
                mf.onSortChanged();
            }
            DetailFragment df = (DetailFragment) getFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.onSortChanged(sort);
            }
            mSort = sort;
        }

         mFavorite = Utility.getFavorite(this);
        System.out.println("mFavorite: " + mFavorite);
        if (mFavorite == true || Utility.getFavorite(this)) {
            MoviesFragment mfs = (MoviesFragment) getFragmentManager().findFragmentById(R.id.fragment);
            if (null != mfs) {
                mfs.onFavorite();

            }
            DetailFragment df = (DetailFragment) getFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.onSortChanged(sort);
            }
        }
        if (mFavorite == false) {
            MoviesFragment mfs = (MoviesFragment) getFragmentManager().findFragmentById(R.id.fragment);
            if (null != mfs) {
                mfs.onFavorite();

            }
            DetailFragment df = (DetailFragment) getFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.onSortChanged(sort);
            }
        }
        System.out.println("Utility Main: " + Utility.getFavorite(this));
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {

    }
}
