package net.validcat.popularmoviesapp.model;

import android.database.Cursor;

import net.validcat.popularmoviesapp.provider.MovieContract;

import java.util.List;

/**
 * Created by Dobrunov on 07.07.2015.
 */
public class MovieItem {
    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL_PATH,
            MovieContract.MovieEntry.COLUMN_FAVORITE,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
    };
    public static final int COL_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_OVERVIEW = 2;
    public static final int COL_MOVIE_RATE = 3;
    public static final int COL_MOVIE_RELEASE_DATE = 4;
    public static final int COL_MOVIE_THUMB_PATH = 5;
    public static final int COL_MOVIE_FAVORITE = 6;
    public static final int COL_MOVIE_ID = 7;

    // keys for packing/unpacking intent
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_THUMB_PATH = "key_path";
    private static final String KEY_OVERVIEW = "key_overview";
    private static final String KEY_RELEASE = "key_release";
    private static final String KEY_RATE = "key_rate";
    // for url building
    public static final String WIDTH_154 = "w154";
    public static final String WIDTH_342 = "w342";
    public static final String WIDTH_500 = "w500";
    public static final String WIDTH_780 = "w780";

    private static final String URL_IMAGE_TMDB_DEFAULT = "http://image.tmdb.org/t/p/";
    // We can use here getter and setter but to simplify working with item I decided to
    // use direct field call (item.title).
    public long id;
    public String title;
    public String thumbPath;
    public String overview;
    public String release;
    public String rate;
    public boolean favorite;
    public long movieId;

    private List<String> trailers;
    private List<String> post;

    public String getFullPosterPath(String preferedWidth) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_IMAGE_TMDB_DEFAULT);
        sb.append(preferedWidth);
        sb.append(thumbPath);

        return sb.toString();
    }

    public static String getFullPosterPath(Cursor c, String preferedWidth) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_IMAGE_TMDB_DEFAULT);
        sb.append(preferedWidth);
        sb.append(c.getString(COL_MOVIE_THUMB_PATH));

        return sb.toString();
    }

    public static MovieItem createMovieItemFromCursor(Cursor cursor) {
        MovieItem item = new MovieItem();
        item.id = cursor.getLong(COL_ID);
        item.title = cursor.getString(COL_MOVIE_TITLE);
        item.release = cursor.getString(COL_MOVIE_RELEASE_DATE);
        item.overview = cursor.getString(COL_MOVIE_OVERVIEW);
        item.rate = cursor.getString(COL_MOVIE_RATE);
        item.thumbPath = cursor.getString(COL_MOVIE_THUMB_PATH);
        item.favorite =  cursor.getInt(COL_MOVIE_FAVORITE) > 0;
        item.movieId = cursor.getLong(COL_MOVIE_ID);

        return item;
    }

    @Override
    public String toString() {
        return "[Movie Item {title="+ title +", id= "+ id +"}]";
    }
}
