package design.semicolon.sillytwitter.dao;

import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceManager;

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
import design.semicolon.sillytwitter.managers.PersistenceManager;
import design.semicolon.sillytwitter.managers.NetworkConnectivityManager;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.TwitterMedia;
import design.semicolon.sillytwitter.models.User;

/**
 * Created by dsaha on 2/20/16.
 */
public class TweetDaoImpl implements TweetDao {

    // private SillyTwitterClient client;

    private static final String OLDEST_TIMELINE_TWEET_ID = "OLDEST_TIMELINE_TWEET_ID";
    private static final String NEWEST_TIMELINE_TWEET_ID = "NEWEST_TIMELINE_TWEET_ID";

    private static final String OLDEST_USER_MENTIONED_TWEET_ID = "OLDEST_USER_MENTIONED_TWEET_ID";
    private static final String NEWEST_USER_MENTIONED_TWEET_ID = "NEWEST_USER_MENTIONED_TWEET_ID";

    private Context mContext;

    public TweetDaoImpl(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public TweetDaoImpl() { super();}

    @Override
    public void fetchTimelineTweets(Context context,  long since_id, long max_id, final OnTweetsLoadedListener onTweetsLoadedListener, int cachingStrategy) throws NoNetworkConnectionException {

        ///////// Remove this ///////////
        if (CachingStrategy.CacheOnly == cachingStrategy) {

            try {
                String mentionedTweetsString = getStringFromRaw(context, R.raw.deborshisaha_user_timeline);
                JSONArray mentionedTweetsJSONArray = new JSONArray(mentionedTweetsString);
                List<Tweet> mentionedTweets = getArrayOfTweetObjects(mentionedTweetsJSONArray, true);
                PersistenceManager.persistTweets(mentionedTweets);
                onTweetsLoadedListener.onTweetsLoaded(Tweet.all(), false);

            } catch(Throwable t) {
                t.printStackTrace();
            }

            return;
        }
        ///////// Remove this ///////////

        // Return true then fetch
        if (PersistenceManager.fetchCachedTimelineTweets(cachingStrategy, onTweetsLoadedListener)) {
            return;
        }

        // if we need to connect to network, check if connected to internet
        if ( !NetworkConnectivityManager.isConnectedToInternet(context)) {
            throw new NoNetworkConnectionException();
        }

        // Get timeline
        SillyTwitterApplication.getRestClient().getHomeTimeLine(getFetchCount(context), since_id, max_id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                super.onSuccess(statusCode, headers, json);
                List<Tweet> mentionedTweets = getArrayOfTweetObjects(json, true);
                PersistenceManager.persistTweets(mentionedTweets);
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

    @Override
    public void fetchMentions( Context context, final OnTweetsLoadedListener onTweetsLoadedListener, int cachingStrategy) throws NoNetworkConnectionException {
        ///////// Remove this ///////////
        if (CachingStrategy.CacheOnly == cachingStrategy) {

            try {

                Tweet.deleteAll();
                User.deleteAll();
                TwitterMedia.deleteAll();
                String mentionedTweetsString = getStringFromRaw(context, R.raw.deborshisaha_mentions);

                JSONArray mentionedTweetsJSONArray = new JSONArray(mentionedTweetsString);
                List<Tweet> mentionedTweets = getArrayOfTweetObjects(mentionedTweetsJSONArray, true);

                PersistenceManager.persistTweets(mentionedTweets);
                onTweetsLoadedListener.onTweetsLoaded(Tweet.getTweetsUserWasMentioned(), false);
            } catch(Throwable t) {
                t.printStackTrace();
            }

            return;
        }
        ///////// Remove this ///////////

        // Return true then fetch
        if (PersistenceManager.fetchUserMentionedTweets(cachingStrategy, onTweetsLoadedListener)) {
            return;
        }

        // if we need to connect to network, check if connected to internet
        if ( !NetworkConnectivityManager.isConnectedToInternet(context)) {
            throw new NoNetworkConnectionException();
        }

        long oldestTweetUserWasMentioned = getOldestTweetInUserMentionFetchedId(context);
        long newestTweetUserWasMentioned = getNewestTweetInUserMentionFetchedId(context);

        // Get User mentions
        SillyTwitterApplication.getRestClient().getUserMentions(getFetchCount(context), newestTweetUserWasMentioned, oldestTweetUserWasMentioned, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                super.onSuccess(statusCode, headers, json);
                List<Tweet> mentionedTweets = getArrayOfTweetObjects(json, true);
                PersistenceManager.persistTweets(mentionedTweets);
                onTweetsLoadedListener.onTweetsLoaded(Tweet.getTweetsUserWasMentioned(), false);
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

                    if (tweet.wasUserMentioned(User.currentUser(mContext), tweetJSONObject)) {
                        tweet.setUserMentioned(true);
                    } else {
                        tweet.setUserMentioned(false);
                    }

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

    private static long getNewestTweetInTimeLineFetchedId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(NEWEST_TIMELINE_TWEET_ID, 0);
    }

    private static void setNewestTweetInTimeLineFetchedId(Context context, long id) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(NEWEST_TIMELINE_TWEET_ID, id).apply();
    }

    private static long getOldestTweetInTimeLineFetchedId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(OLDEST_TIMELINE_TWEET_ID, 0);
    }

    private static void setOldestTweetInTimeLineFetchedId(Context context, long id) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(OLDEST_TIMELINE_TWEET_ID, id).apply();
    }

    private static long getNewestTweetInUserMentionFetchedId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(NEWEST_USER_MENTIONED_TWEET_ID, 0);
    }

    private static void setNewestTweetInUserMentionFetchedId(Context context, long id) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(NEWEST_USER_MENTIONED_TWEET_ID, id).apply();
    }

    private static long getOldestTweetInUserMentionFetchedId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(OLDEST_USER_MENTIONED_TWEET_ID, 0);
    }

    private static void setOldestTweetInUserMentionFetchedId(Context context, long id) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(OLDEST_USER_MENTIONED_TWEET_ID, id).apply();
    }

//    /**
//     * Persist all contents of the tweet
//     */
//    private void persistTweets (List<Tweet> tweets) {
//        try {
//            ActiveAndroid.beginTransaction();
//            for (Tweet tweet : tweets) {
//
//                User user = tweet.getUser();
//
//                if (user != null){
//                    tweet.setUser(user.saveIfNecessaryElseGetUser());
//                }
//
//                tweet.save();
//                tweet.persistMedia();
//
//                if (tweet.getUser()!= null){
//                    Log.d("DEBUG", tweet.getUser().getFullName());
//                }
//            }
//            ActiveAndroid.setTransactionSuccessful();
//        } finally {
//            ActiveAndroid.endTransaction();
//        }
//    }

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

    private int getFetchCount(Context context) {

        if (NetworkConnectivityManager.isConnectedToInternetViaWifi(context)) {
            return 20;
        } else {
            return 10;
        }
    }
}
