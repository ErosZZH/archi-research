package com.rick.archi.soa.nio_tcp_rpc;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RpcFramework {

	public static void publish(final Map<Integer, Object> map) throws Exception {

        Object[] port = map.keySet().toArray();
        int length = port.length;
        ServerSocketChannel[] ssc = new ServerSocketChannel[length];

        Selector selector = SelectorProvider.provider().openSelector();
        for(int i = 0; i < length; i++) {
            ssc[i] = ServerSocketChannel.open();
            ssc[i].configureBlocking(false);
            ssc[i].socket().bind(new InetSocketAddress(Integer.valueOf(String.valueOf(port[i]))));

            ssc[i].register(selector, SelectionKey.OP_ACCEPT);
        }

        ThreadPoolHelper.getExecutorInstance().execute(new WorkThread(selector, map));

    }  
	
	@SuppressWarnings("unchecked")  
    public static <T> T subscribe(final Class<T> interfaceClass, final String host, final int port) throws Exception {  
          
        if (interfaceClass == null)  
            throw new IllegalArgumentException("Interface class == null");  
        if (! interfaceClass.isInterface())  
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");  
        if (host == null || host.length() == 0)  
            throw new IllegalArgumentException("Host == null!");  
        if (port <= 0 || port > 65535)  
            throw new IllegalArgumentException("Invalid port " + port);  
          
        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);  
          
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass}, new InvocationProxy(host,port));
    } 
}
