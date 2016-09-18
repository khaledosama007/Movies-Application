package movie.android.com.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    boolean mTwoPane;
    String mSortMethod;
    final String DETAIL_FRAGMENT_TAG = "DETAILMOVIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortMethod = Utils.getPreferredSortMethod(this);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_details_frame) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_frame, new MovieDetailsFragment() ,DETAIL_FRAGMENT_TAG)
                        .addToBackStack(DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
        else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
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

        if (id == R.id.action_setting) {
            Intent settignsIntent = new Intent(this , SettingsActivity.class);
            startActivity(settignsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume(){
        super.onResume();
        String sort = Utils.getPreferredSortMethod(this);

        if (sort != null && !sort.equals(mSortMethod)) {
            MainActivityFragment mainFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if ( mainFragment != null ) {
                mainFragment.updateData(sort);

            }
            MovieDetailsFragment detailFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if ( detailFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_frame, new MovieDetailsFragment() ,DETAIL_FRAGMENT_TAG).
                        commit();
            }

            mSortMethod = sort;
        }

    }

    @Override
    public void onBackPressed() {
        if(mTwoPane) {
            // remove the fragment added to backstack
            getSupportFragmentManager().popBackStack();
            //remove the original one
            getSupportFragmentManager().popBackStack();
        }
        super.onBackPressed();
    }

    @Override
    public void onMovieSelected(Movie movie) {
        if(mTwoPane){
            Bundle arguments = new Bundle();
            arguments.putSerializable("Movie", movie);
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_frame, fragment)
                    .commit();
        }
        else {
            Intent intent = new Intent(this ,MovieDetails.class);
            intent.putExtra("Movie" , movie);
            startActivity(intent);

        }
    }
}
