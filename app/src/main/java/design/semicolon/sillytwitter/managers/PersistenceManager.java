package design.semicolon.sillytwitter.managers;

import android.util.Log;

import com.activeandroid.ActiveAndroid;

import java.util.List;

import design.semicolon.sillytwitter.dao.TweetDao;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

/**
 * Created by dsaha on 2/20/16.
 */
public class PersistenceManager {

    public static void fetchCachedTimelineTweets( OnTweetsLoadedListener onTweetsLoadedListener) {
        List<Tweet> tweets = Tweet.all();
        onTweetsLoadedListener.olderTweetsLoaded(tweets, true);
    }

    public static void fetchCachedMentions( String screenName, OnTweetsLoadedListener onTweetsLoadedListener) {
        List<Tweet> tweets = Tweet.getTweetsUserWasMentioned();
        onTweetsLoadedListener.olderTweetsLoaded(tweets, true);
    }

    public static void fetchCachedUserTimeLineTweets(User user, OnTweetsLoadedListener onTweetsLoadedListener) {
        List<Tweet> tweets = user.allTweets();
        onTweetsLoadedListener.olderTweetsLoaded(tweets, true);
    }

    public static void fetchCachedUserLikedTweets(User user, OnTweetsLoadedListener onTweetsLoadedListener) {
        List<Tweet> tweets = user.favorites();
        onTweetsLoadedListener.olderTweetsLoaded(tweets, true);
    }
    /**
     * Persist all contents of the tweet
     */
    public static void persistTweets (List<Tweet> tweets) {
        try {
            ActiveAndroid.beginTransaction();
            for (Tweet tweet : tweets) {

                User user = tweet.getUser();

                if (user != null){
                    tweet.setUser(user.saveIfNecessaryElseGetUser());
                }

                tweet.save();
                tweet.persistMedia();

                if (tweet.getUser()!= null){
                    Log.d("DEBUG", tweet.getUser().getFullName());
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static boolean fetchUserMentionedTweets(TweetDao.CachingStrategy cachingStrategy, OnTweetsLoadedListener onTweetsLoadedListener) {

        if (cachingStrategy == TweetDao.CachingStrategy.CacheOnly || cachingStrategy == TweetDao.CachingStrategy.CacheThenNetwork) {
            //onTweetsLoadedListener.onTweetsLoadSuccess(Tweet.getTweetsUserWasMentioned(), true);
        }

        return (cachingStrategy == TweetDao.CachingStrategy.CacheOnly);
    }
}
