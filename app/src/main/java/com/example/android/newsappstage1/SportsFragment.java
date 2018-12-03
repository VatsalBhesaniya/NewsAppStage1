package com.example.android.newsappstage1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SportsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {
    public static final String LOG_TAG = CultureFragment.class.getName();
    private static final String GAURDIAN_REQUEST_URL = "https://content.guardianapis.com/sport?api-key=a9e74512-e71b-478a-af66-5b7ac6f2cffa&show-tags=contributor&show-fields=thumbnail,body";
    private static final int NEWS_LOADER_ID = 6;
    RelativeLayout relativeLayout;
    View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recycler_view, container, false);
        relativeLayout = view.findViewById(R.id.main_content);
        recyclerView = view.findViewById(R.id.recycler_view);
        mEmptyStateTextView = view.findViewById(R.id.empty_view);
        //create and set layout manager for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //Initializing and set adapter for RecyclerView
        mAdapter = new NewsAdapter(getContext(), new ArrayList<News>());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        //Calling method to fetch data.
        fetchData();
        //onClickListner to refresh data
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        return relativeLayout;
    }

    public void fetchData() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getActivity().getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for the bundle.
            // Pass in this activity for the LoaderCallbacks parameter.
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = view.findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    public void refreshFetchedData() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getActivity().getLoaderManager();
            // Restart the loader. Pass in the int ID constant defined above and pass in null for the bundle.
            // Pass in this activity for the LoaderCallbacks parameter.
            loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = view.findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    public void refreshData() {
        refreshFetchedData();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Log.i(LOG_TAG, "TEST: onCreateLoader() called");
        return new NewsLoader(getContext(), GAURDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        Log.i(LOG_TAG, "TEST: onLoadFinished() called");
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = view.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_news);
        // Clear the adapter of previous earthquake data
        mAdapter.updateData(null);
        // If there is a valid list of {@link News}, then add them to the adapter's data set.
        // This will trigger the RecyclerView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.updateData(news);
            mEmptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.i(LOG_TAG, "TEST: onLoaderReset() called");
        // Loader reset, so we can clear out our existing data.
        mAdapter.updateData(null);
    }
}
