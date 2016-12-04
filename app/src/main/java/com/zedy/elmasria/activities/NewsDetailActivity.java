package com.zedy.elmasria.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zedy.elmasria.R;
import com.zedy.elmasria.app.AppController;
import com.zedy.elmasria.jsonParser.Parser;
import com.zedy.elmasria.models.NewsItem;
import com.zedy.elmasria.utils.Constants;
import com.zedy.elmasria.utils.SquaredImageView;
import com.zedy.elmasria.utils.SweetDialogHelper;
import com.zedy.elmasria.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailActivity extends LocalizationActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.feedImage1)
    SquaredImageView imageView;
    @BindView(R.id.progressBar)
    ProgressBar mProgress;

    @BindView(R.id.main_title)
    TextView mainTitel;
    @BindView(R.id.timestamp)
    TextView timeStamp;
    @BindView(R.id.txtDescription)
    TextView textStatusMsg;

    private NewsItem newsItem;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        setToolbar();

        newsItem = getIntent().getParcelableExtra("item");
        id = getIntent().getStringExtra("id");

        if (newsItem != null) {
            setDesign(newsItem);
        } else if (id != null) {
            getObjectDetail();
        } else {
            finish();
        }

    }

    private void setDesign(NewsItem newsItem) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        mainTitel.setText(newsItem.getTitle());
        mainTitel.setTypeface(fontBold);
        timeStamp.setText(newsItem.getTimeStamp());
        timeStamp.setTypeface(font);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(newsItem.getContent())) {
            textStatusMsg.setText(newsItem.getContent());
            textStatusMsg.setTypeface(font);

        } else {
            // status is empty, remove from view
            textStatusMsg.setVisibility(View.GONE);
        }
        // populate mainImage
        Glide.with(this)
                .load(newsItem.getImageUrl())
                .placeholder(R.color.dark_gray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .crossFade()
                .dontAnimate()
                .thumbnail(0.2f)
                .into(imageView);

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

    private void getObjectDetail() {
        if (Utils.isOnline(this)) {
            // Tag used to cancel the request
            String tag_string_req = "string_req";

            final SweetDialogHelper sweetDialogHelper = new SweetDialogHelper(this);
            sweetDialogHelper.showMaterialProgress(getString(R.string.loading));

            String url = "http://elmasria.co/api/news/single";
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    sweetDialogHelper.dismissDialog();
                    newsItem = Parser.parseNewsDetailItem(response);
                    if (newsItem != null)
                        setDesign(newsItem);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    sweetDialogHelper.dismissDialog();
                    new SweetDialogHelper(NewsDetailActivity.this).showErrorMessage(getString(R.string.error),
                            getString(R.string.try_again));

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", Constants.token);
                    params.put("id", id);

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
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        } else {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error),
                    getString(R.string.there_is_no_Inter_net));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
        finish();
    }
}
