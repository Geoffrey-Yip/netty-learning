package com.geoffrey.netty.learning.helloworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

/**
 * 简易的Socket服务器
 *
 * @author Geoffrey.Yip
 * @see SimpleSocketClient 客户端
 */
public class SimpleSocketServer {

    public static void main(String[] args) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        System.out.println("Server starting...");

        try {
            ServerBootstrap boostrap = new ServerBootstrap();
            ChannelFuture future = boostrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new SocketServerChannelInitializer())
                    .bind(8080).sync();
            future.channel().closeFuture().sync();
        } finally {
            System.out.println("Server shutdown!");
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


    public static class SocketServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                    0, 4, 0, 4));
            pipeline.addLast(new LengthFieldPrepender(4));
            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
            pipeline.addLast("mySocketServerHandler", new MySocketServerHandler());
        }
    }


    public static class MySocketServerHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(String.format("[%d][Client %s]:%s", System.currentTimeMillis(), ctx.channel().remoteAddress(), msg));
            Scanner sc = new Scanner(System.in);
            String response = sc.nextLine();
            ctx.channel().writeAndFlush(response);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ;
            ctx.close();
        }
    }


}
