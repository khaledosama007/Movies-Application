package movie.android.com.movieapp;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import movie.android.com.movieapp.database.MovieContract;

public class MovieDetailsFragment extends android.support.v4.app.Fragment {
    private Movie mMovie = null;
    private ImageView mPosterImageView;
    private TextView mTitleView;
    private TextView mRatingView;
    private TextView mDateView;
    private TextView mPlotView;
    private TrailerAdapter trailerAdapter;
    public ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;
    private LinearLayout trailerListView;
    private LinearLayout reviewListView;
    private ReviewAdapter reviewAdapter;
    private Button mFavoriteButton;
    private final String ADD_TO_FAVORITES = "Add To Favorites";
    private final String REMOVE_FROM_FAVORITES = "Remove From Favorites";

    // End Of Content View Elements

    public MovieDetailsFragment() {

    }
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable("trailer" , trailers);
        bundle.putSerializable("review" , reviews);
        bundle.putSerializable("movie" , mMovie);
        super.onSaveInstanceState(bundle);
    }
    // bind view elements to variables
    private void bindViews(View v) {
        mPosterImageView = (ImageView) v.findViewById(R.id.poster_image_view);
        mTitleView = (TextView) v.findViewById(R.id.title_view);
        mRatingView = (TextView) v.findViewById(R.id.rating_view);
        mDateView = (TextView) v.findViewById(R.id.date_view);
        mPlotView = (TextView) v.findViewById(R.id.overview_view);
        trailerListView = (LinearLayout) v.findViewById(R.id.trailer_list_view);
        reviewListView = (LinearLayout) v.findViewById(R.id.review_list_view);
        mFavoriteButton = (Button) v.findViewById(R.id.favorite_button);
    }
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Log.e(getTag() , "Tag");
        if(bundle != null) {
            trailers = (ArrayList<Trailer>) bundle.getSerializable("trailer");
            reviews = (ArrayList<Review>) bundle.getSerializable("review");
            mMovie = (Movie) bundle.getSerializable("movie");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.v(container.toString(), "Name");
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container , false);
        if(savedInstanceState != null){
            if(mMovie == null){
                return inflater.inflate(R.layout.empty_detail_view , container , false);
            }
            bindViews(rootView);
            Picasso.with(getActivity()).load(URLs.IMAGE_BASE_URL + URLs.IMAGE_SIZE + mMovie.getPosterUrl()).fit()
                    .into(mPosterImageView);
            mTitleView.setText(mMovie.getTitle());
            mPlotView.setText(mMovie.getPlot());
            mDateView.setText(mMovie.getReleaseDate());
            mRatingView.setText(mMovie.getRating().toString());
            trailerAdapter = new TrailerAdapter(getActivity() , trailers);
            reviewAdapter = new ReviewAdapter(getActivity() , reviews);
            trailerAdapter.notifyDataSetChanged();
            for (int i = 0; i < trailerAdapter.getCount(); i++) {
                View view = trailerAdapter.getView(i, null, trailerListView);
                trailerListView.addView(view);
            }
            reviewAdapter.notifyDataSetChanged();
            for(int i=0 ; i<reviewAdapter.getCount() ; i++){
                View view = reviewAdapter.getView(i ,null , reviewListView);
                reviewListView.addView(view);
            }
            if(checkInsertedInDatabase()){
                mFavoriteButton.setText(REMOVE_FROM_FAVORITES);
            }
            else {
                mFavoriteButton.setText(ADD_TO_FAVORITES);
            }
            return rootView;
        }
        else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mMovie = (Movie) arguments.getSerializable("Movie");
            } else {
                mMovie = (Movie) getActivity().getIntent().getSerializableExtra("Movie");
                if (mMovie == null) {
                    return inflater.inflate(R.layout.empty_detail_view , container , false);
                }
            }
            bindViews(rootView);
            DisplayMovie(mMovie);
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFavoriteClick(view);
                }
            });
            if(checkInsertedInDatabase()){
                mFavoriteButton.setText(REMOVE_FROM_FAVORITES);
            }
            else {
                mFavoriteButton.setText(ADD_TO_FAVORITES);
            }
            return rootView;
        }

    }


    // Function to bind movie object to views and load trailer and reviews
    public void DisplayMovie(Movie movie){
        Picasso.with(getActivity()).load(URLs.IMAGE_BASE_URL + URLs.IMAGE_SIZE + movie.getPosterUrl()).fit()
                .into(mPosterImageView);
        mTitleView.setText(movie.getTitle());
        mPlotView.setText(movie.getPlot());
        mDateView.setText(movie.getReleaseDate());
        mRatingView.setText(movie.getRating().toString());
        trailers = new ArrayList<>();
        reviews = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(getActivity() , trailers);
        reviewAdapter = new ReviewAdapter(getActivity() , reviews);
        GetTrailerTask trailerTask = new GetTrailerTask();
        trailerTask.execute();
        GetReviewTask reviewTask = new GetReviewTask();
        reviewTask.execute();
    }

    public class GetTrailerTask extends AsyncTask<Void , Void , ArrayList<Trailer>> {

        @Override
        protected void onPostExecute(ArrayList<Trailer> data) {
            super.onPostExecute(data);
            trailers.clear();
            for(int i=0 ; i<data.size()  ;i++){
                trailers.add(data.get(i));
            }
            trailerAdapter.notifyDataSetChanged();
            for (int i = 0; i < trailerAdapter.getCount(); i++) {
                View view = trailerAdapter.getView(i, null, trailerListView);
                trailerListView.addView(view);
            }
            MovieDetails movieDetails = (MovieDetails) getActivity();
            movieDetails.mShareActionProvider.setShareIntent(createShareIntent());
        }

        @Override
        protected ArrayList<Trailer> doInBackground(Void... voids) {
            if(!Utils.getPreferredSortMethod(getActivity()).equals("favorites")) {
                HttpURLConnection connection ;
                BufferedReader reader;
                Uri trailerRequest = Uri.parse(URLs.MOVIE_BASE_URL).buildUpon().appendPath(mMovie.getId().toString())
                        .appendPath(URLs.TRAILER_URL)
                        .appendQueryParameter(URLs.API_KEY, BuildConfig.MOVIE_API_KEY).build();
                try {
                    URL url = new URL(trailerRequest.toString());
                    Log.i("url", trailerRequest.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if (connection.getInputStream() == null) {
                        Toast.makeText(getActivity() , "No data recieved for Trailers , try again" , Toast.LENGTH_SHORT).show();
                    }
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(reader.readLine());
                    String responseJson = buffer.toString();
                    Log.i("response", responseJson);
                    ArrayList<Trailer> trailersFinalResult = parseTrailerJson(responseJson);
                    return trailersFinalResult;


                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            else {
                Uri trailersUri = Uri.parse(MovieContract.TrailerEntry.CONTENT_URI.toString() +"/"+mMovie.getId());
                Cursor cur = getActivity().getContentResolver().query(trailersUri , null , null , null , null);
                ArrayList<Trailer> data = getTrailersFromCursor(cur);
                cur.close();
                return data;
            }
        }
    }
    private ArrayList<Trailer> parseTrailerJson(String jsonResponse) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonResponse);
        JSONArray resultArray = mainObject.getJSONArray("results");
        ArrayList<Trailer> data = new ArrayList<>();
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject entry = resultArray.getJSONObject(i);
            Trailer temp = new Trailer();
            temp.setId(entry.getString("id"));
            temp.setKey(entry.getString("key"));
            temp.setName(entry.getString("name"));
            temp.setSite(entry.getString("site"));
            temp.setSize(entry.getInt("size"));
            data.add(temp);
        }
        return data;
    }
    public ArrayList<Trailer> getTrailersFromCursor(Cursor cursor){
        int trailerId = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_ID);
        int trailerKeyIndex = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEY);
        int trailerNameIndex = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_NAME);
        int trailerSiteIndex = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_SITE);
        int trailerSizeIndex = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_SIZE);
        ArrayList<Trailer> returnData = new ArrayList<>();
        while (cursor.moveToNext()){
            Trailer iterateObject = new Trailer();
            iterateObject.setId(cursor.getString(trailerId));
            iterateObject.setSize(cursor.getInt(trailerSizeIndex));
            iterateObject.setSite(cursor.getString(trailerSiteIndex));
            iterateObject.setName(cursor.getString(trailerNameIndex));
            iterateObject.setKey(cursor.getString(trailerKeyIndex));
            returnData.add(iterateObject);
        }
        cursor.close();
        return returnData;
    }
    public class GetReviewTask extends AsyncTask<Void , Void , ArrayList<Review>>{

        @Override
        protected void onPostExecute(ArrayList<Review> retData) {
            super.onPostExecute(reviews);
            reviews.clear();
            for(Review temp : retData){
                reviews.add(temp);
            }
            reviewAdapter.notifyDataSetChanged();
            for(int i=0 ; i<reviews.size() ; i++){
                View view = reviewAdapter.getView(i , null , reviewListView);
                reviewListView.addView(view);

            }

        }

        @Override
        protected ArrayList<Review> doInBackground(Void... voids) {
            if (!Utils.getPreferredSortMethod(getActivity()).equals("favorites")) {
                HttpURLConnection connection ;
                BufferedReader reader;
                Uri trailerRequest = Uri.parse(URLs.MOVIE_BASE_URL).buildUpon().appendPath(mMovie.getId().toString())
                        .appendPath(URLs.REVIEW_URL)
                        .appendQueryParameter(URLs.API_KEY, BuildConfig.MOVIE_API_KEY).build();
                try {
                    URL url = new URL(trailerRequest.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if (connection.getInputStream() == null) {

                    }
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(reader.readLine());
                    String responseJson = buffer.toString();
                    ArrayList<Review> reviewsFinalResult = parseReviewJson(responseJson);
                    return reviewsFinalResult;


                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            } else {
                Uri reviewWithIdUri =Uri.parse(MovieContract.ReviewEntry.CONTENT_URI.toString()+"/"+mMovie.getId());
                Cursor queryReview = getActivity().getContentResolver().query(reviewWithIdUri,null,null,null,null);
                ArrayList<Review> data = getReviewFromCursor(queryReview);
                return data;
            }
        }



    }
    private ArrayList<Review> parseReviewJson(String jsonResponse) throws JSONException{
        JSONObject mainObject = new JSONObject(jsonResponse);
        JSONArray resultArray = mainObject.getJSONArray("results");
        ArrayList<Review>retData = new ArrayList<>();
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject entry = resultArray.getJSONObject(i);
            Review temp = new Review();
            temp.setId(entry.getString("id"));
            temp.setAuthor(entry.getString("author"));
            temp.setContent(entry.getString("content"));
            temp.setUrl(entry.getString("url"));
            retData.add(temp);
        }
        return retData;
    }
    private ArrayList<Review> getReviewFromCursor(Cursor cursor){
        int reviewId = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_ID);
        int reviewAuthorIdx = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        int reviewContentIdx = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);
        int reviewUrlIdx = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_URL);
        ArrayList<Review> returnData = new ArrayList<>();
        while (cursor.moveToNext()){
            Review iterateObject = new Review();
            iterateObject.setId(cursor.getString(reviewId));
            iterateObject.setContent(cursor.getString(reviewContentIdx));
            iterateObject.setAuthor(cursor.getString(reviewAuthorIdx));
            iterateObject.setUrl(cursor.getString(reviewUrlIdx));
            returnData.add(iterateObject);
        }
        cursor.close();
        return returnData;
    }
    public void onFavoriteClick(View view){
        if(mFavoriteButton.getText().toString().equals(ADD_TO_FAVORITES)) {
            ContentValues reviewValues[] = new ContentValues[reviews.size()];
            for (int i = 0; i < reviews.size(); i++) {
                ContentValues tempVal = new ContentValues();
                tempVal.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviews.get(i).getId());
                tempVal.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, reviews.get(i).getAuthor());
                tempVal.put(MovieContract.ReviewEntry.COLUMN_CONTENT, reviews.get(i).getContent());
                tempVal.put(MovieContract.ReviewEntry.COLUMN_URL, reviews.get(i).getUrl());
                tempVal.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, mMovie.getId());
                reviewValues[i] = tempVal;
            }
            ContentValues trailersValues[] = new ContentValues[trailers.size()];
            for (int i = 0; i < trailers.size(); i++) {
                ContentValues tempVal = new ContentValues();
                tempVal.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, mMovie.getId());
                tempVal.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailers.get(i).getId());
                tempVal.put(MovieContract.TrailerEntry.COLUMN_KEY, trailers.get(i).getKey());
                tempVal.put(MovieContract.TrailerEntry.COLUMN_NAME, trailers.get(i).getName());
                tempVal.put(MovieContract.TrailerEntry.COLUMN_SITE, trailers.get(i).getSite());
                tempVal.put(MovieContract.TrailerEntry.COLUMN_SIZE, trailers.get(i).getSize());
                trailersValues[i] = tempVal;
            }
            ContentValues movieVals = new ContentValues();
            movieVals.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
            movieVals.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
            movieVals.put(MovieContract.MovieEntry.COLUMN_PLOT, mMovie.getPlot());
            movieVals.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, mMovie.getPosterUrl());
            movieVals.put(MovieContract.MovieEntry.COLUMN_RATING, mMovie.getRating());
            movieVals.put(MovieContract.MovieEntry.COLUMN_REALEASE_DATE, mMovie.getReleaseDate());
            Uri returnUriMovie = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieVals);
            int returnTrailerInserted  = 0, returnReviewInserted=0;
            if(trailersValues.length != 0) {
                returnTrailerInserted = getActivity().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI,
                        trailersValues);
            }
            if(reviewValues.length != 0) {
                returnReviewInserted = getActivity().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI,
                        reviewValues);
            }
            if (returnUriMovie != null) {
                mFavoriteButton.setText(REMOVE_FROM_FAVORITES);
            }
        }
        else {
           int trailersDeleted = getActivity().getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI ,
                    MovieContract.TrailerEntry.COLUMN_MOVIE_ID +" = ? " ,new String[]{mMovie.getId().toString()} );
            int reviewDeleted =  getActivity().getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI ,
                    MovieContract.ReviewEntry.COLUMN_MOVIE_ID +" = ? " ,new String[]{mMovie.getId().toString()} );
            int moviesDeleted =getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI ,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID+" = ? ",new String[]{mMovie.getId().toString()});
            if(moviesDeleted!=0){
                mFavoriteButton.setText(ADD_TO_FAVORITES);
            }
        }

    }

    // function to check if the movie is inserted in DB or not
    boolean checkInsertedInDatabase(){
        Uri moviesWithIdUri =Uri.parse(MovieContract.MovieEntry.CONTENT_URI.toString());
        Cursor movieListCursor = getActivity().getContentResolver().query(moviesWithIdUri,null,null,null,null);
        int movieIdIndex =movieListCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        while(movieListCursor.moveToNext()){
            int indexIterator = movieListCursor.getInt(movieIdIndex);
            if(indexIterator == mMovie.getId()){
                return true;
            }
        }
        return false;
    }
    public Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_TEXT , URLs.YOUTUBE_LINK + trailers.get(0).getKey());
        //startActivity(shareIntent);
        return shareIntent;
    }
}
