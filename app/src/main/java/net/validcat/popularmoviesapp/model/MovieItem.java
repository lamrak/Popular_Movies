package net.validcat.popularmoviesapp.model;

import android.content.Intent;

/**
 * Created by Dobrunov on 07.07.2015.
 */
public class MovieItem {
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
    public static final String URL_IMAGE_TMDB_DEFAULT = "http://image.tmdb.org/t/p/";
    // We can use here getter and setter but to simplify working with item I decided to
    // use direct field call (item.title).
    public String title;
    public String thumbPath;
    public String overview;
    public String release;
    public String rate;

    public void fillMovieData(Intent intent) {
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_THUMB_PATH, thumbPath);
        intent.putExtra(KEY_OVERVIEW, overview);
        intent.putExtra(KEY_RELEASE, release);
        intent.putExtra(KEY_RATE, rate);
    }
    
    public static MovieItem createMovieItemFromIntent(Intent intent) {
        MovieItem item = new MovieItem();
        item.title = intent.getStringExtra(KEY_TITLE);
        item.thumbPath = intent.getStringExtra(KEY_THUMB_PATH);
        item.overview = intent.getStringExtra(KEY_OVERVIEW);
        item.release = intent.getStringExtra(KEY_RELEASE);
        item.rate = intent.getStringExtra(KEY_RATE);
        
        return item;
    }

    public String getFullPosterPath(String thumpWidth) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_IMAGE_TMDB_DEFAULT);
        sb.append(thumpWidth);
        sb.append(thumbPath);

        return sb.toString();
    }
}
