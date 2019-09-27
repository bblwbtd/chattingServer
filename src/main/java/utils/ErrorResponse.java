package utils;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public enum ErrorResponse {
    PERMISSION_DENY(-1, "permission deny"),
    BAD_JSON(-2, "bad json"),
    PASSWORD_INCORRECT(-3, "password incorrect"),
    NO_SUCH_USER(-4, "no such user"),
    DATABASE_ERROR(-5, "database error"),
    USER_EXISTED(-6, "user existed"),
    DUPLICATE_REQUEST(-7, "duplicate request"),
    BAD_REQUEST(-8, "bad request")
    ;

    private int status;
    private String msg;
    ErrorResponse(int status, String msg){
        this.msg = msg;
        this.status = status;
    }


    public JsonObject toJson(){
        JsonObject object = new JsonObject();
        object.put("status", status);
        JsonObject data = new JsonObject();
        data.put("msg", msg);
        object.put("data", data);
        return object;
    }


    public static void responseFailure(RoutingContext context, ErrorResponse error){
        ResponseUtils.responseJson(context, error.toJson());
    }


    public void responseFailure(RoutingContext context){
        ResponseUtils.responseJson(context, toJson());
    }

}
