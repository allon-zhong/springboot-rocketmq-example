package rocketmq_example.mqandmysqltraction.mq;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class MqProducer {
	static Logger logger = LoggerFactory.getLogger(MqProducer.class);

	public DefaultMQProducer producer = null;
	
	
	@Autowired
	TransactionListener transactionListenerImpl;

	@PostConstruct
	private void init() throws MQClientException {

		logger.info("MQ事物生产者初始化开始--------------------------------------------------");
		
		
		TransactionMQProducer transactionProducer = new TransactionMQProducer("mytestgroup");
		// Producer 组名， 多个 Producer 如果属于一 个应用，发送同样的消息，则应该将它们 归为同一组
		//transactionProducer.setProducerGroup("mytestgroup");
		// Name Server 地址列表
		transactionProducer.setNamesrvAddr("10.10.6.71:9876;10.10.6.72:9876");
		// 超时时间
		transactionProducer.setSendMsgTimeout(30000);
		
		//这个线程池作用就是  mqbroker端回调信息的本地处理线程池
		ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName("client-transaction-msg-check-thread");
						return thread;
					}
				});
		
		
		transactionProducer.setExecutorService(executorService);

		transactionProducer.setTransactionListener(transactionListenerImpl);
		
		producer = transactionProducer;
		
		
		producer.start();
		logger.info("MQ事物生产者初始化结束--------------------------------------------------");

	}
	
	
	public SendResult send(Message me) throws Exception {
		
		return producer.send(me);
	}

	/**
	 * 发送普通消息
	 * @param Topic
	 * @param Tags
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SendResult send(String Topic, String Tags, String body) throws Exception {
		Message me = new Message();
		// 标示
		me.setTopic(Topic);
		// 标签
		me.setTags(Tags);
		// 内容
		me.setBody(body.getBytes(RemotingHelper.DEFAULT_CHARSET));
		return producer.send(me);
	}

	/**
	 * 发送普通消息
	 * @param Topic
	 * @param Tags
	 * @param key
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SendResult send(String Topic, String Tags, String key, String body) throws Exception {
		try {

			Message me = new Message(Topic, Tags, key, 0, body.getBytes(RemotingHelper.DEFAULT_CHARSET), true);

			return producer.send(me);

		} catch (Exception e) {
			logger.error("发送MQ信息异常Topic{},Tags{},key{},body{}", Topic, Tags, key, body);
			throw e;
		}
	}

	@PreDestroy
	public void Destroy() {
		producer.shutdown();
	}
}
