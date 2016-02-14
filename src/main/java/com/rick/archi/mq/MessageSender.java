package com.rick.archi.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 使用JMS方式收发消息
 * @author eros
 *
 */
public class MessageSender {

	public final static int SEND_NUM = 5;
	
	public final static String BROKER_URL = "tcp://localhost:61616";
	
	public final static String DESTINATION = "rick.mq.queue";
	
	public static void sendMessage(Session session, MessageProducer producer) throws Exception {
		for(int i = 0; i < SEND_NUM; i++) {
			String message = "发送消息第" + (i + 1) + "条";
			TextMessage text = session.createTextMessage(message);
			producer.send(text);
		}
	}
	
	public static void run() throws Exception {
		Connection connection = null;
		Session session = null;
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, 
					ActiveMQConnection.DEFAULT_PASSWORD, 
					BROKER_URL);
			connection = factory.createConnection();
			connection.start();
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(DESTINATION);
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			sendMessage(session, producer);
			session.commit();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(session != null) {
				session.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		MessageSender.run();
	}
}
