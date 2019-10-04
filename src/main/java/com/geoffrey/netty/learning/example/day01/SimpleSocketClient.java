package com.geoffrey.netty.learning.example.day01;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

/**
 * 简易的Socket客户端
 *
 * @author Geoffrey.Yip
 * @see SimpleSocketServer 服务端
 */
public class SimpleSocketClient {

    public static void main(String... args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        System.out.println("Socket client starting...");
        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelFuture future = bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new SocketClientInitializer())
                    .connect("localhost", 8080).sync();

            Scanner sc = new Scanner(System.in);
            System.out.println("Server socket connection successfully,please input you message.");
            String msg = sc.nextLine();
            future.channel().writeAndFlush(msg);

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static class SocketClientInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                    0, 4, 0, 4));
            pipeline.addLast(new LengthFieldPrepender(4));
            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
            pipeline.addLast(new SocketClientHandelr());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static class SocketClientHandelr extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(String.format("[%d][Server %s]:%s", System.currentTimeMillis(), ctx.channel().remoteAddress(), msg));
            Scanner sc = new Scanner(System.in);
            String response = sc.nextLine();
            ctx.channel().writeAndFlush(response);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
