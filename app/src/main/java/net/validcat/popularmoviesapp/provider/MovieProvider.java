package net.validcat.popularmoviesapp.provider;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MovieProvider extends ContentProvider {
    public static final int MOVIE = 100;
    public static final int MOVIE_BY_ID = 101;
    public static final int MOVIE_BY_SORT_ORDER = 102;
    private static UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_BY_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/*", MOVIE_BY_SORT_ORDER);
    }
    MovieDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_BY_SORT_ORDER:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                deletedRows = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_BY_ID:
                deletedRows = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Failed to insert row into: " + uri);
        }

        if (deletedRows != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return deletedRows;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                cursor = dbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_BY_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.TABLE_NAME +
                                "." + MovieContract.MovieEntry._ID + " = ? ",
                        new String[]{MovieContract.MovieEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_BY_SORT_ORDER:
                String sortOrderField = MovieContract.MovieEntry.getSortOrderFromUri(uri);
                selection = MovieContract.MovieEntry.TABLE_NAME +
                        "." + sortOrderField + " = ? ";
                cursor = dbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{"1"},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows;

        switch (uriMatcher.match(uri)) {
            case MOVIE:
//                normalizeDate(values);
                affectedRows = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_BY_ID:
                affectedRows = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Failed to insert row into: " + uri);
        }
        if (affectedRows > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return affectedRows;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri = null;

        switch (uriMatcher.match(uri)) {
            case MOVIE:
//                normalizeDate(values);
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = MovieContract.MovieEntry.buildMoviesUri();
                break;
            default:
                throw new UnsupportedOperationException("Failed to insert row into: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}
