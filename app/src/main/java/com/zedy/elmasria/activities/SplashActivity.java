package com.zedy.elmasria.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zedy.elmasria.R;
import com.zedy.elmasria.store.ElmasriaPrefStore;
import com.zedy.elmasria.utils.Constants;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends LocalizationActivity {
    @BindView(R.id.splash_logo)
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set default language of activity
        setDefaultLanguage("en");

        if (checkFirstTimeOpenApp() == -1) {
            setLanguage("ar");
        } else {
            if (new ElmasriaPrefStore(this).getIntPreferenceValue(Constants.PREFERENCE_LANGUAGE) == 4) {
                setLanguage("ar");
            } else if (new ElmasriaPrefStore(this).getIntPreferenceValue(Constants.PREFERENCE_LANGUAGE) == 5) {
                setLanguage("en");
            } else {
                setLanguage(Locale.getDefault().getLanguage());
            }
        }

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        loadSplashImage();

        animateImageView();

        FirebaseMessaging.getInstance().subscribeToTopic("elmasria");

    }
    private void loadSplashImage() {
        Glide.with(this)
                .load(R.drawable.splash_logo)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .dontAnimate()
                .into(imageView);
    }

    private void animateImageView() {
        Animation fade0 = AnimationUtils.loadAnimation(this, R.anim.fade_in_enter);

        imageView.startAnimation(fade0);
        fade0.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // do some thing
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);




            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private int checkFirstTimeOpenApp() {
        return new ElmasriaPrefStore(this).getIntPreferenceValue(Constants.PREFERENCE_FIRST_TIME_OPEN_APP_STATE);
    }

}
