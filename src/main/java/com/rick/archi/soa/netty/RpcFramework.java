package com.rick.archi.soa.netty;


import com.google.common.collect.Maps;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;

public class RpcFramework {

    private static Map<String, ClientBootstrap> map = Maps.newHashMap();


	public static void publish(final Map<Integer, Object> map) throws Exception {

        ServerBootstrap server = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        ThreadPoolHelper.getExecutorInstance(),
                        ThreadPoolHelper.getExecutorInstance()
                )
        );
        server.setPipelineFactory(	new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new ServerHandler(map));
            }
        });

        Set<Integer> ports = map.keySet();
        for(int port : ports) {
            server.bind(new InetSocketAddress(port));
        }
    }

    public static <T> T subscribe(final Class<T> interfaceClass, final String host, final int port) throws Exception {

        final SimpleChannelHandler handler = new ClientHandler();
        final String[] result = new String[1];

        ClientBootstrap client;
        if((client = map.get("client")) == null) {
            client = new ClientBootstrap(
                    new NioClientSocketChannelFactory(
                            ThreadPoolHelper.getExecutorInstance(),
                            ThreadPoolHelper.getExecutorInstance()
                    )
            );
            map.put("client", client);
        }

        client.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(handler);
            }
        });
        ChannelFuture future = client.connect(new InetSocketAddress(host, port));

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                synchronized (result) {
                    result.notify();
                }
            }
        });

        synchronized (result) {
            result.wait();
        }

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass},
                (InvocationHandler)handler);
    }
}
