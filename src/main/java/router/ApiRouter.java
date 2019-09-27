package router;

import io.vertx.reactivex.ext.web.Router;
import share.Share;

public class ApiRouter {
    public static Router router = Router.router(Share.vertx);
    static {
        router.mountSubRouter("/auth", AuthRouter.router);
        router.mountSubRouter("/social", SocialRouter.router);
        router.mountSubRouter("/chat", ChatRouter.router);
    }
}
