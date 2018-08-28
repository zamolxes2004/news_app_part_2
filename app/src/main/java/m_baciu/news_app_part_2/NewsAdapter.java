package m_baciu.news_app_part_2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mihai on 26-Aug-18.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate
                    (R.layout.news_item, parent, false);
        }

        News currentNews = getItem(position);
        TextView newsTitleTextView = listItemView.findViewById(R.id.title_text_view);
        newsTitleTextView.setText(currentNews != null ? currentNews.getmTitle() : null);

        TextView newsCategorytextView = listItemView.findViewById(R.id.category_text_view);
        newsCategorytextView.setText(currentNews != null ? currentNews.getmCategory() : null);

        TextView newsDateTextView = listItemView.findViewById(R.id.date_text_view);
        newsDateTextView.setText(currentNews != null ? currentNews.getmDate() : null);

        TextView newsAuthorTextView = listItemView.findViewById(R.id.author_text_view);
        newsAuthorTextView.setText(currentNews != null ? currentNews.getmAuthor() : null);

        return listItemView;
    }
}
