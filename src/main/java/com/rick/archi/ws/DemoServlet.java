package com.rick.archi.ws;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

public class DemoServlet extends WebSocketServlet {

	private static final long serialVersionUID = 1L;
	private static List<MyMessageInbound> mmiList = new ArrayList<MyMessageInbound>();

	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest arg1) {
		return new MyMessageInbound();
	}
	
	private class MyMessageInbound extends MessageInbound {
		
		WsOutbound myoutbound;

		@Override
		protected void onClose(int status) {
			System.out.println("Close Client.");
            mmiList.remove(this);
		}

		@Override
		protected void onOpen(WsOutbound outbound) {
			 try {
	                System.out.println("Open Client.");
	                this.myoutbound = outbound;
	                mmiList.add(this);
	                outbound.writeTextMessage(CharBuffer.wrap("Hello!"));
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
		}
		
		@Override
		protected void onTextMessage(CharBuffer arg0) throws IOException {
			System.out.println("Accept Message : " + arg0);
            for (MyMessageInbound mmib : mmiList) {
                CharBuffer buffer = CharBuffer.wrap(arg0);
                mmib.myoutbound.writeTextMessage(buffer);
                mmib.myoutbound.flush();
            }
			
		}

		@Override
		public int getReadTimeout() {
			return 0;
		}

		@Override
		protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
			
		}

	}

}
