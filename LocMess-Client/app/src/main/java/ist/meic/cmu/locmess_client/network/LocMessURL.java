package ist.meic.cmu.locmess_client.network;

/**
 * Created by Catarina on 24/04/2017.
 */

public final class LocMessURL {
    private LocMessURL() {}
    // 10.0.2.2 is the IP for the emulator to talk to localhost
    private static final String LOCALHOST = "10.0.2.2";
    private static final String PORT = "8000";
    private static final String BASE_URL = "http://" + LOCALHOST + ":" + PORT;
    public static final String ID = "{id}";
    public static final String SIGNUP =  BASE_URL + "/api/user/create";
    public static final String LOGIN = BASE_URL + "/api/user/login";
    public static final String NEW_LOCATION = BASE_URL + "/api/location/create";
    public static final String LIST_LOCATIONS = BASE_URL + "/api/location/list";
    public static final String DELETE_LOCATION = BASE_URL + "/api/location/" + ID + "/delete";
    public static final String NEW_KEYPAIR = BASE_URL + "/api/user/info/create";
    public static final String DELETE_KEYPAIR = BASE_URL + "/api/user/info/" + ID + "/delete";
    public static final String NEW_MESSAGE = BASE_URL + "/api/message/create";
    public static final String UPDATE_LOCATION = BASE_URL + "/api/messages/poll";
}
