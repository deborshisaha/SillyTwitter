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

/**
 * Created by dsaha on 2/27/16.
 */
public class UserPhotosFragment  extends Fragment {

    @Bind(R.id.user_photos_recyclerview)
    RecyclerView mUserPhotosRecyclerView;

    public void setTweetViewHolderEventListener(TweetViewHolderEventListener mTweetViewHolderEventListener) {
        this.mTweetViewHolderEventListener = mTweetViewHolderEventListener;
    }

    private TweetViewHolderEventListener mTweetViewHolderEventListener;

    public static UserLikesFragment newInstance(String tabTitle, TweetViewHolderEventListener mTweetViewHolderEventListener) {
        UserLikesFragment fragment = new UserLikesFragment();
        fragment.setTweetViewHolderEventListener(mTweetViewHolderEventListener);
        return fragment;
    }

    public static UserPhotosFragment newInstance(String tabTitle) {
        UserPhotosFragment fragment = new UserPhotosFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_photos, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
