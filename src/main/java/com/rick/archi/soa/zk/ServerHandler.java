package com.rick.archi.soa.zk;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by rick on 16/6/24.
 */
public class ServerHandler extends SimpleChannelHandler {

    private Map<Integer, Object> map;

    public ServerHandler(Map<Integer, Object> map) {
        this.map = map;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        System.out.println("Exception occured...");
        e.getCause().printStackTrace();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        String s = new String(buf.array(), "utf-8");

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
            int port = ((InetSocketAddress)e.getChannel().getLocalAddress()).getPort();
            Object service = map.get(port);
            Method method = service.getClass().getMethod(methodName, paramTypes);
            result = method.invoke(service, args);
        }

        ChannelFuture future = e.getChannel().write(ChannelBuffers.wrappedBuffer(((String)result).getBytes("utf-8")));
        future.sync();
    }
}
