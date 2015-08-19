package net.validcat.popularmoviesapp.network;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import net.validcat.popularmoviesapp.db.Constants;
import net.validcat.popularmoviesapp.model.ReviewItem;
import net.validcat.popularmoviesapp.model.VideoItem;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Dobrunov on 16.06.2015.
 */
public class NetworkMoviesRequest {
    private static final String LOG_TAG = NetworkMoviesRequest.class.getSimpleName();
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_SORT_BY = "sort_by";
    private static final String HTTP_SCHEME = "http";
    private static final String URL_AUTHORITY = "api.themoviedb.org";
    private static final String API_KEY = "da627faeb40cedb6c25f814b357ea48e";
    private static int resultCount = Constants.DEFAULT_RESULT_NETWORK_LIMIT; // 10 by default

    public static void fetchSortedMovies(final Context context, final String sortBy) {
        new FetcherTask(new IResponseListener() {

            @Override
            public void onResult(String json) {
                try {
                    MovieResponceParser.storeMoviesFromJsonInProvider(context, json, resultCount, sortBy);
                } catch (JSONException e) {
                    onError(e.toString());
                }
            }

            @Override
            public void onError(String error) {
                Log.e(LOG_TAG, error);
            }
        }).execute(new Uri.Builder()
                .scheme(HTTP_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter(PARAM_SORT_BY, sortBy + ".desc")
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build().toString());
    }

    public static void fetchVideosById(long movId, final IParamResponseListener<VideoItem> videosListener) { ///movie/{id}/videos
        // Construct the URL for the MovieDB query
        new FetcherTask(new IResponseListener() {
            @Override
            public void onResult(String json) {
                try {
                    List<VideoItem> videos = MovieResponceParser.storeVideosFromJsonInProvider(json, resultCount);
                    if (null != videos && videos.size() > 0)
                        videosListener.onResult(videos);
                    else onError("Review list is empty or null");
                } catch (JSONException e) {
                    onError(e.toString());
                }
            }

            @Override
            public void onError(String error) {
                Log.e(LOG_TAG, error);
            }
        }).execute(new Uri.Builder()
                .scheme(HTTP_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(String.valueOf(movId))
                .appendPath("videos")
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build().toString());
    }

    // http://api.themoviedb.org/3/movie/76341/reviews?sort_by=popularity.desc&api_key=da627faeb40cedb6c25f814b357ea48e
    public static void fetchReviewsById(long movId, final IParamResponseListener<ReviewItem> revListener) { //movie/{id}/reviews
        // Construct the URL for the MovieDB query
        new FetcherTask(new IResponseListener() {

            @Override
            public void onResult(String json) {
                try {
                    List<ReviewItem> reviews = MovieResponceParser.fetchReviewsFromJson(json, resultCount);
                    if (null != reviews && reviews.size() > 0)
                        revListener.onResult(reviews);
                    else onError("Review list is empty or null");
                } catch (JSONException e) {
                    onError(e.toString());
                }
            }

            @Override
            public void onError(String error) {
                Log.e(LOG_TAG, error);
            }
        }).execute(new Uri.Builder()
                .scheme(HTTP_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(String.valueOf(movId))
                .appendPath("reviews")
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build().toString());
    }

    public static void setResultCount(int resultCount) {
        NetworkMoviesRequest.resultCount = resultCount;
    }

    private static class FetcherTask extends AsyncTask<String, Void, String> {
        IResponseListener listener;

        public FetcherTask(IResponseListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String json = null;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                    return null;
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");

                if (buffer.length() == 0)
                    return null;
                json = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                listener.onError(e.toString());
                // If the code didn't successfully get the weather data, there's no point in attemping to parse it.
                return null;
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                        listener.onError(e.toString());
                    }
                }
            }

            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) listener.onError("Server error");
            else listener.onResult(result);
        }
    }

    public interface IResponseListener {
        void onResult(String json);
        void onError(String error);
    }

    /**
     * Think it's not a good idea to create such interface
     * but in luck of time i decided to stay on this decision
     */
    public interface IParamResponseListener<E> {
        void onResult(List<E> review);
    }
}
