package com.tuxmind.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by maddom73 on 08/09/15.
 */
public class ReviewAdapter extends BaseAdapter {
    // View lookup cache
    private List<Review> entries;
    private LayoutInflater inflater;
    private int lastPosition = -1;
    Context mContext;

    public ReviewAdapter(Context context, List<Review> entries, int textViewResourceId) {
        this.entries = entries;
        this.mContext = context;

        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return entries.size();

    }

    @Override
    public Review getItem(int position) {

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

            Review entry = entries.get(position);


            if (view == null) {
                view = inflater
                        .inflate(R.layout.list_review, parent, false);

            } else {

            }

            TextView labelAuthor = ((TextView) view.findViewById(R.id.list_review_author));
            TextView labelContent = ((TextView) view.findViewById(R.id.review_content));
            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            view.startAnimation(animation);
            lastPosition = position;
            labelAuthor.setText(entry.reviewAuthor);
            labelContent.setText(entry.reviewContent);


        } catch (IndexOutOfBoundsException e) {

        }
        return view;
    }
}
