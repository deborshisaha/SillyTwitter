package design.semicolon.sillytwitter.dao;

import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.SillyTwitterApplication;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
import design.semicolon.sillytwitter.managers.NetworkConnectivityManager;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.TwitterMedia;
import design.semicolon.sillytwitter.models.User;
import design.semicolon.sillytwitter.restclient.SillyTwitterClient;

/**
 * Created by dsaha on 2/20/16.
 */
public class TweetDaoImpl implements TweetDao {

    private SillyTwitterClient client;

    private static final String LAST_TWEET_ID = "LAST_TWEET_ID";

    /**
     * @param context
     * @param max_id
     * @param onTweetsLoadedListener
     * @param cachingStrategy
     * @throws NoNetworkConnectionException
     */
    @Override
    public void fetchTimelineTweets(Context context,  long since_id, long max_id, final OnTweetsLoadedListener onTweetsLoadedListener, int cachingStrategy) throws NoNetworkConnectionException {

        /*
        if (CachingStrategy.CacheOnly == cachingStrategy) {

            try {
                String mentionedTweetsString = getStringFromRaw(context, R.raw.deborshisaha_mentions);
                JSONArray mentionedTweetsJSONArray = new JSONArray(mentionedTweetsString);

                Log.d("DEBUG", "Number of tweets in the JSONArray : " + mentionedTweetsJSONArray.length());
                List<Tweet> mentionedTweets = getArrayOfTweetObjects(mentionedTweetsJSONArray, true);
                Log.d("DEBUG", "Number of tweets in mentionedTweets : " + mentionedTweets.size());

                Log.d("DEBUG", "Number of tweets in DB BEFORE persistence : " + Tweet.all().size());
                Log.d("DEBUG", "Number of TwitterMedia in DB BEFORE Tweet persistence : " + TwitterMedia.all().size());
                persistTweets(mentionedTweets);
                Log.d("DEBUG", "Number of tweets in DB AFTER persistence : " + Tweet.all().size());
                Log.d("DEBUG", "Number of TwitterMedia in DB AFTER Tweet persistence : " + TwitterMedia.all().size());

                Log.d("DEBUG", "Finish");
            }
            catch(Throwable t) {
                t.printStackTrace();
            }

            return;
        }
        */

        int count = 0;

        // Check what the network strategy is
        if (cachingStrategy == CachingStrategy.CacheOnly || cachingStrategy == CachingStrategy.CacheThenNetwork) {
            //List<Tweet> list = Tweet.all();
            onTweetsLoadedListener.onTweetsLoaded(Tweet.all(), true);
        }

        if (cachingStrategy == CachingStrategy.CacheOnly) {
            return;
        }

        // if we need to connect to network, check if connected to internet
        if ( !NetworkConnectivityManager.isConnectedToInternet(context)) {
            throw new NoNetworkConnectionException();
        }

        /**
         * To modify throttling
         */
        if (NetworkConnectivityManager.isConnectedToInternetViaWifi(context)) {
            count = 20;
        } else {
            count = 10;
        }

        if (client == null) {
            client = SillyTwitterApplication.getRestClient();
        }

        // Get timeline
        client.getHomeTimeLine(count, since_id, max_id, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                super.onSuccess(statusCode, headers, json);

//                List<Tweet> timelineTweets = getArrayOfTweetObjects(json, false);
//                List<Tweet> savedTweets = Tweet.all();
//                if (savedTweets.size() != processedTweets.size()) {
//                    Log.e("ERROR", "Not all tweets were saved");
//                }
//                Log.d("DEBUG", "Number of tweets in the JSONArray : " + json.length());
                List<Tweet> mentionedTweets = getArrayOfTweetObjects(json, true);
//                Log.d("DEBUG", "Number of tweets in mentionedTweets : " + mentionedTweets.size());
//                Log.d("DEBUG", "Number of tweets in DB BEFORE persistence : " + Tweet.all().size());
//                Log.d("DEBUG", "Number of TwitterMedia in DB BEFORE Tweet persistence : " + TwitterMedia.all().size());
                Log.d("DEBUG", "Number of Users in DB BEFORE Tweet persistence : " + User.all().size());
                persistTweets(mentionedTweets);
                Log.d("DEBUG", "Number of Users in DB BEFORE Tweet persistence : " + User.all().size());
                onTweetsLoadedListener.onTweetsLoaded(Tweet.all(), false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

        });
    }

    private List<Tweet> getArrayOfTweetObjects (JSONArray tweetsJSONArray, boolean debug) {

        List<Tweet> tweets = new ArrayList<Tweet>();

        if (debug) {
            try {
                for (int i = 0; i < tweetsJSONArray.length(); i++) {
                    JSONObject tweetJSONObject = tweetsJSONArray.getJSONObject(i);
                    Tweet tweet = Tweet.fromJSON(tweetJSONObject);
                    if (tweet != null) {
                        tweets.add(tweet);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweets;
    }

    private static long getNewestFetchedId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_TWEET_ID, 0);
    }

    private static void setNewestFetchedId(Context context, long id) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(LAST_TWEET_ID, id).apply();
    }

    /**
     * Persist all contents of the tweet
     */
    private void persistTweets (List<Tweet> tweets) {
        try {
            ActiveAndroid.beginTransaction();
            for (Tweet tweet : tweets) {
                tweet.getUser().save();
                tweet.persistMedia();
                tweet.save();
                if (tweet.getUser()!= null){
                    Log.d("DEBUG", tweet.getUser().getFullName());
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    /**
     * Read from file
     */
    private String getStringFromRaw(Context c, int fileId) throws IOException {
        Resources r = c.getResources();
        InputStream is = r.openRawResource(fileId);
        String statesText = convertStreamToString(is);
        is.close();
        return statesText;
    }

    private String convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = is.read();
        while (i != -1) {
            baos.write(i);
            i = is.read();
        }
        return baos.toString();
    }
}
