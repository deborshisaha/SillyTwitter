package design.semicolon.sillytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.activity.HomeActivity;
import design.semicolon.sillytwitter.adapters.TweetsAdapter;
import design.semicolon.sillytwitter.dao.TweetDao;
import design.semicolon.sillytwitter.dao.TweetDaoImpl;
import design.semicolon.sillytwitter.dao.UserDaoImpl;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.listerners.EndlessRecyclerViewScrollListener;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.managers.PersistenceManager;
import design.semicolon.sillytwitter.models.Tweet;

public class TimelineFragment extends Fragment {

    @Bind(R.id.timeline)
    RecyclerView mTimelineRecyclerView;

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    private OnTweetsLoadedListener mOnTweetsLoadedListener;
    private TweetDaoImpl mTweetDaoImpl;
    private UserDaoImpl mUserDaoImpl;
    private TweetsAdapter mTweetAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private TweetViewHolderEventListener mTweetViewHolderEventListener;

    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);

        // Initialize the adapter
        mTweetAdapter = new TweetsAdapter(getContext(), this.mTweetViewHolderEventListener);

        // Set the layout manager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mTimelineRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Set adapter
        mTimelineRecyclerView.setAdapter(mTweetAdapter);

        // Listener listening to data load
        mOnTweetsLoadedListener = new OnTweetsLoadedListener() {

            @Override
            public void moreRecentTweetsLoaded(List<Tweet> tweets, boolean cached) {

                loading = false;

                if (tweets != null) {
                    // get current adapter size
                    int curSize = mTweetAdapter.getItemCount();

                    // Add results at the front
                    int itemsAdded = mTweetAdapter.addToFrontOfTheList(tweets);

                    // save the most recent tweet
                    saveMostRecentTweetId();

                    mTweetAdapter.notifyItemRangeInserted(0, itemsAdded);
                }
            }

            @Override
            public void olderTweetsLoaded(List<Tweet> tweets, boolean cached) {

                loading = false;

                if (tweets != null) {

                    // get current adapter size
                    int curSize = mTweetAdapter.getItemCount();

                    // Add to adapters
                    mTweetAdapter.addTweets(tweets);

                    // save the most recent tweet
                    saveMostRecentTweetId();

                    Log.d("DEBUG", "curSize:" + curSize + " Cached:" + cached);
                    Log.d("DEBUG", "mTweetAdapter.getItemCount():"+mTweetAdapter.getItemCount() );
                    mTweetAdapter.notifyItemRangeInserted(curSize, mTweetAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onTweetsLoadFailure(List<Tweet> tweets, boolean cached) {

            }
        };

        if (mTweetDaoImpl == null) {
            mTweetDaoImpl = new TweetDaoImpl(getContext());
        }

        if (mUserDaoImpl == null) {
            mUserDaoImpl = new  UserDaoImpl();
        }

        /**
         * Load data on first load
         */
        try {
            long since_id = 0;
            mTweetDaoImpl.fetchHomeTimelineTweets(getContext(), since_id, 0, mOnTweetsLoadedListener,TweetDao.CachingStrategy.CacheThenNetwork);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

        try {
            /* Silently load data */
            mUserDaoImpl.reloadUser(getActivity(), null);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

        mTimelineRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                Tweet tweetLast = mTweetAdapter.getLastTweet();

                if (tweetLast != null) {
                    long maxIdInTheAdapter = tweetLast.getUid();

                    try {
                        mTweetDaoImpl.fetchHomeTimelineTweets(getContext(), 0, maxIdInTheAdapter - 1, mOnTweetsLoadedListener,TweetDao.CachingStrategy.CacheThenNetwork);
                    } catch (NoNetworkConnectionException e) {
                        Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }

    private void  saveMostRecentTweetId () {
        Tweet tweet = (Tweet) mTweetAdapter.getMostRecentTweet();
        if (tweet != null) {
            Log.d("TIMELINE_FRAGMENT", "Newest Tweet in timeline:"+tweet.getUid());
            TweetDaoImpl.setNewestTweetInTimeLineId(getContext(), tweet.getUid());
        }
    }

    public void setHomeActivityEventListener(TweetViewHolderEventListener tweetViewHolderEventListener) {
        this.mTweetViewHolderEventListener = tweetViewHolderEventListener;
    }

    public static TimelineFragment newInstance(String tabTitle, TweetViewHolderEventListener tweetViewHolderEventListener) {
        TimelineFragment fragment = new TimelineFragment();
        fragment.setHomeActivityEventListener(tweetViewHolderEventListener);
        return fragment;
    }

    private static String getFragmentKey(String tabTitle) {
        return tabTitle;
    }
}
