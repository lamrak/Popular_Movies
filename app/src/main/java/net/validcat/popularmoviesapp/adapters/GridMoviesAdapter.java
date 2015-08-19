package net.validcat.popularmoviesapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.validcat.popularmoviesapp.GridMoviesFragment.IClickListener;
import net.validcat.popularmoviesapp.R;
import net.validcat.popularmoviesapp.model.MovieItem;
import net.validcat.popularmoviesapp.provider.MovieContract;

/**
 * Created by Dobrunov on 17.08.2015.
 */
public class GridMoviesAdapter extends CursorRecyclerViewAdapter<GridMoviesAdapter.ViewHolder> {
    private static final String LOG_TAG = GridMoviesAdapter.class.getSimpleName();
    private Context context;

    public GridMoviesAdapter(Context context, Cursor cursor){
        super(context, cursor);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder((ImageView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        long movId = cursor.getLong(MovieItem.COL_ID);
        holder.setId(movId);
        Log.d(LOG_TAG, "Bind View Movie id = " + movId);
        String url = MovieItem.getFullPosterPath(cursor, MovieItem.WIDTH_342); // cursor.getString(MovieItem.COL_MOVIE_THUMB_PATH);
        if (!TextUtils.isEmpty(url))
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.iv);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView iv;
        public long id;

        public ViewHolder(ImageView iv) {
            super(iv);
            this.iv = iv;
            iv.setOnClickListener(this);
        }

        public void setId(long id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            Log.d(LOG_TAG, String.valueOf(id));
            ((IClickListener) context)
                    .onItemClicked(MovieContract.MovieEntry.buildMovieUriById(id));
        }
    }
}

