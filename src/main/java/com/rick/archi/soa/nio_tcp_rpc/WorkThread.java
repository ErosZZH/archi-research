package com.rick.archi.soa.nio_tcp_rpc;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;


public class WorkThread implements Runnable {

    private Selector selector;
    private Map<Integer, Object> map;

    private final static int BLOCK = 4096;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK);

	public WorkThread(Selector selector, Map<Integer, Object> map) {
        this.selector = selector;
        this.map = map;
	}
	@Override
	public void run() {
        while(true) {
            try {
                selector.select();
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while(iter.hasNext()) {
                    SelectionKey sk = iter.next();
                    iter.remove();
                    ServerSocketChannel server = null;
                    SocketChannel client = null;
                    int count = 0;
                    if(sk.isAcceptable()) {
                        server = (ServerSocketChannel)sk.channel();
                        client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    } else if(sk.isReadable()) {
                        client = (SocketChannel)sk.channel();
                        receiveBuffer.clear();
                        count = client.read(receiveBuffer);
                        if(count > 0) {
                            String s = new String(receiveBuffer.array(),0,count);

                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode node = mapper.readTree(s);

                            String methodName = node.get("method").asText();
                            JsonNode ptNode = node.get("parameterType");
                            JsonNode pvNode = node.get("args");

                            Object result = null;
                            if(ptNode.isArray() && pvNode.isArray()) {
                                int length = ptNode.size();
                                Class[] paramTypes = new Class[length];
                                for(int i = 0; i < length; i++) {
                                    paramTypes[i] = Class.forName(ptNode.get(i).asText());
                                }
                                Object[] args = new Object[length];
                                for(int i = 0; i < length; i++) {
                                    args[i] = pvNode.get(i).isInt()? Integer.valueOf(pvNode.get(i).asInt()): pvNode.get(i).asText();
                                }
                                int port = ((InetSocketAddress)client.getLocalAddress()).getPort();
                                Object service = map.get(port);
                                Method method = service.getClass().getMethod(methodName, paramTypes);
                                result = method.invoke(service, args);
                            }

                            sendBuffer.clear();
                            sendBuffer.put(result.toString().getBytes());
                            sendBuffer.flip();
                            client.write(sendBuffer);
                            client.register(selector, SelectionKey.OP_READ);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

}
