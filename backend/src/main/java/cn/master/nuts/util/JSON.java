package cn.master.nuts.util;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.TypeFactory;

import java.util.List;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/9/7, 星期一
 **/
public class JSON {
    private static final JsonMapper mapper = new JsonMapper();
    private static final TypeFactory typeFactory = mapper.getTypeFactory();

    public static String toJSONString(Object value) {
        return mapper.writeValueAsString(value);
    }

    /**
     * Serialize with a Jackson JsonView.
     */
    public static String toJSONStringWithView(Object value, Class<?> view) {
        if (view == null) {
            return toJSONString(value);
        }
        return mapper.writerWithView(view).writeValueAsString(value);
    }

    public static <T> List<T> parseArray(String content) {
        return mapper.readValue(content, new TypeReference<>() {
        });
    }

    public static <T> List<T> parseArray(String content, Class<T> valueType) {
        CollectionType javaType = typeFactory.constructCollectionType(List.class, valueType);
        return mapper.readValue(content, javaType);
    }

    public static Map<String, Object> parseMap(String jsonObject) {
        return mapper.readValue(jsonObject, new TypeReference<>() {
        });
    }

    public static byte[] toJSONBytes(Object value) {
        return mapper.writeValueAsBytes(value);
    }

    public static <T> T parseObject(String content, Class<T> valueType) {
        return mapper.readValue(content, valueType);
    }
}
