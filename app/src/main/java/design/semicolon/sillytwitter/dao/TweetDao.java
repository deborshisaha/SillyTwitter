package design.semicolon.sillytwitter.dao;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

/**
 * Created by dsaha on 2/20/16.
 */
public interface TweetDao {

    //
    public  void fetchHomeTimelineTweets(Context context, final long since_id, final long max_id, final OnTweetsLoadedListener onTweetsLoadedListener, CachingStrategy cachingStrategy) throws NoNetworkConnectionException;
    public  void fetchMentions(Context context, String screenName, final long since_id, final long max_id, final OnTweetsLoadedListener onTweetsLoadedListener, CachingStrategy cachingStrategy) throws NoNetworkConnectionException;
    public  void fetchUserTimeLineTweets(Context context, User user, final long since_id, final long max_id, final OnTweetsLoadedListener onTweetsLoadedListener, CachingStrategy cachingStrategy) throws NoNetworkConnectionException;
    public  void fetchUserLikedTweets(Context context, User user, final long since_id, final long max_id, final OnTweetsLoadedListener onTweetsLoadedListener, CachingStrategy cachingStrategy) throws NoNetworkConnectionException;

    ////
    public void fetchTimelineTweets( Context context, long since_id, long max_id, OnTweetsLoadedListener onTweetsLoadedListener, CachingStrategy cachingStrategy) throws NoNetworkConnectionException;
    public void fetchMentions( Context context, OnTweetsLoadedListener mOnTweetsLoadedListener, CachingStrategy cacheOnly) throws NoNetworkConnectionException;
    public void fetchUserLikedTweets(Context context, int since_id, int max_id, OnTweetsLoadedListener mOnTweetsLoadedListener, CachingStrategy cacheOnly) throws NoNetworkConnectionException;
    public void fetchUserMedia(Context context, int since_id, int max_id, OnTweetsLoadedListener mOnTweetsLoadedListener, CachingStrategy cacheOnly) throws NoNetworkConnectionException;
    public void fetchUserTweets(Context context, int since_id, int max_id, OnTweetsLoadedListener mOnTweetsLoadedListener, CachingStrategy cacheOnly) throws NoNetworkConnectionException;

    public void postTweetLike(Tweet tweet) throws NoNetworkConnectionException;
    public void postTweetUnliked(Tweet tweet) throws NoNetworkConnectionException ;

    public void postRetweet(Tweet tweet)  throws NoNetworkConnectionException;
    public void postUndoRetweet(Tweet tweet) throws NoNetworkConnectionException;

    public enum CachingStrategy {
        NetworkOnly,CacheOnly, CacheThenNetwork;
    }

    public enum FetchStyle {
        FetchStyleLoadMore, loadMore, FetchStyleLoadPrevious
    }
}
