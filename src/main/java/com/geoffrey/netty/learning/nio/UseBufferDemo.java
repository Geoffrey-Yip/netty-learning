package com.geoffrey.netty.learning.nio;

import java.nio.ByteBuffer;

/**
 * 使用NIO提供的{@link java.nio.Buffer}以及相关子类进行输入or输出操作
 *
 * @author Geoffrey.Yip
 */
public class UseBufferDemo {

    public static void main(String... args) {
        ByteBuffer buffer = ByteBuffer.allocate(512);
        byte[] messageArray = "Hello Nio!".getBytes();
        for (byte b : messageArray) {
            buffer.put(b);
        }
        System.out.println("Read buffer from array successfully,buffer = " + buffer.toString());
        buffer.flip();

        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            sb.append((char) buffer.get());
        }
        System.out.println("Read message from buffer successfully,message = " + sb.toString());
    }
}
