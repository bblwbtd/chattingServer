package model;

import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.UpdateResult;
import share.Share;
import utils.JsonUtils;
import utils.ResultSetUtils;

public class FriendsDao {
    public static Single<UpdateResult> sendRequest(String from, String to){
        //language=PostgreSQL
        String sql = "insert into friends (user1, user2) values (?, ?)";
        return Share
                .client
                .rxUpdateWithParams(sql, JsonUtils.createParams(from, to));
    }

    public static Single<UpdateResult> acceptRequest(int id, String username){
        //language=PostgreSQL
        String sql = "update friends set status = 1 where id = ? and (user1 = ? or user2 = ?)";
        return Share
                .client
                .rxUpdateWithParams(sql, JsonUtils.createParams(id, username, username));
    }

    public static Single<JsonArray> fetchRequest(String username){
        //language=PostgreSQL
        String sql = "select id, user1 as \"from\" from friends where user2 = ? and status = 0";
        return Share
                .client
                .rxQueryWithParams(sql, JsonUtils.createParams(username))
                .map(ResultSetUtils::toJsonArray);
    }

    public static Single<JsonArray> fetchFriends(String username){
        //language=PostgreSQL
        String sql = "select user1, user2 from friends where (user2 = ? or user1 = ?) and status = 1";
        JsonArray params = JsonUtils.createParams(username, username);

        return Share.client
                .rxQueryWithParams(sql, params)
                .map(resultSet -> {
                    JsonArray array = new JsonArray();
                    resultSet.getRows().forEach(o ->{
                        if (o.getString("user1").equals(username)){
                            array.add(o.getString("user2"));
                        }else {
                            array.add(o.getString("user1"));
                        }
                    });
                    return array;
                });

    }

    public static Single<UpdateResult> denyRequest(int id, String username){
        //language=PostgreSQL
        String sql = "delete from friends where id = ? and user2 = ? and status = 0";
        return Share.client
                .rxUpdateWithParams(sql, JsonUtils.createParams(id, username));
    }

    public static Single<UpdateResult> deleteFriend(String user1, String user2){
        String sql = "delete from friends where status = 1 and ((user1 = ? and user2 = ?) or (user2 = ? and user1 = ?))";
        return Share.client
                .rxUpdateWithParams(sql, JsonUtils.createParams(user1,user2,user1,user2));
    }
}
