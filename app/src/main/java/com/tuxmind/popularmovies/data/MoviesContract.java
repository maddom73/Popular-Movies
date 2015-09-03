package com.tuxmind.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by maddom73 on 29/08/15.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.tuxmind.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_SORT_ORDER = "sort_order";



    /* Inner class that defines the table contents of the weather table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date_time";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_MOVIE_ID = "id";

        public static final String COLUMN_MOVIE_POSTER = "poster_path";
        public static final String COLUMN_SYNOSIS = "overview";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_USER_RATING = "vote_average";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMovieSort(String sortSetting) {
            return CONTENT_URI.buildUpon().appendPath(sortSetting).build();
        }
        public static String getSortSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }

}

