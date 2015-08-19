package net.validcat.popularmoviesapp.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dobrunov on 29.07.2015.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "net.validcat.popularmoviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_THUMBNAIL_PATH = "thumb_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_FAVORITE = "favorite"; //TODO this value is hardcoded
        public static final String COLUMN_NEWEST = "vote_average"; //this value is hardcoded
        public static final String COLUMN_POPULAR = "popularity"; //this value is hardcoded
        public static final String COLUMN_DURATION = "duration";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMoviesUri() {
            return CONTENT_URI;
        }

        public static Uri buildMoviesUriBySortOrder(String sortOrder) {
            return CONTENT_URI.buildUpon().appendPath(sortOrder).build();
        }

        public static String getSortOrderFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildMovieTitle(String title) {
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }

        public static String getMovieTitleFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildMovieUriById(long l) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(l)).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static String createMovieTable() {
        return "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_THUMBNAIL_PATH + " TEXT, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_RATE + " TEXT, " +
                MovieEntry.COLUMN_NEWEST + " BOOLEAN NOT NULL CHECK (" + MovieEntry.COLUMN_FAVORITE +  " IN (0,1)), " +
                MovieEntry.COLUMN_POPULAR + " BOOLEAN NOT NULL CHECK (" + MovieEntry.COLUMN_FAVORITE +  " IN (0,1)), " +
                MovieEntry.COLUMN_FAVORITE + " BOOLEAN NOT NULL CHECK (" + MovieEntry.COLUMN_FAVORITE +  " IN (0,1))" +
                ");";
    }

}
