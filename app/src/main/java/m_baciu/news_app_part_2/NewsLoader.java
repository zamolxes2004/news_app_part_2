package m_baciu.news_app_part_2;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mihai on 26-Aug-18.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private static String REQUEST_URL =
            "https://content.guardianapis.com/search?q=football&show-tags=contributor&api-key=dcd9ad5e-c852-4c47-bc8f-eab7f3411f07";
    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl= url;
    }

    @Override
    public List<News> loadInBackground() {
        List<News> news = new ArrayList<>();
        if (mUrl == null) {
            return null;
        }
        try {
            news = QueryUtils.fetchNewsData(mUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return news;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}

