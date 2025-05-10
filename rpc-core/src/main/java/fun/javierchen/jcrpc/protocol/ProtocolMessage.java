package fun.javierchen.jcrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolMessage<T> {
    private Header  header;
    private T       body;

    @Data
    public static class Header {
        /**
         * 魔数 保证是rpc协议
         */
        private byte magic;
        private byte version;
        /**
         * 序列化器
         */
        private byte serialization;
        private byte messageType;
        private byte status;
        private long  requestId;
        private int  messageSize;
    }
}
