package net.validcat.popularmoviesapp;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.validcat.popularmoviesapp.adapters.GridMoviesAdapter;
import net.validcat.popularmoviesapp.db.Constants;
import net.validcat.popularmoviesapp.model.MovieItem;
import net.validcat.popularmoviesapp.network.NetworkMoviesRequest;
import net.validcat.popularmoviesapp.provider.MovieContract;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * A placeholder fragment containing a GridMoviesFragment.
 */
public class GridMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = "GridMoviesFragment";
    private static final int LOADER_ID = 0;
    @Bind(R.id.recycler_view) RecyclerView recyclerView;
    private GridMoviesAdapter adapter;
    private boolean useTabLayout;

    private int position = ListView.INVALID_POSITION;

    public GridMoviesFragment() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = Utility.getPreferredSortOrder(getActivity());
        Uri uri = MovieContract.MovieEntry.buildMoviesUriBySortOrder(sortOrder);

        return new CursorLoader(getActivity(),
                uri,
                MovieItem.MOVIE_COLUMNS,
                null, null, MovieContract.MovieEntry.COLUMN_RATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION)
            recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
		recyclerView.setHasFixedSize(true);
        int numOfView = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? Constants.PORTRAIT_COL_NUMBER : Constants.LANDSCAPE_COL_NUMBER; //TODO Can we move this to layout somehow?
        GridLayoutManager lm = new GridLayoutManager(getActivity(), numOfView, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);

        adapter = new GridMoviesAdapter(getActivity(), null);
        recyclerView.setAdapter(adapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.SELECTED_KEY))
            position = savedInstanceState.getInt(Constants.SELECTED_KEY);

        updateMovies();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (position != ListView.INVALID_POSITION)
            outState.putInt(Constants.SELECTED_KEY, position);
        super.onSaveInstanceState(outState);
    }

    public void updateMovies() {
        int limit = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? Constants.DEFAULT_PORTRAIT_RESULT_LIMIT : Constants.DEFAULT_LANDSCAPE_RESULT_LIMIT;
        NetworkMoviesRequest movieRequest = new NetworkMoviesRequest();
        NetworkMoviesRequest.setResultCount(limit); //TODO default result limit value

        SharedPreferences prefs = getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_order_key), getString(R.string.pref_order_popular));
        //TODO in case if sortBy equals to favorite,
        // no need to make network request cause all data
        // is already in database, just fetch this data from
        // db
        //if (sortBy.equals(getString(R.string.pref_order_favorite)))
        movieRequest.fetchSortedMovies(getActivity(), sortBy);
        //else getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void setUseTabLayout(boolean useTabLayout) {
        this.useTabLayout = useTabLayout;
    }

    public void resetAndUpdateMovies() {
        updateMovies();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public interface IClickListener {
        void onItemClicked(Uri uri);
    }

}
