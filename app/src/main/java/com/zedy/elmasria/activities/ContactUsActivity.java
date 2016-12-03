package com.zedy.elmasria.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.zedy.elmasria.R;
import com.zedy.elmasria.app.AppController;
import com.zedy.elmasria.utils.Constants;
import com.zedy.elmasria.utils.SweetDialogHelper;
import com.zedy.elmasria.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactUsActivity extends LocalizationActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.title)EditText title;
    @BindView(R.id.subject)EditText subject;
    @BindView(R.id.email)EditText email;
    @BindView(R.id.message)EditText message;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        ButterKnife.bind(this);
        setToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendMessage();
            }
        });

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        title.setTypeface(font);
        email.setTypeface(font);
        subject.setTypeface(font);
        message.setTypeface(font);
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

    private boolean checkFields(){

        // Store values at the time of the login attempt.
        String titleString = title.getText().toString();
        String emailString = email.getText().toString();
        String subjectString = subject.getText().toString();
        String messageString = message.getText().toString();

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(titleString)) {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error),
                    getString(R.string.enterTitle));
            return false;

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailString)) {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error),
                    getString(R.string.enterEmail));
            return false;

        } else if (!isEmailValid(emailString)) {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error),
                    getString(R.string.enterEmail));
            return false;

        }

        if (TextUtils.isEmpty(subjectString)) {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error),
                    getString(R.string.enterSubject));
            return false;

        }

        if (TextUtils.isEmpty(messageString)) {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error),
                    getString(R.string.enterMessage));
            return false;

        }
        return true;

    }

    private boolean isEmailValid(String email){
        return email.length() > 4;
    }

    private void sendMessage(){
        if (Utils.isOnline(this) && checkFields()){
            // Tag used to cancel the request
            String  tag_string_req = "string_req";

            final SweetDialogHelper sweetDialogHelper = new SweetDialogHelper(this);
            sweetDialogHelper.showMaterialProgress(getString(R.string.loading));

            String url = "http://elmasria.co/api/contact";
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

                    JSONObject rootObject = null;
                    try {
                        rootObject = new JSONObject(response);
                        String error = rootObject.optString("error");
                        if (error.equalsIgnoreCase("true")){
                            new SweetDialogHelper(ContactUsActivity.this).showErrorMessage(getString(R.string.error),
                                    getString(R.string.try_again));
                        }else {
                            new SweetDialogHelper(ContactUsActivity.this).showSuccessfulMessage(getString(R.string.done),
                                    getString(R.string.success));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    sweetDialogHelper.dismissDialog();
                    new SweetDialogHelper(ContactUsActivity.this).showErrorMessage(getString(R.string.error),
                            getString(R.string.try_again));

                }
            }){

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", Constants.token);
                    params.put("title", title.getText().toString());
                    params.put("subject", subject.getText().toString());
                    params.put("email", email.getText().toString());
                    params.put("msg", message.getText().toString());

                    return params;
                }

            };

            strReq.setShouldCache(false);
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        }else {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error),
                    getString(R.string.there_is_no_Inter_net));
        }
    }

}
