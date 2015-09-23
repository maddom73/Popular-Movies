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
    public static final String PATH_FAVORITE = "favorite";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;


        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_DATE = "date_time";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_MOVIE_POSTER = "poster_path";
        public static final String COLUMN_SYNOSIS = "overview";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_USER_RATING = "vote_average";



        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }
        public static Uri buildFavoriteMovieUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildFavoriteWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }
        public static Uri buildFavoriteId(String favoriteId) {
            return CONTENT_URI.buildUpon().appendPath(favoriteId).build();
        }
        public static String getFavoriteIdFromUriDate (Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";



        public static final String COLUMN_DATE = "date_time";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";

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



        public static Uri buildMovieSortDate(String sortSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(sortSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }
        public static String getFavoriteFromUri(Uri uri) {

            return uri.getPathSegments().get(1);
        }


    }

}

