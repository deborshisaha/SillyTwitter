package design.semicolon.sillytwitter.fragments;

import android.content.Context;
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
import design.semicolon.sillytwitter.listerners.EndlessRecyclerViewScrollListener;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
import design.semicolon.sillytwitter.listerners.OnUsersLoadedListener;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

public class UserLikesFragment extends Fragment {

    @Bind(R.id.user_likes_recyclerview)
    RecyclerView mUserLikesRecyclerView;

    private TweetViewHolderEventListener mTweetViewHolderEventListener;
    private OnTweetsLoadedListener mOnTweetsLoadedListener;
    private TweetDaoImpl mTweetDaoImpl;
    private TweetsAdapter mTweetAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private User mUser;
    private boolean loading = true;

    public void setTweetViewHolderEventListener(TweetViewHolderEventListener mTweetViewHolderEventListener) {
        this.mTweetViewHolderEventListener = mTweetViewHolderEventListener;
    }

    public static UserLikesFragment newInstance(String tabTitle, User user, TweetViewHolderEventListener mTweetViewHolderEventListener) {
        UserLikesFragment fragment = new UserLikesFragment();
        fragment.mUser = user;
        fragment.setTweetViewHolderEventListener(mTweetViewHolderEventListener);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_likes, container, false);
        ButterKnife.bind(this, view);

        // Initialize the adapter
        mTweetAdapter = new TweetsAdapter(getContext(), this.mTweetViewHolderEventListener);

        // Set the layout manager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mUserLikesRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Set adapter
        mUserLikesRecyclerView.setAdapter(mTweetAdapter);

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

                    mTweetAdapter.notifyItemRangeInserted(curSize, mTweetAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onTweetsLoadFailure(List<Tweet> tweets, boolean cached) {

            }
        };

        // Initialize
        if (mTweetDaoImpl == null) {
            mTweetDaoImpl = new TweetDaoImpl(getContext());
        }

        if (this.mUser != null ) {
            try {
                long since_id = 0;
                mTweetDaoImpl.fetchUserLikedTweets(getContext(), mUser, since_id, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheThenNetwork);
            } catch (NoNetworkConnectionException e) {
                Toast.makeText(getContext(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
            }

            mUserLikesRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {

                    Tweet tweetLast = mTweetAdapter.getLastTweet();

                    if (tweetLast != null) {
                        long maxIdInTheAdapter = tweetLast.getUid();
                        try {
                            mTweetDaoImpl.fetchUserLikedTweets(getContext(),  mUser, 0, maxIdInTheAdapter - 1, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheThenNetwork);
                        } catch (NoNetworkConnectionException e) {
                            Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }

        return view;
    }

    public void getUserLikes() {
        try {
            mTweetDaoImpl.fetchUserLikedTweets(getActivity(), 1, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

    }
}
