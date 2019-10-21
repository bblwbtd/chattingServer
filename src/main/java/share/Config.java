package share;

import io.vertx.core.json.JsonObject;

public class Config {
    //configuration about server
    public static final String ServerHost = "0.0.0.0";
    public static final int ServerPort = 8888;

    //configuration about database
    private static final String DataBaseHost = "39.106.91.186";
    private static final int DatabasePort = 5432;
    private static final String DatabaseUser = "postgres";
    private static final String DatabasePassword = "1104193296";
    private static final int PoolSize = 10;
    private static final String DatabaseName = "chat";

    public static JsonObject getDatabaseConfig(){
        JsonObject object = new JsonObject();
        object.put("host", DataBaseHost);
        object.put("port", DatabasePort);
        object.put("username", DatabaseUser);
        object.put("password", DatabasePassword);
        object.put("maxPoolSize", PoolSize);
        object.put("sslMode", "disable");
        object.put("database", DatabaseName);
        return object;
    }


}
