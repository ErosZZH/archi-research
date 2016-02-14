package com.rick.archi.mq;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MQueueReceiver {

	public final static String BROKER_URL = "tcp://localhost:61616";
	
	public final static String DESTINATION = "rick.mq.queue";
	
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
			QueueReceiver receiver = session.createReceiver(queue);
			receiver.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message msg) {
					if(msg != null) {
						MapMessage map = (MapMessage)msg;
						try {
							System.out.println(map.getLong("time") + "接收#" + map.getString("text"));
						} catch (JMSException e) {
							e.printStackTrace();
						}
					}
				}
			});
			Thread.sleep(1000 * 100);
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
		MQueueReceiver.run();
	}
}
