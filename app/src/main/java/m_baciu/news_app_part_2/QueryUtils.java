package m_baciu.news_app_part_2;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mihai on 26-Aug-18.
 */

public class QueryUtils {
    public static final String TAG = QueryUtils.class.getSimpleName();
    private static String mURL;

    private QueryUtils() {
    }

        public static String getmURL() {
        return mURL;
    }

    public static void setmURL(String mUrl) {QueryUtils.mURL=mUrl;}

    public static List<News> fetchNewsData(String requestUrl) throws JSONException {
        //Create URL
        URL newsURL = createUrl(requestUrl);
        //Perform HTTP request
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(newsURL);
        } catch (IOException ioe) {
            Log.e(TAG, "fetchNewsData: Problem making HTTP Request", ioe);
        }
        return extractNewsFromJson(jsonResponse);
    }

    // Creates URL Object from String url.
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException mue) {
            Log.e(TAG, "createURL: Problem building URL", mue);
        }
        return url;
    }

    //Makes HTTP Request
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //Check for null
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "makeHttpRequest: Error Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException ioe) {
            Log.e(TAG, "makeHttpRequet: Couldn't retrieve JSON", ioe);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // Read from fetched InputStream and convert it into String
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    // Extract list of news items from fetched String response
    private static List<News> extractNewsFromJson(String newsJSON) throws JSONException {
        //Check for JSON is null
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<News> newsList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject baseJsonResponseResult = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = baseJsonResponseResult.getJSONArray("results");
            // make items
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentResults = resultsArray.getJSONObject(i);
                String Title = currentResults.getString("webTitle");
                String category = currentResults.getString("sectionName");
                String date = currentResults.getString("webPublicationDate");
                String url = currentResults.getString("webUrl");
                JSONArray tagsauthor = currentResults.getJSONArray("tags");
                String author = "";
                if (tagsauthor.length() != 0) {
                    JSONObject currenttagsauthor = tagsauthor.getJSONObject(0);
                    author = currenttagsauthor.getString("webTitle");
                } else {
                    author = "No Author";
                }
                date = date.replaceAll("[a-zA-Z]", " ");
                News news = new News(Title, category, date, url, author);
                newsList.add(news);
            }
        } catch (JSONException je) {
            Log.e(TAG, "extractNewsFromJson: Problem parsing results", je);
        }
        return newsList;
    }
}

