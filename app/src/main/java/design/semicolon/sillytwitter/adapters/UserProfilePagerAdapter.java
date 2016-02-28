package design.semicolon.sillytwitter.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import design.semicolon.sillytwitter.fragments.UserLikesFragment;
import design.semicolon.sillytwitter.fragments.UserPhotosFragment;
import design.semicolon.sillytwitter.fragments.UserTweetsFragment;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;

/**
 * Created by dsaha on 2/27/16.
 */
public class UserProfilePagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Tweets", "Photos", "Likes"};
    private TweetViewHolderEventListener mTweetViewHolderEventListener;

    public UserProfilePagerAdapter(FragmentManager fm, TweetViewHolderEventListener tweetViewHolderEventListener) {
        super(fm);
        this.mTweetViewHolderEventListener = tweetViewHolderEventListener;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return UserTweetsFragment.newInstance(tabTitles[position], this.mTweetViewHolderEventListener);
        } else if (position == 1) {
            return UserPhotosFragment.newInstance(tabTitles[position], this.mTweetViewHolderEventListener);
        }

        return UserLikesFragment.newInstance(tabTitles[position], this.mTweetViewHolderEventListener);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}