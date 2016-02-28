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
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
import design.semicolon.sillytwitter.listerners.OnUsersLoadedListener;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

public class UserLikesFragment extends Fragment {

//    @Bind(R.id.user_likes_recyclerview)
//    RecyclerView mUserLikesRecyclerView;

    private OnTweetsLoadedListener mOnTweetsLoadedListener;
    private TweetDaoImpl mUserLikedTweetDaoImpl;
    private UserDaoImpl mUserDaoImpl;
    private TweetsAdapter mUserLikedTweetsAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private User mCurrentUser;

    public void setTweetViewHolderEventListener(TweetViewHolderEventListener mTweetViewHolderEventListener) {
        this.mTweetViewHolderEventListener = mTweetViewHolderEventListener;
    }

    private TweetViewHolderEventListener mTweetViewHolderEventListener;

    public static UserLikesFragment newInstance(String tabTitle, TweetViewHolderEventListener mTweetViewHolderEventListener) {
        UserLikesFragment fragment = new UserLikesFragment();
        fragment.setTweetViewHolderEventListener(mTweetViewHolderEventListener);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_likes, container, false);
        ButterKnife.bind(this, view);

        // Initialize the adapter
        mUserLikedTweetsAdapter = new TweetsAdapter(getContext(), this.mTweetViewHolderEventListener);

        // Set the layout manager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        // mUserLikesRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Set adapter
        // mUserLikesRecyclerView.setAdapter(mUserLikedTweetsAdapter);

        // Listener listening to data load
        mOnTweetsLoadedListener = new OnTweetsLoadedListener() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets, boolean cached) {
                Log.d("UserLikesFragment", "User liked Tweets count: "+tweets.size());
                if (tweets != null) {
                    mUserLikedTweetsAdapter.addTweets(tweets);
                    mUserLikedTweetsAdapter.notifyDataSetChanged();
                }
            }
        };

        if (mUserLikedTweetDaoImpl == null) {
            mUserLikedTweetDaoImpl = new TweetDaoImpl(getContext());
        }

        if (mUserDaoImpl == null) {
            mUserDaoImpl = new UserDaoImpl();
        }

        mCurrentUser = User.currentUser(getContext());

        // Check if the user is logged in
        if (mCurrentUser != null) {
            // Logged in, fetch mentions
            getUserLikes();
        } else {
            // Fetch the user, then get mentions
            try {
                /* Silently load user */
                mUserDaoImpl.reloadUser(getActivity(), new OnUsersLoadedListener() {
                    @Override
                    public void onCurrentUserLoaded(Context context, OnUsersLoadedListener onUsersLoadedListener) {
                        getUserLikes();
                    }
                });
            } catch (NoNetworkConnectionException e) {
                Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
            }
        }

        /*
        try {
            mUserLikedTweetDaoImpl.fetchUserLikedTweets(getActivity(), 1, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

        try {
            mUserDaoImpl.reloadUser(getActivity(), null);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }


        mUserLikesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
*/
        return view;
    }

    public void getUserLikes() {
        try {
            mUserLikedTweetDaoImpl.fetchUserLikedTweets(getActivity(), 1, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(getActivity(), e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

    }
}
