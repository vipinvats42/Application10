package com.example.tapesh.application10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends Activity {
    static String TWITTER_CONSUMER_KEY="Jn5m2elbjlAxszbCLIgnZYQDK";
    static String TWITTER_CONSUMER_SECRET="UHJRSVbwNwMMLLcQQquXiOkSw0AqN5lNpAyyvRSOXB5G2b7bIe";
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
   // static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    ProgressDialog pDialog;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private static SharedPreferences mSharedPreferences;

    EditText sts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Log.i("Momark","application started execution 1");



        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Log.i("Momark", "application started execution 2");
        }

        if (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
            Toast.makeText(this, "Please set your twitter oauth tokens first!", Toast.LENGTH_LONG).show();

        }

        mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

        findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToTwitter();
            }
        });

        sts = (EditText) findViewById(R.id.editTextedit);
        Log.i("Momark","application started execution 3");
        String status = sts.getText().toString();
        if (status.trim().length() > 0) {
            new updateTwitterStatus().execute(status);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please enter status message", Toast.LENGTH_SHORT).show();
        }


        if (!isTwitterLoggedInAlready()) {
            final String verifier;
            Uri uri = getIntent().getData();
            if (uri != null ) {
                verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
                try {
                    System.out.println("after login");
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                    e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                    e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                    e.commit();
                    findViewById(R.id.button11).setVisibility(View.GONE);
                    Log.i("Momark", "application started execution 4");
                    long userID = accessToken.getUserId();
                    User user = twitter.showUser(userID);
                    String username = user.getName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void loginToTwitter() {
        if (!isTwitterLoggedInAlready()) {
            new Thread(){
                @Override
                public void run() {
// TODO Auto-generated method stub
                    super.run();
                    ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                    builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
                    Configuration configuration = builder.build();
                    TwitterFactory factory = new TwitterFactory(configuration);
                    twitter = factory.getInstance();
                    try {
                       // requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                       // MainActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
                           //     Uri.parse(requestToken.getAuthenticationURL())));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Already Logged into twitter", Toast.LENGTH_LONG).show();
        }
    }


    private boolean isTwitterLoggedInAlready() {
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }


    class updateTwitterStatus extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String...args)
        {
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
                twitter4j.Status response = twitter.updateStatus(status);
            }catch (Exception e)
            {
                Log.d("Twitter Update Error", e.getMessage());
            }
        return null;
        }

        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Status tweeted successfully", Toast.LENGTH_SHORT)
                            .show();
                    sts.setText("");
                }
            });
        }
    }}










