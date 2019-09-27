package router;

import bean.Interface.SendMessage;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import model.ChatDao;
import share.Share;
import utils.ErrorResponse;
import utils.ResponseUtils;
import utils.TrashBin;

public class ChatRouter {
    public static Router router = Router.router(Share.vertx);
    static {
        Session.mountRouter(router);
        router.route().handler(Session::checkPermission);

        router.post("/message/send").handler(ChatRouter::sendMessage);
        router.get("/message/all/:user").handler(ChatRouter::fetchAllMessage);
        router.get("/message/after/:user/:id").handler(ChatRouter::fetchMessageAfterId);
    }

    private static void fetchAllMessage(RoutingContext context) {
        String username = context.session().get("username");
        String target = context.request().getParam("user");
        TrashBin.drop(
            ChatDao.fetchAllMessage(username, target)
                .doOnError(throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
                .subscribe(array -> ResponseUtils.responseSuccess(context, array), Throwable::printStackTrace)
        );
    }

    private static void sendMessage(RoutingContext context) {
        String username = context.session().get("username");
        TrashBin.drop(
            Single.just(context)
                .map(RoutingContext::getBodyAsJson)
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

    private static void fetchMessageAfterId(RoutingContext context){
        String username = context.session().get("username");
        String target = context.request().getParam("user");
        TrashBin.drop(
            Single.just(context)
                .map(context1 -> context1.request()
                .getParam("id"))
                .map(Integer::valueOf)
                .doOnError(throwable -> ErrorResponse.BAD_REQUEST.responseFailure(context))
                .flatMap(id -> ChatDao.fetchMessageAfterId(username, target,id))
                .doOnError(throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
                .subscribe(array -> ResponseUtils.responseSuccess(context, array), Throwable::printStackTrace)
        );
    }
}
