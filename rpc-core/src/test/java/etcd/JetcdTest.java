package etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class JetcdTest {
    @Test
    public void demo() throws ExecutionException, InterruptedException {
        // 使用端点创建客户端
        Client client = Client.builder()
                .endpoints("http://localhost:2379")
                .build();
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        // 存储键值对
        kvClient.put(key, value).get();

        // 通过 CompletableFuture 获取到键值
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);
        GetResponse response = getFuture.get();
        System.out.println("resp" + response);

        // 删除键
        kvClient.delete(key).get();

    }
}
