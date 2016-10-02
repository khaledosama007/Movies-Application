package movie.android.com.movieapp;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import movie.android.com.movieapp.models.Movie;

/**
 * Created by khaled on 01/10/16.
 */

public class RecyclerViewMovieAdapter extends RecyclerView.Adapter<RecyclerViewMovieAdapter.MovieHolder> {
    public static class MovieHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;

        public MovieHolder(View item){
            super(item);
            imageView = (ImageView) item.findViewById(R.id.grid_view_list_item);
        }
    }



    private ArrayList<Movie> mMovies;
    private Context context;
    public RecyclerViewMovieAdapter(Context context, ArrayList<Movie> movies) {
        mMovies = movies;
        this.context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }
    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.grid_view_item, parent, false);
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index =MainActivityFragment.grid.indexOfChild(view);
                Log.e("Here" , String.valueOf(index));
                ((MainActivityFragment.Callback)context).onMovieSelected(mMovies.get(index));
            }
        });
        // Return a new holder instance
        MovieHolder viewHolder = new MovieHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie current = mMovies.get(position);

        // Set item views based on your views and data model
        ImageView imageView = holder.imageView;
        Picasso.with(context)
                .load(URLs.IMAGE_BASE_URL + URLs.IMAGE_SIZE + mMovies.get(position).getPosterUrl())
                .placeholder(R.drawable.ic_movie_placeholder)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
