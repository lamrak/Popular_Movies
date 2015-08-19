package net.validcat.popularmoviesapp.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import net.validcat.popularmoviesapp.R;
import net.validcat.popularmoviesapp.model.MovieItem;
import net.validcat.popularmoviesapp.model.ReviewItem;
import net.validcat.popularmoviesapp.model.VideoItem;
import net.validcat.popularmoviesapp.provider.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * MovieResponceParser converts json in java object.
 * Created by Dobrunov on 22.06.2015.
 */
public class MovieResponceParser {
    private static final String LOG_TAG = MovieResponceParser.class.getSimpleName();

    private static final String KEY_RESULTS = "results";
    private static final String KEY_TITLE = "original_title";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RATE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_ID = "id";
    private static final String KEY_REVIEW_AUTHOR = "author";
    private static final String KEY_REVIEW_CONTENT = "content";
    private static final String KEY_REVIEW_URL = "url";
    private static final String KEY_VIDEO_KEY = "key";
    private static final String KEY_VIDEO_NAME = "name";
    private static final String KEY_VIDEO_SITE = "site";
    private static final String KEY_VIDEO_SIZE = "size";
    private static final String KEY_VIDEO_TYPE = "type";

    /**
     * Take the String representing the complete movies in JSON Format and get list of  MovieItem.
     *
     */
    public static List<MovieItem> getMoviesFromJson(String json, int moviesNum) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        JSONObject forecastJson = new JSONObject(json);
        JSONArray jsonMoviesArray = forecastJson.getJSONArray(KEY_RESULTS);

        List<MovieItem> movies = new ArrayList<>(moviesNum);
        // in case if receive movies less than want to show
        int length = forecastJson.length() < moviesNum ? forecastJson.length() : moviesNum;
        for(int i = 0; i < moviesNum; i++) {
            MovieItem movie = new MovieItem();

            JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);
            movie.title = jsonMovie.getString(KEY_TITLE);
            movie.overview = jsonMovie.getString(KEY_OVERVIEW);
            movie.thumbPath = jsonMovie.getString(KEY_POSTER_PATH);
            movie.release = jsonMovie.getString(KEY_RELEASE_DATE);
            movie.rate = jsonMovie.getString(KEY_RATE);

            movies.add(movie);
        }

        return movies;
    }

    public static void storeMoviesFromJsonInProvider(Context context, String json, int moviesNum, String sortBy) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        JSONArray jsonMoviesArray = new JSONObject(json).getJSONArray(KEY_RESULTS);

        List<ContentValues> movies = new ArrayList<>(moviesNum);
//        // in case if receive movies less than want to show
        int length = jsonMoviesArray.length() < moviesNum ? jsonMoviesArray.length() : moviesNum;

        for(int i = 0; i < length; i++) {
            ContentValues locationValues = new ContentValues();

            JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);

            //movie.movie_title = ?
            Cursor movieCursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_TITLE},
                    MovieContract.MovieEntry.COLUMN_TITLE + " = ? ",
                    new String[]{jsonMovie.getString(KEY_TITLE)}, null); //get movie by title
            if (movieCursor.moveToFirst()) {
                String title = movieCursor.getString(MovieItem.COL_MOVIE_TITLE);
                long id = movieCursor.getLong(MovieItem.COL_ID);
                Log.d(LOG_TAG, "Movie " + title + " = " + id + " is in db");
            } else {
                Log.d(LOG_TAG, "Movie is new, add into db");
                locationValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, jsonMovie.getString(KEY_ID));
                locationValues.put(MovieContract.MovieEntry.COLUMN_TITLE, jsonMovie.getString(KEY_TITLE));
                locationValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, jsonMovie.getString(KEY_OVERVIEW));
                locationValues.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL_PATH, jsonMovie.getString(KEY_POSTER_PATH));
                locationValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, jsonMovie.getString(KEY_RELEASE_DATE));
                locationValues.put(MovieContract.MovieEntry.COLUMN_RATE, jsonMovie.getString(KEY_RATE));
                locationValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, false);
                locationValues.put(MovieContract.MovieEntry.COLUMN_POPULAR, sortBy.equals(context.getString(R.string.pref_order_popular)) ? true : false);
                locationValues.put(MovieContract.MovieEntry.COLUMN_NEWEST, sortBy.equals(context.getString(R.string.pref_order_newest)) ? true : false);

                movies.add(locationValues);
            }
            movieCursor.close();
        }

        int inserted = 0;
        // add to database
        if (movies.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[movies.size()];
            movies.toArray(cvArray);
            inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");
    }

    /**
     * {
     "id":"551afc679251417fd70002b1",
     "iso_639_1":"en",
     "key":"jnsgdqppAYA",
     "name":"Trailer 2",
     "site":"YouTube",
     "size":720,
     "type":"Trailer"
     }

     * @param json
     * @param count
     * @throws JSONException
     */
    public static List<VideoItem> storeVideosFromJsonInProvider(String json, int count) throws JSONException {
        JSONArray jsonMoviesArray = new JSONObject(json).getJSONArray(KEY_RESULTS);

        List<VideoItem> videos = new ArrayList<>(count);

        int length = jsonMoviesArray.length() < count ? jsonMoviesArray.length() : count;

        for(int i = 0; i < length; i++) {
            JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);
            VideoItem item = new VideoItem();
            item.id = jsonMovie.getString(KEY_ID);
            item.key = jsonMovie.getString(KEY_VIDEO_KEY);
            item.name = jsonMovie.getString(KEY_VIDEO_NAME);
            item.site = jsonMovie.getString(KEY_VIDEO_SITE);
            item.size = jsonMovie.getInt(KEY_VIDEO_SIZE);
            item.type = jsonMovie.getString(KEY_VIDEO_TYPE);

            videos.add(item);
        }

        return videos;
    }

    /**
     * @param json
     * @param count
     * @throws JSONException
     */
    public static List<ReviewItem> fetchReviewsFromJson(String json, int count) throws JSONException {
        JSONArray jsonMoviesArray = new JSONObject(json).getJSONArray(KEY_RESULTS);

        List<ReviewItem> reviews = new ArrayList<>(count);
//        // in case if receive movies less than want to show
        int length = jsonMoviesArray.length() < count ? jsonMoviesArray.length() : count;

        for(int i = 0; i < length; i++) {
            JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);
            ReviewItem item = new ReviewItem();
            item.id = jsonMovie.getString(KEY_ID);
            item.author = jsonMovie.getString(KEY_REVIEW_AUTHOR);
            item.content = jsonMovie.getString(KEY_REVIEW_CONTENT);
            item.url = jsonMovie.getString(KEY_REVIEW_URL);

            reviews.add(item);
        }

        return reviews;
    }
}
