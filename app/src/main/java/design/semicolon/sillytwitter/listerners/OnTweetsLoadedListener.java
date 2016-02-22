package design.semicolon.sillytwitter.listerners;

import java.util.List;

import design.semicolon.sillytwitter.models.Tweet;

/**
 * Created by dsaha on 2/20/16.
 */
public interface OnTweetsLoadedListener {
    public void onTweetsLoaded(List<Tweet> tweets, boolean cached);
}
