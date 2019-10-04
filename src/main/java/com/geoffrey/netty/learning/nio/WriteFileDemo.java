package com.geoffrey.netty.learning.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.stream.Stream;

/**
 * 使用Nio将字符串内容输出到文件中
 *
 * @author Geoffrey.Yip
 */
public class WriteFileDemo {
    public static void main(String[] args) throws Exception {

        byte[] writeArray = "Hello Nio".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(512);
        Stream.of(writeArray).forEach(buffer::put);
        buffer.flip();

        try (FileOutputStream outputStream = new FileOutputStream("temp.txt")) {
            FileChannel channel = outputStream.getChannel();
            int result = channel.write(buffer);
            System.out.println("Write file result = " + result);
        }
    }
}
