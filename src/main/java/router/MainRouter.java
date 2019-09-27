package router;

import io.vertx.reactivex.ext.web.Router;
import share.Share;

public class MainRouter {
    public static Router router = Router.router(Share.vertx);
    static {
        router.mountSubRouter("/api", ApiRouter.router);
    }
}
