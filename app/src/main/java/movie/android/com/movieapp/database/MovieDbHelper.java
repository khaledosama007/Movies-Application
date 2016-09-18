package movie.android.com.movieapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import movie.android.com.*;

/**
 * Created by khaled on 23/08/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movie.db";
    public MovieDbHelper(Context context){
        super(context , DATABASE_NAME , null , DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ( "+
                MovieContract.MovieEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_TITLE +" TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_RATING+" REAL NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_PLOT+" TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_POSTER_URL+ " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_REALEASE_DATE+" TEXT NOT NULL, " +
                " FOREIGN KEY ("+ MovieContract.MovieEntry.COLUMN_MOVIE_ID+") REFERENCES " +
                MovieContract.TrailerEntry.TABLE_NAME+ " ("+ MovieContract.TrailerEntry.COLUMN_MOVIE_ID+") " +
                "ON DELETE CASCADE ON UPDATE CASCADE "+
                " FOREIGN KEY ("+ MovieContract.MovieEntry.COLUMN_MOVIE_ID+") REFERENCES " +
                MovieContract.ReviewEntry.TABLE_NAME+ " ("+ MovieContract.ReviewEntry.COLUMN_MOVIE_ID+") " +
                "ON DELETE CASCADE ON UPDATE CASCADE );";
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " ("+
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID +" INTEGER NOT NULL,"+
                MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, "+
                MovieContract.TrailerEntry.COLUMN_KEY + " TEXT NOT NULL, "+
                MovieContract.TrailerEntry.COLUMN_NAME + " TEXT NOT NULL, "+
                MovieContract.TrailerEntry.COLUMN_SITE + " TEXT NOT NULL, "+
                MovieContract.TrailerEntry.COLUMN_SIZE + " INTEGER NOT NULL );";
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE "+ MovieContract.ReviewEntry.TABLE_NAME+" ("+
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID +  " TEXT NOT NULL, "+
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "+
                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, "+
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL" +"); ";

        Log.e("2 : " , SQL_CREATE_REVIEW_TABLE);
        Log.e("3 : " , SQL_CREATE_TRAILER_TABLE);

        Log.e("1 : " , SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
    }
}
