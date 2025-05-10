package fun.javierchen.jcrpc.protocol;

import fun.javierchen.jcrpc.model.RpcRequest;
import fun.javierchen.jcrpc.model.RpcResponse;
import fun.javierchen.jcrpc.serializer.Serializer;
import fun.javierchen.jcrpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageDecoder {
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.MAGIC) {
            throw new RuntimeException("magic 校验失败");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerialization(buffer.getByte(2));
        header.setMessageType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setMessageSize(buffer.getInt(13));
        // 防止粘包
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getMessageSize());
        // 解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerialization());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化消息协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum typeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getMessageType());
        if (typeEnum == null) {
            throw new RuntimeException("消息类型不存在");
        }

        return switch (typeEnum) {
            case REQUEST -> {
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                yield new ProtocolMessage<>(header, request);
            }
            case RESPONSE -> {
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                yield new ProtocolMessage<>(header, response);
            }
            default -> throw new RuntimeException("不支持的消息类型");
        };


    }
}
