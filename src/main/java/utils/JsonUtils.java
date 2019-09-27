package utils;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

import java.util.Arrays;

public class JsonUtils {
    public static JsonArray createParams(Object... params){
        return new JsonArray(Arrays.asList(params));
    }

    public static JsonArray toJsonArray(ResultSet resultSet){
        JsonArray array = new JsonArray();
        resultSet.getRows().forEach(array::add);
        return array;
    }
}
