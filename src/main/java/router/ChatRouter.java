package router;

import bean.Interface.SendMessage;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.parsetools.JsonParser;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import model.ChatDao;
import share.Share;
import utils.EncryptionUtils;
import utils.ErrorResponse;
import utils.ResponseUtils;
import utils.TrashBin;

import java.util.Base64;
import java.util.Objects;

public class ChatRouter {
    static Router router = Router.router(Share.vertx);

    static {
        Session.mountRouter(router);
        router.route().handler(Session::checkPermission);

        router.post("/message/send").handler(ChatRouter::sendMessage);
        router.get("/message/all/:user").handler(ChatRouter::fetchAllMessage);

    }

    private static void fetchAllMessage(RoutingContext context) {
        String username = context.session().get("username");
        String target = context.request().getParam("user");
        String key = context.session().get("key");
        TrashBin.drop(
            ChatDao.fetchAllMessage(username, target)
                .doOnError(throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
                .map( array -> {
                    JsonArray newArray = new JsonArray();
                    array.forEach(o -> {
                        JsonObject object = JsonObject.mapFrom(o);
                        object.put("content","key:" + key + " content:" + object.getString("content"));
                        newArray.add(object);
                    });
                    byte[] data = ResponseUtils.warpSuccess(newArray).toString().getBytes();
                    byte[] encryptedData = EncryptionUtils.DES_CBC_Encrypt(data, key.getBytes());
                    String base64Data = Base64.getEncoder().encodeToString(encryptedData);
                    return base64Data;
                })
                .subscribe(str -> {
                    ResponseUtils.responseJson(context, ResponseUtils.warpSuccess(str));
                }, Throwable::printStackTrace)
        );
    }

    private static void sendMessage(RoutingContext context) {
        String username = context.session().get("username");
        String key = context.session().get("key");
        TrashBin.drop(
            Single.just(context)
                .map(RoutingContext::getBodyAsString)
                .map(shit -> {
                    shit = shit.substring(1,shit.length() - 1);
                    byte[] encryptedContent = Base64.getDecoder().decode(shit);
                    byte[] decryptedContent = EncryptionUtils.DES_CBC_Decrypt(encryptedContent, key.getBytes());
                    String text = new String(decryptedContent);
                    return new JsonObject(text);
                })
                .doOnError(throwable -> {
                    ErrorResponse.WHAT_THE_HELL.responseFailure(context);
                    throwable.printStackTrace();
                })
                .map(o -> o.mapTo(SendMessage.class))
                .flatMap(o -> ChatDao.sendMessage(username, o.to, o.content))
                .doOnError(throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
                .subscribe(updateResult -> {
                    if (updateResult.getUpdated() > 0){
                        ResponseUtils.responseSuccess(context);
                    }else {
                        ErrorResponse.DATABASE_ERROR.responseFailure(context);
                    }
                })
        );
    }

}
