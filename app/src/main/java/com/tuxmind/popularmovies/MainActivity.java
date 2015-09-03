package com.tuxmind.popularmovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tuxmind.popularmovies.sync.PopularMoviesSyncAdapter;

public class MainActivity extends ActionBarActivity implements MoviesFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mSort;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSort = Utility.getPreferredSort(this);
        System.out.println("mSort: " + mSort);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setElevation(0f);


        MoviesFragment moviesFragment = ((MoviesFragment) getFragmentManager()
                .findFragmentById(R.id.fragment));

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

    @Override
    public void onItemSelected(Uri contentUri, String date, String synopsis,
                               String oTitle, String mPoster, String uRating ) {
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(contentUri);
        intent.putExtra("release_date", date);
        intent.putExtra("overview" , synopsis);
        intent.putExtra("original_title" , oTitle);
        intent.putExtra("poster_path" , mPoster);
        intent.putExtra("vote_average" , uRating);
        startActivity(intent);
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
    }

}
