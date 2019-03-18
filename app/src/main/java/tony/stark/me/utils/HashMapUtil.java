package tony.stark.me.utils;

import java.util.HashMap;

public class HashMapUtil {

    public static HashMap<String, String> initUploadImageParams(String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("UserID", "1");
        map.put("TimesTamp", System.currentTimeMillis() + "");
        return map;
    }

}
