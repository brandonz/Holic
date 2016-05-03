package cos333.project_corgis.chat.app;

public class EndPoints {

    // localhost url
    // public static final String BASE_URL = "http://192.168.0.101/gcm_chat/v1";

//    public static final String BASE_URL = "http://172.20.10.2/gcm_chat/v1";
//    public static final String BASE_URL = "http://demo.androidhive.info/gcm_chat/v1";
    public static final String BASE_URL = "https://holic-server.herokuapp.com/api";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
}
