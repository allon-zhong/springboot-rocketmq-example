package rocketmq_example.mq;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class MqConsumer {

	
	@Autowired
	MessageListenerConcurrently acceptmq;

	DefaultMQProducer producer = null;
	DefaultMQPushConsumer consumer = null;

	@PostConstruct
	public void GetBeanMQconsumer() throws MQClientException, InterruptedException {

		/**
		 * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ConsumerGroupName需要由应用来保证唯一
		 */
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("mytest");
		//consumer.setNamesrvAddr("10.10.130.48:9876;10.10.130.49:9876");
		consumer.setNamesrvAddr("10.10.6.71:9876;10.10.6.72:9876");
		consumer.setConsumeThreadMax(20);
		consumer.setConsumeThreadMin(10);
		consumer.setConsumeMessageBatchMaxSize(32);

		/**
		 * 订阅指定topic下tags分别等于TagA或TagC或TagD 订阅指定topic下所有消息<br>
		 * 注意：一个consumer对象可以订阅多个topic
		 */

		consumer.subscribe("ApolloInStockTopic","*");
		/**
		 * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
		 * 如果非第一次启动，那么按照上次消费的位置继续消费
		 */
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

		consumer.registerMessageListener(acceptmq);
		/**
		 * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
		 */
		consumer.start();
		this.consumer = consumer;
	}

	@PreDestroy
	private void consumerDestroy() {
		consumer.shutdown();
	}

}
