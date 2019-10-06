package com.geoffrey.netty.learning.nio;

import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 使用NIO完成一个建议的聊天室服务器端
 *
 * @author Geoffrey.Yip
 */
public class SimpleChatRoomServer {

    private static Map<SocketChannel, String> clientMap = new HashMap<>();

    public static void main(String... args) throws Exception {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8080));
        Selector selector = Selector.open();
        //注册监听事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //监听到客户端连接
                if (key.isAcceptable()) {
                    String allocateClientId = String.valueOf(new SecureRandom().nextInt(100));
                    publishMessage("[服务器消息]客户端" + allocateClientId + "上线。");
                    SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
                    channel.configureBlocking(false);
                    //注册可读事件
                    channel.register(selector, SelectionKey.OP_READ);
                    clientMap.put(channel, allocateClientId);
                } else if (key.isReadable()) {
                    //监听到客户端数据
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(512);
                    int res = channel.read(buffer);
                    if (res > 0) {
                        buffer.flip();
                        String content = String.valueOf(CharsetUtil.UTF_8.decode(buffer).array());
                        if (!"\r\n".equals(content)) {
                            publishMessage("[" + clientMap.get(channel) + "]:" + content);
                        }
                    }
                }
                iterator.remove();
            }
        }
    }

    private static void publishMessage(String msg) {
        System.out.println(msg);
        ByteBuffer buffer = ByteBuffer.allocate(128);
        clientMap.keySet().forEach(client -> {
            buffer.clear();
            buffer.put((msg + "\r\n").getBytes(CharsetUtil.UTF_8));
            buffer.flip();
            try {
                client.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
