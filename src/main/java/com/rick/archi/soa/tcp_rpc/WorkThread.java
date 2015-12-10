package com.rick.archi.soa.tcp_rpc;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;


public class WorkThread implements Runnable {

	private Object service;
	private Socket socket;
	
	public WorkThread(Object service, Socket socket) {
		this.service = service;
		this.socket = socket;
	}
	@Override
	public void run() {
		try {
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			String methodName = input.readUTF();
			Class<?>[] parameterTypes = (Class<?>[])input.readObject();
			Object[] arguments = (Object[])input.readObject();
			
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			Method method = service.getClass().getMethod(methodName, parameterTypes);
			Object result = method.invoke(service, arguments);
			output.writeObject(result);
			
			output.close();
			input.close();
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
