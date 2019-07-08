package rocketmq_example.producerthread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import rocketmq_example.mqandmysqltraction.producer.TransactionProducer;


//@Component
public class MultithreadSendMQ {

	static Logger logger = LoggerFactory.getLogger(MultithreadSendMQ.class);
	
	
	@Bean
	public String sendmq(TransactionProducer mqProducer){
		ExecutorService executorService = new ThreadPoolExecutor(50, 50, 100, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName("test-send-mq");
						return thread;
					}
				});

		for (int i = 0; i < 1000; i++) {
			logger.info("加载线程开始");
			executorService.execute(new Runnable() {

				@Override
				public void run() {

					logger.info("执行发送mq开始");
					for (int i = 0; i < 100000; i++) {

						Message msg;
						try {
							msg = new Message("TopicTest", "TagA",
									(i + "-----Hello RocketMQ aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaassssssssssssssssssssssssddddddddddddddddddddfffffffffffffffffsddssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss")
											.getBytes(RemotingHelper.DEFAULT_CHARSET));
							mqProducer.producer.send(msg);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			});

		}
		return "aaaa";
	}
}
