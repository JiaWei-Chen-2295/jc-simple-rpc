package fun.javierchen.jcrpc.protocol;

public interface ProtocolConstant {
    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 魔数
     */
    byte MAGIC = (byte) 0x5525;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}
