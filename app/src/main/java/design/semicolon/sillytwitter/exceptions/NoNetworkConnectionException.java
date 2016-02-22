package design.semicolon.sillytwitter.exceptions;

/**
 * Created by dsaha on 2/20/16.
 */
public class NoNetworkConnectionException extends Throwable {

    private String reason;
    private String remedy;

    public String getReason() {
        return reason;
    }

    public String getRemedy() {
        return remedy;
    }

    public NoNetworkConnectionException() {
        super();
        this.reason = "Not connected to internet!";
        this.remedy = "Connect to internet and then try again";
    }

}
