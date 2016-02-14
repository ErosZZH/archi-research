package com.rick.archi.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MessageReceiver {

	public final static String BROKER_URL = "tcp://localhost:61616";
	
	public final static String DESTINATION = "rick.mq.queue";
	
	public static void run() throws Exception {
		Connection connection = null;
		Session session = null;
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, 
					ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
			connection = factory.createConnection();
			connection.start();
			session = connection.createSession(true,  Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(DESTINATION);
			MessageConsumer consumer = session.createConsumer(destination);
			while(true) {
				Message message = consumer.receive(1000 *100);
				TextMessage text = (TextMessage)message;
				if(text != null) {
					System.out.println("接收：" + text.getText());
				} else {
					break;
				}
			}
			
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
		MessageReceiver.run();
	}
}
