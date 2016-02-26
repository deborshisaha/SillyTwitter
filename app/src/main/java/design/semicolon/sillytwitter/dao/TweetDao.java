package design.semicolon.sillytwitter.dao;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;

/**
 * Created by dsaha on 2/20/16.
 */
public interface TweetDao {
    public void fetchTimelineTweets( Context context, long since_id, long max_id, OnTweetsLoadedListener onTweetsLoadedListener, int cachingStrategy) throws NoNetworkConnectionException;
    public void fetchMentions( Context context, OnTweetsLoadedListener mOnTweetsLoadedListener, int cacheOnly) throws NoNetworkConnectionException;

    public class CachingStrategy {
        public static final int NetworkOnly = 1;
        public static final int CacheOnly = NetworkOnly << 1;
        public static final int CacheThenNetwork = NetworkOnly << 2;
    }

    public class FetchStyle {
        public static final int FetchStyleLoadMore = 1;
        public static final int FetchStyleLoadPrevious = FetchStyleLoadMore << 1;
        public static final int FetchStyleRange = FetchStyleLoadMore << 2;
    }
}
