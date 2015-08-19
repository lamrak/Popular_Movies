package net.validcat.popularmoviesapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.validcat.popularmoviesapp.adapters.ReviewsAdapter;
import net.validcat.popularmoviesapp.adapters.VideoAdapter;
import net.validcat.popularmoviesapp.model.MovieItem;
import net.validcat.popularmoviesapp.model.ReviewItem;
import net.validcat.popularmoviesapp.model.VideoItem;
import net.validcat.popularmoviesapp.network.NetworkMoviesRequest;
import net.validcat.popularmoviesapp.provider.MovieContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 1;
    public static final String DETAIL_URI = "uri_movie_detail";
    private Uri uri;
    private MovieItem movie;

    @Bind(R.id.tv_header)
    TextView title;
    @Bind(R.id.iv_poster)
    ImageView poster;
    @Bind(R.id.tv_rate) TextView rate;
    @Bind(R.id.tv_date) TextView date;
    @Bind(R.id.tv_overview) TextView overview;
    @Bind(R.id.scroll_content)
    ScrollView scroll;
    //@Bind(R.id.tv_movie_duration) TextView duration;
    @Bind(R.id.check_favorite)
    CheckBox favorite;
    @Bind(R.id.recycler_video)
    RecyclerView videos;
    VideoAdapter videoAdapter;
    @Bind(R.id.recycler_reviews)
    RecyclerView reviews;
    ReviewsAdapter reviewsAdapter;

    public MovieDetailFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");
        if (null == uri)
            return null;

        return new CursorLoader(getActivity(),
                uri,
                MovieItem.MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadFinished");
        //update UI
        if (data != null && data.moveToFirst()) {
            movie = MovieItem.createMovieItemFromCursor(data);
            String url = movie.getFullPosterPath(MovieItem.WIDTH_500);
            if (!TextUtils.isEmpty(url))
                Picasso.with(getActivity())
                        .load(url)
                        .into(poster); //TODO add placeholder
            title.setText(movie.title);
            rate.setText(movie.rate);
            date.setText(movie.release.substring(0, 4));
            overview.setText(movie.overview);
            //duration.setText(movie.duration);

            favorite.setChecked(movie.favorite);
        }

        favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                movie.favorite = isChecked;
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, isChecked);
                getActivity().getContentResolver()
                        .update(MovieContract.MovieEntry.CONTENT_URI, values,
                                MovieContract.MovieEntry._ID + "=?", new String[]{String.valueOf(movie.id)});
            }
        });

        updateAdditionalData();
        //TODO create share intent
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null)
            uri = arguments.getParcelable(DETAIL_URI);
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        initVideoUI();
        initReviewUI();
        scroll.smoothScrollTo(0, 0);
        return rootView;
    }

    private void initReviewUI() {
        //reviews.setHasFixedSize(true);
        reviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        // specify an adapter (see also next example)
        reviewsAdapter = new ReviewsAdapter(new ArrayList<ReviewItem>());
        reviews.setAdapter(reviewsAdapter);
    }

    private void initVideoUI() {
        videos.setHasFixedSize(true);
        videos.setLayoutManager(new LinearLayoutManager(getActivity()));
        // specify an adapter (see also next example)
        videoAdapter = new VideoAdapter(new ArrayList<VideoItem>());
        videos.setAdapter(videoAdapter);
    }

    public void updateMovies() {
        if (uri != null) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);

        }
        updateAdditionalData();
    }

    protected void updateAdditionalData() {
        if (movie == null)
            return;

        NetworkMoviesRequest.fetchVideosById(movie.movieId, new NetworkMoviesRequest.IParamResponseListener<VideoItem>() {
            @Override
            public void onResult(List<VideoItem> video) {
                videoAdapter.addItems(video);
            }
        });

        NetworkMoviesRequest.fetchReviewsById(movie.movieId, new NetworkMoviesRequest.IParamResponseListener<ReviewItem>() {
            @Override
            public void onResult(List<ReviewItem> review) {
                reviewsAdapter.addItems(review);
            }
        });
        scroll.smoothScrollTo(0,0);
    }

}
