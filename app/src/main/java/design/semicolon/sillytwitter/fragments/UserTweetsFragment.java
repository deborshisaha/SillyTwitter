package design.semicolon.sillytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;

public class UserTweetsFragment  extends Fragment {

    @Bind(R.id.user_tweets_recyclerview)
    RecyclerView mUserTweetsRecyclerView;

    public void setTweetViewHolderEventListener(TweetViewHolderEventListener mTweetViewHolderEventListener) {
        this.mTweetViewHolderEventListener = mTweetViewHolderEventListener;
    }

    private TweetViewHolderEventListener mTweetViewHolderEventListener;

    public static UserLikesFragment newInstance(String tabTitle, TweetViewHolderEventListener mTweetViewHolderEventListener) {
        UserLikesFragment fragment = new UserLikesFragment();
        fragment.setTweetViewHolderEventListener(mTweetViewHolderEventListener);
        return fragment;
    }

    public static UserTweetsFragment newInstance(String tabTitle) {
        UserTweetsFragment fragment = new UserTweetsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_tweets, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
