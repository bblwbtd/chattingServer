package model;

import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import share.Share;
import utils.JsonUtils;
import utils.ResultSetUtils;

import java.sql.Timestamp;
import java.time.LocalTime;

public class ChatDao {

    public static Single<JsonArray> fetchMessageAfterId(String username, String target, int id){
        //language=PostgreSQL
        String sql = "select id, \"from\", \"to\", content, time from record where ((\"from\" = ? and  \"to\" = ?) or (\"to\" = ? and  \"from\" = ?)) and id > ?";
        return Share.client
                .rxQueryWithParams(sql, JsonUtils.createParams(username, target, username, target,id))
                .map(resultSet -> {
                    JsonArray array = new JsonArray();
                    resultSet.getRows().forEach(o -> {
                        JsonObject object = new JsonObject();
                        object.put("id", o.getInteger("id"));
                        object.put("from", o.getString("from"));
                        object.put("to", o.getString("to"));
                        object.put("content", o.getString("content"));
                        object.put("time", Timestamp.valueOf(o.getString("time").replace("T", " ")).getTime());
                        array.add(object);
                    });
                    return array;
                });
    }

    public static Single<JsonArray> fetchAllMessage(String username, String target){
        //language=PostgreSQL
        String sql = "select id, \"from\", \"to\", content, time from record where (\"from\" = ? and  \"to\" = ?) or (\"to\" = ? and  \"from\" = ?)";

        return Share.client
                .rxQueryWithParams(sql, JsonUtils.createParams(username, target, username, target))
                .map(resultSet -> {
                    JsonArray array = new JsonArray();
                    resultSet.getRows().forEach(o -> {
                        JsonObject object = new JsonObject();
                        object.put("id", o.getInteger("id"));
                        object.put("from", o.getString("from"));
                        object.put("to", o.getString("to"));
                        object.put("content", o.getString("content"));
                        object.put("time", Timestamp.valueOf(o.getString("time").replace("T", " ")).getTime());
                        array.add(object);
                    });
                    return array;
                });
    }

    public static Single<UpdateResult> sendMessage(String username, String to, String content){
        //language=PostgreSQL
        String sql = "insert into record (\"from\", \"to\", content) values (?, ?, ?)";
        return Share.client
                .rxUpdateWithParams(sql, JsonUtils.createParams(username, to, content));
    }
}
