package movie.android.com.movieapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import movie.android.com.movieapp.models.Trailer;

public class TrailerAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Trailer> trailerList;
    public TrailerAdapter(Context context , ArrayList<Trailer> trailerList){
        super();
        this.context = context;
        this.trailerList = trailerList;
        inflater = ((Activity) context).getLayoutInflater();
    }
    @Override
    public int getCount() {
        return trailerList.size();
    }

    @Override
    public Object getItem(int i) {
        return trailerList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i;
        View rootView = inflater.inflate(R.layout.trailer_item , viewGroup , false);
        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.trailer_image_button);
        TextView trailerName = (TextView) rootView.findViewById(R.id.trailer_name);
        trailerName.setText(trailerList.get(i).getName());
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = URLs.YOUTUBE_LINK + trailerList.get(position).getKey();
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
                context.startActivity(youtubeIntent);
            }
        });
        return rootView;
    }
}
