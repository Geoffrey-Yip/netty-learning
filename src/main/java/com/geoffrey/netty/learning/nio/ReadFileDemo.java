package com.geoffrey.netty.learning.nio;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用NIO将文件读取到内存中
 *
 * @author Geoffrey.Yip
 */
public class ReadFileDemo {

    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream("SomethingText.txt");
        FileChannel channel = inputStream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(512);
        int read = channel.read(buffer);
        System.out.println("read file result = " + read);
        buffer.flip();
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            sb.append((char) buffer.get());
        }
        System.out.println("Read file value : " + sb.toString());
        inputStream.close();
    }

}
