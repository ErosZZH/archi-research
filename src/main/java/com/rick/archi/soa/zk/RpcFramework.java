package com.rick.archi.soa.zk;


import com.google.common.collect.Maps;
import org.I0Itec.zkclient.ZkClient;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RpcFramework {

    private static final String ROOT = "/service";

    private ZkClient client;

    private RpcFramework() {
        client = new ZkClient("127.0.0.1");
    }

    private static RpcFramework instance = new RpcFramework();

    public static RpcFramework getInstance() {
        return instance;
    }


    private static Map<String, ClientBootstrap> map = Maps.newHashMap();


	public void publish(final Map<Integer, Object> map) throws Exception {

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
            Class[] interfaces = map.get(port).getClass().getInterfaces();
            String name = interfaces[0].getSimpleName();
            //register
            client.createPersistent(ROOT + "/" + name + "/" + port, true);
        }
    }

    public <T> T subscribe(final Class<T> interfaceClass) throws Exception {

        String host = "127.0.0.1";
        int port;
        String name = interfaceClass.getSimpleName();
        List<String> ports = client.getChildren(ROOT + "/" + name);

        if(ports.size() == 1) {
            port = Integer.valueOf(ports.get(0));
        } else {
            Random r = new Random();
            int index = r.nextInt(ports.size());
            port = Integer.valueOf(ports.get(index));
        }

        System.out.println("------------------" + port);

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
