package com.rick.archi.mq;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class TopicReceiver {

	public static final String BROKER_URL = "tcp://localhost:61616";
	public static final String DESTINATION = "rick.mq.topic";
	
	public static void run() throws Exception {
		TopicConnection connection = null;
		TopicSession session = null;
		try {
			TopicConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, 
					ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
			connection = factory.createTopicConnection();
			connection.start();
			session = connection.createTopicSession(true, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(DESTINATION);
			TopicSubscriber subscriber = session.createSubscriber(topic);
			subscriber.setMessageListener(new MessageListener() {
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
		TopicReceiver.run();
	}
}
