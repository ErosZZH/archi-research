package com.rick.archi.mq;

import javax.jms.DeliveryMode;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 发布订阅模式
 * @author eros
 *
 */
public class TopicSender {

	public static final int SEND_NUM = 5;
	public static final String BROKER_URL = "tcp://localhost:61616";
	public static final String DESTINATION = "rick.mq.topic";
	
	public static void sendMessage(TopicSession session, TopicPublisher publisher) throws Exception {
		for(int i = 0; i < SEND_NUM; i++) {
			String message = "发送消息第" + (i + 1) + "条";
			MapMessage map = session.createMapMessage();
			map.setString("text", message);
			map.setLong("time", System.currentTimeMillis());
			System.out.println(map);
			publisher.send(map);
		}
	}
	
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
			TopicPublisher publisher = session.createPublisher(topic);
			publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			sendMessage(session, publisher);
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
		TopicSender.run();
	}
}
