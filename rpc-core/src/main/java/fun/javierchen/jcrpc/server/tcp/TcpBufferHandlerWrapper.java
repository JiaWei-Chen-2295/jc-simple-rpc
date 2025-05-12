package fun.javierchen.jcrpc.server.tcp;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

import static fun.javierchen.jcrpc.protocol.ProtocolConstant.MESSAGE_HEADER_LENGTH;

/**
 * 增强 TcpBufferHandler 防止请求半包和粘包
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> handler) {
        recordParser = initRecordParser(handler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    /**
     * 初始化解析器
     * @param bufferHandler
     * @return
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        RecordParser parser = RecordParser.newFixed(MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            // 初始化
            int size = -1;
            // 完整的读取 消息头和消息体
            Buffer resultBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if (-1 == size) {
                    // 读取消息体的长度
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    // 写入信息头到结果
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 写入信息体到结果
                    resultBuffer.appendBuffer(buffer);
                    bufferHandler.handle(resultBuffer);
                    // 重置当前状态
                    parser.fixedSizeMode(MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }


            }
        });

        return parser;
    }
}
