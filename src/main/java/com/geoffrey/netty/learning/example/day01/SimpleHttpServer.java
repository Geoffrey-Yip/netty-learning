package com.geoffrey.netty.learning.example.day01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * 使用Netty完成一个简易的HTTP server 端
 *
 * @author Geoffrey.Yip
 */
public class SimpleHttpServer {

    public static void main(String... args) throws Exception {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        // add shutdown jvm release hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server shutdown!");
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }));
        System.out.println("Server starting...");

        ChannelFuture channelFuture = new ServerBootstrap().group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("HttpServerCodec",new HttpServerCodec())
                                .addLast("HttpServerApiHandler", new HttpServerApiHandler());
                    }
                }).bind(8080).sync();

        channelFuture.channel().closeFuture().sync();
    }


    public static class HttpServerApiHandler extends SimpleChannelInboundHandler<HttpObject> {

        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

            // Write message to browser
            ByteBuf responseContent = Unpooled.copiedBuffer("Hello netty!", CharsetUtil.UTF_8);

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, responseContent);

            // Add Http response headers
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, responseContent.readableBytes());
            ctx.writeAndFlush(response);
        }
    }
}
