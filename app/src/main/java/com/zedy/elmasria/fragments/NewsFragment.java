package com.zedy.elmasria.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.zedy.elmasria.R;
import com.zedy.elmasria.adapters.NewsAdapter;
import com.zedy.elmasria.app.AppController;
import com.zedy.elmasria.jsonParser.Parser;
import com.zedy.elmasria.models.NewsItem;
import com.zedy.elmasria.utils.Constants;
import com.zedy.elmasria.utils.OnLoadMoreListener;
import com.zedy.elmasria.utils.SweetDialogHelper;
import com.zedy.elmasria.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mostafa_anter on 12/2/16.
 */

public class NewsFragment extends Fragment {
    private static final String TAG = "NewsFragment";

    @BindView(R.id.noData) LinearLayout noDataView;

    // for manipulate recyclerView
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private int pageCount = 1;
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected NewsAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<NewsItem> mDataset;

    // for swipe to refresh
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // add listener for loading more view
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public NewsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataset = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);

        ButterKnife.bind(this, view);
        // manipulate recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new NewsAdapter(getActivity(), mDataset);
        // Set TeachersListAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // add listener for recycler view to check if item or loading view
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!mAdapter.isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mAdapter.mOnLoadMoreListener != null) {
                        mAdapter.mOnLoadMoreListener.onLoadMore();
                    }
                    mAdapter.isLoading = true;
                }
            }
        });

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e(TAG, "Load More");
                //pageCount++;
                mDataset.add(null);
                mAdapter.notifyItemInserted(mDataset.size() - 1);

                // loadMoreData
                initiateRefresh();
            }
        });


        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        //noinspection ResourceAsColor
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        24,
                        getResources().getDisplayMetrics()));

        return view;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    // called immediately after onViewCreate
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("swip", "onRefresh called from SwipeRefreshLayout");

                pageCount = 1;
                initiateRefresh();
            }
        });

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        // Start our refresh background task
        initiateRefresh();
    }

    private void initiateRefresh() {
        /**
         * Execute the background task
         */
        makeNewsRequestForRentMotorcycle();

    }

    private void onRefreshComplete() {
        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void makeNewsRequestForRentMotorcycle(){
        if (Utils.isOnline(getActivity())){
            // Tag used to cancel the request
            String  tag_string_req = "string_req";

            String url = Constants.baseUrl + "api/news?page=" + pageCount;
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response.toString());
                    if(pageCount > 1 && mDataset.size() != 0){
                        mDataset.remove(mDataset.size() - 1);
                        mAdapter.notifyItemRemoved(mDataset.size());
                    }else {
                        clearData();
                    }

                    // do what you want here
                    mDataset.addAll(mDataset.size(), Parser.parseNews(response));
                    mAdapter.notifyDataSetChanged();

                    if (pageCount > 1 && mDataset.size() != 0) {
                        mAdapter.setLoaded();
                    }
                    onRefreshComplete();
                    pageCount++;

                    if (mDataset.size() > 0){
                        noDataView.setVisibility(View.GONE);
                    }else {
                        noDataView.setVisibility(View.VISIBLE);
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    onRefreshComplete();
                    if (mDataset.size() > 0){
                        noDataView.setVisibility(View.GONE);
                    }else {
                        noDataView.setVisibility(View.VISIBLE);
                    }
                }
            }){

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", Constants.token);
                    return params;
                }

            };

            strReq.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            strReq.setShouldCache(false);
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        }else {
            onRefreshComplete();
            new SweetDialogHelper(getActivity()).showErrorMessage(getString(R.string.error),
                    getString(R.string.there_is_no_Inter_net));
            if (mDataset.size() > 0){
                noDataView.setVisibility(View.GONE);
            }else {
                noDataView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void clearData() {
        int size = mDataset.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (size > i) {
                    mDataset.remove(0);
                }
            }

            mAdapter.notifyItemRangeRemoved(0, size);
        }
    }




}

