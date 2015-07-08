package net.validcat.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.validcat.popularmoviesapp.model.MovieItem;
import net.validcat.popularmoviesapp.network.NetworkMoviesRequest;

import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * A placeholder fragment containing a GridMoviesFragment.
 */
public class GridMoviesFragment extends Fragment {
    private static final String LOG_TAG = "GridMoviesFragment";

    private GridMoviesAdapter adapter;
    private List<MovieItem> items;

    public GridMoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
//		recyclerView.setHasFixedSize(true);
        GridLayoutManager lm = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);

        items = new ArrayList<>();
        adapter = new GridMoviesAdapter();
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        NetworkMoviesRequest movieRequest = new NetworkMoviesRequest();
        movieRequest.setResultCount(16); //TODO default result limit value

        SharedPreferences prefs = getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_order_key), getString(R.string.pref_order_default));

        movieRequest.fetchSortedMovies(sortBy, new NetworkMoviesRequest.IResponseListener() {
            @Override
            public void onResult(List<MovieItem> items) {
                GridMoviesFragment.this.items.clear();
                GridMoviesFragment.this.items.addAll(items);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class GridMoviesAdapter extends RecyclerView.Adapter<GridMoviesAdapter.ViewHolder> {

        public GridMoviesAdapter() {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ImageView iv = (ImageView) LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.grid_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(iv);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            String url = items.get(position).getFullPosterPath(MovieItem.WIDTH_500);
            if (!TextUtils.isEmpty(url)) Picasso.with(getActivity()).load(url).into(holder.iv);

            holder.iv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    MovieItem item = items.get(position);
                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                    item.fillMovieData(intent);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
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
