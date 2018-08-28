package m_baciu.news_app_part_2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, AdapterView.OnItemClickListener {

    private final String BASE_URL = "https://content.guardianapis.com/search?q=sports&show-tags=contributor&api-key=";
    private final String API_KEY = "dcd9ad5e-c852-4c47-bc8f-eab7f3411f07";
    private final String ORDER_BY_ATTR = "&order-by=oldest";
    private ListView listView;
    private NewsAdapter adapter;
    private ArrayList<News> mNewsItems;

    @Override
    protected void onResume() {
        fetchSavedPreferences();
        super.onResume();
    }
    //Get saved preferences and build the url

    private void fetchSavedPreferences() {
        String url;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString(getString(R.string.key), getString(R.string.default_order_value)).equals(getString(R.string.default_order_value))) {
            url = BASE_URL + API_KEY;
        }
        else {
            url = BASE_URL + API_KEY + ORDER_BY_ATTR;
        }
        Log.v("switch_value", preferences.getString("category_list", ""));
        switch(preferences.getString("category_list", "")) {
            case "All Sports":
                QueryUtils.setmURL(url);
                break;
            case "Football":
                QueryUtils.setmURL(url + "&q=football");
                break;
            case "Tennis":
                QueryUtils.setmURL(url + "&q=tennis");
                break;
            case "Basketball":
                QueryUtils.setmURL(url + "&q=basketball");
                break;
            case "Volleyball":
                QueryUtils.setmURL(url + "&q=volleyball");
                break;
            case "Handball":
                QueryUtils.setmURL(url + "&q=handball");
                break;
            default:
                QueryUtils.setmURL(url);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list_view);
        // create an empty list of news items
        mNewsItems = new ArrayList<>();

        if (!isConnected()) {
            // Device is not connected
            // Display the error information
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.internet_error_messages)
                    .setTitle(R.string.internet_error_title);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // else start to fetch the data in background
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, this);
            // To display details of a news
            // implement onclick for each item in list
            listView.setOnItemClickListener(this);

        }
    }

    // On creation of loader
    // create a http request on guardian server
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        Log.v("URL Received", QueryUtils.getmURL());

        Uri baseUri = Uri.parse(QueryUtils.getmURL());
        Uri.Builder uriBuilder = baseUri.buildUpon();
        return new NewsLoader(this, uriBuilder.toString());
    }

    // on receiving the response, Update the UI
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        mNewsItems = new ArrayList<>(data);
        if (mNewsItems.isEmpty()) {
            // No news fetched
            // Display Message to user
            // possible reasons are server is down
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.content_error_messages)
                    .setTitle(R.string.content_error_title);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        UpdateView(mNewsItems);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
    }

    private void UpdateView(ArrayList<News> newsItems) {
        adapter = new NewsAdapter(getApplicationContext(), newsItems);
        listView.setAdapter(adapter);
    }

    // On click over any item in list
    // a webview will be opened with url specific to that item
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent in = new Intent(getApplicationContext(), NewsWebView.class);
        in.putExtra("URL", mNewsItems.get(position).getmUrl());
        startActivity(in);
    }
    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.choose_topic) {
            Intent intent = new Intent(this, CategoryChoice.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Checks if device is connected to internet
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
