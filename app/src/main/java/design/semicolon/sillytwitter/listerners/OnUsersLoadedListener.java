package design.semicolon.sillytwitter.listerners;

import android.content.Context;

import java.util.List;

import design.semicolon.sillytwitter.models.User;


/**
 * Created by dsaha on 2/21/16.
 */
public interface OnUsersLoadedListener {
    public void onCurrentUserLoaded(Context context, OnUsersLoadedListener onUsersLoadedListener);
}
