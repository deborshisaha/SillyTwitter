package design.semicolon.sillytwitter.dao;

import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import design.semicolon.sillytwitter.SillyTwitterApplication;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
import design.semicolon.sillytwitter.managers.NetworkConnectivityManager;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.restclient.SillyTwitterClient;

/**
 * Created by dsaha on 2/20/16.
 */
public class TweetDaoImpl implements TweetDao {

    private SillyTwitterClient client;

    /**
     * @param context
     * @param max_id
     * @param onTweetsLoadedListener
     * @param cachingStrategy
     * @throws NoNetworkConnectionException
     */
    @Override
    public void fetchTimelineTweets(Context context,  long since_id, long max_id, final OnTweetsLoadedListener onTweetsLoadedListener, int cachingStrategy) throws NoNetworkConnectionException {

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

                List<Tweet> processedTweets = processedTweets(json);
                List<Tweet> savedTweets = Tweet.all();

                if (savedTweets.size() != processedTweets.size()) {
                    Log.e("ERROR", "Not all tweets were saved");
                }

                onTweetsLoadedListener.onTweetsLoaded(processedTweets, false);
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

    private List<Tweet> processedTweets(JSONArray tweetsJSONArray) {

        List<Tweet> tweets = new ArrayList<Tweet>();

        try {

            ActiveAndroid.beginTransaction();
            try {
                Log.d("DEBUG", "number of tweets:"+tweetsJSONArray.length());
                for (int i = 0; i < tweetsJSONArray.length(); i++) {
                    JSONObject tweetJSONObject = tweetsJSONArray.getJSONObject(i);
                    Tweet tweet = Tweet.fromJSON(tweetJSONObject);
                    if (tweet != null) {
                        tweet.getUser().save();
                        tweet.save();
                        tweets.add(tweet);
                    }
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweets;
    }
}
