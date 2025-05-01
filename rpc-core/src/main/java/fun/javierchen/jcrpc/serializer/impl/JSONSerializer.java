package fun.javierchen.jcrpc.serializer.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.javierchen.jcrpc.serializer.Serializer;

import java.io.IOException;

public class JSONSerializer implements Serializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return objectMapper.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        return objectMapper.readValue(bytes, type);
    }

    /**
     * 支持泛型反序列化的方法，参数为可变长 Class 用于构造泛型结构
     * 示例：
     *   List<User> list = serializer.deserialize(jsonBytes, List.class, User.class);
     *   Map<String, User> map = serializer.deserialize(jsonBytes, Map.class, String.class, User.class);
     */
    public <T> T deserialize(byte[] bytes, Class<?> collectionType, Class<?>... elementTypes) throws IOException {
        JavaType javaType = constructJavaType(collectionType, elementTypes);
        return (T) objectMapper.readValue(bytes, javaType);
    }

    /**
     * 使用 Jackson 构造泛型类型
     */
    private JavaType constructJavaType(Class<?> collectionType, Class<?>... elementTypes) {
        if (elementTypes == null || elementTypes.length == 0) {
            return objectMapper.getTypeFactory().constructType(collectionType);
        }

        JavaType[] javaTypes = new JavaType[elementTypes.length];
        for (int i = 0; i < elementTypes.length; i++) {
            javaTypes[i] = objectMapper.getTypeFactory().constructType(elementTypes[i]);
        }

        return objectMapper.getTypeFactory().constructParametricType(collectionType, javaTypes);
    }
}
