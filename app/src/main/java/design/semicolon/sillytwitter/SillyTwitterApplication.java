package design.semicolon.sillytwitter;

import android.content.Context;

import design.semicolon.sillytwitter.restclient.SillyTwitterClient;

public class SillyTwitterApplication extends com.activeandroid.app.Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        SillyTwitterApplication.context = this;
    }

    public static SillyTwitterClient getRestClient() {
        return (SillyTwitterClient) SillyTwitterClient.getInstance(SillyTwitterClient.class, SillyTwitterApplication.context);
    }
}