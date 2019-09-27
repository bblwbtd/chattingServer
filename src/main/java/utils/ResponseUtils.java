package utils;


import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;


public class ResponseUtils {
    public static void responseJson(RoutingContext context, JsonObject object){
        HttpServerResponse response = context.response();
        response.putHeader("Content-Type", "application/json");
        context.response().rxEnd(object.encode()).subscribe();
    }


    public static JsonObject warpSuccess(Object object){
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("status", 0).put("data",object);
        return jsonObject;
    }


    public static JsonObject warpSuccess(String msg){
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("msg", msg);
        return warpSuccess(jsonObject);
    }

    public static void responseSuccess(RoutingContext context){
        responseJson(context, warpSuccess("ok"));
    }

    public static void responseSuccess(RoutingContext context, String msg){
        responseJson(context, warpSuccess(msg));
    }


    public static void responseSuccess(RoutingContext context, JsonObject msg){
        responseJson(context, warpSuccess(msg));
    }


    public static void responseSuccess(RoutingContext context, JsonArray msg){
        responseJson(context, warpSuccess(msg));
    }
    public static JsonObject warpFailure(int status, String message){
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("status", status);
        jsonObject.put("data", new JsonObject().put("msg", message));
        return jsonObject;
    }

}
