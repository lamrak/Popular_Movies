package net.validcat.popularmoviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GridMoviesFragment.IClickListener {
    private static final String FRAGMENT_TAG = "fragment_tag";
    private static final String F_DETAIL_TAG = "detail_fragment";
    private boolean twoPane;
    String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_main);
        sortOrder = Utility.getPreferredSortOrder(this);
        if (findViewById(R.id.movie_detail_container) != null) {
            twoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), F_DETAIL_TAG)
                        .commit();

                GridMoviesFragment mf = (GridMoviesFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_movies);
                mf.setUseTabLayout(!twoPane);
            }
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_movies, new GridMoviesFragment())
                    .commit();
            twoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sortOrder != null && !sortOrder.equals(Utility.getPreferredSortOrder(this))) {
            GridMoviesFragment gmf = (GridMoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            MovieDetailFragment df = (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag(F_DETAIL_TAG);
            if (null != gmf) gmf.resetAndUpdateMovies();
            if (null != df) df.updateMovies();

            sortOrder = Utility.getPreferredSortOrder(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(Uri uri) {
        if (twoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, uri);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, F_DETAIL_TAG)
                    .commit();
        } else startActivity(new Intent(this, MovieDetailActivity.class).setData(uri));
    }
}
