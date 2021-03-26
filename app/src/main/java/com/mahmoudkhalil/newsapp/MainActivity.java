package com.mahmoudkhalil.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;

    private static final int NEWS_LOADER_ID = 1;

    // default values
    private String pageSize = "20";
    private String orderBy = "newest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNewsRecyclerView();

        LoaderManager.getInstance(this).initLoader(NEWS_LOADER_ID, null, newsLoaderCallback());
    }

    private void initNewsRecyclerView() {
        newsRecyclerView = findViewById(R.id.news_recyclerView);
        newsAdapter = new NewsAdapter(this);
        newsRecyclerView.setAdapter(newsAdapter);
    }

    private LoaderManager.LoaderCallbacks<List<News>> newsLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<List<News>>() {
            @NonNull
            @Override
            public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
                String GuardianRequestUrl = "https://content.guardianapis.com/search?";
                Uri baseUri = Uri.parse(GuardianRequestUrl);
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("order-by", orderBy);
                uriBuilder.appendQueryParameter("page-size", pageSize);
                uriBuilder.appendQueryParameter("show-tags", "contributor");
                uriBuilder.appendQueryParameter("api-key", "ef6b7acd-f83e-4d19-87c2-f02b44ea6e6c");
                return new NewsLoader(MainActivity.this, uriBuilder.toString());
            }

            @Override
            public void onLoadFinished(@NonNull Loader<List<News>> loader, final List<News> data) {
                TextView emptyOrNoInternet = findViewById(R.id.empty_view_or_noInternet);
                if(!isNetworkConnected()) {
                    View parentView = findViewById(android.R.id.content);
                    Snackbar.make(parentView, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Exit", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).setActionTextColor(getResources().getColor(R.color.red)).show();
                } else if(data.isEmpty()) {
                    emptyOrNoInternet.setText(R.string.no_news);
                }
                newsAdapter.setNewsList(data);

                ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.GONE);
                newsAdapter.setOnItemClickListener(new NewsAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(position).getUrl()));
                        startActivity(webIntent);
                    }
                });
            }

            @Override
            public void onLoaderReset(@NonNull Loader<List<News>> loader) {
            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.filter) {
            final Dialog filterDialog = new Dialog(this);
            filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            filterDialog.setContentView(R.layout.filter_dialog);
            filterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            filterDialog.setCancelable(false);
            Button okButton = filterDialog.findViewById(R.id.ok);
            Button cancelButton = filterDialog.findViewById(R.id.cancel);
            final RadioButton newestRadioButton = filterDialog.findViewById(R.id.newest_rb);
            final RadioButton relevantRadioButton = filterDialog.findViewById(R.id.relevant_rb);
            final RadioButton oldestRadioButton = filterDialog.findViewById(R.id.oldest_rb);
            final EditText pageSizeEditText= filterDialog.findViewById(R.id.pageSize_EditText);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView newsTextView = findViewById(R.id.newsTextView);
                    if(newestRadioButton.isChecked()){
                        orderBy = "newest";
                        newsTextView.setText(R.string.last_news);
                    } else if(relevantRadioButton.isChecked()){
                        orderBy = "relevance";
                        newsTextView.setText(R.string.relevant_news);
                    } else if(oldestRadioButton.isChecked()) {
                        orderBy = "oldest";
                        newsTextView.setText(R.string.oldest_news);
                    }
                    pageSize = pageSizeEditText.getText().toString();
                    LoaderManager.getInstance(MainActivity.this)
                            .restartLoader(NEWS_LOADER_ID, null, newsLoaderCallback());
                    filterDialog.dismiss();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterDialog.dismiss();
                }
            });
            filterDialog.show();
        }
        return true;
    }

    /**
     * checking the internet connection
     * @return false if there is no internet
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}