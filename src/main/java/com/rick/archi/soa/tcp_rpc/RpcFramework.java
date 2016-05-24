package com.rick.archi.soa.tcp_rpc;

import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Set;

public class RpcFramework {

	public static void publish(final Map<Integer, Object> map) throws Exception {

        Set<Map.Entry<Integer, Object>> set = map.entrySet();
        for(Map.Entry<Integer, Object> entry: set) {
            int port = entry.getKey();
            Object service = entry.getValue();
            ServerSocket server = new ServerSocket(port);
            ThreadPoolHelper.getExecutorInstance().execute(new WorkThread(service, server));
        }
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
