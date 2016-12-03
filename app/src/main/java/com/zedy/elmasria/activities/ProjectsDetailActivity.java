package com.zedy.elmasria.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.zedy.elmasria.R;
import com.zedy.elmasria.app.AppController;
import com.zedy.elmasria.jsonParser.Parser;
import com.zedy.elmasria.models.ProjectItem;
import com.zedy.elmasria.utils.Constants;
import com.zedy.elmasria.utils.SquaredImageView;
import com.zedy.elmasria.utils.SweetDialogHelper;
import com.zedy.elmasria.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectsDetailActivity extends LocalizationActivity implements View.OnClickListener {
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.main_title) TextView mainTitel;
    @BindView(R.id.timestamp) TextView timeStamp;
    @BindView(R.id.txtDescription) TextView textStatusMsg;
    @BindView(R.id.area) TextView area;

    @BindView(R.id.view_pager) RollPagerView rollPagerView;
    @BindView(R.id.progressBar) ProgressBar mProgress;
    @BindView(R.id.navigation)Button navigation;

    private ProjectItem newsItem;


    public static List<String> images;
    private TestLoopAdapter mLoopAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects_detail);
        ButterKnife.bind(this);
        setToolbar();

        images = new ArrayList<>();

        newsItem = getIntent().getParcelableExtra("item");

        if (newsItem != null){
            setDesign(newsItem);
            getImages();
            navigation.setOnClickListener(this);
        }else {
            finish();
        }

    }

    private void setDesign(ProjectItem newsItem){
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        mainTitel.setText(newsItem.getTitle());
        mainTitel.setTypeface(fontBold);
        timeStamp.setText(newsItem.getTimeStamp());
        timeStamp.setTypeface(font);

        area.setText(newsItem.getArea());
        area.setTypeface(font);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(newsItem.getContent())) {
            textStatusMsg.setText(newsItem.getContent());
            textStatusMsg.setTypeface(font);

        } else {
            // status is empty, remove from view
            textStatusMsg.setVisibility(View.GONE);
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        /*
        * hide title
        * */
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

    }

    @Override
    public void onClick(View v) {
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + newsItem.getCoordinators());

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }

    private class TestLoopAdapter extends StaticPagerAdapter {

        private int count = images.size();

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("RollViewPager", "onClick");
                }
            });
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            // populate mainImage
            Glide.with(ProjectsDetailActivity.this)
                    .load(images.get(position))
                    .placeholder(R.color.dark_gray)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .dontAnimate()
                    .thumbnail(0.2f)
                    .into(view);

            return view;
        }

        @Override
        public int getCount() {
            return count;
        }
    }

    private void setViewPagerAdapter() {
        rollPagerView.setPlayDelay(2500);
        rollPagerView.setAdapter(mLoopAdapter = new TestLoopAdapter());
        //rollPagerView.setHintView(new IconHintView(this,R.drawable.point_focus,R.drawable.point_normal));
        //mRollViewPager.setHintView(new ColorPointHintView(this, Color.YELLOW,Color.WHITE));
        //mRollViewPager.setHintView(new TextHintView(this));
        //mRollViewPager.setHintView(null);
        rollPagerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
    }


    private void getImages(){
        if (Utils.isOnline(this)){
            // Tag used to cancel the request
            String  tag_string_req = "string_req";

            String url = Constants.baseUrl + "api/project/images";
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    mProgress.setVisibility(View.GONE);

                    JSONObject rootObject = null;
                    try {
                        rootObject = new JSONObject(response);
                        String error = rootObject.optString("error");
                        if (error.equalsIgnoreCase("true")){
                            setDefaultImage();
                        }else {
                            JSONArray jsonArray = rootObject.optJSONArray("images");
                            for (int i = 0; i < jsonArray.length() ; i++) {
                                JSONObject image = jsonArray.optJSONObject(i);
                                String imageUrl = Constants.baseUrl + image.optString("img");

                                images.add(imageUrl);
                            }

                            if (images.size() > 0){
                                setViewPagerAdapter();
                            }else {
                                setDefaultImage();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgress.setVisibility(View.GONE);
                    // pass default image
                    setDefaultImage();

                }
            }){

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", Constants.token);
                    params.put("id", newsItem.getId());
                    return params;
                }

            };

            strReq.setShouldCache(false);
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        }else {
            mProgress.setVisibility(View.GONE);
            // pass default image
            setDefaultImage();
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error),
                    getString(R.string.there_is_no_Inter_net));
        }
    }

    private void setDefaultImage(){
        images.add(newsItem.getImageUrl());
        setViewPagerAdapter();
    }


}
