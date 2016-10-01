package movie.android.com.movieapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import movie.android.com.movieapp.models.Movie;

/**
 * adapter to fill movies grid view
 */
public class MovieArrayAdapter extends BaseAdapter implements Serializable {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Movie> mMovies;
    public MovieArrayAdapter(Context context, ArrayList<Movie> movies) {
        super();

        this.mContext = context;
        this.mMovies = movies;
        mInflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Object getItem(int i) {
        return mMovies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(int position, View rootView, ViewGroup parent) {
            ViewHolder holder ;
        if(rootView == null){
            rootView = mInflater.inflate(R.layout.grid_view_item, parent, false);
            holder = new ViewHolder();
            holder.poster = (ImageView) rootView.findViewById(R.id.grid_view_list_item);
            rootView.setTag(holder);

        }else{
            holder = (ViewHolder)rootView.getTag();
        }
        Picasso.with(mContext)
                .load(URLs.IMAGE_BASE_URL + URLs.IMAGE_SIZE + mMovies.get(position).getPosterUrl())
                .placeholder(R.drawable.ic_movie_placeholder)
                .into(holder.poster);

        return rootView;

    }

    public class ViewHolder{
        ImageView poster ;
    }

}
