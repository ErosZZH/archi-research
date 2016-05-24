package com.rick.archi.soa.nio_tcp_rpc;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class InvocationProxy implements InvocationHandler {
	
	private String host;
	private int port;

    private final static int BLOCK = 4096;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK);

    public InvocationProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT);
        sc.connect(new InetSocketAddress(host, port));

        Iterator<SelectionKey> iterator;
        SelectionKey selectionKey;
        SocketChannel client;
        String result = "";
        int count=0;
        boolean finish = false;
        while(!finish) {
            selector.select();
            iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                selectionKey = iterator.next();
                if (selectionKey.isConnectable()) {
                    client = (SocketChannel) selectionKey.channel();
                    if (client.isConnectionPending()) {
                        client.finishConnect();
                        sendBuffer.clear();

                        ObjectMapper objectMapper = new ObjectMapper();
                        ObjectNode node = objectMapper.createObjectNode();
                        Class<?>[] clazz = method.getParameterTypes();
                        ArrayNode array1 = objectMapper.valueToTree(clazz);
                        ArrayNode array2 = objectMapper.valueToTree(args);
                        node.put("method", method.getName());
                        node.put("parameterType", array1);
                        node.put("args", array2);

                        String s = node.toString();

                        sendBuffer.put(s.getBytes());
                        sendBuffer.flip();
                        client.write(sendBuffer);
                    }
                    client.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    client = (SocketChannel) selectionKey.channel();
                    receiveBuffer.clear();
                    count = client.read(receiveBuffer);
                    if(count>0){
                        result = new String( receiveBuffer.array(),0,count);
                    }
                    finish = true;
                }
                selector.selectedKeys().clear();
            }
        }
		
		return result;
	}

}
