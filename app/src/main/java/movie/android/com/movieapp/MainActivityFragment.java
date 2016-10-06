package movie.android.com.movieapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import movie.android.com.movieapp.database.MovieContract;
import movie.android.com.movieapp.models.Movie;

/**
 * A fragment to display main movies posters
 */
public class MainActivityFragment extends Fragment implements SearchView.OnQueryTextListener {
    private GridView mMainGrid;
    private String mSortMethod;
    private ArrayList<Movie> mAllMovies;
    private ProgressBar mProgressBar;
    private MovieArrayAdapter mArrayAdapter;
    private static final String SELECTED = "selected_position";
    private int mPosition = GridView.INVALID_POSITION;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RequestQueue mRequestQueue;

    public MainActivityFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState !=null){
            mAllMovies = (ArrayList<Movie>) savedInstanceState.getSerializable("allmovies");
        }else {
            mAllMovies = new ArrayList<>();
        }
        mSortMethod = Utils.getPreferredSortMethod(getActivity());
        mArrayAdapter = new MovieArrayAdapter(getContext() , mAllMovies);

        setHasOptionsMenu(true);

    }
    @Override
    public void onSaveInstanceState(Bundle bundle){

        bundle.putSerializable("allmovies" , mAllMovies);
        if(mPosition != GridView.INVALID_POSITION){
            bundle.putInt(SELECTED , mPosition);
        }
        super.onSaveInstanceState(bundle);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container , false);
        mProgressBar =(ProgressBar) rootView.findViewById(R.id.progress_bar);
        mMainGrid = (GridView) rootView.findViewById(R.id.grid);

        if(savedInstanceState != null){
            mAllMovies = (ArrayList<Movie>) savedInstanceState.getSerializable("allmovies");

            mMainGrid.setAdapter(mArrayAdapter);
            mArrayAdapter.notifyDataSetChanged();
            if (savedInstanceState.containsKey(SELECTED)){
                mPosition = savedInstanceState.getInt(SELECTED);
            }
        }else {
            String sortMethod = Utils.getPreferredSortMethod(getActivity());
            updateData(sortMethod);
            mArrayAdapter = new MovieArrayAdapter(getContext(), mAllMovies);
            mMainGrid.setAdapter(mArrayAdapter);
            mArrayAdapter.notifyDataSetChanged();

        }

        mMainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((Callback)getActivity()).onMovieSelected(mAllMovies.get(i));
                mPosition = i;
            }

        });

        return rootView;
    }


    @Override
    public void onResume(){
        // update data if sort method changed or if the sort method
        // was set to favorites ( in case of deletion from DB to take
        // effect in main gridview )
        // else no need to update
        if(!Utils.getPreferredSortMethod(getActivity()).equals(mSortMethod)){
            updateData(Utils.getPreferredSortMethod(getActivity()));
            }
        else if (Utils.getPreferredSortMethod(getActivity()).equals("favorites")){
            updateData(Utils.getPreferredSortMethod(getActivity()));
        }
        super.onResume();
    }


    public void updateData(String sortMethod) {
        if (!Utils.checkConnection(getActivity()) && !(sortMethod.equals("favorites")) ) {
            Toast.makeText(getActivity(), "Please Check internet Connection", Toast.LENGTH_LONG).show();
        } else{
                GetMoviesData task = new GetMoviesData();
                task.execute(sortMethod);

        }
    }

    public ArrayList<Movie> getMoviesFromCursor(Cursor cursor){
        int imageUrlIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL);
        int titleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int movieIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int movieRatingIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
        int moviePlotIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT);
        int movieDateIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REALEASE_DATE);
        ArrayList<Movie> returnData = new ArrayList<>();
        while (cursor.moveToNext()){
            Movie iterateObject = new Movie();
            iterateObject.setTitle(cursor.getString(titleIndex));
            iterateObject.setId(cursor.getInt(movieIdIndex));
            iterateObject.setPosterUrl(cursor.getString(imageUrlIndex));
            iterateObject.setPlot(cursor.getString(moviePlotIndex));
            iterateObject.setRating(cursor.getDouble(movieRatingIndex));
            iterateObject.setReleaseDate(cursor.getString(movieDateIndex));
            returnData.add(iterateObject);
        }
        cursor.close();
        return returnData;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        final Uri movieSearch = Uri.parse(URLs.MOVIE_SEARCH).buildUpon()
                .appendQueryParameter(URLs.API_KEY, BuildConfig.MOVIE_API_KEY).appendQueryParameter("query" , s).build();
         String response = null;
        new AsyncTask<Uri , Void , ArrayList<Movie> >(){

           @Override
           protected ArrayList<Movie> doInBackground(Uri... uris) {
               ArrayList<Movie> searchData=null ;
               try {
                   String returnData = Utils.performNetworkRequest(movieSearch , getActivity());
                       searchData = parseJson(returnData);
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (JSONException e){

               }
               return searchData;
           };

            @Override
            protected void onPostExecute(ArrayList<Movie> movies) {
                super.onPostExecute(movies);
                if(movies == null){
                    Toast.makeText(getActivity() , "Network Error , Try Again Later" , Toast.LENGTH_LONG).show();
                }
                else {
                    mAllMovies.clear();
                    for (int i = 0; i < movies.size(); i++) {
                        mAllMovies.add(movies.get(i));
                    }

                    mArrayAdapter.notifyDataSetChanged();
                    if (mPosition != GridView.INVALID_POSITION) {
                        mMainGrid.smoothScrollToPosition(mPosition);
                    }
                }

            }
        }.execute(movieSearch);
//        ArrayList<Movie> searchData = null;
//        try {
//            searchData = parseJson(response);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if(searchData== null){
//            Toast.makeText(getActivity() , "Network Error , Try Again Later" , Toast.LENGTH_LONG).show();
//        }
//        else {
//            mAllMovies.clear();
//            for (int i = 0; i < searchData.size(); i++) {
//                mAllMovies.add(searchData.get(i));
//            }
//
//            mArrayAdapter.notifyDataSetChanged();
//
//        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public class GetMoviesData extends AsyncTask <String , Void , ArrayList<Movie>>  {
        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            mProgressBar.setVisibility(View.GONE);
            super.onPostExecute(movies);

            if(movies == null){
                Toast.makeText(getActivity() , "Network Error , Try Again Later" , Toast.LENGTH_LONG).show();
            }
            else {
                mAllMovies.clear();
                for (int i = 0; i < movies.size(); i++) {
                    mAllMovies.add(movies.get(i));
                }

                mArrayAdapter.notifyDataSetChanged();
                if (mPosition != GridView.INVALID_POSITION) {
                    mMainGrid.smoothScrollToPosition(mPosition);
                }
            }

        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if(!Utils.getPreferredSortMethod(getActivity()).equals("favorites")) {
                Uri movieRequest = Uri.parse(URLs.MOVIE_BASE_URL).buildUpon().appendPath(params[0])
                        .appendQueryParameter(URLs.API_KEY, BuildConfig.MOVIE_API_KEY).build();
                try {
                    String responseJson = null;
                    try {
                        responseJson = Utils.performNetworkRequest(movieRequest , getActivity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("Json" , responseJson);
                    ArrayList<Movie> finalResult = parseJson(responseJson);
                    return finalResult;
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return null;
            }
            else {
                ArrayList<Movie> retData;
                Uri moviesUri = MovieContract.MovieEntry.CONTENT_URI;
                Cursor cursor = getActivity().getContentResolver().query(moviesUri , null , null , null , null);
                retData = getMoviesFromCursor(cursor);
                cursor.close();
                return retData;
            }
        }




    }
    public ArrayList<Movie> parseJson(String jsonResponse) throws JSONException {
        ArrayList<Movie> data = new ArrayList<>();
        final String RESULTS = "results";
        final String ID = "id";
        final String TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String DATE = "release_date";
        final String POSTER = "poster_path";
        final String VOTE = "vote_average";
        JSONObject moviesData = new JSONObject(jsonResponse);
        JSONArray moviesArray = moviesData.getJSONArray(RESULTS);
        for (int i = 0; i < moviesArray.length(); i++) {
            Movie temp = new Movie();
            JSONObject currentItem = (JSONObject) moviesArray.get(i);
            temp.setId((int) currentItem.getInt(ID));
            temp.setPlot((String) currentItem.get(OVERVIEW));
            temp.setPosterUrl((String) currentItem.getString(POSTER));
            temp.setRating( currentItem.getDouble(VOTE));
            temp.setTitle((String) currentItem.get(TITLE));
            temp.setReleaseDate((String) currentItem.get(DATE));
            data.add(temp);
        }
        return data;

    }
    public interface Callback{
        void onMovieSelected(Movie movie);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu , menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView =(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

}

