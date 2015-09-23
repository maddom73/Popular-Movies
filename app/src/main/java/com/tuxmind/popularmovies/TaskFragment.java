package com.tuxmind.popularmovies;

/**
 * Created by maddom73 on 20/09/15.
 */
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * TaskFragment manages a single background task and retains itself across
 * configuration changes.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TaskFragment extends Fragment {
    private static final String TAG = TaskFragment.class.getSimpleName();
    private static final boolean DEBUG = true; // Set this to false to disable logs.
    String movieID;

    interface TaskCallbacks {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute();
    }

    private TaskCallbacks mCallbacks;
    private TrailerParse mTask;
    private boolean mRunning;

    @Override
    public void onAttach(Activity activity) {
        if (DEBUG) Log.i(TAG, "onAttach(Activity)");
        super.onAttach(activity);
        if (!(activity instanceof TaskCallbacks)) {
            throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
        }

        mCallbacks = (TaskCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        DetailFragment.list.clear();
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.i(TAG, "onDestroy()");
        super.onDestroy();
        cancel();
    }

    /*****************************/
    /***** TASK FRAGMENT API *****/
    /*****************************/

    /**
     * Start the background task.
     */
    public void start() throws ExecutionException, InterruptedException {
        if (!mRunning) {
            mTask = new TrailerParse();
            mTask.execute().get();
            mRunning = true;
        }
    }

    /**
     * Cancel the background task.
     */
    public void cancel() {
        if (mRunning) {
            mTask.cancel(false);
            mTask = null;
            mRunning = false;
        }
    }

    /**
     * Returns the current state of the background task.
     */
    public boolean isRunning() {
        return mRunning;
    }

    public class TrailerParse extends AsyncTask<String, Void, Boolean> {

        public final String LOG_TAG = TrailerParse.class.getSimpleName();

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

            String trailerJsonStr = null;
            String apiKey = DeveloperKey.DEVELOPER_KEY;
            if (MainActivity.mTwoPane == true){
                movieID = MainActivity.mMovieId;
                System.out.println("MainTaskmMovieId: " + movieID);
            } else {
                movieID = DetailActivity.mMovieId;
            }
            try {

                final String TRAILERS_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + movieID + "/videos?";

                final String QUERY_KEY = "api_key";


                Uri builtUri = Uri.parse(TRAILERS_BASE_URL).buildUpon()
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
                trailerJsonStr = buffer.toString();
                System.out.println("movieJsonStr: " + trailerJsonStr);
                getTrailerFromJson(trailerJsonStr);

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
            if (MainActivity.mTwoPane == true){
                MainActivity.VIDEO_LIST = Collections.unmodifiableList(MainActivity.list);
            } else {
                DetailActivity.VIDEO_LIST = Collections.unmodifiableList(DetailActivity.list);
            }


            status = true;

            return status;
        }

        private void getTrailerFromJson(String trailerJsonStr)
                throws JSONException {
            final String TRAILER_RESULTS = "results";
            final String TRAILER_KEY = "key";
            final String TRAILER_TITLE = "name";
            try {
                JSONObject movieJson = new JSONObject(trailerJsonStr);
                JSONArray movieArray = movieJson.getJSONArray(TRAILER_RESULTS);



                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject mMovie = movieArray.getJSONObject(i);
                    String trailerKey = mMovie.getString(TRAILER_KEY);
                    String trailerTitle = mMovie.getString(TRAILER_TITLE);
                    if (MainActivity.mTwoPane == true){
                        MainActivity.list.add(new Trailer(trailerTitle, trailerKey));
                    } else {
                        DetailActivity.list.add(new Trailer(trailerTitle, trailerKey));
                    }


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
}