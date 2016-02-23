package design.semicolon.sillytwitter.dao;

import android.content.Context;

import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.listerners.OnUsersLoadedListener;

/**
 * Created by dsaha on 2/21/16.
 */
public interface UserDao {

    public void reloadUser(final Context context, OnUsersLoadedListener onUsersLoadedListener) throws NoNetworkConnectionException;
}
