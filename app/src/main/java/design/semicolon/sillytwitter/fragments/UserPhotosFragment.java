package design.semicolon.sillytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.adapters.MediaAdapter;
import design.semicolon.sillytwitter.adapters.TweetsAdapter;
import design.semicolon.sillytwitter.listerners.OnFetchingCompleteListener;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.TwitterMedia;
import design.semicolon.sillytwitter.models.User;

/**
 * Created by dsaha on 2/27/16.
 */
public class UserPhotosFragment  extends Fragment {

    @Bind(R.id.user_photos_recyclerview)
    RecyclerView mUserPhotosRecyclerView;

    private MediaAdapter mMediaAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private User mUser;

    public void setTweetViewHolderEventListener(TweetViewHolderEventListener mTweetViewHolderEventListener) {
        this.mTweetViewHolderEventListener = mTweetViewHolderEventListener;
    }

    private TweetViewHolderEventListener mTweetViewHolderEventListener;

    public static UserPhotosFragment newInstance(String tabTitle, User user, TweetViewHolderEventListener mTweetViewHolderEventListener) {
        UserPhotosFragment fragment = new UserPhotosFragment();
        fragment.setTweetViewHolderEventListener(mTweetViewHolderEventListener);
        fragment.mUser = user;
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

        this.mUser.fetchMediaInBackground(new OnFetchingCompleteListener() {
            @Override
            public void mediaFetched(List<TwitterMedia> media) {
                Log.d("DEBUG", "Current User media:" + media.size());
                mMediaAdapter.appendMedia(media);
                mMediaAdapter.notifyDataSetChanged();
            }
        });


        // Initialize the adapter
        mMediaAdapter = new MediaAdapter(getContext(), this.mTweetViewHolderEventListener);

        // Set the layout manager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mUserPhotosRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Set adapter
        mUserPhotosRecyclerView.setAdapter(mMediaAdapter);


        return view;
    }
}
