package com.tuxmind.popularmovies;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by maddom73 on 01/09/15.
 */
public class DetailActivity extends ActionBarActivity implements TaskFragment.TaskCallbacks {

    private Bundle arguments;
    static String mMovieId;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = true; // Set this to false to disable logs.
    public static List<Trailer> VIDEO_LIST;
    public static List<Trailer> list = new ArrayList<Trailer>();
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private TaskFragment mTaskFragment;
    DetailFragment fragment;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            arguments.putInt("mid", getIntent().getIntExtra("mid", 0));
            arguments.putString("id", getIntent().getStringExtra("id"));
            arguments.putString("release_date", getIntent().getStringExtra("release_date"));
            arguments.putString("overview", getIntent().getStringExtra("overview"));
            arguments.putString("original_title", getIntent().getStringExtra("original_title"));
            arguments.putString("poster_path", getIntent().getStringExtra("poster_path"));
            arguments.putString("vote_average", getIntent().getStringExtra("vote_average"));


        }

        if (arguments != null) {
            mMovieId = arguments.getString("id");
        }


        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
        if (mTaskFragment.isRunning()) {
            mTaskFragment.cancel();

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

        if (savedInstanceState == null) {
            fragment = new DetailFragment();
            fragment.setArguments(arguments);
            System.out.println("arguments: " + arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG) Log.i(TAG, "onSaveInstanceState(Bundle)");
        super.onSaveInstanceState(outState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPause() {

        super.onPause();
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
