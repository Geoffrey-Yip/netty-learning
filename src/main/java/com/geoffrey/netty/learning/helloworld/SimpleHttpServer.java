package com.geoffrey.netty.learning.helloworld;

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
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * <p>使用Netty完成一个简易的HTTP server</p>
 * 请求<a href="http://localhost:8080">服务API</a>时将返回 "Hello netty!"
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
                .childHandler(new HttpChannelInitializer()).bind(8080).sync();

        channelFuture.channel().closeFuture().sync();
    }

    public static class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("HttpServerCodec", new HttpServerCodec())
                    .addLast("HttpServerApiHandler", new HttpServerApiHandler());
        }


        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            System.out.println("handler removed");
            super.handlerRemoved(ctx);
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.println("handler Added");
            super.handlerAdded(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channel unregisted");
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channel active");
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channel inactive");
            super.channelInactive(ctx);
        }
    }


    public static class HttpServerApiHandler extends SimpleChannelInboundHandler<HttpObject> {

        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

            if (!(msg instanceof HttpRequest)) {
                return;
            }
            HttpRequest req = (HttpRequest) msg;

            System.out.println("Received request method :" + req.method().name());
            System.out.println("Received request uri :" + req.uri());

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
