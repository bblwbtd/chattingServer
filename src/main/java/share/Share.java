package share;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.PostgreSQLClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import share.Config;

public class Share {
    public static final Vertx vertx = Vertx.vertx();
    public static final SQLClient client = PostgreSQLClient.createShared(vertx, Config.getDatabaseConfig());
}
