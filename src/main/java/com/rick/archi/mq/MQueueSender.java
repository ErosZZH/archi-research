package com.rick.archi.mq;

import javax.jms.DeliveryMode;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Queue模式收发消息
 * @author eros
 *
 */
public class MQueueSender {

	public final static int SEND_NUM = 5;
	
	public final static String BROKER_URL = "tcp://localhost:61616";
	
	public final static String DESTINATION = "rick.mq.queue";
	
	public static void sendMessage(QueueSession session, QueueSender sender) throws Exception {
		for(int i = 0; i < SEND_NUM; i++) {
			String message = "发送消息第" + (i + 1) + "条";
			MapMessage map = session.createMapMessage();
			map.setString("text", message);
			map.setLong("time", System.currentTimeMillis());
			System.out.println(map);
			sender.send(map);
		}
	}
	
	public static void run() throws Exception {
		QueueConnection connection = null;
		QueueSession session = null;
		try {
			QueueConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, 
					ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
			connection = factory.createQueueConnection();
			connection.start();
			session = connection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(DESTINATION);
			QueueSender sender = session.createSender(queue);
			sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			sendMessage(session, sender);
			session.commit();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(session != null) {
				session.close();
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		MQueueSender.run();
	}
}
