package movie.android.com.movieapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Avtivity holds movie details fragment
 */
public class MovieDetails extends AppCompatActivity {
    public ShareActionProvider mShareActionProvider;
    //MovieDetailsFragment fragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putSerializable("Movie", getIntent().getSerializableExtra("Movie"));
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_details_frame, fragment)
                    .commit();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //mShareActionProvider.setShareIntent(createShareIntent());
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();
        if(id == R.id.menu_item_share){

        }
        return super.onOptionsItemSelected(item);

    }

}




