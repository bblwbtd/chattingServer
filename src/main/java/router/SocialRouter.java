package router;

import io.reactivex.Single;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import model.FriendsDao;
import share.Share;
import utils.ErrorResponse;
import utils.ResponseUtils;
import utils.TrashBin;

public class SocialRouter {
    public static Router router = Router.router(Share.vertx);
    static {
        Session.mountRouter(router);
        router.route().handler(Session::checkPermission);

        router.post("/request").handler(SocialRouter::sendRequest);
        router.post("/request/accept").handler(SocialRouter::acceptRequest);
        router.get("/request/all").handler(SocialRouter::fetchAllRequest);
        router.get("/friends/all").handler(SocialRouter::fetchAllFriends);
        router.post("/request/deny").handler(SocialRouter::denyRequest);
        router.post("/friends/delete").handler(SocialRouter::deleteFriend);
    }

    private static void deleteFriend(RoutingContext context) {
        String username = context.session().get("username");
        TrashBin.drop(
            Single.just(context)
                .map(RoutingContext::getBodyAsJson)
                .map(o -> o.getString("target"))
                .doOnError(throwable -> ErrorResponse.BAD_JSON.responseFailure(context))
                .flatMap(target -> FriendsDao.deleteFriend(username, target))
                .doOnError(throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
                .subscribe(updateResult -> {
                    if (updateResult.getUpdated() > 0){
                        ResponseUtils.responseSuccess(context);
                    }
                }, Throwable::printStackTrace)
        );
    }

    private static void denyRequest(RoutingContext context) {
        String username = context.session().get("username");
        TrashBin.drop(
            Single.just(context)
                .map(RoutingContext::getBodyAsJson)
                .map(o -> o.getInteger("id"))
                .doOnError(throwable -> ErrorResponse.BAD_JSON.responseFailure(context))
                .flatMap(id -> FriendsDao.denyRequest(id, username))
                .doOnError(throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
                .subscribe(updateResult -> ResponseUtils.responseSuccess(context), Throwable::printStackTrace)
        );
    }

    private static void fetchAllFriends(RoutingContext context) {
        String username = context.session().get("username");
        TrashBin.drop(
            FriendsDao.fetchFriends(username)
                .doOnError(throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
                .subscribe(array -> ResponseUtils.responseSuccess(context, array), Throwable::printStackTrace)
        );
    }

    private static void fetchAllRequest(RoutingContext context) {
        String username = context.session().get("username");
        Single.just(context)
        .flatMap(body -> FriendsDao.fetchRequest(username))
        .doOnError(throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
        .doOnSubscribe(TrashBin::drop)
        .subscribe(objects -> ResponseUtils.responseSuccess(context, objects), Throwable::printStackTrace);
    }

    private static void acceptRequest(RoutingContext context) {
        String username = context.session().get("username");
        TrashBin.drop(
            Single.just(context)
            .map(RoutingContext::getBodyAsJson)
            .map(body -> body.getInteger("id"))
            .doOnError(throwable -> ErrorResponse.BAD_JSON.responseFailure(context))
            .flatMap(id -> FriendsDao.acceptRequest(id, username))
            .subscribe(updateResult -> {
                if (updateResult.getUpdated() > 0){
                    ResponseUtils.responseSuccess(context);
                }else {
                    ErrorResponse.DATABASE_ERROR.responseFailure(context);
                }
            }, Throwable::printStackTrace)
        );
    }

    private static void sendRequest(RoutingContext context) {
        String username = context.session().get("username");
        TrashBin.drop(
            Single.just(context)
            .map(RoutingContext::getBodyAsJson)
            .map(body -> body.getString("to"))
            .flatMap(to -> FriendsDao.sendRequest(username, to))
            .doOnError(throwable -> ErrorResponse.DUPLICATE_REQUEST.responseFailure(context))
            .subscribe(updateResult -> {
                if (updateResult.getUpdated() > 0){
                    ResponseUtils.responseSuccess(context);
                }else {
                    ErrorResponse.DUPLICATE_REQUEST.responseFailure(context);
                }
            }, Throwable::printStackTrace)
        );
    }
}
