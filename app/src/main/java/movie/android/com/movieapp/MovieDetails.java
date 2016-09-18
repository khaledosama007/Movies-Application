package movie.android.com.movieapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Avtivity holds movie details fragment
 */
public class MovieDetails extends AppCompatActivity {
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

}




