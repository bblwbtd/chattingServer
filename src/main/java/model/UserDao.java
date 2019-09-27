package model;


import bean.resultSet.User;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.ext.sql.UpdateResult;
import share.Share;
import utils.JsonUtils;


public class UserDao {
    public static Maybe<User> getUser(String username){
        String sql = "select * from chat.public.users where username = ?";
        return Share
                .client
                .rxQueryWithParams(sql, JsonUtils.createParams(username))
                .filter(resultSet -> resultSet.getRows().size() > 0)
                .map(resultSet -> resultSet.getRows().get(0))
                .map(o -> o.mapTo(User.class));
    }

    public static Single<UpdateResult> addUser(String username, String password){
        String sql = "insert into chat.public.users (username, password) VALUES (?, ?)";
        return Share
                .client
                .rxUpdateWithParams(sql, JsonUtils.createParams(username, password));
    }
}
