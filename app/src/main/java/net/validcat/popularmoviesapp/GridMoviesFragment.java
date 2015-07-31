package net.validcat.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.validcat.popularmoviesapp.adapters.CursorRecyclerViewAdapter;
import net.validcat.popularmoviesapp.model.MovieItem;
import net.validcat.popularmoviesapp.network.NetworkMoviesRequest;
import net.validcat.popularmoviesapp.provider.MovieContract;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * A placeholder fragment containing a GridMoviesFragment.
 */
public class GridMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = "GridMoviesFragment";
    private static final int PORTRAIT_COL_NUMBER = 2;
    private static final int LANDSCAPE_COL_NUMBER = 3;
    private static final int DEFAULT_PORTRAIT_RESULT_LIMIT = 20;
    private static final int DEFAULT_LANDSCAPE_RESULT_LIMIT = 21;
    private static final int LOADER_ID = 0;

    private GridMoviesAdapter adapter;

    public GridMoviesFragment() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " ASC";
        Uri uri = MovieContract.MovieEntry.buildMoviesUri();

        return new CursorLoader(getActivity(),
                uri,
                MovieItem.MOVIE_COLUMNS,
                null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
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
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
//		recyclerView.setHasFixedSize(true);

        int numOfView = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT 
                ? PORTRAIT_COL_NUMBER : LANDSCAPE_COL_NUMBER;
        GridLayoutManager lm = new GridLayoutManager(getActivity(), numOfView, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);

        adapter = new GridMoviesAdapter(getActivity(), null);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        int limit = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? DEFAULT_PORTRAIT_RESULT_LIMIT : DEFAULT_LANDSCAPE_RESULT_LIMIT;
        NetworkMoviesRequest movieRequest = new NetworkMoviesRequest();
        NetworkMoviesRequest.setResultCount(limit); //TODO default result limit value

        SharedPreferences prefs = getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_order_key), getString(R.string.pref_order_default));
        //TODO sortBy
        movieRequest.fetchSortedMovies(getActivity(), sortBy, new NetworkMoviesRequest.IResponseListener() {

            @Override
            public void onError(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class GridMoviesAdapter extends CursorRecyclerViewAdapter<GridMoviesAdapter.ViewHolder> {

        public GridMoviesAdapter(Context context, Cursor cursor){
            super(context, cursor);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ImageView iv = (ImageView) LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.grid_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(iv);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final Cursor cursor) {
            String url = MovieItem.getFullPosterPath(cursor, MovieItem.WIDTH_500); // cursor.getString(MovieItem.COL_MOVIE_THUMB_PATH);
            if (!TextUtils.isEmpty(url))
                Picasso.with(getActivity())
                        .load(url)
                        .placeholder(R.drawable.image_placeholder)
                        .into(holder.iv);

            holder.iv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    MovieItem item = MovieItem.createItemFromCursor(cursor);
                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                    item.fillMovieData(intent);
                    startActivity(intent);
                }
            });
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView iv;

            public ViewHolder(ImageView iv) {
                super(iv);
                this.iv = iv;
            }
        }
    }
}
