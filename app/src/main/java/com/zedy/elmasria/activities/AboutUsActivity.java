package com.zedy.elmasria.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zedy.elmasria.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends LocalizationActivity implements View.OnClickListener{
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.feedImage1)
    ImageView imageView;
    @BindView(R.id.progressBar)
    ProgressBar mProgress;
    @BindView(R.id.navigation)Button navigation;
    @BindView(R.id.contact_us)Button contactUs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ButterKnife.bind(this);
        setToolbar();

        navigation.setOnClickListener(this);
        contactUs.setOnClickListener(this);


        // populate mainImage
        Glide.with(this)
                .load(R.drawable.splash_logo)
                .placeholder(R.color.dark_gray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .crossFade()
                .dontAnimate()
                .thumbnail(0.2f)
                .into(imageView);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");

        navigation.setTypeface(font);
        contactUs.setTypeface(font);

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

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        TextView tv = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv.setTypeface(font);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.navigation:
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=27.181904, 31.183667");
                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
                break;
            case R.id.contact_us:
                startActivity(new Intent(this, ContactUsActivity.class));
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
        }
    }
}
