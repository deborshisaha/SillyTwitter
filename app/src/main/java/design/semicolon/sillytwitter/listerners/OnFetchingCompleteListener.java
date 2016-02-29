package design.semicolon.sillytwitter.listerners;

import java.util.List;

import design.semicolon.sillytwitter.models.TwitterMedia;

/**
 * Created by dsaha on 2/28/16.
 */
public interface OnFetchingCompleteListener {
    public void mediaFetched(List<TwitterMedia> media);
}
