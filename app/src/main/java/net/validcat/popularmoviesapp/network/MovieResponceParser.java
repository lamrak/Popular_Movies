package net.validcat.popularmoviesapp.network;

import net.validcat.popularmoviesapp.model.MovieItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
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

    /* The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now. Because the API
     * returns a unix timestamp (measured in seconds), it must be converted to milliseconds in
     * order to be converted to valid date.
     */
    public static String getReadableDateString(long time){
        return new SimpleDateFormat("EEE MMM dd").format(time);
    }

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
}
