package design.semicolon.sillytwitter.helpers;

import android.text.format.DateUtils;
import android.util.Log;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by dsaha on 2/20/16.
 */
public class DateHelper {

    private static final long SECONDS_IN_A_MINUTE = 60;
    private static final long SECONDS_IN_AN_HOUR = SECONDS_IN_A_MINUTE * 60;
    private static final long SECONDS_IN_A_DAY = SECONDS_IN_AN_HOUR * 24;
    private static final long SECONDS_IN_A_WEEK = SECONDS_IN_A_DAY * 7;

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateInSeconds = (sf.parse(rawJsonDate).getTime())/1000;
            relativeDate = getRelativeTimeAgoShort(dateInSeconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static Date convertToDate(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            return new Date(sf.parse(rawJsonDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getRelativeTimeAgoShort(long timeStamp) {
        long currentTimeStamp = System.currentTimeMillis()/1000;
        long numberOfSeconds = (currentTimeStamp - timeStamp);

        String relativeString = new String();

        if (numberOfSeconds < SECONDS_IN_A_MINUTE) {
            relativeString = numberOfSeconds +"s";
        } else if (numberOfSeconds > SECONDS_IN_A_MINUTE && numberOfSeconds < SECONDS_IN_AN_HOUR) {
            relativeString = (numberOfSeconds/SECONDS_IN_A_MINUTE) +"m";
        } else if (numberOfSeconds > SECONDS_IN_AN_HOUR && numberOfSeconds < SECONDS_IN_A_DAY) {
            relativeString = (numberOfSeconds/SECONDS_IN_AN_HOUR) +"h";
        } else if (numberOfSeconds > SECONDS_IN_A_DAY && numberOfSeconds < SECONDS_IN_A_WEEK) {
            relativeString = (numberOfSeconds/SECONDS_IN_A_DAY) +"d";
        } else if (numberOfSeconds > SECONDS_IN_A_WEEK) {
            relativeString = (numberOfSeconds/SECONDS_IN_A_WEEK) +"w";
        }

        return relativeString;
    }
}
