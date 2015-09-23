package com.tuxmind.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by maddom73 on 06/09/15.
 */
public class TrailerAdapter extends BaseAdapter {
    // View lookup cache
    private List<Trailer> entries;
    private LayoutInflater inflater;


    public TrailerAdapter(Context context, List<Trailer> entries, int textViewResourceId) {
        this.entries = entries;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return entries.size();

    }

    @Override
    public Trailer getItem(int position) {

        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        try {

            Trailer entry = entries.get(position);


            if (view == null) {
                view = inflater
                        .inflate(R.layout.list_trailer, parent, false);

            }

            TextView label = ((TextView) view.findViewById(R.id.trailer_text));

            label.setText(entry.trailerName);

        } catch (IndexOutOfBoundsException e) {

        }
        return view;
    }
}