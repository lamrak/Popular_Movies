package net.validcat.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.validcat.popularmoviesapp.model.MovieItem;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent i = getActivity().getIntent();
        if (i != null) {
            MovieItem movie = MovieItem.createMovieItemFromIntent(i);
            String url = movie.getFullPosterPath(MovieItem.WIDTH_500);
            if (!TextUtils.isEmpty(url))
                Picasso.with(getActivity()).load(url).into((ImageView) rootView.findViewById(R.id.iv_poster));
            ((TextView) rootView.findViewById(R.id.tv_header)).setText(movie.title);
            ((TextView) rootView.findViewById(R.id.tv_rate)).setText(String.valueOf(movie.rate));
            // date hardcoded
            ((TextView) rootView.findViewById(R.id.tv_date)).setText(movie.release.substring(0, 4));
            ((TextView) rootView.findViewById(R.id.tv_overview)).setText(movie.overview);
        }

        return rootView;
    }
}
