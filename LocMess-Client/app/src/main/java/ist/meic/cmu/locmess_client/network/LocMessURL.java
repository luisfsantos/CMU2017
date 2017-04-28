package ist.meic.cmu.locmess_client.network;

/**
 * Created by Catarina on 24/04/2017.
 */

public final class LocMessURL {
    // 10.0.2.2 is the IP for the emulator to talk to localhost
    private static final String LOCALHOST = "10.0.2.2";
    private static final String PORT = "8080";
    private static final String BASE_URL = "http://" + LOCALHOST + ":" + PORT;
    public static final String SIGNUP =  BASE_URL + "/api/user/create";
    public static final String LOGIN = BASE_URL + "/api/user/login";
    public static final String NEW_LOCATION = BASE_URL + "/api/location/create";
    public static final String UPDATE_LOCATION = BASE_URL + "/api/messages/poll"; //FIXME replace with actual url
}
