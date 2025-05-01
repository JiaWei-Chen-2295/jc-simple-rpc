package SeriallizerTest;
import common.model.User;
import fun.javierchen.jcrpc.serializer.Serializer;
import fun.javierchen.jcrpc.serializer.impl.JSONSerializer;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONSerializerTest {
    @Test
    public void serializer() throws IOException {

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setName("JC");
            user.setAge(23 + i);
            users.add(user);
        }
        Serializer serializer = new JSONSerializer();
        byte[] serialize = serializer.serialize(users);

        // 这样会导致内部的对象被反序列化为 LinkedHashMap 的键值对
        Object deserialize = serializer.deserialize(serialize, List.class);
        System.out.println(deserialize);

        // 使用我们实现的 支持多个类型的反序列化
//        deserialize = serializer.deserialize(serialize, List.class, User.class);


    }

}
