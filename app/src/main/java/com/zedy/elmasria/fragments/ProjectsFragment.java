package com.zedy.elmasria.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.zedy.elmasria.R;
import com.zedy.elmasria.adapters.ItemClickListener;
import com.zedy.elmasria.adapters.Section;
import com.zedy.elmasria.adapters.SectionedExpandableLayoutHelper;
import com.zedy.elmasria.app.AppController;
import com.zedy.elmasria.jsonParser.Parser;
import com.zedy.elmasria.models.ProjectItem;
import com.zedy.elmasria.utils.Constants;
import com.zedy.elmasria.utils.SweetDialogHelper;
import com.zedy.elmasria.utils.Utils;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mostafa_anter on 12/2/16.
 */

public class ProjectsFragment extends Fragment implements ItemClickListener {

    private static final String TAG = "ProjectsFragment";

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh) SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.noData) LinearLayout noDataView;

    private SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    
    public ProjectsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.projects_fragments, container, false);
        ButterKnife.bind(this, view);
        initSwipeToRefresh();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(getActivity(),
                mRecyclerView, this, 1);
        noDataView.setVisibility(View.VISIBLE);
    }

    private void initSwipeToRefresh() {
        //noinspection ResourceAsColor
        mSwipeRefresh.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        mSwipeRefresh.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        24,
                        getResources().getDisplayMetrics()));


        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                initiateRefresh();
            }
        });
        initiateRefresh();

    }

    private void initiateRefresh() {
        if (mSwipeRefresh != null && !mSwipeRefresh.isRefreshing())
            mSwipeRefresh.setRefreshing(true);
        getTasks();
    }

    private void onRefreshComplete() {
        if (mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void getTasks() {
        if (Utils.isOnline(getActivity())) {
            String url = Constants.baseUrl + "/api/projects";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, response.toString());
                            response = StringEscapeUtils.unescapeJava(response);
                            List<ProjectItem> taskList = Parser.parseProjects(response);
                            if (taskList.size() > 0){
                                sectionedExpandableLayoutHelper.removeAllSection();
                                noDataView.setVisibility(View.GONE);
                                for (ProjectItem projectItem: taskList) {
                                    createSection(projectItem);
                                }
                            }else {
                                sectionedExpandableLayoutHelper.removeAllSection();
                                noDataView.setVisibility(View.VISIBLE);
                                sectionedExpandableLayoutHelper.notifyDataSetChanged();

                            }
                            onRefreshComplete();


                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    onRefreshComplete();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", Constants.token);
                    return params;
                }

            };

            // disable cache
            jsonObjReq.setShouldCache(false);
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } else {
            onRefreshComplete();
            new SweetDialogHelper(getActivity()).showErrorMessage(getString(R.string.error),
                    getString(R.string.there_is_no_Inter_net));
        }

    }

    private void createSection(ProjectItem projectItem){
        if (sectionedExpandableLayoutHelper.sectionIsExist(projectItem.getDeliver())){
            sectionedExpandableLayoutHelper.addItem(projectItem.getDeliver(), projectItem);
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }else{
            ArrayList<ProjectItem> projectItems = new ArrayList<>();
            projectItems.add(projectItem);
            sectionedExpandableLayoutHelper.addSection(projectItem.getDeliver(), projectItems);
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }

    }

    @Override
    public void itemClicked(ProjectItem item) {
        
    }

    @Override
    public void itemClicked(Section section) {

    }
}
