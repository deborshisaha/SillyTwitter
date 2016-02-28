package design.semicolon.sillytwitter.listerners;

import design.semicolon.sillytwitter.models.Tweet;

/**
 * Created by dsaha on 2/28/16.
 */
public interface TweetViewHolderEventListener {

    public Tweet didPressReplyOnTweetWithId(Tweet tweet);
    public Tweet didPressLikeOnTweetWithId(Tweet tweet);
    public Tweet didPressRetweetOnTweetWithId(Tweet tweet);

    public void didPressProfilePicture(Tweet tweet);
    public void didSelectTweet(Tweet tweet);
}
