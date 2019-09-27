package utils;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

public class ResultSetUtils {

    public static JsonArray toJsonArray(ResultSet resultSet){
        JsonArray array = new JsonArray();
        resultSet.getRows().forEach(array::add);
        return array;
    }


}
