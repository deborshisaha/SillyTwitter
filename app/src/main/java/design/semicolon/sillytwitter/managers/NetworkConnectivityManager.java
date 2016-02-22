package design.semicolon.sillytwitter.managers;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
/**
 * Created by dsaha on 2/20/16.
 */
public class NetworkConnectivityManager {

    public static boolean isConnectedToInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnectedToInternet = (activeNetworkInfo != null && activeNetworkInfo.isConnected());

        return isConnectedToInternet;
    }

    public static boolean isConnectedToInternetViaWifi (Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }

        return false;
    }
}
