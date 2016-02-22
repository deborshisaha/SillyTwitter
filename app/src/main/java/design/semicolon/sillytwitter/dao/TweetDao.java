package design.semicolon.sillytwitter.dao;

import android.content.Context;

import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;

/**
 * Created by dsaha on 2/20/16.
 */
public interface TweetDao {
    public void fetchTimelineTweets( Context context,int page, OnTweetsLoadedListener onTweetsLoadedListener, int cachingStrategy) throws NoNetworkConnectionException;

    public class CachingStrategy {
        public static final int NetworkOnly = 1;
        public static final int CacheOnly = NetworkOnly << 1;
        public static final int CacheThenNetwork = NetworkOnly << 2;
    }
}
