package design.semicolon.sillytwitter.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import design.semicolon.sillytwitter.activity.HomeActivity;
import design.semicolon.sillytwitter.fragments.MentionsFragment;
import design.semicolon.sillytwitter.fragments.TimelineFragment;
import design.semicolon.sillytwitter.fragments.UserLikesFragment;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;

public class HomePagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Home", "Mentions"};
    private TweetViewHolderEventListener mTweetViewHolderEventListener;

    public HomePagerAdapter(FragmentManager fm,  TweetViewHolderEventListener tweetViewHolderEventListener) {
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
            return TimelineFragment.newInstance(tabTitles[position], this.mTweetViewHolderEventListener);
        }

        return MentionsFragment.newInstance(tabTitles[position], this.mTweetViewHolderEventListener);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}