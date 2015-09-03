package com.tuxmind.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by maddom73 on 31/08/15.
 */
public class PopularMoviesSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static PopularMoviesSyncAdapter sPopularMoviesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("PMoviesSyncService", "onCreate - PopularMoviesSyncService");
        synchronized (sSyncAdapterLock) {
            if (sPopularMoviesSyncAdapter == null) {
                sPopularMoviesSyncAdapter = new PopularMoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopularMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
