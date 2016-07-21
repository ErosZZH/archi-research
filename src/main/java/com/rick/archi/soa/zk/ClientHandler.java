package com.rick.archi.soa.zk;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ClientHandler extends SimpleChannelHandler implements InvocationHandler {

    private Channel channel;

    private String[] result = new String[1];

	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args)
			throws Throwable {

        this.sendMessage(method, args);

        synchronized (result) {
            result.wait();
        }

		return result[0];
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        System.out.println("Client exception...");
        e.getCause().printStackTrace();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.channel = e.getChannel();
    }

    public void sendMessage(Method method, Object[] args) throws Exception {
        if(channel != null && channel.isConnected()) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.createObjectNode();
            Class<?>[] clazz = method.getParameterTypes();
            ArrayNode array1 = objectMapper.valueToTree(clazz);
            ArrayNode array2 = objectMapper.valueToTree(args);
            node.put("method", method.getName());
            node.put("parameterType", array1);
            node.put("args", array2);

            String s = node.toString();

            ChannelFuture future = channel.write(ChannelBuffers.wrappedBuffer(s.getBytes("utf-8")));
            future.sync();
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        String s = new String(buf.array(), "utf-8");

        synchronized (result) {
            this.result[0] = s;
            result.notify();
        }

    }

}
