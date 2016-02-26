package design.semicolon.sillytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.adapters.TweetsAdapter;
import design.semicolon.sillytwitter.dao.TweetDao;
import design.semicolon.sillytwitter.dao.TweetDaoImpl;
import design.semicolon.sillytwitter.dao.UserDaoImpl;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
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

    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        ButterKnife.bind(this, view);

        // Initialize the adapter
        mTweetAdapter = new TweetsAdapter(getActivity());

        // Set the layout manager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mTimelineRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Set adapter
        mTimelineRecyclerView.setAdapter(mTweetAdapter);

        // Listener listening to data load
        mOnTweetsLoadedListener = new OnTweetsLoadedListener() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets, boolean cached) {
                loading = false;
                swipeContainer.setRefreshing(false);

                if (tweets != null) {
                    mTweetAdapter.addTweets(tweets);
                    mTweetAdapter.notifyDataSetChanged();
                }
            }
        };

        if (mTweetDaoImpl == null) {
            mTweetDaoImpl = new TweetDaoImpl();
        }

        if (mUserDaoImpl == null) {
            mUserDaoImpl = new  UserDaoImpl();
        }

        /**
         * Load data on first load
         */
        try {
            mTweetDaoImpl.fetchTimelineTweets(getActivity(), 1, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

        try {
            /* Silent ly load data */
            mUserDaoImpl.reloadUser(getActivity(), null);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

        mTimelineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                if (dy > 0) {

                    if (!loading && ((visibleItemCount + firstVisibleItem) * 100 / totalItemCount) > 90) {
                        loading = true;

                        Tweet tweet = mTweetAdapter.getLastTweet();

                        if (tweet != null) {
                            long max_id = tweet.getUid();

                            try {
                                mTweetDaoImpl.fetchTimelineTweets(getActivity(), 0, max_id, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
                            } catch (NoNetworkConnectionException e) {
                                Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

        /*
        mTimelineRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                // IF scrolling down
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                Log.d("DEBUG", "visibleItemCount :"+visibleItemCount);
                Log.d("DEBUG", "firstVisibleItem :"+firstVisibleItem);
                Log.d("DEBUG", "firstCompletelyVisibleItem :"+ mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());

                Log.d("DEBUG", "dx :"+ dx + "dy :" + dy);

                if (dy > 0) {

                    if (!loading && ((visibleItemCount + firstVisibleItem)*100/totalItemCount) > 90) {
                        loading = true;

                        Tweet tweet = mTweetAdapter.getLastTweet();

                        if (tweet != null) {
                            long max_id = tweet.getUid();

                            try {
                                mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 0, max_id, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
                            } catch (NoNetworkConnectionException e) {
                                Toast.makeText(TimelineActivity.this, e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });
*/
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                try {
                    swipeContainer.setRefreshing(true);
                    mTweetDaoImpl.fetchTimelineTweets(getActivity(), 1, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
                } catch (NoNetworkConnectionException e) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
                }
            }
        });



        return view;
    }

    public static Fragment newInstance(String tabTitle) {
        TimelineFragment fragment = new TimelineFragment();
        return fragment;
    }

    private static String getFragmentKey(String tabTitle) {
        return tabTitle;
    }
}
