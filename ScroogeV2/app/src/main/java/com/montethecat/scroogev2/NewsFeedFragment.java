package com.montethecat.scroogev2;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.ArrayList;


public class NewsFeedFragment extends Fragment {

    private static final String TAG = NewsFeedFragment.class.getSimpleName();
    private ListView mListView;
    private ProgressBar mProgressBar;
    private ArticlesViewAdapter mListAdapter;
    private ArrayList<ArticlesItem> mListData;
    private String FEED_URL;
    public static ProgressBar progressBar2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("News Feed");
        return inflater.inflate(R.layout.activity_articles, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FEED_URL = "https://newsapi.org/v2/top-headlines?country=sg&category=business&apiKey=ea05b22a2fef43e2bf76c47a46bff18d";

        mListView = getView().findViewById(R.id.listView);
        mProgressBar = getView().findViewById(R.id.progressBar);

        mListData = new ArrayList<>();
        mListAdapter = new ArticlesViewAdapter(getActivity(), R.layout.article_item, mListData);
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArticlesItem item =(ArticlesItem) parent.getItemAtPosition(position);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));

                startActivity(browserIntent);
            }
        });


        //Start download
        new NewsFeedFragment.DownloadTask().execute(FEED_URL);
        mProgressBar.setVisibility(View.VISIBLE);



    }


    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                if (result != null) {

                    String response = streamToString(urlConnection.getInputStream());


                    parseResult(response);


                    return result;


                }
            } catch (MalformedURLException e) {
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }



    @Override
    protected void onPostExecute(String result) {
        // Download complete. Let us update UI
        if (result != null) {

            mListAdapter.setListData(mListData);

        } else {
            Toast.makeText(getActivity(), "Failed to load data!", Toast.LENGTH_SHORT).show();
        }
        mProgressBar.setVisibility(View.GONE);
    }
}

    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     * @param result
     */
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("articles");
            ArticlesItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");
                String image = post.optString("urlToImage");
                String description = post.optString("description");
                String url = post.optString("url");
                item = new ArticlesItem();
                item.setTitle(title);
                item.setImage(image);
                item.setUrl(url);
                item.setDescription(description);

                mListData.add(item);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}



