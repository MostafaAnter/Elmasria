package com.zedy.elmasria.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zedy.elmasria.R;
import com.zedy.elmasria.models.NewsItem;
import com.zedy.elmasria.utils.SquaredImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailActivity extends LocalizationActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.feedImage1) SquaredImageView imageView;
    @BindView(R.id.progressBar) ProgressBar mProgress;

    @BindView(R.id.main_title) TextView mainTitel;
    @BindView(R.id.timestamp) TextView timeStamp;
    @BindView(R.id.txtDescription) TextView textStatusMsg;

    private NewsItem newsItem;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        setToolbar();

        newsItem = getIntent().getParcelableExtra("item");

        if (newsItem != null){
            setDesign(newsItem);
        }else {
            finish();
        }

    }

    private void setDesign(NewsItem newsItem){
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


}
