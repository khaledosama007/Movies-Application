package movie.android.com.movieapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.android.volley.Request;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * class to hold some helper functions
 */
public class Utils {
    public static boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
    public static String getPreferredSortMethod(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("sort", "popular");
           }
    public static String performNetworkRequest(Uri requestUri , Context context) throws IOException {
//        HttpURLConnection connection;
//        BufferedReader reader;
//        try {
//            URL url = new URL(requestUri.toString());
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.connect();
//            if (connection.getInputStream() == null) {
//
//                return null;
//            }
//            InputStream in = connection.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(in));
//            StringBuffer buffer = new StringBuffer();
//            buffer.append(reader.readLine());
//            String responseJson = buffer.toString();
//            return responseJson;
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        OkHttpClient client = new OkHttpClient();
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(requestUri.toString())
                .build();
        Response response = client.newCall(request).execute();
        if(response.body().toString()!= null){
            return response.body().string();
        }
        return null;
    }
    }
