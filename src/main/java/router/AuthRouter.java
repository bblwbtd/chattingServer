package router;

import bean.Interface.Login;
import bean.Interface.Register;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import model.UserDao;
import share.Share;
import utils.EncryptionUtils;
import utils.ErrorResponse;
import utils.ResponseUtils;
import utils.TrashBin;

public class AuthRouter {
    public static Router router = Router.router(Share.vertx);
    static {
        Session.mountRouter(router);

        router.post("/login").handler(AuthRouter::login);
        router.post("/register").handler(AuthRouter::register);
    }

    private static void register(RoutingContext context) {
        TrashBin.drop(
                Single.just(context)
                .map(RoutingContext::getBodyAsJson)
                .map(body -> body.mapTo(Register.class))
                .doOnError(throwable -> ErrorResponse.BAD_JSON.responseFailure(context))
                .doOnSuccess(register -> {
                    TrashBin.drop(
                        UserDao.getUser(register.username)
                        .isEmpty()
                        .subscribe(aBoolean -> {
                            if (!aBoolean){
                                ErrorResponse.USER_EXISTED.responseFailure(context);
                            }else {
                                UserDao.addUser(register.username, register.password)
                                        .doOnSuccess(updateResult -> {
                                            if (updateResult.getUpdated() > 0){
                                                ResponseUtils.responseSuccess(context);
                                            }else {
                                                ErrorResponse.DATABASE_ERROR.responseFailure(context);
                                            }
                                        })
                                        .doOnError(throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
                                        .subscribe();
                            }
                        }, throwable -> ErrorResponse.DATABASE_ERROR.responseFailure(context))
                    );
                }).subscribe()
        );
    }

    private static void login(RoutingContext context){
        TrashBin.drop(
            Single.just(context)
            .map(RoutingContext::getBodyAsJson)
            .map(body -> body.mapTo(Login.class))
            .doOnError(throwable -> ErrorResponse.BAD_JSON.responseFailure(context))
            .flatMap(login ->
                UserDao.getUser(login.username)
                .doOnSuccess(user -> {
                    if (user.password.equals(login.password)){
                        context.session().put("username", login.username);
                        context.session().put("key", EncryptionUtils.generateSecreteKey(login.public_key));
                        ResponseUtils.responseSuccess(context, new JsonObject().put("msg", "ok").put("public_key", EncryptionUtils.generatePublicKey()));
                    }else {
                        ErrorResponse.PASSWORD_INCORRECT.responseFailure(context);
                    }
                })
                .doOnError(throwable -> {
                    ErrorResponse.DATABASE_ERROR.responseFailure(context);
                })
                .isEmpty()
                .doOnSuccess(aBoolean -> {
                    if (aBoolean){
                        ErrorResponse.NO_SUCH_USER.responseFailure(context);
                    }
                })
            ).subscribe()
        );
    }
}
