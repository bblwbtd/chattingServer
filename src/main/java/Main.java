import router.MainRouter;
import share.Config;
import share.Share;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Share.vertx.createHttpServer()
                .requestHandler(MainRouter.router)
                .listen(Config.ServerPort, Config.ServerHost);
        System.out.println("2333");
    }
}
