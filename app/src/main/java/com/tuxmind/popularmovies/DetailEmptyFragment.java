package com.tuxmind.popularmovies;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by maddom73 on 22/09/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailEmptyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;


        rootView = (View) inflater.inflate(R.layout.fragment_empty, null,
                false);

        return rootView;

    }
}
