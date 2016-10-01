package movie.android.com.movieapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import movie.android.com.movieapp.models.Review;


public class ReviewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Review> reviewList;

    public ReviewAdapter(Context context , ArrayList<Review> reviewList){
        this.context = context;
        this.reviewList = reviewList;
        inflater = ((Activity) context).getLayoutInflater();
    }
    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Object getItem(int i) {
        return reviewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = inflater.inflate(R.layout.review_item , viewGroup , false);
        TextView authorLabel = (TextView)rootView.findViewById(R.id.review_author_view);
        authorLabel.setText(reviewList.get(i).getAuthor());
        TextView reviewContent = (TextView) rootView.findViewById(R.id.review_content_view);
        reviewContent.setText(reviewList.get(i).getContent());
        return rootView;
    }
}
