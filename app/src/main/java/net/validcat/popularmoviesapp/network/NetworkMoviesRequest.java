package net.validcat.popularmoviesapp.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import net.validcat.popularmoviesapp.model.MovieItem;

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
    private static int resultCount = 10; // 10 by default

    public void fetchSortedMovies(String sortBy, IResponseListener listener) {
        // Construct the URL for the MovieDB query
        String strUrl = new Uri.Builder()
                .scheme(HTTP_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter(PARAM_SORT_BY, sortBy)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build().toString();
        new FetcherTask(listener).execute(strUrl);
    }

    public static void setResultCount(int resultCount) {
        NetworkMoviesRequest.resultCount = resultCount;
    }

    private class FetcherTask extends AsyncTask<String, Void, String> {
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
            if (result == null)
                listener.onError("Server error");
            try {
                List<MovieItem> movies = MovieResponceParser.getMoviesFromJson(result, resultCount);
                if (movies != null && movies.size() != 0)
                    listener.onResult(movies);
                else
                    listener.onError("ListMovies is null or empty");
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onError(e.toString());
            }
        }
    }

    public interface IResponseListener {
        void onResult(List<MovieItem> items);
        void onError(String error);
    }
}
