package router;

import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.sstore.SessionStore;
import share.Share;
import utils.ErrorResponse;

public class Session {
    public static final SessionStore store = SessionStore.create(Share.vertx);
    public static final BodyHandler bodyHandler = BodyHandler.create(true);
    public static final SessionHandler sessionHandler = SessionHandler.create(store);

    public static void mountRouter(Router router){
        router.route().handler(bodyHandler);
        router.route().handler(sessionHandler);
    }

    public static void checkPermission(RoutingContext context){
        String username = context.session().get("username");
        if (username == null){
            ErrorResponse.PERMISSION_DENY.responseFailure(context);
            return;
        }
        context.next();
    }
}
