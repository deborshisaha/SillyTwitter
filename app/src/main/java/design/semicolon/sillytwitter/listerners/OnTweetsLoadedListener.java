package design.semicolon.sillytwitter.listerners;

import java.util.List;

import design.semicolon.sillytwitter.models.Tweet;

/**
 * Created by dsaha on 2/20/16.
 */
public interface OnTweetsLoadedListener {

    public void onTweetsLoadFailure(List<Tweet> tweets, boolean cached);

    public void moreRecentTweetsLoaded(List<Tweet> tweets, boolean cached);
    public void olderTweetsLoaded(List<Tweet> tweets, boolean cached);

}
