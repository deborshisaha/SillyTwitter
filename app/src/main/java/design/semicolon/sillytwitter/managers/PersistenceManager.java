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

    public static boolean fetchCachedTimelineTweets( int cachingStrategy, OnTweetsLoadedListener onTweetsLoadedListener) {

        if (cachingStrategy == TweetDao.CachingStrategy.CacheOnly || cachingStrategy == TweetDao.CachingStrategy.CacheThenNetwork) {
            onTweetsLoadedListener.onTweetsLoaded(Tweet.all(), true);
        }

        return (cachingStrategy == TweetDao.CachingStrategy.CacheOnly);
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

    public static boolean fetchUserMentionedTweets(int cachingStrategy, OnTweetsLoadedListener onTweetsLoadedListener) {

        if (cachingStrategy == TweetDao.CachingStrategy.CacheOnly || cachingStrategy == TweetDao.CachingStrategy.CacheThenNetwork) {
            onTweetsLoadedListener.onTweetsLoaded(Tweet.getTweetsUserWasMentioned(), true);
        }

        return (cachingStrategy == TweetDao.CachingStrategy.CacheOnly);
    }
}
