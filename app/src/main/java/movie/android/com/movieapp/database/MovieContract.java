package movie.android.com.movieapp.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by khaled on 23/08/16.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY  = "movie.android.com.movieapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";

    public static final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID="movie_id";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_POSTER_URL= "poster_url";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_PLOT= "plot";
        public static final String COLUMN_REALEASE_DATE = "date";
        // additional fields for content provider
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
    }
    public static final class TrailerEntry implements BaseColumns{
        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_TRAILER;
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();
    }
    public static final class ReviewEntry implements BaseColumns{
        public static final String TABLE_NAME = "review";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_REVIEW;
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static Uri buildReviewWithMovie(int movieId){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

    }
}
